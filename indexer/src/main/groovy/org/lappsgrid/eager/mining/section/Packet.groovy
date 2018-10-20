package org.lappsgrid.eager.mining.section

import javax.naming.OperationNotSupportedException

/**
 *
 */
class Packet {
    enum Type { Text, Node, Set, Error }
    String path
    Type type
    Object data

    Packet() {
        this('undefined', Type.Error, null)
    }
    Packet(String path) {
        this(path, Type.Error,null)
    }
    Packet(String path, Type type, Object data) {
        this.path = path
        this.type = type
        this.data = data
    }

    Packet path(String path) {
        this.path = path
        this
    }
    Packet node(Node node) {
        this.data = node
        this.type = Type.Node
        this
    }
    Packet xml(String xml) {
        this.data = xml
        this.type = Type.Text
        this
    }
    Packet set(Set<String> data) {
        this.data = data
        this.type = Type.Set
        this
    }

    Node asNode() {
        if (type != Type.Node) {
            throw new OperationNotSupportedException("Data is not a node: " + type)
        }
        return (Node) data
    }

    String asString() {
        if (type != Type.Text) {
            throw new OperationNotSupportedException("Data is not a string: " + type)
        }
        return data.toString()
    }

    Set<String> asSet() {
        if (type != Type.Set) {
            throw new OperationNotSupportedException("Data is not a set: " + type)
        }
        return (Set) data
    }
}
