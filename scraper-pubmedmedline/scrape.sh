#! /bin/bash 

# assuming medline files uploaded to the nih ftp are numbered with 4 digits, 
# and 928 baseline files from 2018 release (at Dec 2017)

BASE_FTP_URL="ftp://ftp.ncbi.nlm.nih.gov/pubmed/baseline/"
BASE_FILE_PREFIX="pubmed18n"
BASE_FILE_SUFFIX=".xml.gz"
SPLITTER_PYTHON_CODE="./split.py"

# by default, this script will download all files
START=1
END=928


usage() {
    echo "Usage: $0 output_dir [start_idx [end_dix]]"
    echo "    output_dir: directory name to store pubmed-medline documents "
    echo "                see the python script for information on subdir structure."
    echo "    start_idx: (optional, default:1) starting point to download baseline files"
    echo "    sendidx:   (optional, default:928) ending point to download baseline files"
    echo "               NOTE that 2018 baseline set starts at 1 and ends at 928"
    echo "               and 1 - 927 files contains ~ 30,000 documents,"
    echo "               while 928 has ~ 27,000 documents."
    exit 1
}

# parse arguments
if [ -z $1 ] ; then 
    usage
else
    OUTPUT=$1
    shift
fi


if ! [ -z $1 ] ; then 
    START=$1
    shift
fi

if ! [ -z $1 ] ; then 
    END=$1
    shift
fi

for idx in `seq -f "%04g" $START $END`; do 
    wget -O - ftp://ftp.ncbi.nlm.nih.gov/pubmed/baseline/pubmed18n${idx}.xml.gz | gunzip -c | python $SPLITTER_PYTHON_CODE - $OUTPUT
done

