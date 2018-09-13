package org.lappsgrid.eager.web

import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

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
