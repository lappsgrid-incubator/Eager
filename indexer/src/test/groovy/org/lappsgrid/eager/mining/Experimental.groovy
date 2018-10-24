package org.lappsgrid.eager.mining

import org.junit.Test
import org.lappsgrid.eager.core.json.Serializer

/**
 *
 */
class Experimental {

    @Test
    void test() {
        List servers = [1,2].collect { "solr${it}.lappsgrid.org:8983/solr".toString() }
        println Serializer.toPrettyJson(servers)
    }
}
