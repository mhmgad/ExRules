package de.mpii.frequentrulesminning;

import de.mpii.frequentrulesminning.utils.AssocRuleWithExceptions;
import de.mpii.frequentrulesminning.utils.AssocRulesExtended;
import de.mpii.frequentrulesminning.utils.ExceptionItem;
import de.mpii.frequentrulesminning.utils.TransactionsDatabase;
import org.apache.commons.lang3.ArrayUtils;

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



    public double confidence(AssocRuleWithExceptions rule, ExceptionItem exceptionItem){

        int bodySupport=transactionsDB.getTransactionsCount(rule.getItemset1(),exceptionItem.getItems());
        int ruleSupport=transactionsDB.getTransactionsCount(ArrayUtils.addAll(rule.getItemset2(),rule.getItemset1()), exceptionItem.getItems());

        return ((float)ruleSupport)/((float)bodySupport);


    }

    public double coverage(AssocRuleWithExceptions rule){
        return  coverage( rule,null);
    }

    public double coverage(AssocRuleWithExceptions rule, ExceptionItem exceptionItem){
        int ruleSupport=transactionsDB.getTransactionsCount(ArrayUtils.addAll(rule.getItemset2(),rule.getItemset1()), exceptionItem.getItems());
        int headSupport=transactionsDB.getTransactionsCount(rule.getItemset2(),null);
        return ((float)ruleSupport)/((float)headSupport);

    }




}
