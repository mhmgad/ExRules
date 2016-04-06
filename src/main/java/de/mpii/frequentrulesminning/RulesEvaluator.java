package de.mpii.frequentrulesminning;

import com.google.common.collect.Sets;
import de.mpii.frequentrulesminning.utils.*;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by gadelrab on 3/8/16.
 */
public class RulesEvaluator {


    TransactionsDatabase transactionsDB;


    public RulesEvaluator(String transactionsFilePath) throws IOException {
        this(new TransactionsDatabase(transactionsFilePath) );


    }

    public RulesEvaluator(TransactionsDatabase transactionsDatabase) {
        this.transactionsDB=transactionsDatabase;
    }



    public double confidence(AssocRuleWithExceptions rule, ExceptionItem exceptionItem){

//        int bodySupport=transactionsDB.getTransactionsCount(rule.getItemset1(),exceptionItem==null? null:exceptionItem.getItems());
//        int ruleSupport=transactionsDB.getTransactionsCount(ArrayUtils.addAll(rule.getItemset2(),rule.getItemset1()), exceptionItem==null? null:exceptionItem.getItems());
        Set<Transaction> ruleTransactions=transactionsDB.filterOutTransactionsWith(rule.getHornRuleTransactions(),exceptionItem==null? null:exceptionItem.getItems(),false);
        Set<Transaction> bodyTransactions=transactionsDB.filterOutTransactionsWith(rule.getBodyTransactions(),exceptionItem==null? null:exceptionItem.getItems(),false);

        return computeConfidence(ruleTransactions, bodyTransactions);


    }

    public static double computeConfidence(Set<Transaction> ruleTransactions, Set<Transaction> bodyTransactions) {
        int bodySupport= TransactionsDatabase.getTransactionsCount(bodyTransactions);
        int ruleSupport=TransactionsDatabase.getTransactionsCount(ruleTransactions);

        return computeConfidence(ruleSupport,bodySupport );
    }

    public static double computeConfidence(int ruleSupport,int bodySupport ) {
        return ((double)ruleSupport) /bodySupport;
    }

    public double coverage(AssocRuleWithExceptions rule){
        return  coverage( rule,null);
    }

    public double coverage(AssocRuleWithExceptions rule, ExceptionItem exceptionItem){
//        int ruleSupport=transactionsDB.getTransactionsCount(ArrayUtils.addAll(rule.getItemset2(),rule.getItemset1()), exceptionItem==null? null:exceptionItem.getItems());
//        int headSupport=transactionsDB.getTransactionsCount(rule.getItemset2(),null);
        Set<Transaction> ruleTransactions=transactionsDB.filterOutTransactionsWith(rule.getHornRuleTransactions(),exceptionItem==null? null:exceptionItem.getItems(),false);
        Set<Transaction> headTransactions=transactionsDB.filterOutTransactionsWith(rule.getHeadTransactions(),null,false);

        return computeCoverage(ruleTransactions, headTransactions);

    }

    public static double computeCoverage(Set<Transaction> ruleTransactions, Set<Transaction> headTransactions) {
        int ruleSupport= TransactionsDatabase.getTransactionsCount(ruleTransactions);
        int headSupport=TransactionsDatabase.getTransactionsCount(headTransactions);
        return computeCoverage(ruleSupport, headSupport);
    }

    public static double computeCoverage(int ruleSupport, int headSupport) {
        return ((double)ruleSupport) /headSupport;
    }


    public double lift(AssocRuleWithExceptions rule){
        return  lift( rule,null);
    }

    public double lift(AssocRuleWithExceptions rule, ExceptionItem exceptionItem){
//        int ruleSupport=transactionsDB.getTransactionsCount(ArrayUtils.addAll(rule.getItemset2(),rule.getItemset1()), exceptionItem==null? null:exceptionItem.getItems());
//        int bodySupport=transactionsDB.getTransactionsCount(rule.getItemset1(),exceptionItem==null? null:exceptionItem.getItems());
//        int headSupport=transactionsDB.getTransactionsCount(rule.getItemset2(),null);
        Set<Transaction> bodyTransactions=transactionsDB.filterOutTransactionsWith(rule.getBodyTransactions(),exceptionItem==null? null:exceptionItem.getItems(),false);
        Set<Transaction> ruleTransactions=transactionsDB.filterOutTransactionsWith(rule.getHornRuleTransactions(),exceptionItem==null? null:exceptionItem.getItems(),false);
        Set<Transaction> headTransactions=transactionsDB.filterOutTransactionsWith(rule.getHeadTransactions(),null,false);
        return computeLift(ruleTransactions,bodyTransactions,  headTransactions);

    }

