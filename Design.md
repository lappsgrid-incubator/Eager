# Design Notes

## RabbitMQ

Most of the backend services use RabbitMQ to coordinate processing.

### Publishers

All backend services should create a `Subscriber` that listens to the `eager.broadcast` queue. System wide messages (e.g. *shutdown*) will be broadcast to the `eager.broadcast` queue.

### PostOffice

Service messages are routed through the `eager.postoffice` exchange.  The following `MailBox`es have been defined:

1. **load**<br/>the service responsible for loading documents from *redis*.  If the document is not available in *redis* it will be loaded from disk, sent to the **nlp.stanford** pipeline.
1. **nlp.stanford**<br/>performs tokenization, sentence splitting, pos tagging, and name entity recognition.
1. **save**<br/>saves the input document to the *redis* store.
1. **error**<br/>any service that encounters an unrecoverable error should send an appropriate message to the **error** mail box so the condition can be logged in a central location.
