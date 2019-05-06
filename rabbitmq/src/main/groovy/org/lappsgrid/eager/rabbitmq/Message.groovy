package org.lappsgrid.eager.rabbitmq

/**
 *
 */
class Message {
    String command
    Object body
    List<String> route
    Map<String,String> parameters

    Message() {
        command = ''
        route = []
        body = ''
        parameters = [:]
    }

    Message(String command, Object body, String... route) {
        this(command, body, [:], route.toList())
    }

    Message(String command, Object body, Map<String,String> parameters, String... route) {
        this(command, body, route.toList())
        this.parameters = parameters
    }

    Message(String command, Object body, List<String> route) {
        this(command, body, [:], route)
    }

    Message(String command, Object body, Map<String, String> paramters, List<String> route) {
        this.command = command
        this.body = body
        this.route = route
        this.parameters = parameters
    }

    Message command(String command) { this.command = command ; this }
    Message body(Object body)       { this.body = body       ; this }
    Message route(String route)     { this.route.add(route)  ; this}
    Message route(String... route) {
        route.each { this.route.add(it) }
        return this
    }
    Message set(String name, String value) { this.parameters[name] = value ;  this }
    String get(String key) {
        return parameters[key]
    }
}
