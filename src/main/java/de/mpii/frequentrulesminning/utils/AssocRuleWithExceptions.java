package de.mpii.frequentrulesminning.utils;

import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AssocRule;

import com.google.common.base.Joiner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by gadelrab on 2/22/16.
 */
public class AssocRuleWithExceptions extends AssocRule {


    public void setHeadItems(Item[] headItems) {
        this.headItems = headItems;
    }

    public void setBodyItems(Item[] bodyItems) {
        this.bodyItems = bodyItems;
    }

    private  Item[] headItems;
    private  Item[] bodyItems;
    private List<ItemsetString> exceptionCandidates;

    public AssocRuleWithExceptions(Item[] bodyItems, Item [] headItems, int[] itemset1, int[] itemset2, int supportAntecedent, int transactionCount, double confidence, double lift) {
        this(itemset1, itemset2, supportAntecedent, transactionCount, confidence, lift);
        this.bodyItems = bodyItems;
        this.headItems = headItems;
    }

    public AssocRuleWithExceptions( int[] itemset1, int[] itemset2, int supportAntecedent, int transactionCount, double confidence, double lift) {
        super(itemset1, itemset2, supportAntecedent, transactionCount, confidence, lift);
        Arrays.sort(this.getItemset1());
        Arrays.sort(this.getItemset2());
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

    public void setExceptionCandidates(List<ItemsetString> exceptionCandidates) {
        this.exceptionCandidates = exceptionCandidates;
        Collections.sort(this.exceptionCandidates,( x,  y)-> y.getAbsoluteSupport()-x.getAbsoluteSupport());
    }

    public List<ItemsetString> getExceptionCandidates() {
        return exceptionCandidates;
    }

    public boolean hasExceptions(){
        return (exceptionCandidates==null||exceptionCandidates.size()==0);
    }



}
