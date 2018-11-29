package org.lappsgrid.eager.mining.preprocess.jmx

import org.lappsgrid.eager.mining.preprocess.pmc.ExtractText

/**
 *
 */
class Manager implements ManagerMBean {
    ExtractText app

    Manager(ExtractText app) {
        this.app = app
    }

    void shutdown() {
        app.terminate()
    }
}
