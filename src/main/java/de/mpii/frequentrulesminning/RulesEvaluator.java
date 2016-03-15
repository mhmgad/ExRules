package de.mpii.frequentrulesminning;

import com.google.common.collect.Sets;
import de.mpii.frequentrulesminning.utils.*;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
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



    public double coverage(Collection<AssocRuleWithExceptions> rules, boolean withExceptions){


        Set<Transaction> containsBody= Sets.newHashSet();
        ArrayList<Integer> rulesTransactionsCount=new ArrayList<>();
        for (AssocRuleWithExceptions rule:rules ) {

            Set<Transaction> bodyTransactions = transactionsDB.getTransactions(rule.getItemset1(), null);

            rulesTransactionsCount.add(TransactionsDatabase.getTransactionsCount(bodyTransactions));

            // combine to all bodies transactions
            containsBody.addAll(bodyTransactions);


        }
        int coverageMultiplication=rulesTransactionsCount.stream().reduce((i,j)-> i*j).get();
        int allTransactionsCount=TransactionsDatabase.getTransactionsCount(containsBody);
        int rSize=rules.size();

        double groupCoverage= Math.pow(((float)coverageMultiplication),(1.0/rSize))/((float)allTransactionsCount);

        return groupCoverage;
    }


    public double coverage(Collection<AssocRuleWithExceptions> assocRuleWithExceptionses) {
        return coverage(assocRuleWithExceptionses,false);

    }
}
