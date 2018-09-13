# Eager
Biomedical text mining and question answering.

## Modules

- **api**<br/>Interface definitions.
- **core**<br/>Common classes for interacting with Solr.
- **indexing**<br/>Standalone program for creating the Solr index of PubMed and PubMed Central.
- **preprocessing** *Coming soon.*<br/>Process PubMed documents to write each document to its own file.
- **query**<br/>Query processors.  Accepts natural language from the user and converts it into a search engine query.
- **ranking** *Coming soon.*<br/>Document ranking algorithms.
- **rabbitmq**<br/>RabbitMQ messaging services.
- **web**<br/>Spring Boot application that provides a web user interface and REST API.

## Building

Running `mvn install` in the top level project directory will build all of the Java/Groovy modules, but not all modules are Maven projects.

## Running

- *Preprocessing*
- [Indexing](indexing/README.md) 
- [Web / REST](web/README.html)
