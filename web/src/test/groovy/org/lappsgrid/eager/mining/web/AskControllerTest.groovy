package org.lappsgrid.eager.mining.web

import org.junit.Ignore
import org.lappsgrid.eager.mining.web.controllers.AskController
import org.springframework.beans.factory.annotation.Autowired

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
}
