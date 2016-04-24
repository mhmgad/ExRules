#!/usr/bin/env bash

IN_DIRECT=/GW/D5data-5/gadelrab/yago3/spmf/out_LIFT2
OUT_DIRCT=$IN_DIRECT/DLV_ALL

FILES=$(ls $IN_DIRECT/*.tsv.dlv)

for FILE in $IN_DIRECT/*.tsv.dlv; do

     FILE_SIZE=$(wc -l < $FILE)

     for i in `seq 1 10`;
     do
         v=$(perl -w -e "use POSIX; print ceil($i * 0.1 * $FILE_SIZE), qq{\n}")
         echo "$i $V $FILE Start"
         ./predict_dlv.sh $v $FILE $OUT_DIRCT
         echo "$i $V $FILE Done!"
               #  echo $(($i * 0.1 * $FILE_SIZE))
     done
done


