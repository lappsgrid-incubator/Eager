#! /usr/bin/env python3
#! /usr/bin/env py3
#! /usr/bin/env python

import sys
import os
import re

def csplit_chunk(in_filename, output_dir):
    if not os.path.exists(output_dir):
        os.makedirs(output_dir)
    if in_filename == "-":
        in_file = sys.stdin
    else:
        in_file = open(in_filename)
    residue = open("{}/residue".format(output_dir), 'w')
    line_count = 0
    article_count = 0
    in_set = False
    in_article = False
    cur_article_file = None
    for line in in_file:
        # skip xml declaration and the dtd reference
        if line_count < 2:
            pass
        else:
            if line.strip().startswith('</PubmedArticleSet'):
                in_set = False
            elif line.strip().startswith('<PubmedArticleSet'):
                in_set = True
            elif line.strip().startswith('</PubmedArticle'):
                if cur_article_file is not None:
                    cur_article_file.write(line)
                    cur_article_file.close()
                in_article = False
                pmid = get_pmid(cur_article_file.name)
                final_name = get_output_path(output_dir, pmid)

                if os.path.exists(final_name):
                    print("Found duplicate PMID, terminating.")
                    print(in_filename, line_count, article_count, pmid)
                    exit(1)
                os.rename(cur_article_file.name, final_name)
            elif line.strip().startswith('<PubmedArticle'):
                in_article = True
                article_count += 1
                cur_article_filename = "{}/{:06}.xml".format(output_dir, article_count)
                cur_article_file = open(cur_article_filename, 'w')
                cur_article_file.write(line)
            elif in_article:
                cur_article_file.write(line)
            else: 
                residue.write(line)
        line_count += 1

def get_output_path(output_dir, pmid):
    subdirs = os.path.join(output_dir, pmid.split('-')[0], pmid.split('-')[1][:4])
    if not os.path.exists(subdirs):
        os.makedirs(subdirs)
    return os.path.join(subdirs, pmid + '.xml')

def get_pmid(article_filename):
    pmid = 'UNK'
    article_file = open(article_filename)
    for line in article_file:
        match = re.search(r'<PMID [vV]ersion="(\d)">(\d+)</PMID>', line)
        if match is not None:
            pmid = "{}-{:08}".format(match.group(1), int(match.group(2)))
            break
    article_file.close()
    #  print(pmid)
    return pmid

if __name__ == "__main__":
    if len(sys.argv) == 3:
        csplit_chunk(sys.argv[1], sys.argv[2])
    else:
        print("Usage: {} input_file output_dir".format(sys.argv[0]))
        print("    Use \"-\" specifically to use stdin as the input file. ")
        print("    Documents in the input_file are splitted into each file. ")
        print("    Split files are named after the document's PMID, then are")
        print("    placed in subdirectories under the output_dir named after")
        print("    the PMID version and first 4 digits of PMID.")
        print("    e.g. <PMID version=\"1\">00100143</PMID> ")
        print("         --> /output_dir/1/0010/1-00100143.xml")
        exit(1)
