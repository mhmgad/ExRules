#!/usr/bin/env bash

OUTPUT_SORTING_TYPE=LIFT

EXCEPTION_MIN_SUPP=0.05
RULE_MIN_SUPP=0.008
RULE_MIN_CONF=0.6
RULE_MAX_CONF=1
PM_MINSUPP=0.0

FILTERS="-f1 -f2"

OUTPUT_TYPES="-oPrASP -oDLV -stats -oDLV_CONFLICT"


FACTS_FILE=/GW/D5data-5/gadelrab/imdb/facts_to_mine_imdb.tsv


OUT_DIRECTORY=/GW/D5data-5/gadelrab/imdb/out_$OUTPUT_SORTING_TYPE_
#/GW/D5data-5/gadelrab/yago3/spmf/out_LIFT_ALL

mkdir -p $OUT_DIRECTORY

IN_DIR=/GW/D5data-5/gadelrab/imdb/in

mkdir -p $IN_DIR

DATA_FILE=$IN_DIR/facts_to_mine_imdb

./assemble/bin/rdf2int.sh SPMF $FACTS_FILE $DATA_FILE



INPUT_TRANSACTIONS_FILE=$DATA_FILE.transactions

PREDICATE_MAPPING_FILE=$DATA_FILE.mapping_predicates


echo "OUTPUT_SORTING_TYPE=$OUTPUT_SORTING_TYPE"
echo "FACTS_FILE=$FACTS_FILE"
echo "OUT_DIRECTORY=$OUT_DIRECTORY"
echo "EXCEPTION_MIN_SUPP=$EXCEPTION_MIN_SUPP"
echo "RULE_MIN_SUPP=$RULE_MIN_SUPP"
echo "RULE_MIN_CONF=$RULE_MIN_CONF"
echo "RULE_MAX_CONF=$RULE_MAX_CONF"
echo "PM_MINSUPP=$PM_MINSUPP"
echo "OUTPUT_TYPES=$OUTPUT_TYPES"



#normal horn rules
echo "Mining Horn Rules"
#sh assemble/bin/mine_rules.sh -i $INPUT_TRANSACTIONS_FILE -o $OUT_DIRECTORY/rules_spmf_supp001_conf25_100_f1_f2_$OUTPUT_SORTING_TYPE.tsv -minConf $RULE_MIN_CONF    -de -m $PREDICATE_MAPPING_FILE -s $OUTPUT_SORTING_TYPE -f1 -f2  -oPrASP -oDLV -stats -oDLV_CONFLICT -minS $RULE_MIN_SUPP
sh assemble/bin/mine_rules.sh -i $INPUT_TRANSACTIONS_FILE -o $OUT_DIRECTORY/rules_spmf_supp001_conf25_100_f1_f2_$OUTPUT_SORTING_TYPE.tsv -minConf $RULE_MIN_CONF    -de -m $PREDICATE_MAPPING_FILE -s $OUTPUT_SORTING_TYPE $FILTERS  $OUTPUT_TYPES -minS $RULE_MIN_SUPP


#no materialization (support only)
echo "Mining Revised Rules: Naive"
sh assemble/bin/mine_rules.sh -i $INPUT_TRANSACTIONS_FILE -o $OUT_DIRECTORY/rules_spmf_supp001_conf25_100_excep02_PNCONF_f1_f2_$OUTPUT_SORTING_TYPE.tsv -minConf $RULE_MIN_CONF    -de -m $PREDICATE_MAPPING_FILE -ex -exMinSup $EXCEPTION_MIN_SUPP -exRank PNCONF -s $OUTPUT_SORTING_TYPE $FILTERS  $OUTPUT_TYPES -minS $RULE_MIN_SUPP


#no weighted count and order
echo "Mining Revised Rules: PMO"
sh assemble/bin/mine_rules.sh -i $INPUT_TRANSACTIONS_FILE -o $OUT_DIRECTORY/rules_spmf_supp001_conf25_100_excep02_PNCONF_cPM05_noWeight_order_f1_f2_$OUTPUT_SORTING_TYPE.tsv -minConf $RULE_MIN_CONF    -de -m $PREDICATE_MAPPING_FILE -ex -exMinSup $EXCEPTION_MIN_SUPP -exRank PNCONF -s $OUTPUT_SORTING_TYPE $FILTERS -pm -cPM $PM_MINSUPP -oPrASP -oDLV  -stats -oDLV_CONFLICT -PMo -minS $RULE_MIN_SUPP


#weights order
echo "Mining Revised Rules: PMOW"
sh assemble/bin/mine_rules.sh -i $INPUT_TRANSACTIONS_FILE -o $OUT_DIRECTORY/rules_spmf_supp001_conf25_100_excep02_PNCONF_cPM05_weight_order_f1_f2_$OUTPUT_SORTING_TYPE.tsv -minConf $RULE_MIN_CONF    -de -m $PREDICATE_MAPPING_FILE -ex -exMinSup $EXCEPTION_MIN_SUPP -exRank PNCONF -s $OUTPUT_SORTING_TYPE  -w $FILTERS -pm -cPM $PM_MINSUPP $OUTPUT_TYPES -PMo -minS $RULE_MIN_SUPP


#no weighted and no order
echo "Mining Revised Rules: PM"
sh assemble/bin/mine_rules.sh -i $INPUT_TRANSACTIONS_FILE -o $OUT_DIRECTORY/rules_spmf_supp001_conf25_100_excep02_PNCONF_cPM05_noWeight_noOrder_f1_f2_$OUTPUT_SORTING_TYPE.tsv -minConf $RULE_MIN_CONF    -de -m $PREDICATE_MAPPING_FILE -ex -exMinSup $EXCEPTION_MIN_SUPP -exRank PNCONF -s $OUTPUT_SORTING_TYPE $FILTERS -pm -cPM $PM_MINSUPP -oPrASP -oDLV  -stats -oDLV_CONFLICT -minS $RULE_MIN_SUPP


#weights and no order
echo "Mining Revised Rules: PMW"
sh assemble/bin/mine_rules.sh -i $INPUT_TRANSACTIONS_FILE -o $OUT_DIRECTORY/rules_spmf_supp001_conf25_100_excep02_PNCONF_cPM05_weight_noOrder_f1_f2_$OUTPUT_SORTING_TYPE.tsv -minConf $RULE_MIN_CONF    -de -m $PREDICATE_MAPPING_FILE -ex -exMinSup $EXCEPTION_MIN_SUPP -exRank PNCONF -s $OUTPUT_SORTING_TYPE  -w $FILTERS -pm -cPM $PM_MINSUPP $OUTPUT_TYPES -minS $RULE_MIN_SUPP


