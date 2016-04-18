package de.mpii.frequentrulesminning;

import com.google.common.collect.Sets;
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
    private boolean useOrder;


    public Evaluator(String transactionsFilePath) throws IOException {
        this(new TransactionsDatabase(transactionsFilePath));


    }

    public Evaluator(TransactionsDatabase transactionsDatabase) {
        this.transactionsDB = transactionsDatabase;
        this.countPrediction = false;
        this.useWeights = false;
        this.useOrder = false;
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
        return ruleSupport / bodySupport;
    }

//    public double computeLift(Set<Transaction> ruleTransactions, Set<Transaction> bodyTransactions, Set<Transaction> headTransactions) {
//        double bodySupport = TransactionsDatabase.getTransactionsCount(bodyTransactions);
//        double ruleSupport = TransactionsDatabase.getTransactionsCount(ruleTransactions);
//        double headSupport = TransactionsDatabase.getTransactionsCount(headTransactions);
//        return computeLift(ruleSupport, bodySupport, headSupport);
//    }

    public double computeCoverage(double ruleSupport, double headSupport) {
        return ((double) ruleSupport) / headSupport;
    }

    public double confidence(AssocRuleWithExceptions rule, ExceptionItem exceptionItem) {

        //Set<Transaction> ruleTransactions = transactionsDB.filterOutTransactionsWith(rule.getHornRuleTransactions(), exceptionItem == null ? null : exceptionItem.getItems(), this.countPrediction);
        //Set<Transaction> bodyTransactions = transactionsDB.filterOutTransactionsWith(rule.getBodyTransactions(), exceptionItem == null ? null : exceptionItem.getItems(),  this.countPrediction);

        Set<Transaction> ruleTransactions = transactionsDB.getTransactions(rule.getBodyAndHead(), ExceptionItem.toArray(exceptionItem), this.countPrediction);
        Set<Transaction> bodyTransactions = transactionsDB.getTransactions(rule.getBody(), ExceptionItem.toArray(exceptionItem), this.countPrediction);

        if (this.useOrder) {
            ruleTransactions = TransactionsDatabase.filterBetterQualityRules(ruleTransactions, rule);
            bodyTransactions = TransactionsDatabase.filterBetterQualityRules(bodyTransactions, rule);
        }

        double bodySupport = TransactionsDatabase.getTransactionsCount(bodyTransactions, rule.getBody(), ExceptionItem.toArray(exceptionItem), this.useWeights);
        double ruleSupport = TransactionsDatabase.getTransactionsCount(ruleTransactions, rule.getBodyAndHead(), ExceptionItem.toArray(exceptionItem), this.useWeights);

        return computeConfidence(ruleSupport, bodySupport);


//      return computeConfidence(ruleTransactions, bodyTransactions);


    }

    public double coverage(AssocRuleWithExceptions rule) {
        return coverage(rule, null);
    }

    public double coverage(AssocRuleWithExceptions rule, ExceptionItem exceptionItem) {
//        Set<Transaction> ruleTransactions = transactionsDB.filterOutTransactionsWith(rule.getHornRuleTransactions(), exceptionItem == null ? null : exceptionItem.getItems(), false);
//        Set<Transaction> headTransactions = transactionsDB.filterOutTransactionsWith(rule.getHeadTransactions(), null, false);

        Set<Transaction> ruleTransactions = transactionsDB.getTransactions(rule.getBodyAndHead(), ExceptionItem.toArray(exceptionItem), this.countPrediction);
        Set<Transaction> headTransactions = transactionsDB.getTransactions(rule.getHead(), null, this.countPrediction);

        if (this.useOrder) {
            ruleTransactions = TransactionsDatabase.filterBetterQualityRules(ruleTransactions, rule);
            headTransactions = TransactionsDatabase.filterBetterQualityRules(headTransactions, rule);
        }

        double ruleSupport = TransactionsDatabase.getTransactionsCount(ruleTransactions, rule.getBodyAndHead(), ExceptionItem.toArray(exceptionItem), this.useWeights);
        double headSupport = TransactionsDatabase.getTransactionsCount(headTransactions, rule.getHead(), ExceptionItem.toArray(exceptionItem), this.useWeights);

        return computeCoverage(ruleSupport, headSupport);

//        return computeCoverage(ruleTransactions, headTransactions);

    }

    public double lift(AssocRuleWithExceptions rule) {
        return lift(rule, null);
    }

    public double lift(AssocRuleWithExceptions rule, ExceptionItem exceptionItem) {

//        Set<Transaction> bodyTransactions = transactionsDB.filterOutTransactionsWith(rule.getBodyTransactions(), exceptionItem == null ? null : exceptionItem.getItems(), false);
//        Set<Transaction> ruleTransactions = transactionsDB.filterOutTransactionsWith(rule.getHornRuleTransactions(), exceptionItem == null ? null : exceptionItem.getItems(), false);
//        Set<Transaction> headTransactions = transactionsDB.filterOutTransactionsWith(rule.getHeadTransactions(), null, false);


        Set<Transaction> ruleTransactions = transactionsDB.getTransactions(rule.getBodyAndHead(), ExceptionItem.toArray(exceptionItem), this.countPrediction);
        Set<Transaction> bodyTransactions = transactionsDB.getTransactions(rule.getBody(), ExceptionItem.toArray(exceptionItem), this.countPrediction);
        Set<Transaction> headTransactions = transactionsDB.getTransactions(rule.getHead(), null, this.countPrediction);

        if (this.useOrder) {
            ruleTransactions = TransactionsDatabase.filterBetterQualityRules(ruleTransactions, rule);
            bodyTransactions = TransactionsDatabase.filterBetterQualityRules(bodyTransactions, rule);
            headTransactions = TransactionsDatabase.filterBetterQualityRules(headTransactions, rule);
        }

        double ruleSupport = TransactionsDatabase.getTransactionsCount(ruleTransactions, rule.getBodyAndHead(), ExceptionItem.toArray(exceptionItem), this.useWeights);
        double bodySupport = TransactionsDatabase.getTransactionsCount(bodyTransactions, rule.getBody(), ExceptionItem.toArray(exceptionItem), this.useWeights);
        double headSupport = TransactionsDatabase.getTransactionsCount(headTransactions, rule.getHead(), ExceptionItem.toArray(exceptionItem), this.useWeights);


        return computeLift(ruleSupport, bodySupport, headSupport);

//        return computeLift(ruleTransactions, bodyTransactions, headTransactions);

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


        if (this.useOrder) {
            ruleTransactions = TransactionsDatabase.filterBetterQualityRules(ruleTransactions, rule);
            bodyWithExceptionTransactions = TransactionsDatabase.filterBetterQualityRules(bodyWithExceptionTransactions, rule);
        }

        double ruleSupport = TransactionsDatabase.getTransactionsCount(ruleTransactions, ArrayUtils.addAll(rule.getBody(), ExceptionItem.toArray(exceptionItem)), rule.getHead(), this.useWeights);
        double bodySupport = TransactionsDatabase.getTransactionsCount(bodyWithExceptionTransactions, ArrayUtils.addAll(rule.getBody(), ExceptionItem.toArray(exceptionItem)), null, this.useWeights);

        return computeConfidence(ruleSupport, bodySupport);

    }


    public void setCountPrediction(boolean countPrediction) {
        this.countPrediction = countPrediction;
    }


    public void setUseWeights(boolean useWeights) {
        this.useWeights = useWeights;
    }

    public void setUseOrder(boolean useOrder) {
        this.useOrder = useOrder;
    }

    /**
     * Computes the average confidence of (head <-body, not exceptionItem) and (not head <- body, exceptionItem)
     *
     * @param rule
     * @param exceptionItem
     */
    public double computePosNegConfidence(AssocRuleWithExceptions rule, ExceptionItem exceptionItem) {

        double positiveConf = this.confidence(rule, exceptionItem);

        double negConf = negativeRuleConfidence(rule, exceptionItem);

        return computePosNegConfidence(positiveConf,negConf);


    }

    public static double computePosNegConfidence(double positiveConf,double negConf){
        return (positiveConf + negConf) / 2;
    }


    public double negativeRuleConfidence(AssocRuleWithExceptions rule) {
        return negativeRuleConfidence(rule,null);
    }



    public double JaccardCoefficient(AssocRuleWithExceptions rule,ExceptionItem exceptionItem){

        Set<Transaction> ruleTransactions = transactionsDB.getTransactions(rule.getBodyAndHead(), ExceptionItem.toArray(exceptionItem), this.countPrediction);
        Set<Transaction> bodyTransactions = transactionsDB.getTransactions(rule.getBody(), ExceptionItem.toArray(exceptionItem), this.countPrediction);
        Set<Transaction> headTransactions = transactionsDB.getTransactions(rule.getHead(), null, this.countPrediction);

        if (this.useOrder) {
            ruleTransactions = TransactionsDatabase.filterBetterQualityRules(ruleTransactions, rule);
            bodyTransactions = TransactionsDatabase.filterBetterQualityRules(bodyTransactions, rule);
            headTransactions = TransactionsDatabase.filterBetterQualityRules(headTransactions, rule);
        }

        double ruleSupport = TransactionsDatabase.getTransactionsCount(ruleTransactions, rule.getBodyAndHead(), ExceptionItem.toArray(exceptionItem), this.useWeights);
        double bodySupport = TransactionsDatabase.getTransactionsCount(bodyTransactions, rule.getBody(), ExceptionItem.toArray(exceptionItem), this.useWeights);
        double headSupport = TransactionsDatabase.getTransactionsCount(headTransactions, rule.getHead(), ExceptionItem.toArray(exceptionItem), this.useWeights);


        double bodyAndHeadUnionSupport=bodySupport+headSupport-ruleSupport;

        return computeJaccardCoefficient(ruleSupport, bodyAndHeadUnionSupport);

    }

    private double computeJaccardCoefficient(double ruleSupport, double bodyAndHeadUnionSupport) {
        return ruleSupport/bodyAndHeadUnionSupport;
    }

    public double JaccardCoefficient(AssocRuleWithExceptions rule) {
        return JaccardCoefficient(rule,null);
    }


