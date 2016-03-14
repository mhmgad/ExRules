package de.mpii.frequentrulesminning.utils;


import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AssocRule;
import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AssocRules;
import com.google.common.collect.Multimap;

import java.util.*;
import java.util.function.Consumer;

/**
 * Created by gadelrab on 2/25/16.
 */
public class AssocRulesExtended implements Iterable<AssocRuleWithExceptions> {
    //extends AssocRules{

//    public AssocRulesExtended(AssocRules assocRules) {
//        super("Frequent Rules");
//        this.getRules().addAll(assocRules.getRules());
//
//   }

    public ArrayList<AssocRuleWithExceptions> getRules() {
        return rules;
    }

    ArrayList<AssocRuleWithExceptions> rules;
    Multimap<Integer,AssocRuleWithExceptions> head2Rules;

    double confidence;

    public double getConfidence() {
        return confidence;
    }

    public void addRule(AssocRuleWithExceptions rule) {
        rules.add(rule);

    }

    public int getRulesCount() {
        return getRules().size();
    }

    @Override
    public Iterator<AssocRuleWithExceptions> iterator() {
        return rules.iterator();
    }

    @Override
    public void forEach(Consumer<? super AssocRuleWithExceptions> action) {
        rules.forEach(action);
    }

    @Override
    public Spliterator<AssocRuleWithExceptions> spliterator() {
        return rules.spliterator();
    }

    public enum SortingType{CONF,HEAD,BODY,HEAD_CONF}



//    public AssocRulesExtended(String name) {
//        super(name);
//    }


    public void sortByConfidence(){
        Collections.sort(getRules(),(AssocRuleWithExceptions c1, AssocRuleWithExceptions c2) -> ((int)((c2.getConfidence() - c1.getConfidence()) * 2.147483647E9D)));

    }




    public void sortByHead(){
        Collections.sort(getRules(),(AssocRuleWithExceptions c1,AssocRuleWithExceptions c2) -> (c2).getItemset1()[0] - (c1).getItemset1()[0]);
    }


    public void sortByBodyLength(){
        Collections.sort(getRules(),(AssocRuleWithExceptions c1,AssocRuleWithExceptions c2) -> (c2).getItemset1().length - (c1).getItemset1().length);
    }


    public void sortByHeadAndConfidence(){
        Collections.sort(getRules(), new Comparator<AssocRuleWithExceptions>() {
            @Override
            public int compare(AssocRuleWithExceptions o1, AssocRuleWithExceptions o2) {
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


    public String toString(int databaseSize,SortingType type,boolean hasExceptionOnly) {
        StringBuilder buffer = new StringBuilder();

        int i = 0;
        int prevHead=-1;
        for(Iterator var5 = this.rules.iterator(); var5.hasNext(); ++i) {
            AssocRule rule = (AssocRule)var5.next();

            // skip if i do not has exceptions
            if(hasExceptionOnly&&!((AssocRuleWithExceptions)rule).hasExceptions())
                continue;

            if((type==SortingType.HEAD||type==SortingType.HEAD_CONF)&&prevHead!=rule.getItemset2()[0]){
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
            Exceptions exceptionCandidate=((AssocRuleWithExceptions)rule).getExceptionCandidates();
            if(exceptionCandidate!=null&&exceptionCandidate.size()>0){
                buffer.append("Except:\n");
                buffer.append(exceptionCandidate);
                buffer.append('\n');
            }
            buffer.append('\n');
        }

        return buffer.toString();
    }
}