    public static double computeLift( Set<Transaction> ruleTransactions,Set<Transaction> bodyTransactions, Set<Transaction> headTransactions) {
        int bodySupport= TransactionsDatabase.getTransactionsCount(bodyTransactions);
        int ruleSupport=TransactionsDatabase.getTransactionsCount(ruleTransactions);
        int headSupport=TransactionsDatabase.getTransactionsCount(headTransactions);
        return computeLift(ruleSupport,bodySupport,  headSupport);
    }

    public static double computeLift(int ruleSupport,int bodySupport,  int headSupport) {
        return (((double) ruleSupport) / headSupport) / bodySupport;
    }


    /**
     * Coverage of set of rules R with same head using coverage = (1/E) * ((supp(r1)* supp(r2)..sup(rn))^(1/|R|) )
     * @param head
     * @param rules
     * @param withExceptions
     */
    public void coverage(HeadGroup head,Collection<AssocRuleWithExceptions> rules, boolean withExceptions){

        // Exception Handling is not yet implemented

//        Set<Transaction> containsBodyOrHead= new HashSet<>(transactionsDB.getTransactions(head.getHeadItems(),null));
//        Set<Transaction> containsBodyOrHead= Sets.union()
        Set<Transaction> containsBodyOrHead= transactionsDB.getTransactions(head.getHeadItems(),null,false);

        //ArrayList<Integer> rulesTransactionsCount=new ArrayList<>();
        BigDecimal coverageMultiplication=BigDecimal.ONE;
        for (AssocRuleWithExceptions rule:rules ) {

//            Set<Transaction> bodyTransactions = transactionsDB.getTransactions(rule.getItemset1(), null);
            Set<Transaction> bodyTransactions = rule.getBodyTransactions();
            int bodyTransactionsCount=TransactionsDatabase.getTransactionsCount(bodyTransactions);
//            rulesTransactionsCount.add(bodyTransactionsCount);
            if(bodyTransactionsCount==0||coverageMultiplication.equals(BigDecimal.ZERO))
                System.out.println(rule+" "+bodyTransactions.size()+" "+bodyTransactionsCount+" "+coverageMultiplication);
            coverageMultiplication.multiply(BigDecimal.valueOf(bodyTransactionsCount));
            //coverageMultiplication*=bodyTransactionsCount;

            // combine to all bodies transactions
//            containsBodyOrHead.addAll(bodyTransactions);
            containsBodyOrHead=Sets.union(containsBodyOrHead,bodyTransactions);


        }


        //int coverageMultiplication=rulesTransactionsCount.stream().reduce((i,j)-> i*j).get();
        int allTransactionsCount=TransactionsDatabase.getTransactionsCount(containsBodyOrHead);
        int rSize=rules.size();

        double groupCoverage= Math.pow((coverageMultiplication.doubleValue()),(1.0D/(double)rSize))/((double)allTransactionsCount);
//        double groupCoverage= coverageMultiplication.pow((1.0D/(double)rSize))/((double)allTransactionsCount);
        if(groupCoverage==0||allTransactionsCount==0)
            System.out.print("groupCoverage = " + groupCoverage+" coverageMultiplication = " + coverageMultiplication+" rSize: = "+rSize +" all Transactions Count = "+ allTransactionsCount);
         head.setCoverage(groupCoverage);
         head.setAllTransactionsCount(allTransactionsCount);
    }


    public void coverage(HeadGroup head, Collection<AssocRuleWithExceptions> assocRuleWithExceptionses) {
        coverage(head,assocRuleWithExceptionses,false);

    }