//TODO Commented as they do not handle weights

    //    /**
    //     * Coverage of set of rules R with same head using coverage = (1/E) * ((supp(r1)* supp(r2)..sup(rn))^(1/|R|) )
    //     *
    //     * @param head
    //     * @param rules
    //     * @param withExceptions
    //     */
//    public void groupCoverage(HeadGroup head, Collection<AssocRuleWithExceptions> rules, boolean withExceptions) {
//
//        //TODO Exception Handling is not yet implemented
//        Set<Transaction> containsBodyOrHead = transactionsDB.getTransactions(head.getHeadItems(), null, false);
//
//        //ArrayList<Integer> rulesTransactionsCount=new ArrayList<>();
//        BigDecimal coverageMultiplication = BigDecimal.ONE;
//        for (AssocRuleWithExceptions rule : rules) {
//
//
//            Set<Transaction> bodyTransactions = rule.getBodyTransactions();
//            double bodyTransactionsCount = TransactionsDatabase.getTransactionsCount(bodyTransactions);
//
//            if (bodyTransactionsCount == 0 || coverageMultiplication.equals(BigDecimal.ZERO))
//                System.out.println(rule + " " + bodyTransactions.size() + " " + bodyTransactionsCount + " " + coverageMultiplication);
//            coverageMultiplication.multiply(BigDecimal.valueOf(bodyTransactionsCount));
//
//
//            // combine to all bodies transactions
//
//            containsBodyOrHead = Sets.union(containsBodyOrHead, bodyTransactions);
//
//
//        }
//
//
//        //int coverageMultiplication=rulesTransactionsCount.stream().reduce((i,j)-> i*j).get();
//        double allTransactionsCount = TransactionsDatabase.getTransactionsCount(containsBodyOrHead);
//        int rSize = rules.size();
//
//        double groupCoverage = Math.pow((coverageMultiplication.doubleValue()), (1.0D / (double) rSize)) / ((double) allTransactionsCount);
////        double groupCoverage= coverageMultiplication.pow((1.0D/(double)rSize))/((double)allTransactionsCount);
//        if (groupCoverage == 0 || allTransactionsCount == 0)
//            System.out.print("groupCoverage = " + groupCoverage + " coverageMultiplication = " + coverageMultiplication + " rSize: = " + rSize + " all Transactions Count = " + allTransactionsCount);
//        head.setCoverage(groupCoverage);
////        head.setAllTransactionsCount(allTransactionsCount);
//    }


