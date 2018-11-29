package org.lappsgrid.eager.mining.preprocess.pmc

import groovy.util.logging.Slf4j

/**
 *
 */
@Slf4j("logger")
class DirectoryIterator {

    Stack<File> stack
    FileFilter filter

    DirectoryIterator(String path) {
        this(new File(path))
    }

    DirectoryIterator(File root) {
        filter = { File file -> file.isDirectory() || file.name.endsWith(".nxml")}

        stack = new ArrayList<File>()
        stack.push(root)
    }

    File next() {
        if (stack.isEmpty()) {
            return null
        }

        File entry = stack.pop()
        while (!entry.isFile()) {
            stack.addAll(entry.listFiles(filter))
            if (stack.isEmpty()) {
                return null
            }
            entry = stack.pop()
        }
        return entry
    }
}
