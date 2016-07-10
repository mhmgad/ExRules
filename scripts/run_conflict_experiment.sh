#!/usr/bin/env bash

SCRIPT=`realpath $0`
SCRIPTPATH=`dirname $SCRIPT`

IN_DIRECT=$1
#/GW/D5data-5/gadelrab/yago3/spmf/out_LIFT_PM
OUT_DIRCT=$IN_DIRECT/DLV_1000
MAPPING_DIRCT=$2

FILES=$(ls $IN_DIRECT/*.tsv.dlv)

for i in `seq 1 5`;
    do

    TOPK=$(perl -w -e "use POSIX; print ceil($i * 200 ), qq{\n}")
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