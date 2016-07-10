#!/usr/bin/env bash

SCRIPT=$(realpath $0)
SCRIPTPATH=$(dirname $SCRIPT)

IN_DIRECT=$1
#/GW/D5data-5/gadelrab/yago3/spmf/out_LIFT_PM
OUT_DIRCT=$IN_DIRECT/DLV_1000
MAPPING_DIRCT=$2

RULES_FILE=$IN_DIRECT/rules_Horn.tsv.dlv
NUMBER_OF_RULES=$(wc -l $RULES_FILE | cut -d' ' -f1)

STEP_DEFAULT=200
STEP_CAL=$(perl -w -e "use POSIX; print $NUMBER_OF_RULES/5, qq{\n}")
STEP=$(perl -w -e "use POSIX; print $STEP_CAL < $STEP_DEFAULT ? $STEP_CAL : $STEP_DEFAULT, qq{\n}")

for i in `seq 1 5`;
    do

    TOPK=$(perl -w -e "use POSIX; print ceil($i * $STEP), qq{\n}")
    SUMMARY_FILE=$OUT_DIRCT/summary.top$TOPK
    > $SUMMARY_FILE
    for FILE in $IN_DIRECT/*.tsv.dlv; do
        INPUT_FILE_NAME=$(basename $FILE)
        #FILE_SIZE=$(wc -l < $FILE)
#        TOPK=$(perl -w -e "use POSIX; print ceil($i * 0.1 * $FILE_SIZE), qq{\n}")

        echo "$i $TOPK $FILE Start"
        $SCRIPTPATH/predict_dlv.sh $TOPK $FILE $OUT_DIRCT $MAPPING_DIRCT
        echo "$i $TOPK $FILE Done!"

        STATS=$(sed -n 2p  $OUT_DIRCT/$INPUT_FILE_NAME.predictions.top$TOPK.stats)
        echo $STATS $INPUT_FILE_NAME>>$SUMMARY_FILE


               #  echo $(($i * 0.1 * $FILE_SIZE))
     done
done


echo "Summary and Plot"
sh $SCRIPTPATH/summary_plot_conflict.sh $OUT_DIRCT