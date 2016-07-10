#!/usr/bin/env bash



WORKING_DIR=$1
#~/mpiRoot/GW/D5data-5/gadelrab/yago3/spmf/out_RM-CONF_s-LIFT_201605210250

#WORKING_DIR=/GW/D5data-5/gadelrab/yago3/spmf/out_LIFT_ALL_time2


RO_STATS_DIR=$WORKING_DIR/ro_stats

mkdir -p $RO_STATS_DIR
mv  $WORKING_DIR/*.ro* $RO_STATS_DIR/


#Confidence
CONF_SUMMARY_FILE=$RO_STATS_DIR/confidence_summary_ro.tsv
CONf_SUMMARY_FILE_TRANS=$CONF_SUMMARY_FILE.trans
CONF_SUMMARY_PLOT=$RO_STATS_DIR/confidence_ro_plot.pdf



echo "method\t10\t20\t30\t40\t50\t60\t70\t80\t90\t100" > $CONF_SUMMARY_FILE
# Get confidence before changes and the Naive methods
echo "Horn  $(tail -n +2 $RO_STATS_DIR/*Naive*.tsv.stat.ro |head -n-1 | grep 'Before' |cut -f 3 | awk -vRS="\n" -vORS="\t" '1')" >> $CONF_SUMMARY_FILE
echo "Naive $(tail -n +2 $RO_STATS_DIR/*Naive*.tsv.stat.ro |head -n-1 | grep 'After' |cut -f 3 | awk -vRS="\n" -vORS="\t" '1')" >> $CONF_SUMMARY_FILE
echo "PM $(tail -n +2 $RO_STATS_DIR/*_PM*.tsv.stat.ro |head -n-1 | grep 'After' |cut -f 3 | awk -vRS="\n" -vORS="\t" '1')" >> $CONF_SUMMARY_FILE
echo "OPM $(tail -n +2 $RO_STATS_DIR/*_OPM*.tsv.stat.ro |head -n-1 | grep 'After' |cut -f 3 | awk -vRS="\n" -vORS="\t" '1')" >> $CONF_SUMMARY_FILE
echo "OWPM $(tail -n +2 $RO_STATS_DIR/*_OWPM*.tsv.stat.ro |head -n-1 | grep 'After' |cut -f 3 | awk -vRS="\n" -vORS="\t" '1')" >> $CONF_SUMMARY_FILE


#trabspose for plotting

sh ./support/transpose_file.sh $CONF_SUMMARY_FILE $CONf_SUMMARY_FILE_TRANS
gnuplot -e "dataFile='$CONf_SUMMARY_FILE_TRANS'; outputPlot='$CONF_SUMMARY_PLOT'; y_label='Avg. Confidence'" ./plot/plot_quality.gp

##################################################################################################################33
#Jaccard

JACC_SUMMARY_FILE=$RO_STATS_DIR/jaccard_summary_ro.tsv
JACC_SUMMARY_FILE_TRANS=$JACC_SUMMARY_FILE.trans
JACC_SUMMARY_PLOT=$RO_STATS_DIR/jaccard_ro_plot.pdf



echo "method\t10\t20\t30\t40\t50\t60\t70\t80\t90\t100" > $JACC_SUMMARY_FILE
# Get confidence before changes and the Naive methods
echo "Horn  $(tail -n +2 $RO_STATS_DIR/*Naive*.tsv.stat.ro |head -n-1 | grep 'Before' |cut -f 7 | awk -vRS="\n" -vORS="\t" '1')" >> $JACC_SUMMARY_FILE
echo "Naive $(tail -n +2 $RO_STATS_DIR/*Naive*.tsv.stat.ro |head -n-1 | grep 'After' |cut -f 7 | awk -vRS="\n" -vORS="\t" '1')" >> $JACC_SUMMARY_FILE
echo "PM $(tail -n +2 $RO_STATS_DIR/*_PM*.tsv.stat.ro |head -n-1 | grep 'After' |cut -f 7 | awk -vRS="\n" -vORS="\t" '1')" >> $JACC_SUMMARY_FILE
echo "OPM $(tail -n +2 $RO_STATS_DIR/*_OPM*.tsv.stat.ro |head -n-1 | grep 'After' |cut -f 7 | awk -vRS="\n" -vORS="\t" '1')" >> $JACC_SUMMARY_FILE
echo "OWPM $(tail -n +2 $RO_STATS_DIR/*_OWPM*.tsv.stat.ro |head -n-1 | grep 'After' |cut -f 7 | awk -vRS="\n" -vORS="\t" '1')" >> $JACC_SUMMARY_FILE


#trabspose for plotting

sh ./support/transpose_file.sh $JACC_SUMMARY_FILE $JACC_SUMMARY_FILE_TRANS
gnuplot -e "dataFile='$JACC_SUMMARY_FILE_TRANS'; outputPlot='$JACC_SUMMARY_PLOT'; y_label='Avg. Jaccard'" ./plot/plot_quality.gp


##################################################################################################################33
#Conviction

CONV_SUMMARY_FILE=$RO_STATS_DIR/conviction_summary_ro.tsv
CONV_SUMMARY_FILE_TRANS=$CONV_SUMMARY_FILE.trans
CONV_SUMMARY_PLOT=$RO_STATS_DIR/conviction_ro_plot.pdf



echo "method\t10\t20\t30\t40\t50\t60\t70\t80\t90\t100" > $CONV_SUMMARY_FILE
# Get confidence before changes and the Naive methods
echo "Horn  $(tail -n +2 $RO_STATS_DIR/*Naive*.tsv.stat.ro |head -n-1 | grep 'Before' |cut -f 11 | awk -vRS="\n" -vORS="\t" '1')" >> $CONV_SUMMARY_FILE
echo "Naive $(tail -n +2 $RO_STATS_DIR/*Naive*.tsv.stat.ro |head -n-1 | grep 'After' |cut -f 11 | awk -vRS="\n" -vORS="\t" '1')" >> $CONV_SUMMARY_FILE
echo "PM $(tail -n +2 $RO_STATS_DIR/*_PM*.tsv.stat.ro |head -n-1 | grep 'After' |cut -f 11 | awk -vRS="\n" -vORS="\t" '1')" >> $CONV_SUMMARY_FILE
echo "OPM $(tail -n +2 $RO_STATS_DIR/*_OPM*.tsv.stat.ro |head -n-1 | grep 'After' |cut -f 11 | awk -vRS="\n" -vORS="\t" '1')" >> $CONV_SUMMARY_FILE
echo "OWPM $(tail -n +2 $RO_STATS_DIR/*_OWPM*.tsv.stat.ro |head -n-1 | grep 'After' |cut -f 11 | awk -vRS="\n" -vORS="\t" '1')" >> $CONV_SUMMARY_FILE


#trabspose for plotting

sh ./support/transpose_file.sh $CONV_SUMMARY_FILE $CONV_SUMMARY_FILE_TRANS
gnuplot -e "dataFile='$CONV_SUMMARY_FILE_TRANS'; outputPlot='$CONV_SUMMARY_PLOT'; y_label='Avg. Conviction'" ./plot/plot_quality.gp