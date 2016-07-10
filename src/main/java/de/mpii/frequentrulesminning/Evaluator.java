package de.mpii.frequentrulesminning;

import de.mpii.frequentrulesminning.utils.AssocRuleWithExceptions;
import de.mpii.frequentrulesminning.utils.ExceptionItem;
import de.mpii.frequentrulesminning.utils.Transaction;
import de.mpii.frequentrulesminning.utils.TransactionsDatabase;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.util.Set;

/**
 * Created by gadelrab on 3/8/16.
 */
public class Evaluator {


    TransactionsDatabase transactionsDB;
    private boolean countPrediction;
    private boolean useWeights;
//    private boolean useOrder;


    public Evaluator(String transactionsFilePath) throws IOException {
        this(new TransactionsDatabase(transactionsFilePath));


    }

    public Evaluator(TransactionsDatabase transactionsDatabase) {
        this.transactionsDB = transactionsDatabase;
        this.countPrediction = false;
        this.useWeights = false;
//        this.useOrder = false;
    }

//    public  double computeConfidence(Set<Transaction> ruleTransactions, Set<Transaction> bodyTransactions) {
//        double bodySupport = TransactionsDatabase.getTransactionsCount(bodyTransactions);
//        double ruleSupport = TransactionsDatabase.getTransactionsCount(ruleTransactions);
//
//        return computeConfidence(ruleSupport, bodySupport);
//    }

    public static double computeLift(double ruleSupport, double bodySupport, double headSupport) {
        return (((double) ruleSupport) / headSupport) / bodySupport;
    }

//    public double computeCoverage(Set<Transaction> ruleTransactions, Set<Transaction> headTransactions) {
//        double ruleSupport = TransactionsDatabase.getTransactionsCount(ruleTransactions);
//        double headSupport = TransactionsDatabase.getTransactionsCount(headTransactions);
//        return computeCoverage(ruleSupport, headSupport);
//    }

    public double computeConfidence(double ruleSupport, double bodySupport) {
        return (bodySupport==0)? 0:(ruleSupport / bodySupport);
    }

//    public double computeLift(Set<Transaction> ruleTransactions, Set<Transaction> bodyTransactions, Set<Transaction> headTransactions) {
//        double bodySupport = TransactionsDatabase.getTransactionsCount(bodyTransactions);
//        double ruleSupport = TransactionsDatabase.getTransactionsCount(ruleTransactions);
//        double headSupport = TransactionsDatabase.getTransactionsCount(headTransactions);
//        return computeLift(ruleSupport, bodySupport, headSupport);
//    }

    public double computeCoverage(double ruleSupport, double headSupport) {
        return ruleSupport / headSupport;
    }

    public double confidence(AssocRuleWithExceptions rule){
        return confidence( rule, rule.getHornRuleTransactions(), rule.getBodyTransactions(), null);
    }

    public double confidence(AssocRuleWithExceptions rule, ExceptionItem exceptionItem) {

        //Set<Transaction> ruleTransactions = transactionsDB.filterOutTransactionsWith(rule.getHornRuleTransactions(), exceptionItem == null ? null : exceptionItem.getItems(), this.countPrediction);
        //Set<Transaction> bodyTransactions = transactionsDB.filterOutTransactionsWith(rule.getBodyTransactions(), exceptionItem == null ? null : exceptionItem.getItems(),  this.countPrediction);

        Set<Transaction> ruleTransactions = transactionsDB.getTransactions(rule.getBodyAndHead(), ExceptionItem.toArray(exceptionItem), this.countPrediction);
        Set<Transaction> bodyTransactions = transactionsDB.getTransactions(rule.getBody(), ExceptionItem.toArray(exceptionItem), this.countPrediction);

//        if (this.useOrder) {
//            ruleTransactions = TransactionsDatabase.filterBetterQualityRulesPredictions(ruleTransactions, rule);
//            bodyTransactions = TransactionsDatabase.filterBetterQualityRulesPredictions(bodyTransactions, rule);
//        }

        ruleTransactions = filterTransactions(ruleTransactions, rule);
        bodyTransactions = filterTransactions(bodyTransactions, rule);

        return confidence(rule, ruleTransactions, bodyTransactions, exceptionItem);


//      return computeConfidence(ruleTransactions, bodyTransactions);


    }

