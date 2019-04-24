# Eager
Biomedical text mining and question answering.

## Modules

- **api**<br/>Interface definitions.<br/>*This package will likely be removed.*
- **core**<br/>Common classes and utilities.
- **docs**<br/>Documentation.
- **elasticsearch** *Not Used*<br/>A placeholder project for eventual indexing and searching with ElasticSearch.
- **error**<br/>Error logging service. Services can send logging message to consolidate error messages in a common location.
- **indexer**<br/>Standalone program for creating the Solr index of PubMed and PubMed Central.
- **nlp**<br/>Standalone service that uses Stanford CoreNLP to perform sentence splitting, tokenization, lemmatization, and part of speech tagging.
- **preprocess**<br/>Process PMC documents to:
  1. Extract just the text content to  a separate file.
  1. Create LIF versions with `sentence`, `token`, `lemma', and `pos` annotations.
  1. Create text versions with stop words, punctuation, numbers and symbols removed ready to be processed with `word2vec` or `doc2vec`
- **query**<br/>Query processors.  Accepts natural language from the user and converts it into a search engine query.
- **rabbitmq**<br/>RabbitMQ messaging services.
- **ranking**<br/>Document ranking algorithms.
- **retreival**<br/>Standalone service for retrieving PubMed or PubMed central documents.
- **scraper-pubmedmedline**<br/>Python script used to download and extract PubMed documents from the NIH FTP server.
- **solr**<br/>Solr configuration files.
- **test** *(To be removed)*<br/>Experimental programming.  This module has nothing to do with actual testing.
- **upload**<br/>Upload service for loading json into Galaxy.
- **web**<br/>Spring Boot application that provides a web user interface and REST API.

## Building

Running `mvn install` in the top level project directory will build all of the Java/Groovy modules, but not all modules are Maven projects.

### Building The Web Application

The *web* project includes a Makefile that can be used to generate the Docker image and push the image to `docker.lappsgrid.org`.

```
$> make clean
$> make 
$> make docker
$> make push
```

Since the web project is a Spring Boot application simply run the jar file:

``` 
$> java -Xmx8G -jar eager.jar
```

***Note** In the (near) future JMX capabilities will be added which means the start up procedure will change considerably.  Check for the presence of a `startup.sh` script in the root directory of the project.*

## Services

See the README.md files in each project for instruction on running that module.  

The following modules are intended to be run as standalone services:

1. [error](error/README.md) Error logging service used to collect error messages in a single location.
1. [nlp](nlp/README.md) Stanford Core NLP processing service.
1. [retrieval](retrieval/README.md) Document retrieval service.
1. [upload](upload/README.md) Galaxy upload service.

All of the above services use RabbitMQ as a message broker.  The *nlp* project has an [example Groovy script](nlp/src/test/scripts/stanford.groovy) for submitting documents to the Stanford NLP service for processing.

## Applications

The following modules contain standalone programs that are intended to be run from the command line.

1. [indexer](indexer/README.md) Creates the Solr index(es).
1. [preprocess](preprocess/README.md

)