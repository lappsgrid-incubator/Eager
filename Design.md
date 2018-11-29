# Design Notes

**NOTE** This is a work in progress and is likely already out of date...


# Workflow

## RabbitMQ

Most of the backend services exchange messages via RabbitMQ to coordinate processing.  

### PostOffice

Service messages are routed through the `eager.postoffice` exchange.  The following `MailBox`es have been defined:

1. **load**<br/>loads a PubMed or PubMedCentral document.  Currently the service loads documents from the file system. Future versions will load documents from *redis*.
1. **nlp.stanford**<br/>performs tokenization, sentence splitting, and part of speech tagging.
1. **save**<br/>saves the input document to the *redis* store. *(not yet available)*
1. **error**<br/>an error logging service. Any service that encounters an unrecoverable error should send an appropriate message to the **error** mail box so the condition can be logged in a central location.
