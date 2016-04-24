#!/usr/bin/env bash


DLV=~/dlv.i386-linux-elf-static.bin

TOPK="$1"



#INPUT_DIR=/GW/D5data-5/gadelrab/yago3/spmf/out_LIFT2
#INPUT_FILE=rules_spmf_supp0001_conf25_100_excep02_PNCONF_f1_f2_LIFT.tsv.dlv

INPUT_DIR=`dirname $2`
INPUT_FILE=`basename $2`

OUTPUT_DIR=$3

mkdir $OUTPUT_DIR


KB_FILE=/GW/D5data-5/gadelrab/yago3/spmf/in/facts_to_mine.dlv
PREDICTS_MAPPING=/GW/D5data-5/gadelrab/yago3/spmf/in/facts_to_mine.mapping_predicates
SUBJECT_MAPPING=/GW/D5data-5/gadelrab/yago3/spmf/in/facts_to_mine.mapping_subjects

INPUT_FILE_CONFLICT=$INPUT_DIR/$INPUT_FILE.conflict

INPUT_FILE_NEG=$INPUT_FILE.neg

INPUT_FILE_TOPK=$OUTPUT_DIR/$INPUT_FILE.top$TOPK

INPUT_FILE_NEG_TOPK=$OUTPUT_DIR/$INPUT_FILE_NEG.top$TOPK

OUTPUT_FILE=$OUTPUT_DIR/$INPUT_FILE.predictions_conflicts.top$TOPK

head -$TOPK $INPUT_DIR/$INPUT_FILE > $INPUT_FILE_TOPK

head -$TOPK $INPUT_DIR/$INPUT_FILE_NEG > $INPUT_FILE_NEG_TOPK

$DLV -nofacts $KB_FILE $INPUT_FILE_TOPK $INPUT_FILE_NEG_TOPK $INPUT_FILE_CONFLICT > $OUTPUT_FILE

#rm $INPUT_FILE_TOPK
#rm $INPUT_FILE_NEG_TOPK


#run conflicts stats
sh assemble/bin/dlv2kb.sh NONE $OUTPUT_FILE $PREDICTS_MAPPING $SUBJECT_MAPPING


