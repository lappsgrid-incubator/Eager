package org.lappsgrid.eager.mining.web.db

import javax.persistence.Entity
import javax.persistence.Id

/**
 *
 */
@Entity
class Question {
    @Id
    String uuid

    String text

    protected Question() { }

    public Question(String uuid, String text) {
        this.uuid = uuid
        this.text = text
    }

    String toString() {
        return uuid + '\t' + text
    }
}
