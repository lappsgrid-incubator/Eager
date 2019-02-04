package org.lappsgrid.eager.mining.web.db

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

/**
 *
 */
@Entity
class Settings {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id

    String question
    String name
    Float weight

    protected Settings() { }
    public Settings(String question, String name, Float weight) {
        this.question = question
        this.name = name
        this.weight = weight
    }

    String toString() {
        return String.format("%s\t%s\t%2.3f", question, name, weight)
    }
}
