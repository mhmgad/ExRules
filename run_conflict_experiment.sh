#!/usr/bin/env bash

IN_DIRECT=/GW/D5data-5/gadelrab/yago3/spmf/out_LIFT2

FILES=($IN_DIRECT/*.tsv.dlv)

for FILE in $IN_DIRECT/*.tsv.dlv; do

     FILE_SIZE=$(wc -l $FILE)
     echo $FILE_SIZE
done


