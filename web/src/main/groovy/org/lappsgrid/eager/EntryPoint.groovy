package org.lappsgrid.eager

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 *
 */
@SpringBootApplication
class EntryPoint {
    static final Logger logger = LoggerFactory.getLogger("org.springframework.boot.SpringApplication")
    static void main(String[] args) {
        SpringApplication.run(EntryPoint, args)
    }
}
