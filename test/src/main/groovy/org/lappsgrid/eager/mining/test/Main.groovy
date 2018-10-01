package org.lappsgrid.eager.mining.test

import groovy.cli.picocli.CliBuilder
import org.lappsgrid.eager.rabbitmq.tasks.TaskQueue
import org.lappsgrid.eager.rabbitmq.tasks.Worker

/**
 *
 */
class Main {

    Object semaphore = new Object()
    Worker worker

    void worker(String id) {
        println "Starting worker $id"
        //TaskQueue queue = new TaskQueue('test.queue')
        worker = new Worker('test.queue') {

            @Override
            void work(String message) {
                if ('shutdown' == message) {
                    println "Shutting down worker $id"
                    synchronized (semaphore) {
                        semaphore.notifyAll()
                    }
                }
                else {
                    println "Woker $id: $message"
                }
            }
        }
    }

    static void send(String message) {
        TaskQueue queue = new TaskQueue('test.queue')
        queue.send(message)
        queue.close()
        println 'Message sent.'
    }

    static void main(String[] args) {
        if (args.length < 1) {
            println "No args"
            return
        }
        String command = args[0]
        if ('shutdown' == command) {
            send('shutdown')
        }
        else if ('start' == command) {
            if (args.length != 2) {
                println "Invalid command: no ID specified"
                println "java -jar rabbit.jar start <worker id>"
                return
            }
            Main app = new Main()
            app.worker(args[1])
            synchronized (app.semaphore) {
                app.semaphore.wait()
            }
            app.worker.close()
            println "Worker $args[1] terminated."
        }
        else if ('send' == command) {
            String message = args[1..-1].join(' ')
            send(message)
        }
        else {
            println "Unrecognized command: $command"
        }

    }
}