    public double confidence(AssocRuleWithExceptions rule, Set<Transaction> ruleTransactions, Set<Transaction> bodyTransactions, ExceptionItem exceptionItem) {
        double bodySupport = TransactionsDatabase.getTransactionsCount(bodyTransactions, rule.getBody(), ExceptionItem.toArray(exceptionItem), this.useWeights);
        double ruleSupport = TransactionsDatabase.getTransactionsCount(ruleTransactions, rule.getBodyAndHead(), ExceptionItem.toArray(exceptionItem), this.useWeights);

        return computeConfidence(ruleSupport, bodySupport);
    }

    public double coverage(AssocRuleWithExceptions rule) {
        return coverage( rule,  rule.getHornRuleTransactions(), rule.getHeadTransactions(),  null);
    }

    public double coverage(AssocRuleWithExceptions rule, ExceptionItem exceptionItem) {
//        Set<Transaction> ruleTransactions = transactionsDB.filterOutTransactionsWith(rule.getHornRuleTransactions(), exceptionItem == null ? null : exceptionItem.getItems(), false);
//        Set<Transaction> headTransactions = transactionsDB.filterOutTransactionsWith(rule.getHeadTransactions(), null, false);

        Set<Transaction> ruleTransactions = transactionsDB.getTransactions(rule.getBodyAndHead(), ExceptionItem.toArray(exceptionItem), this.countPrediction);
        Set<Transaction> headTransactions = transactionsDB.getTransactions(rule.getHead(), null, this.countPrediction);

//        if (this.useOrder) {
//            ruleTransactions = TransactionsDatabase.filterBetterQualityRulesPredictions(ruleTransactions, rule);
//            headTransactions = TransactionsDatabase.filterBetterQualityRulesPredictions(headTransactions, rule);
//        }

            ruleTransactions = filterTransactions(ruleTransactions, rule);
            headTransactions = filterTransactions(headTransactions, rule);


        return coverage(rule, ruleTransactions, headTransactions, exceptionItem);

//        return computeCoverage(ruleTransactions, headTransactions);

    }

    public double coverage(AssocRuleWithExceptions rule, Set<Transaction> ruleTransactions, Set<Transaction> headTransactions, ExceptionItem exceptionItem) {
        double ruleSupport = TransactionsDatabase.getTransactionsCount(ruleTransactions, rule.getBodyAndHead(), ExceptionItem.toArray(exceptionItem), this.useWeights);
        double headSupport = TransactionsDatabase.getTransactionsCount(headTransactions, rule.getHead(), ExceptionItem.toArray(exceptionItem), this.useWeights);

        return computeCoverage(ruleSupport, headSupport);
    }

    public double lift(AssocRuleWithExceptions rule) {
        return lift( rule,rule.getHornRuleTransactions(), rule.getBodyTransactions(), rule.getHeadTransactions(),null);
    }

    public double lift(AssocRuleWithExceptions rule, ExceptionItem exceptionItem) {

//        Set<Transaction> bodyTransactions = transactionsDB.filterOutTransactionsWith(rule.getBodyTransactions(), exceptionItem == null ? null : exceptionItem.getItems(), false);
//        Set<Transaction> ruleTransactions = transactionsDB.filterOutTransactionsWith(rule.getHornRuleTransactions(), exceptionItem == null ? null : exceptionItem.getItems(), false);
//        Set<Transaction> headTransactions = transactionsDB.filterOutTransactionsWith(rule.getHeadTransactions(), null, false);


        Set<Transaction> ruleTransactions = transactionsDB.getTransactions(rule.getBodyAndHead(), ExceptionItem.toArray(exceptionItem), this.countPrediction);
        Set<Transaction> bodyTransactions = transactionsDB.getTransactions(rule.getBody(), ExceptionItem.toArray(exceptionItem), this.countPrediction);
        Set<Transaction> headTransactions = transactionsDB.getTransactions(rule.getHead(), null, this.countPrediction);

//        if (this.useOrder) {
//            ruleTransactions = TransactionsDatabase.filterBetterQualityRulesPredictions(ruleTransactions, rule);
//            bodyTransactions = TransactionsDatabase.filterBetterQualityRulesPredictions(bodyTransactions, rule);
//            headTransactions = TransactionsDatabase.filterBetterQualityRulesPredictions(headTransactions, rule);
//        }


            ruleTransactions = filterTransactions(ruleTransactions, rule);
            bodyTransactions = filterTransactions(bodyTransactions, rule);
            headTransactions = filterTransactions(headTransactions, rule);


        return lift(rule, ruleTransactions, bodyTransactions, headTransactions, exceptionItem);

//        return computeLift(ruleTransactions, bodyTransactions, headTransactions);

    }

