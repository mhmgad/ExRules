#!/usr/bin/env bash

OUTPUT_SORTING_TYPE=LIFT

EXCEPTION_MIN_SUPP=0.05
RULE_MIN_SUPP=0.005
RULE_MIN_CONF=0.6
RULE_MAX_CONF=1
PM_MINSUPP=0.0
RM_FUNC=CONF

EX_RANKING="PN${RM_FUNC}"

FILTERS="-f1 -f2"

OUTPUT_TYPES="-oPrASP -oDLV -stats" # -oDLV_CONFLICT"

WORKING_DIRCT=/GW/D5data-5/gadelrab/imdb/attempt_2

FACTS_FILE=$WORKING_DIRCT/facts_to_mine_imdb.tsv

DATE=$(date +"%Y%m%d%H%M")

OUT_DIRECTORY=$WORKING_DIRCT/out_RM-${RM_FUNC}_s-${OUTPUT_SORTING_TYPE}_${DATE}
#/GW/D5data-5/gadelrab/yago3/spmf/out_LIFT_ALL

mkdir -p $OUT_DIRECTORY

IN_DIR=$WORKING_DIRCT/in

mkdir -p $IN_DIR

DATA_FILE=$IN_DIR/facts_to_mine_imdb

./assemble/bin/rdf2int.sh DLV_SAFE $FACTS_FILE $DATA_FILE




INPUT_TRANSACTIONS_FILE=$DATA_FILE.transactions

echo "INPUT_TRANSACTIONS_FILE_Size=$(wc -l $INPUT_TRANSACTIONS_FILE)"

PREDICATE_MAPPING_FILE=$DATA_FILE.mapping_predicates

RUN_CONFIG_FILE=$OUT_DIRECTORY/experiment.prop

echo "INPUT_TRANSACTIONS_FILE_Size=$(wc -l $INPUT_TRANSACTIONS_FILE)" > $RUN_CONFIG_FILE
echo "OUTPUT_SORTING_TYPE=$OUTPUT_SORTING_TYPE" >> $RUN_CONFIG_FILE
echo "FACTS_FILE=$FACTS_FILE" >> $RUN_CONFIG_FILE
echo "OUT_DIRECTORY=$OUT_DIRECTORY" >> $RUN_CONFIG_FILE
echo "EXCEPTION_MIN_SUPP=$EXCEPTION_MIN_SUPP" >> $RUN_CONFIG_FILE
echo "RULE_MIN_SUPP=$RULE_MIN_SUPP" >> $RUN_CONFIG_FILE
echo "RULE_MIN_CONF=$RULE_MIN_CONF" >> $RUN_CONFIG_FILE
echo "RULE_MAX_CONF=$RULE_MAX_CONF" >> $RUN_CONFIG_FILE
echo "PM_MINSUPP=$PM_MINSUPP" >> $RUN_CONFIG_FILE
echo "RM_FUNC=$RM_FUNC" >> $RUN_CONFIG_FILE
echo "OUTPUT_TYPES=$OUTPUT_TYPES" >> $RUN_CONFIG_FILE




#normal horn rules
echo "Mining Horn Rules"
#sh assemble/bin/mine_rules.sh -i $INPUT_TRANSACTIONS_FILE -o $OUT_DIRECTORY/rules_spmf_supp001_conf25_100_f1_f2_$OUTPUT_SORTING_TYPE.tsv -minConf $RULE_MIN_CONF    -de -m $PREDICATE_MAPPING_FILE -s $OUTPUT_SORTING_TYPE -f1 -f2  -oPrASP -oDLV -stats -oDLV_CONFLICT -minS $RULE_MIN_SUPP
sh assemble/bin/mine_rules.sh -i $INPUT_TRANSACTIONS_FILE -o $OUT_DIRECTORY/rules_Horn.tsv -minConf $RULE_MIN_CONF -maxConf $RULE_MAX_CONF    -de -m $PREDICATE_MAPPING_FILE -s $OUTPUT_SORTING_TYPE $FILTERS  $OUTPUT_TYPES -minS $RULE_MIN_SUPP


#no materialization (support only)
echo "Mining Revised Rules: Naive"
sh assemble/bin/mine_rules.sh -i $INPUT_TRANSACTIONS_FILE -o $OUT_DIRECTORY/rules_Naive.tsv -minConf $RULE_MIN_CONF  -maxConf $RULE_MAX_CONF  -de -m $PREDICATE_MAPPING_FILE -ex -exMinSup $EXCEPTION_MIN_SUPP -exRank $EX_RANKING -s $OUTPUT_SORTING_TYPE $FILTERS  $OUTPUT_TYPES -minS $RULE_MIN_SUPP

#no weighted and no order
echo "Mining Revised Rules: PM"
sh assemble/bin/mine_rules.sh -i $INPUT_TRANSACTIONS_FILE -o $OUT_DIRECTORY/rules_PM.tsv -minConf $RULE_MIN_CONF  -maxConf $RULE_MAX_CONF  -de -m $PREDICATE_MAPPING_FILE -ex -exMinSup $EXCEPTION_MIN_SUPP -exRank $EX_RANKING -s $OUTPUT_SORTING_TYPE $FILTERS -pm -cPM $PM_MINSUPP $OUTPUT_TYPES -minS $RULE_MIN_SUPP

#no weighted count and order
echo "Mining Revised Rules: OPM"
sh assemble/bin/mine_rules.sh -i $INPUT_TRANSACTIONS_FILE -o $OUT_DIRECTORY/rules_OPM.tsv -minConf $RULE_MIN_CONF   -maxConf $RULE_MAX_CONF -de -m $PREDICATE_MAPPING_FILE -ex -exMinSup $EXCEPTION_MIN_SUPP -exRank $EX_RANKING -s $OUTPUT_SORTING_TYPE $FILTERS -pm -cPM $PM_MINSUPP $OUTPUT_TYPES -PMo -minS $RULE_MIN_SUPP


#weights order
echo "Mining Revised Rules: OWPM"
sh assemble/bin/mine_rules.sh -i $INPUT_TRANSACTIONS_FILE -o $OUT_DIRECTORY/rules_OWPM.tsv -minConf $RULE_MIN_CONF  -maxConf $RULE_MAX_CONF  -de -m $PREDICATE_MAPPING_FILE -ex -exMinSup $EXCEPTION_MIN_SUPP -exRank $EX_RANKING -s $OUTPUT_SORTING_TYPE  -w $FILTERS -pm -cPM $PM_MINSUPP $OUTPUT_TYPES -PMo -minS $RULE_MIN_SUPP


echo "Summary and Plot"
sh summary_plot_confidence.sh $OUT_DIRECTORY

#weights and no order
#echo "Mining Revised Rules: WPM"
#sh assemble/bin/mine_rules.sh -i $INPUT_TRANSACTIONS_FILE -o $OUT_DIRECTORY/rules_WPM.tsv -minConf $RULE_MIN_CONF  -maxConf $RULE_MAX_CONF  -de -m $PREDICATE_MAPPING_FILE -ex -exMinSup $EXCEPTION_MIN_SUPP -exRank $EX_RANKING -s $OUTPUT_SORTING_TYPE  -w $FILTERS -pm -cPM $PM_MINSUPP $OUTPUT_TYPES -minS $RULE_MIN_SUPP
