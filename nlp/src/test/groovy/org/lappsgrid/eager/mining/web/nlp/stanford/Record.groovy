package org.lappsgrid.eager.mining.web.nlp.stanford
/**
 *
 */
class Record {
    String path
    String json

    Record() { }
    Record(File file) {
        this.path = file.name
        this.json = file.text
    }

    Record(String path, String json) {
        this.path = path
        this.json = json
    }
    Record(Map map) {
        this.path = map.path
        this.json = map.json
    }

    Record path(String path) {
        this.path = path
        this
    }
    Record json(String json) {
        this.json = json
        this
    }
//    String asJson() {
//        new JsonBuilder(this).toPrettyString()
//    }
//
//    static Record parse(String json) {
//        Map map = new JsonSlurper().parseText(json)
//        return new Record(map)
//    }
}
