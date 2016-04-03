package de.mpii.frequentrulesminning.utils;

import java.util.Arrays;
import java.util.Set;

/**
 * Created by gadelrab on 3/18/16.
 */
public class BodyGroup {

    int [] bodyItems;
    private Set<AssocRuleWithExceptions> rules;


    public BodyGroup(int[] itemset2) {
        this.bodyItems=itemset2;
    }


    @Override
    public int hashCode() {
        return Arrays.hashCode(bodyItems);
    }




    @Override
    public boolean equals(Object obj) {
        return Arrays.equals(bodyItems,((BodyGroup)obj).bodyItems);
    }


    @Override
    public String toString() {
        return Arrays.toString(bodyItems);
    }

    public int[] getBodyItems() {
        return bodyItems;
    }


    public void setRules(Set<AssocRuleWithExceptions> rules) {
        this.rules = rules;
    }
}