    public double lift(AssocRuleWithExceptions rule, Set<Transaction> ruleTransactions, Set<Transaction> bodyTransactions, Set<Transaction> headTransactions, ExceptionItem exceptionItem) {
        double ruleSupport = TransactionsDatabase.getTransactionsCount(ruleTransactions, rule.getBodyAndHead(), ExceptionItem.toArray(exceptionItem), this.useWeights);
        double bodySupport = TransactionsDatabase.getTransactionsCount(bodyTransactions, rule.getBody(), ExceptionItem.toArray(exceptionItem), this.useWeights);


        // TODO: we do not want to capture exceptions in head
        // double headSupport = TransactionsDatabase.getTransactionsCount(headTransactions, rule.getHead(), ExceptionItem.toArray(exceptionItem), this.useWeights);
        double headSupport = TransactionsDatabase.getTransactionsCount(headTransactions, rule.getHead(), null, this.useWeights);


        return computeLift(ruleSupport, bodySupport, headSupport);
    }


    /**
     * computes the confidence of (not head <- body, exceptionItem)
     *
     * @param rule
     * @param exceptionItem
     * @return
     */
    public double negativeRuleConfidence(AssocRuleWithExceptions rule, ExceptionItem exceptionItem) {
        // negativeConf (not head <- body, exceptionItem)

//        Set<Transaction> ruleTrnasactions = Sets.difference(rule.getBodyTransactions(), rule.getHeadTransactions());
//        Set<Transaction> bodyWithExceptionTransactions = transactionsDB.filterTransactionsWith(rule.getBodyTransactions(), exceptionItem == null ? null : exceptionItem.getItems(), false);

        Set<Transaction> bodyWithExceptionTransactions = transactionsDB.getTransactions(ArrayUtils.addAll(rule.getBody(), ExceptionItem.toArray(exceptionItem)), null, this.countPrediction);
        Set<Transaction> ruleTransactions = transactionsDB.getTransactions(ArrayUtils.addAll(rule.getBody(), ExceptionItem.toArray(exceptionItem)), rule.getHead(), this.countPrediction);


//        if (this.useOrder) {
//            ruleTransactions = TransactionsDatabase.filterBetterQualityRulesPredictions(ruleTransactions, rule);
//            bodyWithExceptionTransactions = TransactionsDatabase.filterBetterQualityRulesPredictions(bodyWithExceptionTransactions, rule);
//        }
            ruleTransactions =  filterTransactions(ruleTransactions, rule);
            bodyWithExceptionTransactions = filterTransactions(bodyWithExceptionTransactions, rule);


        return negativeRuleConfidence(rule, bodyWithExceptionTransactions, ruleTransactions, exceptionItem);

    }


    public double negativeRuleJaccardCoefficient(AssocRuleWithExceptions rule, ExceptionItem exceptionItem) {
        // negativeConf (not head <- body, exceptionItem)

//        Set<Transaction> ruleTrnasactions = Sets.difference(rule.getBodyTransactions(), rule.getHeadTransactions());
//        Set<Transaction> bodyWithExceptionTransactions = transactionsDB.filterTransactionsWith(rule.getBodyTransactions(), exceptionItem == null ? null : exceptionItem.getItems(), false);

        Set<Transaction> bodyWithExceptionTransactions = transactionsDB.getTransactions(ArrayUtils.addAll(rule.getBody(), ExceptionItem.toArray(exceptionItem)), null, this.countPrediction);
        Set<Transaction> ruleTransactions = transactionsDB.getTransactions(ArrayUtils.addAll(rule.getBody(), ExceptionItem.toArray(exceptionItem)), rule.getHead(), this.countPrediction);
        //TODO check again this.countPrediction can cause problems in case of switching it on in filterOutTransactionsWith(transactions, withoutItems, 0, false/*!withPredictions*/);
        Set<Transaction> notHeadTransactions= transactionsDB.getTransactions(null, rule.getHead(), this.countPrediction);

//        if (this.useOrder) {
//            ruleTransactions = TransactionsDatabase.filterBetterQualityRulesPredictions(ruleTransactions, rule);
//            bodyWithExceptionTransactions = TransactionsDatabase.filterBetterQualityRulesPredictions(bodyWithExceptionTransactions, rule);
//        }
        ruleTransactions =  filterTransactions(ruleTransactions, rule);
        bodyWithExceptionTransactions = filterTransactions(bodyWithExceptionTransactions, rule);
        //TODO error: when removing less quality rules predictions we will loose some transactions
        notHeadTransactions= filterTransactions(notHeadTransactions, rule);

        return negativeRuleJaccardCoefficient(rule, bodyWithExceptionTransactions, notHeadTransactions,ruleTransactions, exceptionItem);

    }

