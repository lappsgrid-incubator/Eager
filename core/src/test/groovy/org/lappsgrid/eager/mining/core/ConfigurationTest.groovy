package org.lappsgrid.eager.mining.core

import org.junit.Test

import java.time.*
import java.time.temporal.*

/**
 *
 */
class ConfigurationTest {

    @Test
    void defaults() {
        Configuration config = new Configuration()
        assert 'eager.broadcast' == config.BROADCAST
        assert 'eager.postoffice' == config.POSTOFFICE
        assert 'error' == config.BOX_ERROR
        assert 'load' == config.BOX_LOAD
        assert 'nlp.stanford' == config.BOX_NLP_STANFORD
    }

    @Test
    void parsed() {
        String script = '''
broadcast='test_broadcast'
postoffice='test_postoffice'
pobox.error='test_error'
pobox.load='test_load'
pobox.nlp.stanford='test_stanford'
'''
        Configuration config = new Configuration(script)
        assert 'test_broadcast' == config.BROADCAST
        assert 'test_postoffice' == config.POSTOFFICE
        assert 'test_error' == config.BOX_ERROR
        assert 'test_load' == config.BOX_LOAD
        assert 'test_stanford' == config.BOX_NLP_STANFORD
    }

    @Test
    void nested() {
        String script = '''
pobox {
    error = 'test_error'
    load = 'test_load'
    nlp {
        stanford = 'test_stanford'
    }
}
'''
        Configuration config = new Configuration(script)
        assert 'test_error' == config.BOX_ERROR
        assert 'test_load' == config.BOX_LOAD
        assert 'test_stanford' == config.BOX_NLP_STANFORD
    }

    @Test
    void missingBroadcast() {
        String script = '''
postoffice='test_postoffice'
pobox.error='test_error'
pobox.load='test_load'
pobox.nlp.stanford='test_stanford'
'''
        Configuration config = new Configuration(script)
        assert 'eager.broadcast' == config.BROADCAST
        assert 'test_postoffice' == config.POSTOFFICE
        assert 'test_error' == config.BOX_ERROR
        assert 'test_load' == config.BOX_LOAD
        assert 'test_stanford' == config.BOX_NLP_STANFORD

    }

}
