#!/usr/bin/env bash

IN_DIRECT=/GW/D5data-5/gadelrab/yago3/spmf/out_LIFT2
OUT_DIRCT=$IN_DIRECT/DLV_ALL4

FILES=$(ls $IN_DIRECT/*.tsv.dlv)

for i in `seq 1 10`;
    do

    TOPK=$(perl -w -e "use POSIX; print ceil($i * 200 ), qq{\n}")
    SUMMARY_FILE=OUT_DIRCT/summary.top$TOPK
    for FILE in $IN_DIRECT/*.tsv.dlv; do
        INPUT_FILE_NAME=$('basename $FILE')
        FILE_SIZE=$(wc -l < $FILE)
#        TOPK=$(perl -w -e "use POSIX; print ceil($i * 0.1 * $FILE_SIZE), qq{\n}")

        echo "$i $TOPK $FILE Start"
        ./predict_dlv.sh $TOPK $FILE $OUT_DIRCT
        echo "$i $TOPK $FILE Done!"

        STATS=$(sed -n 2p  $OUTPUT_DIR/$INPUT_FILE_NAME.predictions.top$TOPK.stats)
        echo $STATS $INPUT_FILE_NAME>>$SUMMARY_FILE


               #  echo $(($i * 0.1 * $FILE_SIZE))
     done
done


