#!/usr/bin/env bash

OUT_DIRECTORY=/GW/D5data-5/gadelrab/yago3/spmf/out_LIFT_NO_ORDER

mkdir -p $OUT_DIRECTORY




SORTING_TYPE=LIFT


CPM_MINSUPP=0.0
MIN_EXCEPT_SUPP=0.05


#normal horn rules

sh assemble/bin/mine_rules.sh -i /GW/D5data-5/gadelrab/yago3/spmf/in/facts_to_mine.transactions -o $OUT_DIRECTORY/rules_spmf_supp0001_conf25_100_f1_f2_$SORTING_TYPE.tsv -minConf 0.25    -de -m /GW/D5data-5/gadelrab/yago3/spmf/in/facts_to_mine.mapping_predicates -s $SORTING_TYPE -f1 -f2  -oPrASP -oDLV -stats -oDLV_CONFLICT

#no materialization (support only)
sh assemble/bin/mine_rules.sh -i /GW/D5data-5/gadelrab/yago3/spmf/in/facts_to_mine.transactions -o $OUT_DIRECTORY/rules_spmf_supp0001_conf25_100_excep02_PNCONF_f1_f2_$SORTING_TYPE.tsv -minConf 0.25    -de -m /GW/D5data-5/gadelrab/yago3/spmf/in/facts_to_mine.mapping_predicates -ex -exMinSup $MIN_EXCEPT_SUPP -exRank PNCONF -s $SORTING_TYPE -f1 -f2  -oPrASP -oDLV -stats -oDLV_CONFLICT


#no weighted count and order
sh assemble/bin/mine_rules.sh -i /GW/D5data-5/gadelrab/yago3/spmf/in/facts_to_mine.transactions -o $OUT_DIRECTORY/rules_spmf_supp0001_conf25_100_excep02_PNCONF_cPM05_noWeight_order_f1_f2_$SORTING_TYPE.tsv -minConf 0.25    -de -m /GW/D5data-5/gadelrab/yago3/spmf/in/facts_to_mine.mapping_predicates -ex -exMinSup $MIN_EXCEPT_SUPP -exRank PNCONF -s $SORTING_TYPE -f1 -f2 -pm -cPM $CPM_MINSUPP -oPrASP -oDLV  -stats -oDLV_CONFLICT -PMo


#weights order
sh assemble/bin/mine_rules.sh -i /GW/D5data-5/gadelrab/yago3/spmf/in/facts_to_mine.transactions -o $OUT_DIRECTORY/rules_spmf_supp0001_conf25_100_excep02_PNCONF_cPM05_weight_order_f1_f2_$SORTING_TYPE.tsv -minConf 0.25    -de -m /GW/D5data-5/gadelrab/yago3/spmf/in/facts_to_mine.mapping_predicates -ex -exMinSup $MIN_EXCEPT_SUPP -exRank PNCONF -s $SORTING_TYPE  -w -f1 -f2 -pm -cPM $CPM_MINSUPP -oPrASP -oDLV -stats -oDLV_CONFLICT -PMo


#no weighted and no order
sh assemble/bin/mine_rules.sh -i /GW/D5data-5/gadelrab/yago3/spmf/in/facts_to_mine.transactions -o $OUT_DIRECTORY/rules_spmf_supp0001_conf25_100_excep02_PNCONF_cPM05_noWeight_noOrder_f1_f2_$SORTING_TYPE.tsv -minConf 0.25    -de -m /GW/D5data-5/gadelrab/yago3/spmf/in/facts_to_mine.mapping_predicates -ex -exMinSup $MIN_EXCEPT_SUPP -exRank PNCONF -s $SORTING_TYPE -f1 -f2 -pm -cPM $CPM_MINSUPP -oPrASP -oDLV  -stats -oDLV_CONFLICT


#weights and no order
sh assemble/bin/mine_rules.sh -i /GW/D5data-5/gadelrab/yago3/spmf/in/facts_to_mine.transactions -o $OUT_DIRECTORY/rules_spmf_supp0001_conf25_100_excep02_PNCONF_cPM05_weight_noOrder_f1_f2_$SORTING_TYPE.tsv -minConf 0.25    -de -m /GW/D5data-5/gadelrab/yago3/spmf/in/facts_to_mine.mapping_predicates -ex -exMinSup $MIN_EXCEPT_SUPP -exRank PNCONF -s $SORTING_TYPE  -w -f1 -f2 -pm -cPM $CPM_MINSUPP -oPrASP -oDLV -stats -oDLV_CONFLICT


