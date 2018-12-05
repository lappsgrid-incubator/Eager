# Design Notes

**NOTE** This is a work in progress and is likely already out of date...


# Workflow

1. Query generation. Take the user input and generate a Solr query.
1. Execute the Solr query. Solr returns up to 10K documents.
1. For each document
  - Apply algorithm to each section (title, abstract, introduction, etc)
  - Weight each section/algorithm
  - Normalize scores
1. Return sorted list of documents (document IDs). Limited to top 100.

# Web Application

The `web` modules contains a Spring Boot web application that provides a Web front and a REST API.  The web application is written in Groovy and also uses Groovy templates to generate the HTML UI.  The HTML UI uses [Less](http://lesscss.org) for CSS styling with some [JQuery](https://jquery.com) and JavaScript.

# Query Processing

The query processing module takes a single sentence (the question) and transforms it into a query for the search engine being used.  Currently only Solr is supported.

 