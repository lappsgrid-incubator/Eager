package org.lappsgrid.eager.rabbitmq

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.Consumer
import com.rabbitmq.client.MessageProperties

/**
 *
 */
class MessageQueue {
    private String name
    private Connection connection
    private Channel channel

    public MessageQueue(String name) {
        this(name, 'localhost' , true, true)
    }

    public MessageQueue(String name, String host) {
        this(name, host, true, true)
    }

    public MessageQueue(String name, String host, boolean durable, boolean fair) {
        this.name = name
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        connection = factory.newConnection();
        channel = connection.createChannel();
        if (fair) {
            channel.basicQos(1)
        }
        channel.queueDeclare(name, true, durable, false, null);

//        String message = getMessage(argv);
//
//        channel.basicPublish( "", name,
//                MessageProperties.PERSISTENT_TEXT_PLAIN,
//                message.getBytes());
//        System.out.println(" [x] Sent '" + message + "'");
//
    }

    void register(Consumer consumer) {
        channel.basicConsume(name, false, consumer)
    }

    void send(String message) {
        channel.basicPublish('', name, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes())
    }

    void close() {
        channel.close();
        connection.close();
    }
}