    public void confidence(HeadGroup head, Collection<AssocRuleWithExceptions> assocRuleWithExceptionses){
        confidence(head,assocRuleWithExceptionses,false);
    }


    /**
     * Measures a set of rule confidence by computing  the support of all rules union over the union of the bodies supp(all rules)/supp(bodies)
     * @param head
     * @param rules
     * @param withExceptions
     */
    public void confidence(HeadGroup head, Collection<AssocRuleWithExceptions> rules, boolean withExceptions) {

        Set<Transaction> containsBody= new HashSet<>();



        for (AssocRuleWithExceptions rule:rules ) {
//            Set<Transaction> bodyTransactions = transactionsDB.getTransactions(rule.getItemset1(), null);
            Set<Transaction> bodyTransactions = rule.getBodyTransactions();
            //containsBody.addAll(bodyTransactions);
            containsBody=Sets.union(containsBody,bodyTransactions);
        }

        int bodyTransactionsCount=TransactionsDatabase.getTransactionsCount(containsBody);

        int rulesBodyandHeadCount=TransactionsDatabase.getTransactionsCount(transactionsDB.filterTransactionsWith(containsBody,head.getHeadItems(),false));

        head.setConfidence((double)rulesBodyandHeadCount/bodyTransactionsCount);

    }


//    public void exceptionsConflictScore(AssocRuleWithExceptions targetRule, HashMap<AssocRuleWithExceptions, Set<Transaction>> predictableTransactions, AssocRulesExtended rulesSource){
//        // compute intersection score for each exception
//        for (ExceptionItem e:targetRule.getExceptionCandidates() ){
//            // TODO compute score for each exception
//            double conflictScore=0;
//            int conflictTransactionsCount=0;
//            for (AssocRuleWithExceptions rule:predictableTransactions.keySet()) {
//                    if(targetRule.equals(rule)){
//                        continue;
//                    }
//
//                // filter transactions that contains the body and the excpetion
////                Set<Transaction> transactionsWithBodyandException=transactionsDB.filterTransactionsWith(predictableTransactions.get(rule), ArrayUtils.addAll(e.getItems(),targetRule.getBody()));
//                Set<Transaction> transactionsWithBodyandException=transactionsDB.filterTransactionsWith(r.getSa, ArrayUtils.addAll(e.getItems(),targetRule.getBody()));
//                int singleConflictTransactionsCount=TransactionsDatabase.getTransactionsCount(transactionsWithBodyandException);
//                conflictScore+=(singleConflictTransactionsCount*rule.getConfidence());
//                conflictTransactionsCount+=singleConflictTransactionsCount;
//
//
//            }
//            e.setConflictScore(conflictTransactionsCount==0? 0:(conflictScore/conflictTransactionsCount));
//            e.setConflictCount(conflictTransactionsCount);
//
//            // ComputeInverted conflict
//            if(rulesSource!=null)
//                invertedConflictScore(targetRule,e,rulesSource.getRules(e.getItems(),null));
//
//        }
//    }

