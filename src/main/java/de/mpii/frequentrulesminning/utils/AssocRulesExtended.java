package de.mpii.frequentrulesminning.utils;


import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AssocRule;
import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AssocRules;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import de.mpii.frequentrulesminning.RulesEvaluator;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Created by gadelrab on 2/25/16.
 */
public class AssocRulesExtended implements Iterable<AssocRuleWithExceptions> {


    private  SetMultimap<BodyGroup, AssocRuleWithExceptions> body2Rules;
    List<AssocRuleWithExceptions> rules;
    SetMultimap<HeadGroup, AssocRuleWithExceptions> head2Rules;
    double confidence;

    public AssocRulesExtended() {
        rules = new ArrayList<>();
        head2Rules = HashMultimap.create();
        body2Rules= HashMultimap.create();

    }

    public List<AssocRuleWithExceptions> getRules() {
        return rules;
    }

    public double getConfidence() {
        return confidence;
    }

    public void addRule(AssocRuleWithExceptions rule) {
        getRules().add(rule);
        head2Rules.put(new HeadGroup(rule.getItemset2()), rule);
        body2Rules.put(new BodyGroup(rule.getItemset1()),rule);

    }

    public int getRulesCount() {
        if (getRules().size() != head2Rules.size())
            System.out.println("Non Consistent rules list:" + rules.size() + " Map:" + head2Rules.size());
        return getRules().size();
    }

    @Override
    public Iterator<AssocRuleWithExceptions> iterator() {
        return getRules().iterator();
    }

    @Override
    public void forEach(Consumer<? super AssocRuleWithExceptions> action) {
        getRules().forEach(action);
    }

    @Override
    public Spliterator<AssocRuleWithExceptions> spliterator() {
        return getRules().spliterator();
    }

    public void filterRules(Predicate<AssocRuleWithExceptions> predicate) {

        getRules().removeIf(
                predicate.and(assocRule -> {
                    // remove from the head2Rules map.
                    head2Rules.remove(new HeadGroup(assocRule.getItemset2()), assocRule);
                    body2Rules.remove(new HeadGroup(assocRule.getItemset1()), assocRule);
                    return true;
                }));

    }

    public void evaluateIndividuals(RulesEvaluator evaluator) {
        // Individual Rules Evaluation
        getRules().stream().parallel().forEach((r) -> {
            r.setCoverage(evaluator.coverage(r));
            r.getExceptionCandidates().forEach((ex) -> {
                ex.setCoverage(evaluator.coverage(r, ex));
                ex.setConfidence(evaluator.confidence(r, ex));
            });

        });

    }

    public void evaluateHeadGroups(RulesEvaluator evaluator) {

        System.out.println("headGroups: "+head2Rules.keySet().size());;
        head2Rules.keySet().parallelStream().forEach((key) -> evaluateHeadGroup(evaluator, key));


    }

    private void evaluateHeadGroup(RulesEvaluator evaluator, HeadGroup key) {
        evaluator.coverage(key,head2Rules.get(key));
        evaluator.confidence(key,head2Rules.get(key));
        evaluator.conflict(key,head2Rules.get(key));



//        if(coverage==0)
//            System.out.println("\t"+key);
//        key.setCoverage(coverage);


    }

    public void sortByConfidence(List<AssocRuleWithExceptions> rulesToSort) {
        Collections.sort(rulesToSort, (AssocRuleWithExceptions c1, AssocRuleWithExceptions c2) -> ((int) ((c2.getConfidence() - c1.getConfidence()) * 2.147483647E9D)));

    }


//    public AssocRulesExtended(String name) {
//        super(name);
//    }

    public void sortByHead(List<AssocRuleWithExceptions> rulesToSort) {
        Collections.sort(rulesToSort, (AssocRuleWithExceptions c1, AssocRuleWithExceptions c2) -> (c2).getItemset1()[0] - (c1).getItemset1()[0]);
    }

    public void sortByBodyLength(){
        sortByBodyLength(getRules());
    }

    public void sortByBodyLength(List<AssocRuleWithExceptions> rulesToSort) {
        Collections.sort(rulesToSort, (AssocRuleWithExceptions c1, AssocRuleWithExceptions c2) -> (c2).getItemset1().length - (c1).getItemset1().length);
    }

    public void sortByHeadAndConfidence(List<AssocRuleWithExceptions> rulesToSort) {
        Collections.sort(rulesToSort, new Comparator<AssocRuleWithExceptions>() {
            @Override
            public int compare(AssocRuleWithExceptions o1, AssocRuleWithExceptions o2) {
                int headDiff = o2.getItemset2()[0] - o1.getItemset2()[0];

                if (headDiff != 0)
                    return headDiff;

                int confidenceDiff = (int) ((o2.getConfidence() - o1.getConfidence()) * 2.147483647E9D);
                if (confidenceDiff != 0)
                    return confidenceDiff;

                return o1.getItemset1().length - o2.getItemset1().length;

            }
        });
    }

