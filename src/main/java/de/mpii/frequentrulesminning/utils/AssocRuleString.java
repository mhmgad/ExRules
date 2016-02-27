package de.mpii.frequentrulesminning.utils;

import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AssocRule;

import com.google.common.base.Joiner;
import de.mpii.frequentrulesminning.Item;

import java.util.Arrays;

/**
 * Created by gadelrab on 2/22/16.
 */
public class AssocRuleString extends AssocRule {


    private  Item[] headItems;
    private  Item[] bodyItems;

    public AssocRuleString(Item[] bodyItems, Item [] headItems, int[] itemset1, int[] itemset2, int supportAntecedent, int transactionCount, double confidence, double lift) {
        super(itemset1, itemset2, supportAntecedent, transactionCount, confidence, lift);
        Arrays.sort(this.getItemset1());
        Arrays.sort(this.getItemset2());
        this.bodyItems = bodyItems;
        this.headItems = headItems;
    }

    @Override
    public String toString() {
        String body = Joiner.on(" ").join(bodyItems);
        String head=  Joiner.on(" ").join(this.headItems);
        return body+" ==> "+head;

    }

    public Item[] getHeadItems(){
        return headItems;
    }


    public Item[] getbodyItems(){
        return bodyItems;
    }
}
