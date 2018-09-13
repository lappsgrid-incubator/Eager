package org.lappsgrid.eager.mining.solr

/**
 *
 */
class Values {
    String pmid
    String pmc
    String doi
    String path

    String toString() {
        "$pmid,$pmc,$doi,$path"
    }
}