    public void sort(List<AssocRuleWithExceptions>rules,SortingType type) {
        switch (type) {
            case CONF:
                sortByConfidence(rules);
                break;
            case HEAD:
                sortByHead(rules);
                break;
            case HEAD_CONF:
                sortByHeadAndConfidence(rules);
                break;
            case BODY:
                sortByBodyLength(rules);
                break;
        }
    }

    public String toString(SortingType sortType, boolean hasExceptionOnly) {
        if (sortType == SortingType.HEAD || sortType == SortingType.HEAD_CONF)
            return toStringGroups(sortType, hasExceptionOnly);

        StringBuilder buffer = new StringBuilder();

        // Sort based on type
        sort(this.getRules(),sortType);
        int i = 0;
        int prevHead = -1;
        for (Iterator var5 = this.getRules().iterator(); var5.hasNext(); ++i) {
            AssocRuleWithExceptions rule = (AssocRuleWithExceptions) var5.next();

            // skip if i do not has exceptions
            if (hasExceptionOnly && !rule.hasExceptions())
                continue;

//            if ((type == SortingType.HEAD || type == SortingType.HEAD_CONF) && prevHead != rule.getItemset2()[0]) {
//                buffer.append("**************************************************************************\n");
//            }
            prevHead = rule.getItemset2()[0];
            buffer.append("r");
            buffer.append(i);
            buffer.append(":\t");
            buffer.append(rule.toString());

            buffer.append("\tcov: ");
            buffer.append(String.format("%.5f", rule.getCoverage()));
            buffer.append("\tconf: ");
            buffer.append(String.format("%.5f", rule.getConfidence()));
            buffer.append("\tsupp: ");
            buffer.append(rule.getAbsoluteSupport());
            buffer.append("\n");
            Exceptions exceptionCandidate = ((AssocRuleWithExceptions) rule).getExceptionCandidates();
            if (exceptionCandidate != null && exceptionCandidate.size() > 0) {
                buffer.append("Except:\n");
                buffer.append(exceptionCandidate);
                buffer.append('\n');
            }
            buffer.append('\n');
        }

        return buffer.toString();
    }

    private String toStringGroups(SortingType type, boolean hasExceptionOnly) {
        StringBuilder buffer = new StringBuilder();


        List<HeadGroup> groups = new ArrayList<HeadGroup>(head2Rules.keySet());

        int i = 0;
        for (HeadGroup g : groups) {


            List<AssocRuleWithExceptions> groupRules = new ArrayList<>(head2Rules.get(g));
            buffer.append("*********************************"+ g + "*****************************************\n");
            sort(groupRules,type);
            for (AssocRuleWithExceptions rule : groupRules) {
                if (hasExceptionOnly && !rule.hasExceptions())
                    continue;
                i++;
                buffer.append("r");
                buffer.append(i);
                buffer.append(":\t");
                buffer.append(rule.toString());

                buffer.append("\tcov: ");
                buffer.append(String.format("%.5f", rule.getCoverage()));
                buffer.append("\tconf: ");
                buffer.append(String.format("%.5f", rule.getConfidence()));
                buffer.append("\tsupp: ");
                buffer.append(rule.getAbsoluteSupport());
                buffer.append("\n");
                Exceptions exceptionCandidate = rule.getExceptionCandidates();
                if (exceptionCandidate != null && exceptionCandidate.size() > 0) {
                    buffer.append("Except:\n");
                    buffer.append(exceptionCandidate);
                    buffer.append('\n');
                }
                buffer.append('\n');


            }
            buffer.append("**************************************************************************\n");


        }

        return buffer.toString();

    }

    public Set<AssocRuleWithExceptions> getRules(int[] head, int[] body) {

        Set<AssocRuleWithExceptions> rulesToReturn=new HashSet<>();
        if(head!=null){
            rulesToReturn.addAll(head2Rules.get(new HeadGroup(head)));
        }
        if(body!=null){
            Set<AssocRuleWithExceptions> bodyRules=body2Rules.get(new BodyGroup(body));
            if(head==null)
                return bodyRules;
            else
                return new HashSet<>(Sets.intersection(bodyRules,rulesToReturn));
        }

        return rulesToReturn;
    }


    public enum SortingType {CONF, HEAD, BODY, HEAD_CONF}
}
