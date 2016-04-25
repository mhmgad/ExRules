#!/usr/bin/env bash

OUT_DIRECTORY=/GW/D5data-5/gadelrab/yago3/spmf/out_LIFT2

mkdir -p $OUT_DIRECTORY


SORTING_TYPE=LIFT

#normal horn rules

sh assemble/bin/mine_rules.sh -i /GW/D5data-5/gadelrab/yago3/spmf/in/facts_to_mine.transactions -o $OUT_DIRECTORY/rules_spmf_supp0001_conf25_100_f1_f2_$SORTING_TYPE.tsv -minConf 0.25    -de -m /GW/D5data-5/gadelrab/yago3/spmf/in/facts_to_mine.mapping_predicates -s $SORTING_TYPE -f1 -f2  -oPrASP -oDLV -stats -oDLV_CONFLICT

#no materialization (support only)
sh assemble/bin/mine_rules.sh -i /GW/D5data-5/gadelrab/yago3/spmf/in/facts_to_mine.transactions -o $OUT_DIRECTORY/rules_spmf_supp0001_conf25_100_excep02_PNCONF_f1_f2_$SORTING_TYPE.tsv -minConf 0.25    -de -m /GW/D5data-5/gadelrab/yago3/spmf/in/facts_to_mine.mapping_predicates -ex -exMinSup 0.02 -exRank PNCONF -s $SORTING_TYPE -f1 -f2  -oPrASP -oDLV -stats -oDLV_CONFLICT


#no weighted count
sh assemble/bin/mine_rules.sh -i /GW/D5data-5/gadelrab/yago3/spmf/in/facts_to_mine.transactions -o $OUT_DIRECTORY/rules_spmf_supp0001_conf25_100_excep02_PNCONF_cPM05_noWeight_f1_f2_$SORTING_TYPE.tsv -minConf 0.25    -de -m /GW/D5data-5/gadelrab/yago3/spmf/in/facts_to_mine.mapping_predicates -ex -exMinSup 0.02 -exRank PNCONF -s $SORTING_TYPE -f1 -f2 -pm -cPM 0.05 -oPrASP -oDLV  -stats -oDLV_CONFLICT


#weights
sh assemble/bin/mine_rules.sh -i /GW/D5data-5/gadelrab/yago3/spmf/in/facts_to_mine.transactions -o $OUT_DIRECTORY/rules_spmf_supp0001_conf25_100_excep02_PNCONF_cPM05_weight_f1_f2_$SORTING_TYPE.tsv -minConf 0.25    -de -m /GW/D5data-5/gadelrab/yago3/spmf/in/facts_to_mine.mapping_predicates -ex -exMinSup 0.02 -exRank PNCONF -s $SORTING_TYPE  -w -f1 -f2 -pm -cPM 0.05 -oPrASP -oDLV -stats -oDLV_CONFLICT