//    public void groupCoverage(HeadGroup head, Collection<AssocRuleWithExceptions> assocRuleWithExceptionses) {
//        groupCoverage(head, assocRuleWithExceptionses, false);
//
//    }

//    public double groupConfidence(HeadGroup head, Collection<AssocRuleWithExceptions> assocRuleWithExceptionses) {
//        return groupConfidence(head, assocRuleWithExceptionses, false);
//    }


//    /**
//     * Measures a set of rule confidence by computing  the support of all rules union over the union of the bodies supp(all rules)/supp(bodies)
//     *
//     * @param head
//     * @param rules
//     * @param withExceptions
//     */
//    public double groupConfidence(HeadGroup head, Collection<AssocRuleWithExceptions> rules, boolean withExceptions) {
//
//        Set<Transaction> containsBody = new HashSet<>();
//
//
//        for (AssocRuleWithExceptions rule : rules) {
////            Set<Transaction> bodyTransactions = transactionsDB.getTransactions(rule.getItemset1(), null);
//            Set<Transaction> bodyTransactions = rule.getBodyTransactions();
//            //containsBody.addAll(bodyTransactions);
//            containsBody = Sets.union(containsBody, bodyTransactions);
//        }
//
//        double bodyTransactionsCount = TransactionsDatabase.getTransactionsCount(containsBody);
//
//        double rulesBodyandHeadCount = TransactionsDatabase.getTransactionsCount(transactionsDB.filterTransactionsWith(containsBody, head.getHeadItems(), false));
//
//        //head.setConfidence((double) rulesBodyandHeadCount / bodyTransactionsCount);
//        return (double) rulesBodyandHeadCount / bodyTransactionsCount;
//
//    }


