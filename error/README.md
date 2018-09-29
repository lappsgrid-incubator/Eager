# Error Message Logging

The `org.lappsgrid.eager.minning.error.MessageHandler` class provides a service that listens for message to `error` on the `eager.postoffice` exchange and writes them to a log file.

Services that encounter an error should consider sending an error message to the error message logging service so that all errors, from all services can be collected in a single location.  In many cases throwing an exception is not useful as the process that sent the *exceptional* message has no way to catch any exception that is thrown.  Furthermore, any service that is processing a message typically has no way to know where the message originated and therefore what process should be notified of the error.

## Usage

The error message handler uses [RabbitMQ](https://www.rabbitmq.com) to exchange messages.  By default the message handler connects to the RabbitMQ server at *rabbitmq.lappsgrid.org* and listens for messages sent to the **errorr** queue on the **eager.postoffice** message exchange.  See the [rabbitmq module](../rabbitmq/) for information on the RabbitMQ message queues and exchanges used.

``` 
PostOffice po = new PostOffice("eager.postoffice");
po.send("error", "Something really bad happened.");
``` 

## Building

The `MessageHandler` is meant to be deployed as a standalone service inside its own Docker container. On Linux/MacOS system the *Makefile* can be used to build and deploy the container to the docker.lappsgrid.org repository.

### Makefile Goals

- **jar**<br/>Builds the executable jar file.  This is the default goal.
- **clean**<br/>Runs `mvn clean` and deletes the jar file from `src/main/docker`.
- **docker**<br/>Builds and tags the Docker image.
- **push**<br/>Pushes the current image to *docker.lappsgrid.org*. 

If using the Makefile is not an option then the application can be built manually:

```
$> mvn clean
$> rm src/main/docker/*.jar
$> mvn package
$> cp target/*.jar src/main/docker
$> cd src/main/docker
$> docker build -t mining/error .
$> docker tag mining/error docker.lappsgrid.org/mining/error
$> docker push docker.lappsgrid.org/mining/error
```

## Running

The build produces an executable jar file that can also be run from the command line:

```bash
java -jar target/error-handler.jar
```

## Stopping

If you would like the error-handler service to shutdown cleanly as opposed to killing the process, send a shutdown message to the service:

```
PostOffice po = new PostOffice("eager.postoffice");
po.send("error", "shutdown");
```
