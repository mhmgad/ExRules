package de.mpii.frequentrulesminning;

import de.mpii.frequentrulesminning.utils.AssocRuleWithExceptions;
import de.mpii.frequentrulesminning.utils.AssocRulesExtended;
import de.mpii.frequentrulesminning.utils.TransactionsDatabase;

import java.io.IOException;
import java.io.InputStream;

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



//    public double confidence(AssocRuleWithExceptions rule){
//
//
//
//    }

}
