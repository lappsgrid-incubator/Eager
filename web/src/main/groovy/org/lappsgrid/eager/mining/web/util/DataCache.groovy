package org.lappsgrid.eager.mining.web.util

import groovy.util.logging.Slf4j
import org.lappsgrid.serialization.Serializer

import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * Stores result documents for a short period of time (five minutes) and then removes them from disk and from memory.
 */
@Slf4j("logger")
class DataCache {

    static final int DEFAULT_TTL = 5
    static final String DEFAULT_DIR = "/tmp/eager/cache"

    /** Disk cache */
    File cacheDir
    /** In memory cache */
    Map<String,String> index

    /** Executor service used to schedule cleanup tasks. */
    ScheduledExecutorService executor

    /** How many minutes items will live in the cache before being removed. */
    int ttl

    DataCache() throws IOException {
        this(new File(DEFAULT_DIR), DEFAULT_TTL)
    }

    DataCache(int ttl) throws IOException {
        this(new File(DEFAULT_DIR), ttl)
    }

    DataCache(String path) throws IOException {
        this(new File(path), DEFAULT_TTL)
    }

    DataCache(String path, int ttl) throws IOException {
        this(new File(path), ttl)
    }

    DataCache(File directory, int ttl) throws IOException {
        this.ttl = ttl
        cacheDir = directory
        if (!cacheDir.exists()) {
            if (!cacheDir.mkdirs()) {
                logger.error("Unable to create cache directory {}", cacheDir.path)
                throw new IOException("Unable to create the data cache directory " + cacheDir.path)
            }
        }
        cacheDir.deleteOnExit()
        index = new HashMap<>()
        executor = Executors.newScheduledThreadPool(1)
        logger.info("Data cache initialized.")
        logger.debug("Cache directory: {}", cacheDir.path)
        logger.debug("Cache TTL: {}", ttl)
    }

    String add(Map data) {
        return add(Serializer.toJson(data))
    }

    String add(String uuid, Map data) {
        return add(uuid, Serializer.toJson(data))
    }

    String add(String data) {
        return add(UUID.randomUUID(), data)
    }

    String add(String uuid, String data) {
        File datafile = new File(cacheDir, uuid)
        datafile.text = data
        datafile.deleteOnExit()
        index.put(uuid, datafile.path)
        scheduleForRemoval(uuid)
        logger.info("Added {} bytes with key {}", data.size(), uuid)
        return uuid
    }

    String get(String key) {
        String path = index.get(key)
        if (path == null) {
            return null
        }
        File file = new File(path)
        if (!file.exists()) {
            return null
        }
        String result = file.text
        file.delete()
        return result
    }

    void scheduleForRemoval(String key) {
        Runnable task = new Runnable() {
            void run() {
                remove(key)
            }
        }
        logger.info("Scheduling {} for removal.", key)
        executor.schedule(task, ttl, TimeUnit.MINUTES)
    }

    void remove(String key) {
        logger.info("Removing {} from index.", key)
        String path = index.get(key)
        if (path != null) {
            index.remove(key)
            File file = new File(path)
            if (file.exists()) {
                file.delete()
                logger.debug("Removed {} from the disk cache.", file.name)
            }
            else {
                logger.warn("File for key {} not found.", key)
            }
        }
        else {
            logger.warn("Key {} was not found in index.", eky)
        }
    }

}