    private double negativeRuleJaccardCoefficient(AssocRuleWithExceptions rule, Set<Transaction> bodyWithExceptionTransactions, Set<Transaction> NotHeadBody,Set<Transaction> ruleTransactions, ExceptionItem exceptionItem) {
        double ruleSupport = TransactionsDatabase.getTransactionsCount(ruleTransactions, ArrayUtils.addAll(rule.getBody(), ExceptionItem.toArray(exceptionItem)), rule.getHead(), this.useWeights);
        double bodySupport = TransactionsDatabase.getTransactionsCount(bodyWithExceptionTransactions, ArrayUtils.addAll(rule.getBody(), ExceptionItem.toArray(exceptionItem)), null, this.useWeights);
        double headSupport = TransactionsDatabase.getTransactionsCount(NotHeadBody,null,rule.getHead() , this.useWeights);

        double bodyAndHeadUnionSupport=bodySupport+headSupport-ruleSupport;
        return computeJaccardCoefficient(ruleSupport,bodyAndHeadUnionSupport);

    }

    public double negativeRuleConfidence(AssocRuleWithExceptions rule, Set<Transaction> bodyWithExceptionTransactions, Set<Transaction> ruleTransactions, ExceptionItem exceptionItem) {
        double ruleSupport = TransactionsDatabase.getTransactionsCount(ruleTransactions, ArrayUtils.addAll(rule.getBody(), ExceptionItem.toArray(exceptionItem)), rule.getHead(), this.useWeights);
        double bodySupport = TransactionsDatabase.getTransactionsCount(bodyWithExceptionTransactions, ArrayUtils.addAll(rule.getBody(), ExceptionItem.toArray(exceptionItem)), null, this.useWeights);



        return computeConfidence(ruleSupport, bodySupport);
    }



    private Set<Transaction> filterTransactions(Set<Transaction> transactions, AssocRuleWithExceptions rule) {
//        if(useOrder){
//            transactions=TransactionsDatabase.filterBetterQualityRulesPredictions(transactions, rule, negated);
//        }
//        else{
        if(this.countPrediction)
            transactions= TransactionsDatabase.filterOtherRulesPredictions(transactions, rule);
//        }
        return transactions;
    }


    public void setCountPrediction(boolean countPrediction) {
        this.countPrediction = countPrediction;
    }


    public void setUseWeights(boolean useWeights) {
        this.useWeights = useWeights;
    }



    public double negativeRuleConfidence(AssocRuleWithExceptions rule) {
        return negativeRuleConfidence(rule,null);
    }



    public double JaccardCoefficient(AssocRuleWithExceptions rule,ExceptionItem exceptionItem){

        Set<Transaction> ruleTransactions = transactionsDB.getTransactions(rule.getBodyAndHead(), ExceptionItem.toArray(exceptionItem), this.countPrediction);
        Set<Transaction> bodyTransactions = transactionsDB.getTransactions(rule.getBody(), ExceptionItem.toArray(exceptionItem), this.countPrediction);
        Set<Transaction> headTransactions = transactionsDB.getTransactions(rule.getHead(), null, this.countPrediction);

//        if (this.useOrder) {
//            ruleTransactions = TransactionsDatabase.filterBetterQualityRulesPredictions(ruleTransactions, rule);
//            bodyTransactions = TransactionsDatabase.filterBetterQualityRulesPredictions(bodyTransactions, rule);
//            headTransactions = TransactionsDatabase.filterBetterQualityRulesPredictions(headTransactions, rule);
//        }

        ruleTransactions = filterTransactions(ruleTransactions, rule);
        bodyTransactions = filterTransactions(bodyTransactions, rule);
        headTransactions =filterTransactions(headTransactions, rule);

        return JaccardCoefficient(rule, ruleTransactions, bodyTransactions, headTransactions, exceptionItem);

    }

