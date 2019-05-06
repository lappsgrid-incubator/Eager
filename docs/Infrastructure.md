# Infrastructure

Currently all nodes are running on Jetstream at TACC (Texas Advanced Computing Center). Additional Solr nodes and at least one Zookeeper node will be added to run on the Indiana University Jetstream in the future.  Additional compute nodes will be added to the Docker swarm, but those will remain at TACC to reduce latency and improve performance.

The current configuration consists of:

- a Solr Cloud with two nodes (20 CPU).
- a Docker swarm with four nodes (40 CPU).
- a single Zookeeper node (2 CPU).

## Zookeeper

[Apache Zookeeper](https://zookeeper.apache.org) is a service for configuring and managing clusters.  In our case Zookeeper stores Solr configuration json and the only configuration a Sorl node needs is the address of the Zookeeper service.

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

Uploading a new configuration to Zookeeper is an execeeding rare occurrence.

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

To add additional nodes to the Solr Cloud launch a new server and install Solr using the [Solr service installation script](https://lucene.apache.org/solr/guide/7_5/taking-solr-to-production.html#taking-solr-to-production).  Use the default installation location (*/opt/solr-7.5.0*)

#### Increase Open Files Limit

Set the open file limit to 65000 to prevent problems running Solr.

Add the following lines to the end of `/etc/security/limits.conf`

```
solr    soft    nofile  65000
solr    hard    nofile  65000
```

Add the following to `/etc/pam.d/common-session` and `/etc/pam.d/common-session-noninteractive`

``` 
session required    pam_limits.so
```

Reboot the server so the changes take effect. Check if the limit has been changed for the *solr* user:

```bash
su solr --shell /bin/bash --command "ulimit -n"
```

#### Solr Configuration

Since the Solr cloud is managed by a Zookeeper instance all we need to do is tell Solr the IP address of Zookeeper.

1. Edit the file `/etc/default/solr.in.sh`
1. Set `ZK_HOST="129.114.17.81:2181"`
1. It is highly recommended to set the Java heap size to at least 20GB<br/>`SOLR_JAVA_MEM="-Xmx20G -Xmx20G"`
1. Set `SOLR_HOST=<user>:<password>@<host>` where &lt;user> and &lt;password> are the admin username and password you set in the Solr admin panel.  You **did** change the admin username and password *didn't you*?

When `ZK_HOST` has been set Solr will automatically start up in Cloud mode.

#### Create a collection

Zookeeper will create the collection(s) for the Eager application.  But there are times one may want to create a separate collection.

Log in to one of the Solr nodes (does not matter which one)

```
$> sudo -u solr bin/solr create -c <collection name> -n <config name> -shards <n> 
```

#### Delete a collection

``` 
$> sudo -u solr bin/solr delete -c <collection name>
```

## RabbitMQ

[RabbitMQ](https://www.rabbitmq.com) is an open source message broker
Most of the backend services exchange messages via RabbitMQ to coordinate processing.  

### PostOffice

Service messages are routed through the `eager.postoffice` exchange.  The following `MailBox`es have been defined:

1. **load**<br/>loads a PubMed or PubMedCentral document.  Currently the service loads documents from the file system. Future versions will load documents from *redis*.
1. **nlp.stanford**<br/>performs tokenization, sentence splitting, and part of speech tagging.
1. **save**<br/>saves the input document to the *redis* store. *(not yet available)*
1. **error**<br/>an error logging service. Any service that encounters an unrecoverable error should send an appropriate message to the **error** mail box so the condition can be logged in a central location.

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
| swarm-1 | 129.114.17.83, 10.1.1.11  | 10  | 30 GB  | 60 GB |
| swarm-2 | 10.1.1.3       | 10  | 30 GB  | 60 GB |
| swarm-3 | 10.1.1.17      | 10  | 30 GB  | 60 GB |
| swarm-4 | 10.1.1.12      | 10  | 30 GB  | 60 GB |

