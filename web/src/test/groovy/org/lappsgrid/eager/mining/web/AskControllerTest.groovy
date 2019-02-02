package org.lappsgrid.eager.mining.web

import org.junit.Ignore
import org.junit.Test
import org.lappsgrid.eager.mining.core.json.Serializer
import org.lappsgrid.eager.mining.web.controllers.AskController
import org.lappsgrid.eager.mining.web.util.Utils
import org.springframework.beans.factory.annotation.Autowired

import static org.junit.Assert.*

/**
 *
 */
//@RunWith(SpringRunner.class)
//@SpringBootTest
@Ignore
class AskControllerTest {

    @Autowired
    AskController ask

    @Ignore
    void question1() {
        String question = "What kinases phosphorylate AKT1 on threonine 308"
        String json = ask.post(question)
        println json
    }

    @Test
    void galaxyTest() {
        String email = 'suderman@cs.vassar.edu'
        ConfigObject config = Utils.loadConfiguration()
        String url = config.galaxy.host + '/api/users?key=' + config.galaxy.key + '&f_email=' + email
//        String json = new URL(url).text
//        println url
        groovy.json.JsonSlurper parser = new groovy.json.JsonSlurper()
        try {
            def users = parser.parse(new URL(url))
            println Serializer.toPrettyJson(users)
            assert 1 == users.size()
            assert users[0].active
            println Serializer.toPrettyJson(users[0])
        }
        catch (Exception e) {
            fail e.message
        }
//        Map user = users.find { it.email == 'suderman@cs.vassar.edu'}
//        if (user) {
//            println Serializer.toPrettyJson(user)
//        }
    }
}
