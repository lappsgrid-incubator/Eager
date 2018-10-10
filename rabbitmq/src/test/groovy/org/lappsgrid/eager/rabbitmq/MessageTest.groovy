package org.lappsgrid.eager.rabbitmq

import org.junit.Test

/**
 *
 */
class MessageTest {

    @Test
    void route() {
        Message message = new Message().route('a', 'b', 'c')
        assert ['a','b','c'] == message.route
    }
}
