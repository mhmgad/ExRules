**Development:**

**Requirements:**

1. maven
2. Java 8
3. recommended IntelliJ (not a must)


**Prepare Project:**

1. Run scripts:

 `sh scripts/local_libs.sh`

 `sh scripts/download_large_data.sh`

2. Import the project as maven project



**Installation:**

To install the project run scripts

`mvn compile`

`mvn package`

`mvn install`


**Running Experiments:**

`sh run_experiment.sh` for yago experiments

`sh run_IMDB_experiment.sh` fo IMDB experiments

Note: fix the directories inside the scripst to point to facts_to_mine.tsv file



**Important Scripts:**

To convert the KB from RDF to different formats
`rdf2int.sh <required conversion [SPMF|DLV_SAFE|PrASP]> <input file path> <output prefix>`

Ex: `sh assemble/bin/rdf2int.sh DLV_SAFE /GW/D5data-5/gadelrab/imdb/facts_to_mine_imdb.tsv /GW/D5data-5/gadelrab/imdb/in/facts_to_mine_imdb`

SPMF : outputs transactional KB in numbers 1,2,3 for projected predicates
DLV_SAFE : outputs unary Encoding in format p1234t(s1234o) for projected predicates.
PrASP : outputs in PrASP format without encoding for example isMarriedToScientist(X). (Good fro viewing but causes problems with PrASP)

A mapping will be generated in case of encoding

<Other scripts to come>

for running examples
 running_scripts_sample.txt for examples running the main rule mining script mine_rules.sh


