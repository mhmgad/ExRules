#!/usr/bin/env bash



WORKING_DIR=$1
#~/mpiRoot/GW/D5data-5/gadelrab/yago3/spmf/out_RM-CONF_s-LIFT_201605210250

#WORKING_DIR=/GW/D5data-5/gadelrab/yago3/spmf/out_LIFT_ALL_time2


RO_STATS_DIR=$WORKING_DIR/ro_stats

mkdir -p $RO_STATS_DIR

CONF_SUMMARY_FILE=$RO_STATS_DIR/confidence_summary_ro.tsv
SUMMARY_FILE_TRANS=$CONF_SUMMARY_FILE.trans
SUMMARY_PLOT=$RO_STATS_DIR/confidence_ro_plot.pdf


mv  $WORKING_DIR/*.ro* $RO_STATS_DIR/


# Get confidence before changes and the Naive methods
FILE=$(ls $RO_STATS_DIR/*Naive.tsv.stat.ro |  head -1)
echo "$(tail -n +2 $FILE |head -n-1 | grep 'Before' |cut -f 1 | awk -vRS="\n" -vORS="\t" '1')" > $CONF_SUMMARY_FILE
echo "method\t10\t20\t30\t40\t50\t60\t70\t80\t90\t100" > $CONF_SUMMARY_FILE

echo "Horn  $(tail -n +2 $RO_STATS_DIR/*Naive*.tsv.stat.ro |head -n-1 | grep 'Before' |cut -f 3 | awk -vRS="\n" -vORS="\t" '1')" >> $CONF_SUMMARY_FILE
echo "Naive $(tail -n +2 $RO_STATS_DIR/*Naive*.tsv.stat.ro |head -n-1 | grep 'After' |cut -f 3 | awk -vRS="\n" -vORS="\t" '1')" >> $CONF_SUMMARY_FILE
echo "PM $(tail -n +2 $RO_STATS_DIR/*_PM*.tsv.stat.ro |head -n-1 | grep 'After' |cut -f 3 | awk -vRS="\n" -vORS="\t" '1')" >> $CONF_SUMMARY_FILE
echo "OPM $(tail -n +2 $RO_STATS_DIR/*_OPM*.tsv.stat.ro |head -n-1 | grep 'After' |cut -f 3 | awk -vRS="\n" -vORS="\t" '1')" >> $CONF_SUMMARY_FILE
echo "OWPM $(tail -n +2 $RO_STATS_DIR/*_OWPM*.tsv.stat.ro |head -n-1 | grep 'After' |cut -f 3 | awk -vRS="\n" -vORS="\t" '1')" >> $CONF_SUMMARY_FILE


#trabspose for plotting

awk '
{
    for (i=1; i<=NF; i++)  {
        a[NR,i] = $i
    }
}
NF>p { p = NF }
END {
    for(j=1; j<=p; j++) {
        str=a[1,j]
        for(i=2; i<=NR; i++){
            str=str" "a[i,j];
        }
        print str
    }
} ' $CONF_SUMMARY_FILE | tr ' ' '\t'  > $SUMMARY_FILE_TRANS




gnuplot -e "dataFile='$SUMMARY_FILE_TRANS'; outputPlot='$SUMMARY_PLOT'" plot_confidence.gp

#for FILE in $(ls $RO_STATS_DIR/*PM.tsv.stat.ro |LANG=C sort -r);
#do

#    basename $FILE | sed -e 's/rules_\(.*\).tsv.stat.ro/\1/'
    #filename="$(basename $FILE)"
    #filename="$(basename $FILE |sed -n '/^rules/,/ro/p')"

    #echo "$filename"
	#echo  "$methodName $(tail -n +2 $FILE |head -n-1 | grep 'After' |cut -f 3 | awk -vRS="\n" -vORS="\t" '1')" >> $SUMMARY_FILE

#done
