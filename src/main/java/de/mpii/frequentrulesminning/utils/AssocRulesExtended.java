package de.mpii.frequentrulesminning.utils;


import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Created by gadelrab on 2/25/16.
 */
public class AssocRulesExtended implements Iterable<AssocRuleWithExceptions> {


    public double size() {
        return getRules().size();
    }

    public long getRevisedRuleCount() {

        return rules.stream().filter(AssocRuleWithExceptions::hasExceptions).count();
    }


    public enum SortingType {CONF, HEAD, BODY, LIFT, HEAD_CONF, HEAD_LIFT, NEW_LIFT}

    List<AssocRuleWithExceptions> rules;
    SetMultimap<HeadGroup, AssocRuleWithExceptions> head2Rules;
    double confidence;
    private SetMultimap<BodyGroup, AssocRuleWithExceptions> body2Rules;

    public AssocRulesExtended() {
        rules = new ArrayList<>();
        head2Rules = HashMultimap.create();
        body2Rules = HashMultimap.create();

    }

    public List<AssocRuleWithExceptions> getRules() {
        return rules;
    }

    public double getConfidence() {
        return confidence;
    }

    public void addRule(AssocRuleWithExceptions rule) {
        getRules().add(rule);
        HeadGroup h = new HeadGroup(rule.getHead());
        BodyGroup b = new BodyGroup(rule.getItemset1());

        if (!head2Rules.containsKey(h))
            h.setRules(head2Rules.get(h));

        if (!body2Rules.containsKey(b))
            b.setRules(body2Rules.get(b));
        head2Rules.put(h, rule);
        body2Rules.put(b, rule);

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

    public void removeRulesIf(Predicate<AssocRuleWithExceptions> predicate) {

        getRules().removeIf(
                predicate.and(assocRule -> {
                    // remove from the head2Rules map.
                    head2Rules.remove(new HeadGroup(assocRule.getHead()), assocRule);
                    body2Rules.remove(new BodyGroup(assocRule.getBody()), assocRule);
                    return true;
                }));

    }



    public void sortByBodyLength() {
        sort(getRules(),SortingType.BODY);
    }


    public void sort(List<AssocRuleWithExceptions> rules, SortingType type) {
        Comparator<AssocRuleWithExceptions> comparator = getRulesComparator(type);

        Collections.sort(rules, comparator);
    }

    public Comparator<AssocRuleWithExceptions> getRulesComparator(SortingType type) {
        Comparator<AssocRuleWithExceptions> comparator=Comparator.comparing(AssocRuleWithExceptions::getId);
        switch (type) {
            case CONF:
                //sortByConfidence(rules);
                comparator=Comparator.comparing(AssocRuleWithExceptions::getConfidence).reversed();
                break;
            case HEAD:
                //sortByHead(rules);
                comparator=Comparator.comparing(AssocRuleWithExceptions::getHeadItem);
                break;
            case HEAD_CONF:
                //sortByHeadAndConfidence(rules);
                comparator=Comparator.comparing(AssocRuleWithExceptions::getHeadItem).thenComparing(Comparator.comparing(AssocRuleWithExceptions::getConfidence).reversed());
                break;
            case HEAD_LIFT:
//                sortByHeadAndLift(rules);
                comparator=Comparator.comparing(AssocRuleWithExceptions::getHeadItem).thenComparing(Comparator.comparing(AssocRuleWithExceptions::getLift).reversed());
                break;
            case LIFT:
                comparator=Comparator.comparing(AssocRuleWithExceptions::getLift).reversed();
                break;
            case BODY:
                //sortByBodyLength(rules);
                comparator=Comparator.comparing(AssocRuleWithExceptions::getBodyLength).reversed();
                break;
            case NEW_LIFT:
                comparator=Comparator.comparing(AssocRuleWithExceptions::getRevisedLift).reversed();
                break;
        }
        return comparator;
    }


    public String toString(SortingType sortType, boolean hasExceptionOnly) {
        if (sortType == SortingType.HEAD || sortType == SortingType.HEAD_CONF || sortType == sortType.HEAD_LIFT)
            return toStringGroups(sortType, hasExceptionOnly);

        StringBuilder buffer = new StringBuilder();

        // Sort based on type
        sort(this.getRules(), sortType);
        int i = 0;
//        int prevHead = -1;
        for (Iterator var5 = this.getRules().iterator(); var5.hasNext(); ++i) {
            AssocRuleWithExceptions rule = (AssocRuleWithExceptions) var5.next();

            // skip if i do not has exceptions
            if (hasExceptionOnly && !rule.hasExceptions())
                continue;


            addRuleToPrintBuffer(buffer, i, rule);
        }

        return buffer.toString();
    }

    private String toStringGroups(SortingType type, boolean hasExceptionOnly) {
        StringBuilder buffer = new StringBuilder();


        List<HeadGroup> groups = new ArrayList<HeadGroup>(head2Rules.keySet());

        int i = 0;
        for (HeadGroup g : groups) {


            List<AssocRuleWithExceptions> groupRules = new ArrayList<>(head2Rules.get(g));
            buffer.append("*********************************" + g + "*****************************************\n");
            sort(groupRules, type);
            for (AssocRuleWithExceptions rule : groupRules) {
                if (hasExceptionOnly && !rule.hasExceptions())
                    continue;
                i++;
                addRuleToPrintBuffer(buffer, rule.getId(), rule);

            }
            buffer.append("**************************************************************************\n");


        }

        return buffer.toString();

    }

    private void addRuleToPrintBuffer(StringBuilder buffer, int id, AssocRuleWithExceptions rule) {
        buffer.append("r");
        buffer.append(id);
        buffer.append(":\t");
        buffer.append(rule.toString());

        buffer.append("\tlift: ");
        buffer.append(String.format("%.5f", rule.getLift()));
        buffer.append("\tcov: ");
        buffer.append(String.format("%.5f", rule.getCoverage()));
        buffer.append("\tconf: ");
        buffer.append(String.format("%.5f", rule.getConfidence()));

        buffer.append("\tPosNegConf: ");
        buffer.append(String.format("%.5f", rule.getPosNegConfidence()));


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


    /**
     * to retrieve set of rules based on head and body
     * @param head
     * @param body
     * @return
     */
    public Set<AssocRuleWithExceptions> getRules(int[] head, int[] body) {

        Set<AssocRuleWithExceptions> rulesToReturn = new HashSet<>();
        if (head != null) {
            rulesToReturn.addAll(head2Rules.get(new HeadGroup(head)));
        }
        if (body != null) {
            Set<AssocRuleWithExceptions> bodyRules = body2Rules.get(new BodyGroup(body));
            if (head == null)
                return bodyRules;
            else
                return new HashSet<>(Sets.intersection(bodyRules, rulesToReturn));
        }

        return rulesToReturn;
    }


    public String toStringPrASP(SortingType sortType, int exceptionsNumber,boolean showRulesWithExceptionsOnly) {

        sort(this.getRules(), sortType);

        StringBuilder buffer = new StringBuilder();

        for(AssocRuleWithExceptions r:getRules()){
            if(showRulesWithExceptionsOnly&&!r.hasExceptions())
                continue;
            buffer.append(r.toStringPrASPWithWeight(exceptionsNumber));
            buffer.append('\n');
        }

        buffer.append('\n');
        return buffer.toString();

    }

    public String toStringdlvSafe(SortingType sortType, int exceptionsNumber, boolean rulesWithExceptionsOnly, boolean positive) {

        sort(this.getRules(), sortType);

        StringBuilder buffer = new StringBuilder();

        for(AssocRuleWithExceptions r:getRules()){
            if(rulesWithExceptionsOnly &&!r.hasExceptions())
                continue;
            buffer.append(r.toDLVSafe(exceptionsNumber,positive));
            buffer.append('\n');
        }


        return buffer.toString();

    }

    public String toStringdlvConflict() {
        System.out.println("Number of Heads: "+head2Rules.keySet().size());
        StringBuilder buffer = new StringBuilder();

        for(HeadGroup head: head2Rules.keySet()){
            String dlvPredicate=head.asDLVString("X");
            String conflictHead="conflict_"+dlvPredicate;
            String conflictRule=conflictHead+" :- not_"+dlvPredicate+", "+ dlvPredicate+".";
//            String numberConflictRule="number_of_conflicts_"+head.asDLVString("Y")+" :- #count{X : "+conflictHead+"} = Y.";
            buffer.append(conflictRule);
            buffer.append('\n');
//            buffer.append(numberConflictRule);
//            buffer.append('\n');
        }


        return buffer.toString();

    }


    public DoubleSummaryStatistics getConfidenceStats(int k, boolean exception, boolean revisedOnly){
        if(exception)
            return rules.stream().filter((r)-> (!revisedOnly)||r.hasExceptions()).limit(k).mapToDouble(AssocRuleWithExceptions::getRevisedConfidence).summaryStatistics();
        else
            return rules.stream().filter((r)-> (!revisedOnly)||r.hasExceptions()).limit(k).mapToDouble(AssocRuleWithExceptions::getConfidence).summaryStatistics();

    }

    public DoubleSummaryStatistics getConfidenceStats2(int k, boolean exception, boolean revisedOnly){
        if(exception)
            return rules.stream().limit(k).filter((r)-> (!revisedOnly)||r.hasExceptions()).mapToDouble(AssocRuleWithExceptions::getRevisedConfidence).summaryStatistics();
        else
            return rules.stream().limit(k).filter((r)-> (!revisedOnly)||r.hasExceptions()).mapToDouble(AssocRuleWithExceptions::getConfidence).summaryStatistics();

    }


    public DoubleSummaryStatistics getConfidenceDiffStats(int k, boolean revisedOnly){

        return rules.stream().limit(k).filter((r)-> (!revisedOnly)||r.hasExceptions()).mapToDouble((r)->r.getRevisedConfidence()-r.getConfidence()).summaryStatistics();

    }


    public DoubleSummaryStatistics getJaccardCoefficientStats(int k, boolean withException, boolean revisedOnly){
        if(withException)
            return rules.stream().limit(k).filter((r)-> (!revisedOnly)||r.hasExceptions()).mapToDouble(AssocRuleWithExceptions::getRevisedJaccardCoefficient).summaryStatistics();
        else
            return rules.stream().limit(k).filter((r)-> (!revisedOnly)||r.hasExceptions()).mapToDouble(AssocRuleWithExceptions::getJaccardCoefficient).summaryStatistics();
    }

    public DoubleSummaryStatistics getJaccardDiffStats(int k, boolean revisedOnly){

            return rules.stream().limit(k).filter((r)-> (!revisedOnly)||r.hasExceptions()).mapToDouble((r)->r.getRevisedJaccardCoefficient()-r.getJaccardCoefficient()).summaryStatistics();

    }

    public DoubleSummaryStatistics getLiftStats(int k, boolean withException, boolean revisedOnly){
        if(withException)
            return rules.stream().limit(k).filter((r)-> (!revisedOnly)||r.hasExceptions()).mapToDouble(AssocRuleWithExceptions::getRevisedLift).summaryStatistics();
        else
            return rules.stream().limit(k).filter((r)-> (!revisedOnly)||r.hasExceptions()).mapToDouble(AssocRuleWithExceptions::getLift).summaryStatistics();
    }

    public DoubleSummaryStatistics getLiftDiffStats(int k,boolean revisedOnly){

            return rules.stream().limit(k).filter((r)-> (!revisedOnly)||r.hasExceptions()).mapToDouble((r)->r.getRevisedLift()-r.getLift()).summaryStatistics();

    }




    //    private void sortByLift(List<AssocRuleWithExceptions> rulesToSort) {
//        Collections.sort(rulesToSort, Comparator.comparing(AssocRuleWithExceptions::getLift).reversed());
//
//    }

//    public void sortByConfidence(List<AssocRuleWithExceptions> rulesToSort) {
//        Collections.sort(rulesToSort, Comparator.comparing(AssocRuleWithExceptions::getConfidence).reversed());
//
//
//    }


//    public AssocRulesExtended(String name) {
//        super(name);
//    }

    //    public void sortByHead(List<AssocRuleWithExceptions> rulesToSort) {
//        Collections.sort(rulesToSort, ( c1,  c2) -> (c2).getHead()[0] - (c1).getHead()[0]);
//    }


//    public void sortByBodyLength(List<AssocRuleWithExceptions> rulesToSort) {
//        Collections.sort(rulesToSort, ( c1,  c2) -> (c2).getItemset1().length - (c1).getItemset1().length);
//    }

//    public void sortByHeadAndConfidence(List<AssocRuleWithExceptions> rulesToSort) {
//
//        Collections.sort(rulesToSort, new Comparator<AssocRuleWithExceptions>() {
//            @Override
//            public int compare(AssocRuleWithExceptions o1, AssocRuleWithExceptions o2) {
//                int headDiff = o2.getItemset2()[0] - o1.getItemset2()[0];
//
//                if (headDiff != 0)
//                    return headDiff;
//
//                int confidenceDiff = Double.compare(o2.getConfidence(), o1.getConfidence());
//                if (confidenceDiff != 0)
//                    return confidenceDiff;
//
//                return o1.getItemset1().length - o2.getItemset1().length;
//
//            }
//        });
//    }
//
//    public void sortByHeadAndLift(List<AssocRuleWithExceptions> rulesToSort) {
//
//        Collections.sort(rulesToSort, new Comparator<AssocRuleWithExceptions>() {
//            @Override
//            public int compare(AssocRuleWithExceptions o1, AssocRuleWithExceptions o2) {
//                int headDiff = o2.getItemset2()[0] - o1.getItemset2()[0];
//
//                if (headDiff != 0)
//                    return headDiff;
//
//                int confidenceDiff = Double.compare(o2.getLift(), o1.getLift());
//                if (confidenceDiff != 0)
//                    return confidenceDiff;
//
//                return o1.getItemset1().length - o2.getItemset1().length;
//
//            }
//        });
//    }




    //    public void evaluateIndividuals(Evaluator evaluator) {
//        // Individual Rules Evaluation
//        getRules().stream().parallel().forEach((r) -> {
////            r.setCoverage(evaluator);
////            r.setLift(evaluator);
//            r.getExceptionCandidates().forEach((ex) -> {
////                ex.setCoverage(evaluator.coverage(r, ex));
////                ex.setConfidence(evaluator.confidence(r, ex));
////                ex.setLift(evaluator.lift(r,ex));
////                ex.setInvertedConflictScore(evaluator.invertedConflictScore(r,ex,this.getRules(ex.getItems(),null)));
//            });
//
//        });
//
//    }

//    public void evaluateHeadGroups(Evaluator evaluator) {
//
//        System.out.println("headGroups: "+head2Rules.keySet().size());;
//        head2Rules.keySet().parallelStream().forEach((key) -> evaluateHeadGroup(evaluator, key));
//
//
//    }

//    private void evaluateHeadGroup(Evaluator evaluator, HeadGroup key) {
//        evaluator.coverage(key,head2Rules.get(key));
//        evaluator.confidence(key,head2Rules.get(key));
////        evaluator.lift(key,head2Rules.get(key));
//        evaluator.conflict(key,head2Rules.get(key),this);
//
//
//
////        if(coverage==0)
////            System.out.println("\t"+key);
////        key.setCoverage(coverage);
//

    //    }
}
