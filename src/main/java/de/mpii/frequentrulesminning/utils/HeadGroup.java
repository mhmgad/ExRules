package de.mpii.frequentrulesminning.utils;

import java.util.Arrays;
import java.util.Set;

/**
 * Created by gadelrab on 3/15/16.
 */
public class HeadGroup {


    int [] headItems;
    private double coverage;
    private double confidence;
    private int allTransactionsCount;
    private Set<AssocRuleWithExceptions> rules;

    public HeadGroup(int[] itemset2) {
        this.headItems=itemset2;
    }


    @Override
    public int hashCode() {
        return Arrays.hashCode(headItems);
    }


    public void setCoverage(double coverage) {
        this.coverage = coverage;
    }

    @Override
    public boolean equals(Object obj) {
        return Arrays.equals(headItems,((HeadGroup)obj).headItems);
    }

    public double getCoverage() {
        return coverage;
    }

    public double  getConfidence() {
        return confidence;
    }

    @Override
    public String toString() {
        return Arrays.toString(headItems)+" Cov: " + String.format("%.5f", getCoverage()) + " AllTrans: " + getAllTransactionsCount() + " Conf: " + String.format("%.5f",getConfidence());
    }

    public int[] getHeadItems() {
        return headItems;
    }

    public void setAllTransactionsCount(int allTransactionsCount) {
        this.allTransactionsCount = allTransactionsCount;
    }

    public int getAllTransactionsCount() {
        return allTransactionsCount;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public void setRules(Set<AssocRuleWithExceptions> rules) {
        this.rules = rules;
    }
}
