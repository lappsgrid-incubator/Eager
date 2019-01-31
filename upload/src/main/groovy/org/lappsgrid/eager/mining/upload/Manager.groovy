package org.lappsgrid.eager.mining.upload

/**
 *
 */
class Manager implements ManagerMBean {
    App app

    Manager(App app) {
        this.app = app
    }

    void stop() {
        app.stop()
    }
}
