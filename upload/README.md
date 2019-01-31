# Galaxy Upload Service

The Galaxy upload service waits for data (zip files) to arrive from the RabbitMQ server and unzips them into Galaxy's FTP directory.

## Starting The Service

Since the upload service exposes management and metrix via JMX it must be started with the appropriate Java properties defined on the command line.  See the [start.sh](./start.sh) script for details.