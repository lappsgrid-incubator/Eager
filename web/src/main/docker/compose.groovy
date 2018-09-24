/*
 * Generates the docker-compose.yaml file used to deploy the Eager
 * web application to our Docker swarm on Jetstream.
 */
version 3
services {
    eager {
        image 'docker.lappsgrid.org/lappsgrid/eager'
        deploy {
            replicas 1
            resources {
                limits {
                    cpus 0.5
                    memory '3G'
                }
            }
            restart_policy {
                condition 'on-failure'
            }
        }
        ports '8081:8080'
    }

}