    public double JaccardCoefficient(AssocRuleWithExceptions rule, Set<Transaction> ruleTransactions, Set<Transaction> bodyTransactions, Set<Transaction> headTransactions, ExceptionItem exceptionItem) {
        double ruleSupport = TransactionsDatabase.getTransactionsCount(ruleTransactions, rule.getBodyAndHead(), ExceptionItem.toArray(exceptionItem), this.useWeights);
        double bodySupport = TransactionsDatabase.getTransactionsCount(bodyTransactions, rule.getBody(), ExceptionItem.toArray(exceptionItem), this.useWeights);
//        double headSupport = TransactionsDatabase.getTransactionsCount(headTransactions, rule.getHead(), ExceptionItem.toArray(exceptionItem), this.useWeights);
        double headSupport = TransactionsDatabase.getTransactionsCount(headTransactions, rule.getHead(), null, this.useWeights);

        double bodyAndHeadUnionSupport=bodySupport+headSupport-ruleSupport;

        return computeJaccardCoefficient(ruleSupport, bodyAndHeadUnionSupport);
    }

    private double computeJaccardCoefficient(double ruleSupport, double bodyAndHeadUnionSupport) {
        return (bodyAndHeadUnionSupport==0)? 0:(ruleSupport/bodyAndHeadUnionSupport);
    }

    public double JaccardCoefficient(AssocRuleWithExceptions rule) {
        return JaccardCoefficient( rule, rule.getHornRuleTransactions(), rule.getBodyTransactions(),rule.getHeadTransactions(), null);
    }

    public double bodyCoverage(AssocRuleWithExceptions rule) {
        return bodyCoverage(rule,rule.getBodyTransactions()/*,rule.getHeadTransactions()*/,null);
    }

    private double bodyCoverage(AssocRuleWithExceptions rule, Set<Transaction> bodyTransactions, /*Set<Transaction> headTransactions,*/ ExceptionItem exceptionItem) {
        double bodySupport = TransactionsDatabase.getTransactionsCount(bodyTransactions, rule.getBody(), ExceptionItem.toArray(exceptionItem), this.useWeights);
//        double headSupport = TransactionsDatabase.getTransactionsCount(headTransactions, rule.getHead(), ExceptionItem.toArray(exceptionItem), this.useWeights);
//        double headSupport = TransactionsDatabase.getTransactionsCount(headTransactions, rule.getHead(), null, this.useWeights);

        return computeBodyCoverage(bodySupport);//,headSupport);

    }

    private double computeBodyCoverage(double bodySupport){//}, double headSupport) {
        return bodySupport;///headSupport;
    }

    public double bodyCoverage(AssocRuleWithExceptions rule, ExceptionItem exceptionItem) {
        Set<Transaction> bodyTransactions = transactionsDB.getTransactions(rule.getBody(), ExceptionItem.toArray(exceptionItem), this.countPrediction);
//        Set<Transaction> headTransactions = transactionsDB.getTransactions(rule.getHead(), null, this.countPrediction);

        bodyTransactions = filterTransactions(bodyTransactions, rule);
//        headTransactions =filterTransactions(headTransactions, rule);

        return bodyCoverage(rule,bodyTransactions/*,headTransactions*/,exceptionItem);

    }


    public double conviction(AssocRuleWithExceptions rule){
        return conviction(null);
    }


