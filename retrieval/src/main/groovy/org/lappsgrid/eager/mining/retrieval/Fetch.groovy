package org.lappsgrid.eager.mining.retrieval

import org.lappsgrid.eager.rabbitmq.Message
import org.lappsgrid.eager.rabbitmq.topic.MailBox
import org.lappsgrid.eager.rabbitmq.topic.PostOffice
import org.lappsgrid.serialization.Serializer

import com.lambdaworks.redis.*;

/**
 *
 */
class Fetch {
    static final String EXCHANGE = 'eager.postoffice'

    RedisClient redis

    PostOffice office
    MailBox box

    Fetch() {
        office = new PostOffice('eager.postoffice')
        RedisURI uri = RedisURI.create("redis://password@localhost:6379")
        redis = new RedisClient(uri)
    }

    void start() {
        box = new MailBox(EXCHANGE, 'load') {
            @Override
            boolean recv(String message) {
                return process(message)
            }
        }
    }

    boolean process(String json) {
        Message message = Serializer.parse(json, Message)

    }
}
