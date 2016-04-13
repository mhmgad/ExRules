package de.mpii.frequentrulesminning;

import de.mpii.frequentrulesminning.utils.AssocRuleWithExceptions;
import de.mpii.frequentrulesminning.utils.Transaction;
import de.mpii.frequentrulesminning.utils.TransactionsDatabase;
import de.mpii.frequentrulesminning.utils.Weight;
import mpi.tools.javatools.util.FileUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.*;
import java.util.*;
import java.util.function.DoubleBinaryOperator;
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

    public void materialize(List<AssocRuleWithExceptions> rules, boolean cautious, boolean withPredictions) throws IOException {

        rules.stream().sorted(Comparator.comparing(AssocRuleWithExceptions::getLift).reversed()).forEach((r) -> {
            try {
                materialize(r, cautious, withPredictions);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        this.outputBufferedWritter.flush();

    }


    public void materialize(AssocRuleWithExceptions rule,boolean cautious,boolean withPredictions) throws IOException {
        Set<Transaction> transactionSet=transDB.getTransactions(rule.getBody(),ArrayUtils.addAll(rule.getHead(),rule.getExceptionsCandidatesInts()),withPredictions);
        materialize(rule,transactionSet,cautious);

    }

    public void materialize(AssocRuleWithExceptions rule, Collection<Transaction> transactions, boolean cautious) throws IOException {

        this.outputBufferedWritter.write(rule.toString());
        this.outputBufferedWritter.newLine();
        transactions.parallelStream().forEach((transaction -> {
            try {

                materialize(rule, transaction, cautious);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }));
        this.outputBufferedWritter.newLine();

    }

    public void materialize(AssocRuleWithExceptions rule, Transaction transaction,boolean cautious) throws IOException {
        // TODO needs more thinking about accumlating weights
        if(!transaction.contains(rule.getBody()))
            return;

        // if we are cautiously materializing .. then we skipp transactions with any exception candidates
        if(cautious && transaction.containsAny(rule.getExceptionsCandidatesInts()))
            return;


        // Do not generate based on generated data. All previous data should be independent
        if(!Arrays.stream(rule.getBody()).allMatch((i) -> transaction.getItemWeight(i).isIndependent()))
            return;

        // get transaction weight with respect to body and exceptions
        double bodyWeightValue=transaction.getWeight(rule.getBody(),rule.getExceptionsCandidatesInts());


        // new prediction weight = bodyWeightValue * ruleWeight .. Weight=Confidence for now.
        double weight= bodyWeightValue * rule.getConfidence();


        // add the predicted to the transaction
        transDB.addPredictions(rule.getHead(),transaction);

        // add Item with weight to transaction
        transaction.addItemsWithWeight(rule.getHead(),new Weight(rule,weight));

        // exports the
        if(debugMaterialization){
            double[] bodyWeights = Arrays.stream(rule.getBody()).mapToDouble((i) -> transaction.getItemWeight(i).getFinalWeight()).toArray();
            outputBufferedWritter.write(transaction.getId()+"\t"+Arrays.toString(bodyWeights)+" = " + rule.getConfidence()+ " => "+ weight);
            outputBufferedWritter.newLine();
        }


    }
}
