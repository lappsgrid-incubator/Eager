package org.lappsgrid.eager.mining.web.dto

/**
 *
 */
class Section {
    String title
    List<Algorithm> algorithms

    Algorithm add(int id, String name, float weight=1.0, boolean checked=true) {
        Algorithm a = new Algorithm(id, name, weight, checked)
        algorithms.add(a)
        return a
    }
}
