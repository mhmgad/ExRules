This is the implementation of the paper _"Exception-Enriched Rule Learning from Knowledge Graphs_" [Link](https://link.springer.com/chapter/10.1007/978-3-319-46523-4_15).

**Requirements:**
=================

1. maven 2
2. Java 8
3. For development, IntelliJ (recommended)


**Prepare Project:**
====================

1. Run scripts:

 `sh scripts/local_libs.sh`

 `sh scripts/download_large_data.sh`
 

2. Import the project as maven project to intellij if needed



**Installation:**
=================

To install the project run scripts

`mvn compile`

`mvn package -Dmaven.test.skip=true`

`mvn install -Dmaven.test.skip=true`


**Running**
===========

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
 -exRank,--exception_ranking <order>     Exception ranking method(LIFT|PNCONF|SUPP|CONF|PNCONV|PNJACC)
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
 -pm,--materialization                   Use partial materialization
 -PMo,--materialization_order            Materialize with order. Only useful with Materialization
 -s,--sorting <order>                    Output sorting(CONF|HEAD|BODY|LIFT|HEAD_CONF|HEAD_LIFT|NEW_LIFT|CONV)
 -stats,--export_statistics              Export statistics to file
 -w,--weighted_transactions              Count transactions with weights. Only useful with Materialization
 ```

****

**Output sorting Methods**

_HEAD_: Sort according to the head predicates (useful for grouping)

_BODY_: According to the rules body

_CONF_: Association Rules [Confidence](https://en.wikipedia.org/wiki/Association_rule_learning#Confidence) (Original horn rule confidence is used)

_LIFT_: Association Rules [Lift measurement](https://en.wikipedia.org/wiki/Association_rule_learning#Lift) (Original horn rule confidence is used)

_HEAD_CONF_: Sort according to head then confidence.

_HEAD_LIFT_: Sort according to head then lift.

_NEW_LIFT_: Revised Rules Lift

_CONV_: [Conviction measurement] (www3.di.uminho.pt/~pja/ps/conviction.pdf).

**Exception ranking Methods**

_SUPP_: Used in the naive approach. Only consider increase in support.
_LIFT_: Only consider increase in lift 
_CONF_: Only consider increase in confidence 

_PNCONF_: Used in partial materialization. Considers the increase of average confidence of positive and negative predictions. 

_PNCONV_: Used in partial materialization. Considers the increase of average conviction of positive and negative predictions. 

_PNJACC_: Used in partial materialization. Considers the increase of average Jaccard Coefficient of positive and negative predictions. 



**Running Experiments:**
==========================

To Run YAGO experiments

`sh run_experiment.sh <sorting_Type[CONF|HEAD|BODY|LIFT|HEAD_CONF|HEAD_LIFT|NEW_LIFT|CONV]> <RM[LIFT|SUPP|CONF|CONV|JACC]>` 

to Run IMDB experiments 

`sh run_IMDB_experiment.sh <sorting_Type[CONF|HEAD|BODY|LIFT|HEAD_CONF|HEAD_LIFT|NEW_LIFT|CONV]> <RM[LIFT|SUPP|CONF|CONV|JACC]>` 

Note: fix the directories inside the scripts to point to facts_to_mine.tsv file



**Other Important Scripts:**
=======================

To convert the KB from RDF to different formats

`rdf2int.sh <required conversion [SPMF|DLV_SAFE|PrASP]> <input file path> <output prefix>`

Ex: `sh assemble/bin/rdf2int.sh DLV_SAFE /GW/D5data-5/gadelrab/imdb/facts_to_mine_imdb.tsv /GW/D5data-5/gadelrab/imdb/in/facts_to_mine_imdb`

SPMF : outputs transactional KB in numbers 1,2,3 for projected predicates
DLV_SAFE : outputs unary Encoding in format p1234t(s1234o) for projected predicates.
PrASP : outputs in PrASP format without encoding for example isMarriedToScientist(X). (Good fro viewing but causes problems with PrASP)

A mapping will be generated in case of encoding

_P.S: Other scripts to be added_

## References

This is an implementation of the paper:


*Exception-enriched Rule Learning from Knowledge Graphs* 
Mohamed Gad-Elrab, Daria Stepanova, Jacopo Urbani, Gerhard Weikum
In 15th International Semantic Web Conference (ISWC 2016),234-251, Springer 2016.


