package org.lappsgrid.eager.mining.solr.unused

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
