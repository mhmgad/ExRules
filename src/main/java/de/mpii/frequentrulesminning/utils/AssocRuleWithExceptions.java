package de.mpii.frequentrulesminning.utils;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
//    Set<Transaction> safePredictableTransactions;
    private Set<Transaction> predictableTransactions;
    /**
     * This is the confidence of not head <- body
     */
    private double negConfidence;
    private double jaccardCoefficient;
    private double revisedConfidence=-1;
    private double revisedLift=-1;
    private double revisedJaccardCoefficient=-1;
    private int id;
    private static int nextID;
//    private int[] bodyAndHead;


    public AssocRuleWithExceptions(int id,int[] itemset1, int[] itemset2, int coverage, int transactionCount, double confidence) {
        this.id=id;
        this.itemset1 = itemset1;
        this.itemset2 = itemset2;
        this.coverage = coverage;
        this.transactionCount = transactionCount;
        this.confidence = confidence;

    }

    public AssocRuleWithExceptions(int id,Item[] bodyItems, Item[] headItems, int[] itemset1, int[] itemset2, int supportAntecedent, int transactionCount, double confidence, double lift) {
        this(id,itemset1, itemset2, supportAntecedent, transactionCount, confidence, lift);
        this.bodyItems = bodyItems;
        this.headItems = headItems;
    }


    public AssocRuleWithExceptions(int id,int[] itemset1, int[] itemset2, int supportAntecedent, int transactionCount, double confidence, double lift) {
        this(id,itemset1, itemset2, supportAntecedent, transactionCount, confidence);

        Arrays.sort(this.getItemset1());
        Arrays.sort(this.getItemset2());
        this.lift = lift;
    }

    public synchronized static int getNextID() {
        nextID++;
        return nextID;
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
        return head + " <== " + body;


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

    public int[] getExceptionsCandidatesInts(double minimumSupport){
        return exceptionCandidates.getExceptionsInts(minimumSupport);
    }

    public void setExceptionCandidates(List<ExceptionItem> exceptionCandidates) {
        this.exceptionCandidates = new Exceptions(exceptionCandidates);

    }

    public boolean hasExceptions() {
        return (exceptionCandidates != null && exceptionCandidates.size()> 0);
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

//    public Set<Transaction> getPredicatableTransactions(){
//        return predictableTransactions;
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

    public int getBodyLength() {
        return getItemset1().length;
    }

    public boolean isBodySubsetOf(AssocRuleWithExceptions superset) {
        Set<Integer> intersection= Sets.intersection(ImmutableSet.copyOf(Ints.asList(getItemset1())),(ImmutableSet.copyOf(Ints.asList(superset.getItemset1()))));
        if (intersection.size()== getBodyLength())
            return true;
        else
            return false;
    }

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

//    public void setSafePredictableTransactions(Set<Transaction> safePredictableTransactions) {
//        this.safePredictableTransactions = safePredictableTransactions;
//    }

    public Set<Transaction> getHeadTransactions() {
        return headTransactions;
    }

//    public void setCoverage(Evaluator evaluator) {
//        setCoverage(evaluator.coverage(this));
//    }
//
//    public void setLift(Evaluator evaluator) {
//        setLift(evaluator.lift(this));
//    }

    public void setPredictableTransactions(Set<Transaction> predictableTransactions) {
        this.predictableTransactions = predictableTransactions;
    }

//    public Set<Transaction> getSafePredictableTransactions() {
//        return safePredictableTransactions;
//    }

    public Set<Transaction> getPredictableTransactions() {
        return predictableTransactions;
    }


    public double getOrderingQuality() {
        return this.getLift();
    }

    public int[] getBodyAndHead() {
        return ArrayUtils.addAll(this.getBody(),this.getHead());
    }

    public double getPosNegConfidence() {
        return (getConfidence() + getNegConfidence())/2;
    }

    public double getNegConfidence() {
        return negConfidence;
    }

    public void setNegConfidence(double negConfidence){
        this.negConfidence=negConfidence;
    }


    public List<ExceptionItem> getExceptionCandidates(double minimumException) {
        return getExceptionCandidates().getExceptions(minimumException);
    }

    public String toStringPrASP(int numberOfEceptions) {
        String body = Joiner.on(", ").join(itemsToStringPrASP(getbodyItems(),false));
        String negBody= (hasExceptions())? Joiner.on(", ").join(itemsToStringPrASP(getTopKExceptionsItem(numberOfEceptions),true)):"";
        String head = Joiner.on(" ").join(itemsToStringPrASP(getHeadItems(),false));
        return head+" :- "+body+ (!negBody.isEmpty()? (", "+negBody):"")+".";
    }

    public Item[] getTopKExceptionsItem(int numberOfEceptions) {
        return getExceptionCandidates().getTopKExceptionsItem(numberOfEceptions);
    }

    public ExceptionItem getTopException() {
        if(hasExceptions())
            return getExceptionCandidates().getTopException();
        else
            return null;
    }

    public String toStringPrASPWithWeight(int numberOfEceptions) {

        return "["+String.format("%.5f",getRevisedConfidence())+"] "+toStringPrASP(numberOfEceptions);
    }

    private List<String> itemsToStringPrASP(Item[] items, boolean negated) {
       return  Arrays.stream(items).map((item)-> (negated? "not ":"")+item.toStringPrASP()).collect(Collectors.toList());
    }

    public void setJaccardCoefficient(double jaccardCoefficient) {
        this.jaccardCoefficient = jaccardCoefficient;
    }


//    public double getConfidenceWithTopException(){
//        if(hasExceptions())
//            return exceptionCandidates.getTopException().getConfidence();
//        else
//            return getConfidence();
//    }

//    public double getJaccardCoefficientWithTopException() {
//        if (hasExceptions())
//            return exceptionCandidates.getTopException().getJaccardCoefficient();
//        else
//            return getJaccardCoefficient();
//    }

    public double getJaccardCoefficient() {
        return jaccardCoefficient;
    }

//    public  double getLiftWithTopException() {
//
//        if (hasExceptions())
//            return exceptionCandidates.getTopException().getLift();
//        else
//            return getLift();
//    }

    public void setRevisedConfidence(double revisedConfidence) {
        this.revisedConfidence = revisedConfidence;
    }

    public void setRevisedLift(double revisedLift) {
        this.revisedLift = revisedLift;
    }

    public void setRevisedJaccardCoefficient(double revisedJaccardCoefficient) {
        this.revisedJaccardCoefficient = revisedJaccardCoefficient;
    }

    public double getRevisedConfidence() {
        if (revisedConfidence==-1)
            return getConfidence();
        return revisedConfidence;
    }

    public double getRevisedLift() {
        if (revisedLift==-1)
            return getLift();
        return revisedLift;
    }

    public double getRevisedJaccardCoefficient() {
        if (revisedJaccardCoefficient==-1)
            return getJaccardCoefficient();
        return revisedJaccardCoefficient;
    }

    public int getId() {
        return this.id;

    }

    public int getHeadItem(){
        return getHead()[0];
    }


    public String toDLVSafe(int numberOfEceptions, boolean positiveRule) {
        String body = Joiner.on(", ").join(itemsToStringDLV(getbodyItems(), false));
        String negBody;
        if (positiveRule) {// negated part should be the same
            negBody = (hasExceptions()) ? ", "+Joiner.on(", ").join(itemsToStringDLV(getTopKExceptionsItem(numberOfEceptions), true)) : "";
        }
        else
        {// add exceptions ass positive and
            negBody = ", "+((hasExceptions()) ? Joiner.on(", ").join(itemsToStringDLV(getTopKExceptionsItem(numberOfEceptions), false)) : " dummy(X)");
        }
        String head = Joiner.on(" ").join(itemsToStringDLV(getHeadItems(),!positiveRule));
        head=(positiveRule)? head:(head.trim().replace(' ','_'));
        return head+" :- "+body+negBody+".";
    }

    private List<String> itemsToStringDLV(Item[] items, boolean negated) {
        return  Arrays.stream(items).map((item)-> (negated? "not ":"")+item.todlvSafe("X")).collect(Collectors.toList());

    }

}
