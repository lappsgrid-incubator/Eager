package org.lappsgrid.eager.mining.web.dto

/**
 *
 */
class Algorithm {
    int id
    String name
    float weight
    boolean checked

    protected Algorithm() { }

    Algorithm(int id, String name, float  weight = 1.0, boolean checked = false) {
        this.id = id
        this.name = name
        this.weight = weight
        this.checked = checked
    }
}
