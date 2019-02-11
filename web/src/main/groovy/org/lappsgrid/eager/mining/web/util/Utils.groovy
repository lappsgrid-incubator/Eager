package org.lappsgrid.eager.mining.web.util

import groovy.util.logging.Slf4j

import static org.lappsgrid.discriminator.Discriminators.*
import org.lappsgrid.serialization.lif.Annotation
import org.lappsgrid.serialization.lif.Container
import org.lappsgrid.serialization.lif.View

/**
 *
 */
@Slf4j("logger")
class Utils {

    static List<Annotation> getTokens(Container container) {
        List<View> views = container.findViewsThatContain(Uri.TOKEN)
        if (views.size() == 0) {
            return []
        }
        View view = views[-1]
        return view.findByAtType(Uri.TOKEN)
    }

    static ConfigObject loadConfiguration() {
        ConfigSlurper parser = new ConfigSlurper()

        String filename = "eager-web.conf"

        // Look for the config file in the local (working) directory.
        File file = new File(filename)
        if (file.exists()) {
            logger.info("Loaded configuration from {}", file.path)
            return parser.parse(file.text)
        }

        // Look in a few other default locations.
        file  = new File("/etc/defaults", filename)
        if (file.exists()) {
            logger.info("Loaded configuration from {}", file.path)
            return parser.parse(file.text)
        }
        file = new File("/etc/eager", filename)
        if (file.exists()) {
            logger.info("Loaded configuration from {}", file.path)
            return parser.parse(file.text)
        }

        // Use the bundled config
        InputStream stream = this.class.getResourceAsStream('/' + filename)
        if (stream != null) {
            logger.info("Loaded configuration from JAR")
            return parser.parse(stream.text)
        }

        // This is bad...
        logger.warn("Using hard coded configuration")
        String script = '''
solr.host = "http://129.114.16.34:8983/solr"
solr.collection = "bioqa"
solr.rows = "5000"
galaxy.host = "https://jetstream.lappsgrid.org"
galaxy.key = System.getenv("GALAXY_API_KEY")
work.dir = "/tmp/eager/work"
cache.dir = "/tmp/eager/cache" 
cache.ttl = 10
question.dir = "/tmp/eager/questions"
upload.postoffice = "galaxy.upload.service"
upload.address = "zip"
'''
        return parser.parse(script)
    }
}