    public void exceptionsConflictScore(AssocRuleWithExceptions targetRule, Set<AssocRuleWithExceptions> groupRules, AssocRulesExtended rulesSource){
        // compute intersection score for each exception
        for (ExceptionItem e:targetRule.getExceptionCandidates() ){
            // TODO compute score for each exception
            double conflictScore=0;
            int conflictTransactionsCount=0;
            for (AssocRuleWithExceptions rule:groupRules) {
                if(targetRule.equals(rule)){
                    continue;
                }

                // filter transactions that contains the body and the excpetion
//                Set<Transaction> transactionsWithBodyandException=transactionsDB.filterTransactionsWith(predictableTransactions.get(rule), ArrayUtils.addAll(e.getItems(),targetRule.getBody()));
                Set<Transaction> transactionsWithBodyandException=transactionsDB.filterTransactionsWith(rule.getSafePredictableTransactions(), ArrayUtils.addAll(e.getItems(),targetRule.getBody()),false);
                int singleConflictTransactionsCount=TransactionsDatabase.getTransactionsCount(transactionsWithBodyandException);
                conflictScore+=(singleConflictTransactionsCount*rule.getConfidence());
                conflictTransactionsCount+=singleConflictTransactionsCount;


            }
            e.setConflictScore(conflictTransactionsCount==0? 0:(conflictScore/conflictTransactionsCount));
            e.setConflictCount(conflictTransactionsCount);

            // ComputeInverted conflict
            if(rulesSource!=null)
                invertedConflictScore(targetRule,e,rulesSource.getRules(e.getItems(),null));

        }
    }


//    public void conflict(HeadGroup key, Set<AssocRuleWithExceptions> groupRules) {
//
////        // get predictable transactions for each rule
////        HashMap<AssocRuleWithExceptions,Set<Transaction>> predictableTransactions=new HashMap<>(groupRules.size());
////        for(AssocRuleWithExceptions rule:groupRules){
////            // transactions with the body but neither the exceptions nor the head ... they are predictable with this rule
////            Set<Transaction> rulePredictableTransactions=rule.getPredicatableTransactions(transactionsDB,true);
////            predictableTransactions.put (rule, rulePredictableTransactions);
////        }
////
////
////        for(AssocRuleWithExceptions rule:groupRules){
////            exceptionsConflictScore(rule,predictableTransactions);
////        }
//        conflict( key,groupRules,null);
//
//    }

    public void conflict(HeadGroup key, Set<AssocRuleWithExceptions> groupRules,AssocRulesExtended rulesSource) {

        // get predictable transactions for each rule
//        HashMap<AssocRuleWithExceptions,Set<Transaction>> predictableTransactions=new HashMap<>(groupRules.size());
//        for(AssocRuleWithExceptions rule:groupRules){
//            // transactions with the body but neither the exceptions nor the head ... they are predictable with this rule
//            Set<Transaction> rulePredictableTransactions=rule.getPredicatableTransactions(transactionsDB,true);
//            predictableTransactions.put (rule, rulePredictableTransactions);
//        }


        for(AssocRuleWithExceptions rule:groupRules){
//            exceptionsConflictScore(rule,predictableTransactions,rulesSource);
            exceptionsConflictScore(rule,groupRules,rulesSource);
        }

    }


    /**
     * Computes the conflict score between the Exception Candidate generated from rules and positive examples.
     * @param targetRule
     * @param exceptionCandidate
     * @param exceptionRules
     * @return
     */
    public void invertedConflictScore(AssocRuleWithExceptions targetRule, ExceptionItem exceptionCandidate, Set<AssocRuleWithExceptions> exceptionRules) {


        // positive examples of the rule that fo not contain the target exception
        Set<Transaction> ruleTransactionsWithoutException=transactionsDB.filterOutTransactionsWith(targetRule.getHornRuleTransactions(),exceptionCandidate.getItems(),false);

//        HashMap<AssocRuleWithExceptions,Set<Transaction>> predictableTransactions=new HashMap<>(exceptionRules.size());
        double conflictScore=0;
        int conflictTransactionsCount=0;
        for(AssocRuleWithExceptions rule:exceptionRules){
            if(!targetRule.isBodySubsetOf(rule))
                    continue;

            // transactions with the body but neither the exceptions nor the head ... they are predictable with this rule
//            Set<Transaction> rulePredictableTransactions=rule.getPredicatableTransactions(transactionsDB,true);
            Set<Transaction> rulePredictableTransactions=rule.getSafePredictableTransactions();
//            predictableTransactions.put (rule, rulePredictableTransactions);

            // Count the intersection between the exception predection and the target rule positive examples
            int predicatableExceptionCount=TransactionsDatabase.getTransactionsCount(Sets.intersection(rulePredictableTransactions,ruleTransactionsWithoutException));
            conflictScore+=(predicatableExceptionCount*rule.getConfidence());
            conflictTransactionsCount+=predicatableExceptionCount;

        }

//        return  conflictTransactionsCount==0? 0:(conflictScore/conflictTransactionsCount);
        exceptionCandidate.setInvertedConflictScore(conflictTransactionsCount==0? 0:(conflictScore/conflictTransactionsCount));
        exceptionCandidate.setInvertedConflictCount(conflictTransactionsCount);

    }


}