//    public void exceptionsConflictScore(AssocRuleWithExceptions targetRule, Set<AssocRuleWithExceptions> groupRules, AssocRulesExtended rulesSource) {
//        // compute intersection score for each exception
//        for (ExceptionItem e : targetRule.getExceptionCandidates()) {
//            // TODO compute score for each exception
//            double conflictScore = 0;
//            int conflictTransactionsCount = 0;
//            for (AssocRuleWithExceptions rule : groupRules) {
//                if (targetRule.equals(rule)) {
//                    continue;
//                }
//
//                // filter transactions that contains the body and the excpetion
////                Set<Transaction> transactionsWithBodyandException=transactionsDB.filterTransactionsWith(predictableTransactions.get(rule), ArrayUtils.addAll(e.getItems(),targetRule.getBody()));
//                Set<Transaction> transactionsWithBodyandException = transactionsDB.filterTransactionsWith(rule.getSafePredictableTransactions(), ArrayUtils.addAll(e.getItems(), targetRule.getBody()), false);
//                double singleConflictTransactionsCount = TransactionsDatabase.getTransactionsCount(transactionsWithBodyandException);
//                conflictScore += (singleConflictTransactionsCount * rule.getConfidence());
//                conflictTransactionsCount += singleConflictTransactionsCount;
//
//
//            }
//            e.setConflictScore(conflictTransactionsCount == 0 ? 0 : (conflictScore / conflictTransactionsCount));
//            e.setConflictCount(conflictTransactionsCount);
//
//            // ComputeInverted conflict
//            if (rulesSource != null)
//                invertedConflictScore(targetRule, e, rulesSource.getRules(e.getItems(), null));
//
//        }
//    }


//    public void conflict(HeadGroup group, AssocRulesExtended rulesSource) {
//
//        Set<AssocRuleWithExceptions> groupRules = group.getRules();
//        for (AssocRuleWithExceptions rule : groupRules) {
////            exceptionsConflictScore(rule,predictableTransactions,rulesSource);
//            exceptionsConflictScore(rule, groupRules, rulesSource);
//        }
//
//    }


//    /**
//     * Computes the conflict score between the Exception Candidate generated from rules and positive examples.
//     *
//     * @param targetRule
//     * @param exceptionCandidate
//     * @param exceptionRules
//     * @return
//     */
//    public void invertedConflictScore(AssocRuleWithExceptions targetRule, ExceptionItem exceptionCandidate, Set<AssocRuleWithExceptions> exceptionRules) {
//
//
//        // positive examples of the rule that fo not contain the target exception
//        Set<Transaction> ruleTransactionsWithoutException = transactionsDB.filterOutTransactionsWith(targetRule.getHornRuleTransactions(), exceptionCandidate.getItems(), false);
//
////        HashMap<AssocRuleWithExceptions,Set<Transaction>> predictableTransactions=new HashMap<>(exceptionRules.size());
//        double conflictScore = 0;
//        int conflictTransactionsCount = 0;
//        for (AssocRuleWithExceptions rule : exceptionRules) {
//            if (!targetRule.isBodySubsetOf(rule))
//                continue;
//
//            // transactions with the body but neither the exceptions nor the head ... they are predictable with this rule
////            Set<Transaction> rulePredictableTransactions=rule.getPredicatableTransactions(transactionsDB,true);
//            Set<Transaction> rulePredictableTransactions = rule.getSafePredictableTransactions();
////            predictableTransactions.put (rule, rulePredictableTransactions);
//
//            // Count the intersection between the exception predection and the target rule positive examples
//            double predicatableExceptionCount = TransactionsDatabase.getTransactionsCount(Sets.intersection(rulePredictableTransactions, ruleTransactionsWithoutException));
//            conflictScore += (predicatableExceptionCount * rule.getConfidence());
//            conflictTransactionsCount += predicatableExceptionCount;
//
//        }
//
////        return  conflictTransactionsCount==0? 0:(conflictScore/conflictTransactionsCount);
//        exceptionCandidate.setInvertedConflictScore(conflictTransactionsCount == 0 ? 0 : (conflictScore / conflictTransactionsCount));
//        exceptionCandidate.setInvertedConflictCount(conflictTransactionsCount);
//
//    }

}
