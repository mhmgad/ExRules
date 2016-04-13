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
        return rule.getLift();
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
}
