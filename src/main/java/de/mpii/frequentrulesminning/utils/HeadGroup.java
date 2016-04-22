package de.mpii.frequentrulesminning.utils;

import java.util.Arrays;
import java.util.Set;

/**
 * Created by gadelrab on 3/15/16.
 */
public class HeadGroup {


    private  Item[] headItems;
    int [] headItemsIds;
    private double coverage;
    private double confidence;
//    private int allTransactionsCount;
    private Set<AssocRuleWithExceptions> rules;

    public HeadGroup(int[] headItemsId) {
        this(headItemsId,null);
    }

    public HeadGroup(int[] headItemsIds,Item[] headItems) {
        this.headItemsIds =headItemsIds;
        this.headItems=headItems;

    }


    @Override
    public int hashCode() {
        return Arrays.hashCode(headItemsIds);
    }


    public void setCoverage(double coverage) {
        this.coverage = coverage;
    }

    @Override
    public boolean equals(Object obj) {
        return Arrays.equals(headItemsIds,((HeadGroup)obj).headItemsIds);
    }

    public double getCoverage() {
        return coverage;
    }

    public double  getConfidence() {
        return confidence;
    }

    @Override
    public String toString() {
        return Arrays.toString(headItemsIds)+" Cov: " + String.format("%.5f", getCoverage())  + " Conf: " + String.format("%.5f",getConfidence());//+" AllTrans: " + getAllTransactionsCount()
    }

    public int[] getHeadItemsIds() {
        return headItemsIds;
    }

//    public void setAllTransactionsCount(int allTransactionsCount) {
//        this.allTransactionsCount = allTransactionsCount;
//    }

//    public int getAllTransactionsCount() {
//        return allTransactionsCount;
//    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public void setRules(Set<AssocRuleWithExceptions> rules) {
        this.rules = rules;
    }

    public Set<AssocRuleWithExceptions> getRules(){
        return rules;
    }

    public Item[] getHeadItems() {
        return headItems;
    }
}
