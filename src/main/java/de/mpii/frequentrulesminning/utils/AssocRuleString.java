package de.mpii.frequentrulesminning.utils;

import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AssocRule;

import com.google.common.base.Joiner;
import de.mpii.frequentrulesminning.Item;

/**
 * Created by gadelrab on 2/22/16.
 */
public class AssocRuleString extends AssocRule {


    private  Item[] items2Str;
    private  Item[] items1Str;

    public AssocRuleString(Item[] items1Str, Item []items2Str, int[] itemset1, int[] itemset2, int supportAntecedent, int transactionCount, double confidence, double lift) {
        super(itemset1, itemset2, supportAntecedent, transactionCount, confidence, lift);
        this.items1Str=items1Str;
        this.items2Str=items2Str;
    }

    @Override
    public String toString() {
        String body = Joiner.on(" ").join(items1Str);
        String head=  Joiner.on(" ").join(items2Str);
        return body+" ==> "+head+"\t";

    }
}
