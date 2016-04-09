package de.mpii.frequentrulesminning.utils;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

/**
 * Created by gadelrab on 4/9/16.
 */
public class Weight {
    public static final Weight INDEPENDENT = new Weight();

    TIntSet dependencyItems;

    double ruleWeight;

    double finalWeight;

    public Weight(){
            this(null,1,1);
    }

    public Weight(int[] dependencyItems, double ruleWeight, double finalWeight) {

        this.dependencyItems = new TIntHashSet();
        addDependencyItems(dependencyItems);
        this.ruleWeight = ruleWeight;
        this.finalWeight = finalWeight;
    }

    public void addDependencyItems(int[] dependencyItemsArr) {
        if(dependencyItemsArr!=null){
            dependencyItems.addAll(dependencyItemsArr);
        }
    }


    public void addDependencyItems(TIntSet dependencyItemsSet) {
        if(dependencyItemsSet!=null){
            addDependencyItems(dependencyItemsSet.toArray());
        }
    }

    public boolean isIndependent() {
        return dependencyItems.isEmpty();
    }

    public double getFinalWeight() {
        return finalWeight;
    }

    public double getRuleWeight(){
        return ruleWeight;
    }
}
