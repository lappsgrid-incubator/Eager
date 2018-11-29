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


 