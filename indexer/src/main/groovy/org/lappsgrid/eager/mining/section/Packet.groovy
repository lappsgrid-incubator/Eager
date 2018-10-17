package org.lappsgrid.eager.mining.section

/**
 *
 */
class Packet {
    String path
    Object data

    Packet() { }
    Packet(String path) {
        this(path, null)
    }
    Packet(String path, Object data) {
        this.path = path
        this.data = data
    }

    Node asNode() {
        return (Node) data
    }

    String asString() {
        return data.toString()
    }
}
