package org.lappsgrid.eager.mining.scoring

import org.lappsgrid.eager.mining.api.Metric
import org.lappsgrid.eager.mining.core.Document

/**
 *
 */
interface Algorithm {
    Metric score(Document document)
}