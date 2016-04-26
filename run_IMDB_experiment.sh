#!/usr/bin/env bash

OUT_DIRECTORY=/GW/D5data-5/gadelrab/imdb/out_LIFT
#/GW/D5data-5/gadelrab/yago3/spmf/out_LIFT_ALL

mkdir -p $OUT_DIRECTORY

IN_DIR=/GW/D5data-5/gadelrab/imdb/in

mkdir -p $IN_DIR

DATA_FILE=$IN_DIR/facts_to_mine_imdb

./assemble/bin/rdf2int.sh SPMF /GW/D5data-5/gadelrab/imdb/facts_to_mine_imdb.tsv $DATA_FILE



INPUT_TRANSACTIONS_FILE=$DATA_FILE.transactions

MAPPING_FILE=$DATA_FILE.mapping_predicates



SORTING_TYPE=LIFT


CPM_MINSUPP=0.0
MIN_EXCEPT_SUPP=0.05
MIN_EXCEPTION=0.001



#normal horn rules

sh assemble/bin/mine_rules.sh -i $INPUT_TRANSACTIONS_FILE -o $OUT_DIRECTORY/rules_spmf_supp001_conf25_100_f1_f2_$SORTING_TYPE.tsv -minConf 0.25    -de -m $MAPPING_FILE -s $SORTING_TYPE -f1 -f2  -oPrASP -oDLV -stats -oDLV_CONFLICT -minS $MIN_EXCEPTION

#no materialization (support only)
sh assemble/bin/mine_rules.sh -i $INPUT_TRANSACTIONS_FILE -o $OUT_DIRECTORY/rules_spmf_supp001_conf25_100_excep02_PNCONF_f1_f2_$SORTING_TYPE.tsv -minConf 0.25    -de -m $MAPPING_FILE -ex -exMinSup $MIN_EXCEPT_SUPP -exRank PNCONF -s $SORTING_TYPE -f1 -f2  -oPrASP -oDLV -stats -oDLV_CONFLICT -minS $MIN_EXCEPTION


#no weighted count and order
sh assemble/bin/mine_rules.sh -i $INPUT_TRANSACTIONS_FILE -o $OUT_DIRECTORY/rules_spmf_supp001_conf25_100_excep02_PNCONF_cPM05_noWeight_order_f1_f2_$SORTING_TYPE.tsv -minConf 0.25    -de -m $MAPPING_FILE -ex -exMinSup $MIN_EXCEPT_SUPP -exRank PNCONF -s $SORTING_TYPE -f1 -f2 -pm -cPM $CPM_MINSUPP -oPrASP -oDLV  -stats -oDLV_CONFLICT -PMo -minS $MIN_EXCEPTION


#weights order
sh assemble/bin/mine_rules.sh -i $INPUT_TRANSACTIONS_FILE -o $OUT_DIRECTORY/rules_spmf_supp001_conf25_100_excep02_PNCONF_cPM05_weight_order_f1_f2_$SORTING_TYPE.tsv -minConf 0.25    -de -m $MAPPING_FILE -ex -exMinSup $MIN_EXCEPT_SUPP -exRank PNCONF -s $SORTING_TYPE  -w -f1 -f2 -pm -cPM $CPM_MINSUPP -oPrASP -oDLV -stats -oDLV_CONFLICT -PMo -minS $MIN_EXCEPTION


#no weighted and no order
sh assemble/bin/mine_rules.sh -i $INPUT_TRANSACTIONS_FILE -o $OUT_DIRECTORY/rules_spmf_supp001_conf25_100_excep02_PNCONF_cPM05_noWeight_noOrder_f1_f2_$SORTING_TYPE.tsv -minConf 0.25    -de -m $MAPPING_FILE -ex -exMinSup $MIN_EXCEPT_SUPP -exRank PNCONF -s $SORTING_TYPE -f1 -f2 -pm -cPM $CPM_MINSUPP -oPrASP -oDLV  -stats -oDLV_CONFLICT -minS $MIN_EXCEPTION


#weights and no order
sh assemble/bin/mine_rules.sh -i $INPUT_TRANSACTIONS_FILE -o $OUT_DIRECTORY/rules_spmf_supp001_conf25_100_excep02_PNCONF_cPM05_weight_noOrder_f1_f2_$SORTING_TYPE.tsv -minConf 0.25    -de -m $MAPPING_FILE -ex -exMinSup $MIN_EXCEPT_SUPP -exRank PNCONF -s $SORTING_TYPE  -w -f1 -f2 -pm -cPM $CPM_MINSUPP -oPrASP -oDLV -stats -oDLV_CONFLICT -minS $MIN_EXCEPTION


