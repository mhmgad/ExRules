package de.mpii.frequentrulesminning;

import com.google.common.collect.Sets;
import com.google.common.math.BigIntegerMath;
import de.mpii.frequentrulesminning.utils.*;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
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

        int bodySupport=transactionsDB.getTransactionsCount(rule.getItemset1(),exceptionItem==null? null:exceptionItem.getItems());
        int ruleSupport=transactionsDB.getTransactionsCount(ArrayUtils.addAll(rule.getItemset2(),rule.getItemset1()), exceptionItem==null? null:exceptionItem.getItems());

        return ((float)ruleSupport)/((float)bodySupport);


    }

    public double coverage(AssocRuleWithExceptions rule){
        return  coverage( rule,null);
    }

    public double coverage(AssocRuleWithExceptions rule, ExceptionItem exceptionItem){
        int ruleSupport=transactionsDB.getTransactionsCount(ArrayUtils.addAll(rule.getItemset2(),rule.getItemset1()), exceptionItem==null? null:exceptionItem.getItems());
        int headSupport=transactionsDB.getTransactionsCount(rule.getItemset2(),null);
        return ((float)ruleSupport)/((float)headSupport);

    }



    public void coverage(HeadGroup head,Collection<AssocRuleWithExceptions> rules, boolean withExceptions){

        // Exception Handling is not yet implemented

        Set<Transaction> containsBodyOrHead= transactionsDB.getTransactions(head.getHeadItems(),null);


        //ArrayList<Integer> rulesTransactionsCount=new ArrayList<>();
        BigDecimal coverageMultiplication=BigDecimal.ONE;
        for (AssocRuleWithExceptions rule:rules ) {

            Set<Transaction> bodyTransactions = transactionsDB.getTransactions(rule.getItemset1(), null);
            int bodyTransactionsCount=TransactionsDatabase.getTransactionsCount(bodyTransactions);
//            rulesTransactionsCount.add(bodyTransactionsCount);
            if(bodyTransactionsCount==0||coverageMultiplication.equals(BigDecimal.ZERO))
                System.out.println(rule+" "+bodyTransactions.size()+" "+bodyTransactionsCount+" "+coverageMultiplication);
            coverageMultiplication.multiply(BigDecimal.valueOf(bodyTransactionsCount));
            //coverageMultiplication*=bodyTransactionsCount;

            // combine to all bodies transactions
            containsBodyOrHead.addAll(bodyTransactions);


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

    private void confidence(HeadGroup head, Collection<AssocRuleWithExceptions> rules, boolean b) {

        Set<Transaction> containsBody= new HashSet<>();



        for (AssocRuleWithExceptions rule:rules ) {
            Set<Transaction> bodyTransactions = transactionsDB.getTransactions(rule.getItemset1(), null);
            containsBody.addAll(bodyTransactions);
        }

        int bodyTransactionsCount=TransactionsDatabase.getTransactionsCount(containsBody);

        int rulesBodyandHeadCount=TransactionsDatabase.getTransactionsCount(transactionsDB.filterTransactionsWith(containsBody,head.getHeadItems()));

        head.setConfidence((double)bodyTransactionsCount/rulesBodyandHeadCount);

    }


}
