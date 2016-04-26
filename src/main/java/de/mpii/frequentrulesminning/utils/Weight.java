package de.mpii.frequentrulesminning.utils;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

/**
 * Created by gadelrab on 4/9/16.
 */
public class Weight {
    public static final Weight INDEPENDENT = new Weight();

    TIntSet dependencyItems;

    AssocRuleWithExceptions rule;

    double finalWeight;

    public Weight(){
            this(null,null,1);
    }

    public Weight(int[] dependencyItems, AssocRuleWithExceptions rule, double finalWeight) {

        this.dependencyItems = new TIntHashSet();
        addDependencyItems(dependencyItems);
        this.rule = rule;
        this.finalWeight = finalWeight;
    }

    public Weight(AssocRuleWithExceptions rule, double finalWeight) {
        this(rule.getBody(),rule,finalWeight);
    }

    public void addDependencyItems(int[] dependencyItemsArr) {
        if(dependencyItemsArr!=null){
            dependencyItems.addAll(dependencyItemsArr);
        }
    }


    public double getRuleQuality(){
        return (rule==null)? 1:rule.getOrderingQuality();
    }

    public boolean isIndependent() {
        return dependencyItems.isEmpty();
    }

    public double getFinalWeight() {
        return finalWeight;
    }

    public double getRuleWeight(){
        return rule.getConfidence();
    }


    public boolean predictedWithBetterQualityRules(AssocRuleWithExceptions rule) {

        if(isIndependent())
            return true;

        if(rulesChainContains(rule)){
            return false;
        }

        if(this.getMinimumRulesChainQuality()<=rule.getOrderingQuality())
        {
            return false;
        }
        return true;
    }

    private boolean rulesChainContains(AssocRuleWithExceptions rule) {
        // Currently one rule only .. no chains yet
        if(rule==this.rule)
            return true;
        return false;
    }

    public double getMinimumRulesChainQuality() {
        // Currently only one rule
        return this.getRuleQuality();
    }

    public boolean predictedWithDifferentRules(AssocRuleWithExceptions rule) {
        return !rulesChainContains(rule);

    }
}
