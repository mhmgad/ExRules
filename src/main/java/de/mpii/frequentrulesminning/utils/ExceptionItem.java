package de.mpii.frequentrulesminning.utils;

import java.util.List;

/**
 * Created by gadelrab on 3/11/16.
 */
public class ExceptionItem extends ItemsetString{


    double ruleCoverage;
    double ruleConfidence;
    private double coverage;
    private double confidence;
    private int invertedConflictCount;
    private int conflictCount;
    private double lift;

    public double getInvertedConflictScore() {
        return invertedConflictScore;
    }

    private double invertedConflictScore;

    public double getConflictScore() {
        return conflictScore;
    }

    private double conflictScore;


    public ExceptionItem(Item[] itemsetItem, int[] items, int support, int totalCount) {
        super(itemsetItem, items, support, totalCount);
    }

    public ExceptionItem(int[] items) {
        super(items);
    }

    public ExceptionItem(int size) {
        super(size);
    }

    public ExceptionItem(int[] items, int support) {
        super(items, support);
    }

    public ExceptionItem(Item[] itemsetItem, int[] items, int support) {
        super(itemsetItem, items, support);
    }

    public ExceptionItem(List<Integer> itemset, int support) {
        super(itemset, support);
    }


    public void setCoverage(double coverage) {
        this.coverage=coverage;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    @Override
    public String toString() {
        return super.toString()+"\tcov: "+String.format("%.5f",getCoverage())+"\tconf: "+String.format("%.5f",getConfidence())+"\tlift: "+String.format("%.5f",getLift())+	"\tgConflict: "+String.format("%.5f",getConflictScore())+	"\tinvConflict: "+String.format("%.5f",getInvertedConflictScore()) +	"\ttotalConflict: "+String.format("%.5f",getCombinedConflict());
    }

    public double getCoverage() {
        return coverage;
    }

    public double getConfidence() {
        return confidence;
    }

    public int getFirstItems() {
        return getItems()[0];
    }

    public void setConflictScore(double conflictScore) {
        this.conflictScore = conflictScore;
    }

    public void setInvertedConflictScore(double invertedConflictScore) {
        this.invertedConflictScore=invertedConflictScore;
    }

    public void setInvertedConflictCount(int invertedConflictCount) {
        this.invertedConflictCount = invertedConflictCount;
    }

    public void setConflictCount(int conflictCount) {
        this.conflictCount = conflictCount;
    }

    public double getCombinedConflict(){
        return conflictCount+invertedConflictCount==0? 0:((invertedConflictScore*invertedConflictCount)+(conflictScore*conflictCount))/(conflictCount+invertedConflictCount);
    }

    public void setLift(double lift) {
        this.lift = lift;
    }

    public double getLift() {
        return lift;
    }
}
