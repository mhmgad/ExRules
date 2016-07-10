#!/usr/bin/env bash


SCRIPT=$(realpath $0)
SCRIPTPATH=$(dirname $SCRIPT)

WORKING_DIR=$1


SUMMARY_FILE_POS=$WORKING_DIR/confilict_summary_pos.tsv
SUMMARY_FILE_NEG=$WORKING_DIR/confilict_summary_neg.tsv

SUMMARY_PLOT_POS=$WORKING_DIR/conflict_pos_plot.pdf
SUMMARY_PLOT_NEG=$WORKING_DIR/conflict_neg_plot.pdf

echo "method\tNaive\tPM\tOPM\tOWPM" > $SUMMARY_FILE_POS
echo "method\tNaive\tPM\tOPM\tOWPM" > $SUMMARY_FILE_NEG

RULES_FILE=$WORKING_DIR/../rules_Horn.tsv.dlv
NUMBER_OF_RULES=$(wc -l $RULES_FILE | cut -d' ' -f1)

STEP_DEFAULT=200
STEP_CAL=$(perl -w -e "use POSIX; print $NUMBER_OF_RULES/5, qq{\n}")
STEP=$(perl -w -e "use POSIX; print $STEP_CAL < $STEP_DEFAULT ? $STEP_CAL : $STEP_DEFAULT, qq{\n}")

for i in `seq 1 5`;
    do

    TOPK=$(perl -w -e "use POSIX; print ceil($i * $STEP), qq{\n}")
    PERC=$(perl -w -e "use POSIX; print ceil($i * 20), qq{\n}")
    FILE=$WORKING_DIR/summary.top$TOPK

    echo -n "${PERC}\t" >> $SUMMARY_FILE_POS
    echo -n "${PERC}\t" >> $SUMMARY_FILE_NEG

    CONFLICTS=$(grep '_Naive.tsv.dlv' $FILE | cut -f1 -d ' ')
    POSITIVES=$(grep '_Naive.tsv.dlv' $FILE | cut -f3 -d ' ')
    NEGATIONS=$(grep '_Naive.tsv.dlv' $FILE | cut -f4 -d ' ')
#    echo "$FILE $CONFLICTS $POSITIVES $NEGATIONS"
    echo -n  "$(perl -w -e "use POSIX; print $CONFLICTS/$POSITIVES, qq{\n}")\t" >> $SUMMARY_FILE_POS
    echo -n  "$(perl -w -e "use POSIX; print $CONFLICTS/$NEGATIONS, qq{\n}")\t" >> $SUMMARY_FILE_NEG

    CONFLICTS=$(grep '_PM.tsv.dlv' $FILE | cut -f1 -d ' ')
    POSITIVES=$(grep '_PM.tsv.dlv' $FILE | cut -f3 -d ' ')
    NEGATIONS=$(grep '_PM.tsv.dlv' $FILE | cut -f4 -d ' ')
#    echo "$FILE $CONFLICTS $POSITIVES $NEGATIONS"
    echo -n  "$(perl -w -e "use POSIX; print $CONFLICTS/$POSITIVES, qq{\n}")\t" >> $SUMMARY_FILE_POS
    echo -n  "$(perl -w -e "use POSIX; print $CONFLICTS/$NEGATIONS, qq{\n}")\t" >> $SUMMARY_FILE_NEG

    CONFLICTS=$(grep '_OPM.tsv.dlv' $FILE | cut -f1 -d ' ')
    POSITIVES=$(grep '_OPM.tsv.dlv' $FILE | cut -f3 -d ' ')
    NEGATIONS=$(grep '_OPM.tsv.dlv' $FILE | cut -f4 -d ' ')
#    echo "$FILE $CONFLICTS $POSITIVES $NEGATIONS"
    echo -n  "$(perl -w -e "use POSIX; print $CONFLICTS/$POSITIVES, qq{\n}")\t" >> $SUMMARY_FILE_POS
    echo -n  "$(perl -w -e "use POSIX; print $CONFLICTS/$NEGATIONS, qq{\n}")\t" >> $SUMMARY_FILE_NEG

    CONFLICTS=$(grep '_OWPM.tsv.dlv' $FILE | cut -f1 -d ' ')
    POSITIVES=$(grep '_OWPM.tsv.dlv' $FILE | cut -f3 -d ' ')
    NEGATIONS=$(grep '_OWPM.tsv.dlv' $FILE | cut -f4 -d ' ')
#    echo "$FILE $CONFLICTS $POSITIVES $NEGATIONS"
    echo "$(perl -w -e "use POSIX; print $CONFLICTS/$POSITIVES, qq{\n}")" >> $SUMMARY_FILE_POS
    echo "$(perl -w -e "use POSIX; print $CONFLICTS/$NEGATIONS, qq{\n}")" >> $SUMMARY_FILE_NEG

    done

gnuplot -e "dataFile='$SUMMARY_FILE_POS'; outputPlot='$SUMMARY_PLOT_POS'" $SCRIPTPATH/plot/plot_conflict.gp
gnuplot -e "dataFile='$SUMMARY_FILE_NEG'; outputPlot='$SUMMARY_PLOT_NEG'" $SCRIPTPATH/plot/plot_conflict.gp
