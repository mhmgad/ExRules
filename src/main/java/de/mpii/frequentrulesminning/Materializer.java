package de.mpii.frequentrulesminning;

import de.mpii.frequentrulesminning.utils.AssocRuleWithExceptions;
import de.mpii.frequentrulesminning.utils.Transaction;
import de.mpii.frequentrulesminning.utils.TransactionsDatabase;
import mpi.tools.javatools.util.FileUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.*;
import java.util.*;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

/**
 * Created by gadelrab on 4/5/16.
 */
public class Materializer {


    private BufferedWriter outputBufferedWritter;
    TransactionsDatabase transDB;

    boolean debugMaterialization;

    public Materializer(TransactionsDatabase transDB) throws Exception {
        this(transDB,false,null);
    }


    public Materializer(TransactionsDatabase transDB,boolean debugMaterialization, String materializationFile) throws Exception {
        this.transDB = transDB;
        this.debugMaterialization=debugMaterialization;
        if(debugMaterialization){
            this.outputBufferedWritter= FileUtils.getBufferedUTF8Writer(materializationFile);
        }
    }

    public void materialize(List<AssocRuleWithExceptions> rules, boolean cautious, boolean withPredictions){

        rules.stream().sorted(Comparator.comparing(AssocRuleWithExceptions::getLift).reversed()).forEach((r)->materialize(r,cautious,withPredictions));

    }


    public void materialize(AssocRuleWithExceptions rule,boolean cautious,boolean withPredictions){
        Set<Transaction> transactionSet=transDB.getTransactions(rule.getBody(),ArrayUtils.addAll(rule.getHead(),rule.getExceptionsCandidatesInts()),withPredictions);
        materialize(rule,transactionSet,cautious);
    }

    public void materialize(AssocRuleWithExceptions rule, Collection<Transaction> transactions, boolean cautious){

        transactions.parallelStream().forEach((transaction -> {
            try {
                materialize(rule, transaction, cautious);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

    }

    public void materialize(AssocRuleWithExceptions rule, Transaction transaction,boolean cautious) throws IOException {

        if(!transaction.contains(rule.getBody()))
            return;

        if(cautious && transaction.contains(rule.getExceptionsCandidatesInts()))
            return;

        DoubleStream bodyWeights = Arrays.stream(rule.getBody()).mapToDouble((i) -> transaction.getItemWeight(i));
        double averageBodyConf=bodyWeights.average().getAsDouble();
        // new prediction weight = averageBodyWeight * ruleWeight .. Weight=Confidence for now.
        double weight= averageBodyConf* rule.getConfidence();

        transaction.addItemsWithWeight(rule.getHead(),weight);
        // add the predicted to the transaction
        transDB.addPredictions(rule.getHead(),transaction);

        if(debugMaterialization){
            outputBufferedWritter.write(Arrays.toString(rule.getBody())+" ==> " +Arrays.toString(rule.getHead())+"\t "+Arrays.toString(bodyWeights.toArray())+" = " + rule.getConfidence()+ " => "+ weight);
            outputBufferedWritter.newLine();
        }

    //transaction.
    }
}
