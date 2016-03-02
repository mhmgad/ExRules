package de.mpii.frequentrulesminning.utils;


import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AssocRule;
import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AssocRules;
import ca.pfv.spmf.patterns.itemset_array_integers_with_tids_bitset.Itemset;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by gadelrab on 2/25/16.
 */
public class AssocRulesExtended extends AssocRules{

    public AssocRulesExtended(AssocRules assocRules) {
        super("Frequent Rules");
        this.getRules().addAll(assocRules.getRules());

   }

    public enum SortingType{CONF,HEAD,BODY,HEAD_CONF}



    public AssocRulesExtended(String name) {
        super(name);
    }



    public void sortByConfidence(){
        super.sortByConfidence();
    }


    public void sortByHead(){
        Collections.sort(getRules(),(c1, c2) -> ((AssocRule)c2).getItemset1()[0] - ((AssocRule)c1).getItemset1()[0]);
    }


    public void sortByBodyLength(){
        Collections.sort(getRules(),(c1, c2) -> ((AssocRule)c2).getItemset1().length - ((AssocRule)c1).getItemset1().length);
    }


    public void sortByHeadAndConfidence(){
        Collections.sort(getRules(), new Comparator<AssocRule>() {
            @Override
            public int compare(AssocRule o1, AssocRule o2) {
                int headDiff=o2.getItemset2()[0] - o1.getItemset2()[0];

                if(headDiff!=0)
                    return headDiff;

                int confidenceDiff=(int)((o2.getConfidence() - o1.getConfidence()) * 2.147483647E9D);
                if(confidenceDiff!=0)
                    return confidenceDiff;

                return o1.getItemset1().length - o2.getItemset1().length;

            }
        });
    }

    public void sort(SortingType type){
        switch (type){
            case CONF:
                sortByConfidence();
                break;
            case HEAD:
                sortByHead();
                break;
            case HEAD_CONF:
                sortByHeadAndConfidence();
                break;
            case BODY:
                sortByBodyLength();
                break;
        }
    }

    @Override
    public String toString(int databaseSize) {
        StringBuilder buffer = new StringBuilder();

        int i = 0;
        int prevHead=-1;
        for(Iterator var5 = this.rules.iterator(); var5.hasNext(); ++i) {
            AssocRule rule = (AssocRule)var5.next();
            if(prevHead!=rule.getItemset2()[0]){
                buffer.append("**************************************************************************\n");
            }
            prevHead=rule.getItemset2()[0];
            buffer.append("r");
            buffer.append(i);
            buffer.append(":\t");
            buffer.append(rule.toString());

            buffer.append("\tconf: ");
            buffer.append(String.format("%.6f",rule.getConfidence()));
            buffer.append("\tsupp: ");
            buffer.append(rule.getAbsoluteSupport());
            buffer.append("\n");
            List<ItemsetString> exceptionCandidate=((AssocRuleString)rule).getExceptionCandidates();
            if(exceptionCandidate!=null&&exceptionCandidate.size()>0){
                buffer.append("Except: ");
                buffer.append(exceptionCandidate);
                buffer.append('\n');
            }
            buffer.append('\n');
        }

        return buffer.toString();
    }
}
