package de.mpii.frequentrulesminning;

import de.mpii.frequentrulesminning.utils.AssocRuleWithExceptions;
import de.mpii.frequentrulesminning.utils.Transaction;
import de.mpii.frequentrulesminning.utils.TransactionsDatabase;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by gadelrab on 4/5/16.
 */
public class Materializer {



    TransactionsDatabase transDB;

    public Materializer(TransactionsDatabase transDB) {
        this.transDB = transDB;
    }


    public void materialize(AssocRuleWithExceptions rule, Collection<Transaction> transactions, boolean cautious){

        transactions.parallelStream().forEach((transaction ->  materialize( rule, transaction)));

    }

    public void materialize(AssocRuleWithExceptions rule, Transaction transaction){
    //TODO continue here
        Arrays.stream(rule.getBody()).forEach();


    //transaction.
    }
}
