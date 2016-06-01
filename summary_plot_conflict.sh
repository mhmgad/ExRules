#!/usr/bin/env bash



WORKING_DIR=$1
#RANGE_START=$2
#RANGE_END=$3
#RANGE_STEP=$4


#~/mpiRoot/GW/D5data-5/gadelrab/yago3/spmf/out_RM-CONF_s-LIFT_201605210250

#WORKING_DIR=/GW/D5data-5/gadelrab/yago3/spmf/out_LIFT_ALL_time2


#RO_STATS_DIR=$WORKING_DIR/ro_stats

#mkdir -p $RO_STATS_DIR

SUMMARY_FILE_POS=$WORKING_DIR/confilict_summary_pos.tsv
SUMMARY_FILE_NEG=$WORKING_DIR/confilict_summary_neg.tsv

SUMMARY_PLOT_NEG=$WORKING_DIR/conflict_neg_plot.pdf
SUMMARY_PLOT_NEG=$WORKING_DIR/conflict_pos_plot.pdf

#mv  $WORKING_DIR/*.ro* $RO_STATS_DIR/



echo "method\tNaive\tPM\tOPM\tOWPM" > $SUMMARY_FILE_POS
echo "method\tNaive\tPM\tOPM\tOWPM" > $SUMMARY_FILE_NEG
for i in `seq 1 5`;
    do

    TOPK=$(perl -w -e "use POSIX; print ceil($i * 200 ), qq{\n}")
    FILE=$WORKING_DIR/summary.top$TOPK

    echo -n "$TOPK\t" >> $SUMMARY_FILE_POS
    echo -n "$TOPK\t" >> $SUMMARY_FILE_NE

    CONFLICTS=$(grep '_Naive.tsv.dlv' $FILE | cut -f1 -d ' ')
    POSITIVES=$(grep '_Naive.tsv.dlv' $FILE | cut -f3 -d ' ')
    NEGATIONS=$(grep '_Naive.tsv.dlv' $FILE | cut -f4 -d ' ')

    echo "$FILE $CONFLICTS $POSITIVES $NEGATIONS"
    echo -n  "$(perl -w -e "use POSIX; print $CONFLICTS/$POSITIVES, qq{\n}")\t" >> $SUMMARY_FILE_POS
    echo -n  "$(perl -w -e "use POSIX; print $CONFLICTS/$NEGATIONS, qq{\n}")\t" >> $SUMMARY_FILE_NEG

    CONFLICTS=$(grep '_PM.tsv.dlv' $FILE | cut -f1 -d ' ')
    POSITIVES=$(grep '_PM.tsv.dlv' $FILE | cut -f3 -d ' ')
    NEGATIONS=$(grep '_PM.tsv.dlv' $FILE | cut -f4 -d ' ')
    echo "$FILE $CONFLICTS $POSITIVES $NEGATIONS"
    echo -n  "$(perl -w -e "use POSIX; print $CONFLICTS/$POSITIVES, qq{\n}")\t" >> $SUMMARY_FILE_POS
    echo -n  "$(perl -w -e "use POSIX; print $CONFLICTS/$NEGATIONS, qq{\n}")\t" >> $SUMMARY_FILE_NEG

    CONFLICTS=$(grep '_OPM.tsv.dlv' $FILE | cut -f1 -d ' ')
    POSITIVES=$(grep '_OPM.tsv.dlv' $FILE | cut -f3 -d ' ')
    NEGATIONS=$(grep '_OPM.tsv.dlv' $FILE | cut -f4 -d ' ')
    echo "$FILE $CONFLICTS $POSITIVES $NEGATIONS"
    echo -n  "$(perl -w -e "use POSIX; print $CONFLICTS/$POSITIVES, qq{\n}")\t" >> $SUMMARY_FILE_POS
    echo -n  "$(perl -w -e "use POSIX; print $CONFLICTS/$NEGATIONS, qq{\n}")\t" >> $SUMMARY_FILE_NEG

    CONFLICTS=$(grep '_OWPM.tsv.dlv' $FILE | cut -f1 -d ' ')
    POSITIVES=$(grep '_OWPM.tsv.dlv' $FILE | cut -f3 -d ' ')
    NEGATIONS=$(grep '_OWPM.tsv.dlv' $FILE | cut -f4 -d ' ')
    echo "$FILE $CONFLICTS $POSITIVES $NEGATIONS"
    echo "$(perl -w -e "use POSIX; print $CONFLICTS/$POSITIVES, qq{\n}")" >> $SUMMARY_FILE_POS
    echo "$(perl -w -e "use POSIX; print $CONFLICTS/$NEGATIONS, qq{\n}")" >> $SUMMARY_FILE_NEG

    done

#echo "$(tail -n +2 $FILE |head -n-1 | grep 'Before' |cut -f 1 | awk -vRS="\n" -vORS="\t" '1')" > $SUMMARY_FILE
#echo "method\t10\t20\t30\t40\t50\t60\t70\t80\t90\t100" > $SUMMARY_FILE

#echo "Horn  $(tail -n +2 $RO_STATS_DIR/*Naive*.tsv.stat.ro |head -n-1 | grep 'Before' |cut -f 3 | awk -vRS="\n" -vORS="\t" '1')" >> $SUMMARY_FILE
#echo "Naive $(tail -n +2 $RO_STATS_DIR/*Naive*.tsv.stat.ro |head -n-1 | grep 'After' |cut -f 3 | awk -vRS="\n" -vORS="\t" '1')" >> $SUMMARY_FILE
#echo "PM $(tail -n +2 $RO_STATS_DIR/*_PM*.tsv.stat.ro |head -n-1 | grep 'After' |cut -f 3 | awk -vRS="\n" -vORS="\t" '1')" >> $SUMMARY_FILE
#echo "OPM $(tail -n +2 $RO_STATS_DIR/*_OPM*.tsv.stat.ro |head -n-1 | grep 'After' |cut -f 3 | awk -vRS="\n" -vORS="\t" '1')" >> $SUMMARY_FILE
#echo "OWPM $(tail -n +2 $RO_STATS_DIR/*_OWPM*.tsv.stat.ro |head -n-1 | grep 'After' |cut -f 3 | awk -vRS="\n" -vORS="\t" '1')" >> $SUMMARY_FILE


#trabspose for plotting

#awk '
#{
#    for (i=1; i<=NF; i++)  {
#        a[NR,i] = $i
#    }
#}
#NF>p { p = NF }
#END {
#    for(j=1; j<=p; j++) {
#        str=a[1,j]
#        for(i=2; i<=NR; i++){
#            str=str" "a[i,j];
#        }
#        print str
#    }
#} ' $SUMMARY_FILE | tr ' ' '\t'  > $SUMMARY_FILE_TRANS




#gnuplot -e "dataFile='$SUMMARY_FILE_TRANS'; outputPlot='$SUMMARY_PLOT'" plot_confidence.gp

#for FILE in $(ls $RO_STATS_DIR/*PM.tsv.stat.ro |LANG=C sort -r);
#do

#    basename $FILE | sed -e 's/rules_\(.*\).tsv.stat.ro/\1/'
    #filename="$(basename $FILE)"
    #filename="$(basename $FILE |sed -n '/^rules/,/ro/p')"

    #echo "$filename"
	#echo  "$methodName $(tail -n +2 $FILE |head -n-1 | grep 'After' |cut -f 3 | awk -vRS="\n" -vORS="\t" '1')" >> $SUMMARY_FILE

#done
