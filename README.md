# Eager
Biomedical text mining and question answering.

## Modules

- **api**<br/>Interface definitions.
- **core**<br/>Common classes and utilities.
- **elasticsearch** *Not Used*<br/>A placeholder project for eventual indexing and searching with ElasticSearch.
- **error**<br/>Error logging service. Services can send logging message to consolidate error messages in a common location.
- **indexer**<br/>Standalone program for creating the Solr index of PubMed and PubMed Central.
- **nlp**<br/>Standalone service that does sentence splitting, tokenization, lemmatization, and part of speech tagging.
- **preprocess**<br/>Process PMC documents to:
  1. Extract just the text content to  a separate file.
  1. Create LIF versions with `sentence`, `token`, `lemma', and `pos` annotations.
  1. Create text versions with stop words, punctuation, numbers and symbols removed ready to be processed with `word2vec` or `doc2vec`
- **query**<br/>Query processors.  Accepts natural language from the user and converts it into a search engine query.
- **rabbitmq**<br/>RabbitMQ messaging services.
- **ranking** *Coming soon.*<br/>Document ranking algorithms.
- **retreival**<br/>Standalone service for retrieving PubMed or PubMed central documents.
- **scraper-pubmedmedline**<br/>Python script used to download and extract PubMed documents from the NIH FTP server.
- **solr**br/>Solr configuration files
- **web**<br/>Spring Boot application that provides a web user interface and REST API.

## Building

Running `mvn install` in the top level project directory will build all of the Java/Groovy modules, but not all modules are Maven projects.

## Running

- *Preprocessing*
- [Indexing](indexing/README.md) 
- [Web / REST](web/README.html)
