# Infrastructure

Currently all nodes are running on Jetstream at TACC (Texas Advanced Computing Center). Additional Solr nodes and at least one Zookeeper node will be added to run on the Indiana University Jetstream in the future.  Additional compute nodes will be added to the Docker swarm, but those will remain at TACC to reduce latency and improve performance.

The current configuration consists of:

- a Solr Cloud with two nodes (20 CPU).
- a Docker swarm with four nodes (40 CPU).
- a single Zookeeper node (2 CPU).

## Zookeeper

[Apache Zookeeper](https://zookeeper.apache.org) is a service for configuring and managing clusters.  In our case Zookeeper stores Solr configuration data and the only configuration a Sorl node needs is the address of the Zookeeper service.

Configuration for the Solr cloud is stored in a single node zookeeper *ensemble*.

| Name   | IP Address     | CPU | Memory | Disk  |
|--------|----------------|-----|--------|-------|
| zk-1   | 129.114.17.81  | 2   | 4 GB   | 20 GB |


#### Upload a configuration to Zookeeper

It is recommended (required?) to use the `zkcli.sh` script in `/opt/solr/server/scripts/cloud-scripts` on one of the Solr instances rather than using the `zkCli.sh` script on the Zookeeper instance.  You can run the commands from any Solr node in the cluster.

```
$> sudo -u solr ./zkcli.sh -cmd upconfig -n eager -d /home/eager/Eager/solr/lappsgrid -z zk1.lappsgrid.org
```

- **-n** configuration name
- **-d** configuration directory.  This is the directory that contains the *conf* directory with the Solr configuration files (schema.xml, solconfig.xml, etc.)
- **-z** address of the Zookeeper host

#### Remove (clear) a configuration

```
$> sudo -u solr ./zkcli.sh -cmd clear -z zk1.lappsgrid.org /config/<config-to-delete>
```
E.g.
```
$> sudo -u solr ./zkcli.sh -cmd clear -z zk1.lappsgrid.org /config/eager
```

## Solr Cloud

There are currently two nodes in the Solr cloud

| Name   | IP Address     | CPU | Memory | Disk  |
|--------|----------------|-----|--------|-------|
| solr-1 | 129.114.16.34  | 10  | 30 GB  | 60 GB |
| solr-2 | 129.114.16.102 | 10  | 30 GB  | 60 GB |

#### Create a collection

Log in to one of the Solr nodes (does not matter which one)

```
$> sudo -u solr bin/solr create -c <collection name> -n <config name> -shards <n> 
```

#### Delete a collection

``` 
$> sudo -u solr bin/solr delete -c <collection name>
```
## Docker Swarm

Only the swarm manager (swarm-1) has been assigned an external IP address. To connect to one of the other swarm nodes first SSH in to swarm-1 and from there you can ssh to any other worker using its local IP address and the key file ~/.ssh/tacc-shared-key.pem

``` 
$> ssh -i ~/.ssh/tacc-shared-key.pem 10.1.1.17
```

### Portainer

Portainer is a simple management UI for Docker swarms available on [http://129.114.17.83:8999](http://129.114.17.83:8999).  

***Note** Portainer's default port is 9000, however that port has been used by other services deployed to the swarm.*

### Compute Nodes


| Name    | IP Address     | CPU | Memory | Disk  |
|---------|----------------|-----|--------|-------|
| swarm-1 | 129.114.17.83  | 10  | 30 GB  | 60 GB |
| swarm-2 | 10.1.1.3       | 10  | 30 GB  | 60 GB |
| swarm-3 | 10.1.1.17      | 10  | 30 GB  | 60 GB |
| swarm-4 | 10.1.1.12      | 10  | 30 GB  | 60 GB |

