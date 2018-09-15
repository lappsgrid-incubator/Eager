## Requirement

* python3
* wget
* gunzip

## Usage
Use `scrape.sh` to download any range of baseline files and split them into individual documents. 
Help messages of scripts: 

```
Usage: ./scrape.sh output_dir [start_idx [end_dix]]
    output_dir: directory name to store pubmed-medline documents
                see the python script for information on subdir structure.
    start_idx: (optional, default:1) starting point to download baseline files
    sendidx:   (optional, default:928) ending point to download baseline files
               NOTE that 2018 baseline set starts at 1 and ends at 928
               and 1 - 927 files contains ~ 30,000 documents,
               while 928 has ~ 27,000 documents.
```

``` 
Usage: ./split.py input_file output_dir
    Use "-" specifically to use stdin as the input file.
    Documents in the input_file are splitted into each file.
    Split files are named after the document's PMID, then are
    placed in subdirectories under the output_dir named after
    the PMID version and first 4 digits of PMID.
    e.g. <PMID version="1">00100143</PMID>
         --> /output_dir/1/0010/1-00100143.xml
```
