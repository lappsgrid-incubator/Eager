package org.lappsgrid.eager.mining.ranking

/**
 *
 */
class RankingException extends Exception {
    RankingException() {
    }

    RankingException(String message) {
        super(message)
    }

    RankingException(String message, Throwable reason) {
        super(message, reason)
    }

    RankingException(Throwable var1) {
        super(var1)
    }

    RankingException(String message, Throwable reason, boolean enableSuppression, boolean writableStackTrace) {
        super(message, reason, enableSuppression, writableStackTrace)
    }
}
