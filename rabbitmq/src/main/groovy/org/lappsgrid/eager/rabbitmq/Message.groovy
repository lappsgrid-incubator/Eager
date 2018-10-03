package org.lappsgrid.eager.rabbitmq

/**
 *
 */
class Message {
    String command
    String body
    List<String> route
    Map<String,String> parameters

    Message() {
        command = ''
        route = []
        body = ''
        parameters = [:]
    }

    Message(String command, String body, String... route) {
        this(command, body, [:], route.toList())
    }

    Message(String command, String body, Map<String,String> parameters, String... route) {
        this(command, body, route.toList())
    }

    Message(String command, String body, List<String> route) {
//        this.command = command
//        this.body = body
//        this.route = route
        this(command, body, [:], route)
    }

    Message(String command, String body, Map<String, String> paramters, List<String> route) {
        this.command = command
        this.body = body
        this.route = route
        this.parameters = parameters
    }

    Message command(String command) { this.command = command ; this }
    Message body(String body)       { this.body = body       ; this }
    Message route(String route)     { this.route.add(route)  ; this}
    Message route(String... route) {
        route.each { this.route.add(it) }
    }
    Message set(String name, String value) { this.parameters[name] = value ;  this }
    String get(String key) {
        return parameters[key]
    }
}
