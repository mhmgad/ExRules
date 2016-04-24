#!/usr/bin/env bash

IN_DIRECT=/GW/D5data-5/gadelrab/yago3/spmf/out_LIFT2

FILES=$(ls $IN_DIRECT/*.tsv.dlv)

for FILE in $IN_DIRECT/*.tsv.dlv; do

     FILE_SIZE=$(wc -l < $FILE)

     for i in `seq 1 10`;
     do
         v=$(perl -w -e "use POSIX; print ceil($i * 0.1 * $FILE_SIZE), qq{\n}")
         echo $v
              #  echo $(($i * 0.1 * $FILE_SIZE))
     done
done


