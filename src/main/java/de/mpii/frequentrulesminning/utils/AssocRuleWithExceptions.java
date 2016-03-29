package de.mpii.frequentrulesminning.utils;

import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AssocRule;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import de.mpii.frequentrulesminning.RulesEvaluator;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by gadelrab on 2/22/16.
 */
public class AssocRuleWithExceptions {// extends AssocRule {

    private double coverage;

    private double lift;

    private int[] itemset1;
    private int[] itemset2;
    //    private int coverage;
    private int transactionCount;
    private double confidence;
    private Item[] headItems;
    private Item[] bodyItems;
    private Exceptions exceptionCandidates;

    Set<Transaction> bodyTransactions;
    Set<Transaction> headTransactions;
    Set<Transaction> hornRuleTransactions;
    Set<Transaction> safePredictableTransactions;
    private Set<Transaction> predictableTransactions;



    public AssocRuleWithExceptions(int[] itemset1, int[] itemset2, int coverage, int transactionCount, double confidence) {
        this.itemset1 = itemset1;
        this.itemset2 = itemset2;
        this.coverage = coverage;
        this.transactionCount = transactionCount;
        this.confidence = confidence;
    }

    public AssocRuleWithExceptions(Item[] bodyItems, Item[] headItems, int[] itemset1, int[] itemset2, int supportAntecedent, int transactionCount, double confidence, double lift) {
        this(itemset1, itemset2, supportAntecedent, transactionCount, confidence, lift);
        this.bodyItems = bodyItems;
        this.headItems = headItems;
    }


    public AssocRuleWithExceptions(int[] itemset1, int[] itemset2, int supportAntecedent, int transactionCount, double confidence, double lift) {
        this(itemset1, itemset2, supportAntecedent, transactionCount, confidence);

        Arrays.sort(this.getItemset1());
        Arrays.sort(this.getItemset2());
        this.lift = lift;
    }

    public double getRelativeSupport(int databaseSize) {
        return (double) this.transactionCount / (double) databaseSize;
    }

    public int getAbsoluteSupport() {
        return this.transactionCount;
    }

    public double getConfidence() {
        return this.confidence;
    }

    public double getCoverage() {
        return this.coverage;
    }

    public void setCoverage(double coverage) {
        this.coverage = coverage;
    }

    public double getLift() {
        return this.lift;
    }

    public void setBodyItems(Item[] bodyItems) {
        this.bodyItems = bodyItems;
    }

    @Override
    public String toString() {
        String body = Joiner.on(" ").join(bodyItems);
        String head = Joiner.on(" ").join(this.headItems);
        return body + " ==> " + head;


    }

    public Item[] getHeadItems() {
        return headItems;
    }

    public void setHeadItems(Item[] headItems) {
        this.headItems = headItems;
    }

    public Item[] getbodyItems() {
        return bodyItems;
    }

    public Exceptions getExceptionCandidates() {
        return exceptionCandidates;
    }

    public int[] getExceptionsCandidatesInts(){
        return exceptionCandidates.getExceptionsInts();
    }

    public void setExceptionCandidates(List<ExceptionItem> exceptionCandidates) {
        this.exceptionCandidates = new Exceptions(exceptionCandidates);

    }

    public boolean hasExceptions() {
        return (exceptionCandidates == null || exceptionCandidates.size() == 0);
    }

    public int[] getItemset1() {
        return this.itemset1;
    }

    public int[] getItemset2() {
        return this.itemset2;
    }

    public int[] getHead(){
        return getItemset2();
    }

    public int[] getBody(){
        return getItemset1();
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(itemset1)^Arrays.hashCode(itemset2);

    }

    @Override
    public boolean equals(Object obj) {
        AssocRuleWithExceptions casted= (AssocRuleWithExceptions)obj;
        return Arrays.equals(itemset2,casted.itemset2)&&Arrays.equals(itemset1,casted.itemset1);
    }

    public Set<Transaction> getPredicatableTransactions(){
        return predictableTransactions;
    }
//    public Set<Transaction> getPredicatableTransactions(TransactionsDatabase transactionsDB, boolean excludeExceptions) {
//
//
//        Set<Transaction> rulePredictableTransactions=transactionsDB.getTransactions(getBody(),ArrayUtils.addAll(excludeExceptions? getExceptionsCandidatesInts():null,getHead()));
//
//
//        return rulePredictableTransactions;
//
//    }

    /**
     * checks is the rule is subset of another rule. Subset = the body is subset from the other rule body
     * @param superset
     * @return
     */
    public boolean isSubsetOf(AssocRuleWithExceptions superset){
        // if they have different head then they cannot be subset of each other
        if(!Arrays.equals(this.getHead(),superset.getHead()))
            return false;
       return isBodySubsetOf(superset);
    }

    private int getBodyLength() {
        return getItemset1().length;
    }

    public boolean isBodySubsetOf(AssocRuleWithExceptions superset) {
        Set<Integer> intersection= Sets.intersection(ImmutableSet.copyOf(Ints.asList(getItemset1())),(ImmutableSet.copyOf(Ints.asList(superset.getItemset1()))));
        if (intersection.size()== getBodyLength())
            return true;
        else
            return false;
    }

//    public Set<Transaction> getKnownPositiveTransactions(TransactionsDatabase transactionsDB, boolean exclupdeExceptions) {
//       return transactionsDB.getTransactions(ArrayUtils.addAll(getBody(),getHead()),exclupdeExceptions? getExceptionsCandidatesInts():null);
//    }

    public void setLift(double lift) {
        this.lift = lift;
    }


    public void setBodyTransactions(Set<Transaction> bodyTransactions) {
        this.bodyTransactions = bodyTransactions;
    }

    public void setHeadTransactions(Set<Transaction> headTransactions) {
        this.headTransactions = headTransactions;
    }

    public Set<Transaction> getBodyTransactions() {
        return bodyTransactions;
    }

    public void setHornRuleTransactions(Set<Transaction> hornRuleTransactions) {
        this.hornRuleTransactions = hornRuleTransactions;
    }

    public Set<Transaction> getHornRuleTransactions() {
        return hornRuleTransactions;
    }

    public void setSafePredictableTransactions(Set<Transaction> safePredictableTransactions) {
        this.safePredictableTransactions = safePredictableTransactions;
    }

    public Set<Transaction> getHeadTransactions() {
        return headTransactions;
    }

    public void setCoverage(RulesEvaluator evaluator) {
        setCoverage(evaluator.coverage(this));
    }

    public void setLift(RulesEvaluator evaluator) {
        setLift(evaluator.lift(this));
    }

    public void setPredictableTransactions(Set<Transaction> predictableTransactions) {
        this.predictableTransactions = predictableTransactions;
    }

    public Set<Transaction> getSafePredictableTransactions() {
        return safePredictableTransactions;
    }
}
