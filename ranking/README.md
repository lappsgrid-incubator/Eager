# Document Ranking

## Scoring Algorithms

A *scoring algorithm* accepts a passage of text and computes a numeric score for that passage.  For example, a simple scoring algorithm might count the number of times the query terms appear in the passage. In general the scoring algorithms do not know or care about the length of the passage, the exception being the sentence based evaluators which only make sense on passages that contain more than one sentence.

## Weighted Scoring Algorithms

A *weighted scoring algorithm* is simply a scoring algorithm plus a floating point weight. Users are able to adjust the weights assigned to the various scoring algorithms to change how documents are ranked.

## Ranking Engine

A *ranking engine* manages a collection of scoring algorithms and ranks a collection of input documents based on the scores calculated by the scoring algorithms.  There will be one ranking engine for each section type (title, abstract, etc) to be evaluated.

## Composite Ranking Engine

A *composite ranking engine* manages a collection of ranking engines for each section type (title, abstract etc) to be evaluated.  Each ranking engine can be assigned a floating point weight to change how documents are ranked.

