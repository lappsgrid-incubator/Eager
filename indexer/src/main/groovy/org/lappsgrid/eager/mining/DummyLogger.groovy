package org.lappsgrid.eager.mining

/**
 *
 */
class DummyLogger {

    void trace(String msg) {
        println "TRACE: $msg"
    }
    void debug(String msg) {
        println "DEBUG: $msg"
    }
    void info(String msg) {
        println "INFO : $msg"
    }
    void warn(String msg) {
        println "WARN : $msg"
    }
    void error(String msg) {
        println "ERROR: $msg"
    }

    void trace(String msg, Object... args) {
        print("TRACE", msg, args)
    }
    void debug(String msg, Object... args) {
        print("DEBUG", msg, args)
    }
    void info(String msg, Object... args) {
        print("INFO ", msg, args)
    }
    void warn(String msg, Object... args) {
        print("WARN ", msg, args)
    }
    void error(String msg, Object... args) {
        print("ERROR", msg, args)
    }

    private void print(String level, String template, Object... args) {
        args.each { template = template.replace("{}", it.toString()) }
        println "$level: $template"
    }
}