    public double conviction(AssocRuleWithExceptions rule, ExceptionItem exceptionItem){
        Set<Transaction> ruleTransactions = transactionsDB.getTransactions(rule.getBodyAndHead(), ExceptionItem.toArray(exceptionItem), this.countPrediction);
        Set<Transaction> bodyTransactions = transactionsDB.getTransactions(rule.getBody(), ExceptionItem.toArray(exceptionItem), this.countPrediction);
        Set<Transaction> headTransactions = transactionsDB.getTransactions(rule.getHead(), null, this.countPrediction);

//        if (this.useOrder) {
//            ruleTransactions = TransactionsDatabase.filterBetterQualityRulesPredictions(ruleTransactions, rule);
//            bodyTransactions = TransactionsDatabase.filterBetterQualityRulesPredictions(bodyTransactions, rule);
//            headTransactions = TransactionsDatabase.filterBetterQualityRulesPredictions(headTransactions, rule);
//        }

        ruleTransactions = filterTransactions(ruleTransactions, rule);
        bodyTransactions = filterTransactions(bodyTransactions, rule);
        headTransactions =filterTransactions(headTransactions, rule);

        return conviction(rule, ruleTransactions, bodyTransactions, headTransactions, exceptionItem);

    }

    public double conviction(AssocRuleWithExceptions rule, Set<Transaction> ruleTransactions, Set<Transaction> bodyTransactions, Set<Transaction> headTransactions, ExceptionItem exceptionItem) {

        double ruleSupport = TransactionsDatabase.getTransactionsCount(ruleTransactions, rule.getBodyAndHead(), ExceptionItem.toArray(exceptionItem), this.useWeights);
        double bodySupport = TransactionsDatabase.getTransactionsCount(bodyTransactions, rule.getBody(), ExceptionItem.toArray(exceptionItem), this.useWeights);
        double headSupport = TransactionsDatabase.getTransactionsCount(headTransactions, rule.getHead(), null, this.useWeights);

        return computeConviction(ruleSupport,bodySupport,headSupport, transactionsDB.getSize());



    }

    private double computeConviction(double ruleSupport, double bodySupport, double headSupport, double kbSize) {

        double confidence=computeConfidence(ruleSupport,bodySupport);

        return ((kbSize-headSupport)/kbSize)/(1-confidence);

    }


    public double negativeRuleConviction(AssocRuleWithExceptions rule) {
        return negativeRuleConviction(rule,null);
    }

    public double negativeRuleConviction(AssocRuleWithExceptions rule, ExceptionItem exceptionItem){


        Set<Transaction> bodyWithExceptionTransactions = transactionsDB.getTransactions(ArrayUtils.addAll(rule.getBody(), ExceptionItem.toArray(exceptionItem)), null, this.countPrediction);
        Set<Transaction> ruleTransactions = transactionsDB.getTransactions(ArrayUtils.addAll(rule.getBody(), ExceptionItem.toArray(exceptionItem)), rule.getHead(), this.countPrediction);
        //TODO check again this.countPrediction can cause problems in case of switching it on in filterOutTransactionsWith(transactions, withoutItems, 0, false/*!withPredictions*/);
        Set<Transaction> notHeadTransactions= transactionsDB.getTransactions(null, rule.getHead(), this.countPrediction);


        ruleTransactions =  filterTransactions(ruleTransactions, rule);
        bodyWithExceptionTransactions = filterTransactions(bodyWithExceptionTransactions, rule);
        //TODO error: when removing less quality rules predictions we will loose some transactions
        notHeadTransactions= filterTransactions(notHeadTransactions, rule);

        return negativeRuleConviction(rule, bodyWithExceptionTransactions, notHeadTransactions,ruleTransactions, exceptionItem);

    }

    private double negativeRuleConviction(AssocRuleWithExceptions rule, Set<Transaction> bodyWithExceptionTransactions, Set<Transaction> notHeadTransactions, Set<Transaction> ruleTransactions, ExceptionItem exceptionItem) {
        double ruleSupport = TransactionsDatabase.getTransactionsCount(ruleTransactions, ArrayUtils.addAll(rule.getBody(), ExceptionItem.toArray(exceptionItem)), rule.getHead(), this.useWeights);
        double bodySupport = TransactionsDatabase.getTransactionsCount(bodyWithExceptionTransactions, ArrayUtils.addAll(rule.getBody(), ExceptionItem.toArray(exceptionItem)), null, this.useWeights);
        double headSupport = TransactionsDatabase.getTransactionsCount(notHeadTransactions,null,rule.getHead() , this.useWeights);

        return computeConviction(ruleSupport,bodySupport,headSupport, transactionsDB.getSize());

    }


}
