package de.mpii.frequentrulesminning;

import de.mpii.frequentrulesminning.utils.*;
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


    private double cautiousMaterializationThreshold;
    private BufferedWriter outputBufferedWritter;
    TransactionsDatabase transDB;

    boolean debugMaterialization;

    public Materializer(TransactionsDatabase transDB) throws Exception {
        this(transDB,0.01,false,null);
    }


    public Materializer(TransactionsDatabase transDB,double cautiousMaterializationThreshold,boolean debugMaterialization, String materializationFile) throws Exception {
        this.transDB = transDB;
        this.debugMaterialization=debugMaterialization;
        this.cautiousMaterializationThreshold=cautiousMaterializationThreshold;
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
        if(debugMaterialization) {
            this.outputBufferedWritter.flush();
        }
    }


    public void materialize(AssocRuleWithExceptions rule,boolean cautious,boolean withPredictions) throws IOException {

        int [] exceptions=new int[0];
        if(cautious)
            exceptions=rule.getExceptionsCandidatesInts(this.cautiousMaterializationThreshold);
        Set<Transaction> transactionSet=transDB.getTransactions(rule.getBody(),ArrayUtils.addAll(rule.getHead(),exceptions),withPredictions);
        materialize(rule,transactionSet,exceptions);

    }



    public void materialize(AssocRuleWithExceptions rule, Collection<Transaction> transactions,int[] exceptions ) throws IOException {
        if(debugMaterialization) {
            this.outputBufferedWritter.write(rule.toString());
            this.outputBufferedWritter.newLine();
        }
        transactions.stream().forEach((transaction -> {
            try {

                materialize(rule, transaction, exceptions);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }));
        if(debugMaterialization) {
            this.outputBufferedWritter.newLine();
        }
    }

    public void materialize(AssocRuleWithExceptions rule, Transaction transaction,int[] exceptions/*,boolean cautious*/) throws IOException {
        // TODO needs more thinking about accumlating weights
        if(!transaction.contains(rule.getBody()))
            return;

        // if we are cautiously materializing .. then we skipp transactions with any exception candidates
        if(transaction.containsAny(exceptions))
            return;


        // Do not generate based on generated data. All previous data should be independent
        if(!Arrays.stream(rule.getBody()).allMatch((i) -> transaction.getItemWeight(i).isIndependent()))
            return;

        // get transaction weight with respect to body and exceptions
        double bodyWeightValue=transaction.getWeight(rule.getBody(),null);


        // new prediction weight = bodyWeightValue * ruleWeight .. Weight=Confidence for now.
        double weight= bodyWeightValue * rule.getConfidence();


        // add the predicted to the transaction
        transDB.addPredictions(rule.getHead(),transaction);

        // add Item with weight to transaction
        transaction.addItemsWithWeight(rule.getHead(),new Weight(rule,weight));

        // exports the
        if(debugMaterialization){
            double[] bodyWeights = Arrays.stream(rule.getBody()).mapToDouble((i) -> transaction.getItemWeight(i).getFinalWeight()).toArray();
            outputBufferedWritter.write(transaction.getId()+"\t"+weight+" <= " + rule.getConfidence()+ " = "+Arrays.toString(bodyWeights) +"\n");
            //outputBufferedWritter.newLine();
        }


    }
}
