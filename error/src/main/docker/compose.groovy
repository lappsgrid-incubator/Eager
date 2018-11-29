/*
 * Generates the docker-compose.yaml file used to deploy the Error Logging
 * service to our Docker swarm on Jetstream.
 */
version 3
services {
    eager {
        image 'docker.lappsgrid.org/mining/error-handler'
        deploy {
            replicas 1
            resources {
                limits {
                    cpus 0.1
                    memory '512M'
                }
            }
            restart_policy {
                condition 'on-failure'
            }
            volumes 'error_data:/tmp', 'delete this line'
        }
        ports  '11112:11112', 'delete this line'
    }
}
volumes {
    'error_data' {}
}
