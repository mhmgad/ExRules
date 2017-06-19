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

`mvn package -Dmaven.test.skip=true`

`mvn install -Dmaven.test.skip=true`


**Running**

To mine rules with or without exceptions use `mine_riles.sh` use the following options

```
usage: mine_rules.sh
 -cPM,--cautious-materialization <arg>    Use partial materialization cautiously, here the minimum support for exceptions
 -de                                     Decode the output
 -dPM,--Debug_materialization <file>     debug Materialization file
 -en                                     Encode the input
 -ex                                     Mine the output
 -exMinSup <EXCEPTION_MIN_SUPP_RATIO>    Exception Minimum support for the rule
 -expOnly                                Output rules with exceptions only
 -exRank,--exception_ranking <order>     Exception ranking
                                         method(LIFT|PNCONF|SUPP|CONF|PNCONV|PNJACC)
 -f1,--first_filter                      first filter based on size (4 body atoms at most, 1 head and Max conf)
 -f2,--second_filter                     Second filter based on type hierarchy
 -i,--input <file>                       Input file inform of RDF or Integer transactions
 -m,--mapping_file <file>                Mapping RDF to Integer
 -maxConf <MAX_CONF_RATIO>               Maximum Confidence for the rule (default=1.0)
 -minConf <MIN_CONF_RATIO>               Minimum Confidence for the rule (default=0.001)
 -minS <MIN_SUPP_RATIO>                  Minimum support for the rule(default=0.0001)
 -o,--output <file>                      Input file inform of RDF or Integer transactions
 -oDLV,--output_DLV                      Export rules as PrASP
 -oDLV_CONFLICT,--export_DLVConflict     Export rules to count conflict to file
 -oPrASP,--output_PrASP                  Export rules as PrASP
 -pm,--materialization                    Use partial materialization
 -PMo,--materialization_order            Materialize with order. Only useful with Materialization
 -s,--sorting <order>                    Output sorting(CONF|HEAD|BODY|LIFT|HEAD_CONF|HEAD_LIFT|NEW_LIFT|CONV)
 -stats,--export_statistics              Export statistics to file
 -w,--weighted_transactions              Count transactions with weights. Only useful with Materialization
 ```



**Running Experiments:**

To Run YAGO experiments

`sh run_experiment.sh <sorting_Type[LIFT|CONF|]> <RM[LIFT|PNCONF|SUPP|CONF|PNCONV|PNJACC]>` 

to Run IMDB experiments 

`sh run_IMDB_experiment.sh <sorting_Type[LIFT|CONF|]> <RM[LIFT|PNCONF|SUPP|CONF|PNCONV|PNJACC]>` 

Note: fix the directories inside the scripts to point to facts_to_mine.tsv file



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


