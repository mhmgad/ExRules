package de.mpii.frequentrulesminning;

import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemsets;
import ca.pfv.spmf.test.MainTestDCharm_bitset_saveToMemory;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import de.mpii.frequentrulesminning.utils.AssocRuleString;
import de.mpii.frequentrulesminning.utils.Transaction;
import gnu.trove.map.hash.TObjectIntHashMap;
import mpi.tools.javatools.util.FileUtils;

import java.io.*;
import java.util.*;

/**
 * Created by gadelrab on 2/26/16.
 */
public class ExceptionMining {

    TObjectIntHashMap<Transaction> transactionsSet;

    SetMultimap<Integer,Transaction> items2transactions;


    public ExceptionMining(InputStream transactions) throws IOException {
        this.items2transactions = HashMultimap.create();
        this.transactionsSet = new TObjectIntHashMap<>();
        loadTransactions( transactions);
    }


    public ExceptionMining(String transactionsFile) throws IOException {

        this(new FileInputStream(transactionsFile));


    }


    private void loadTransactions(InputStream transactionsStream) throws IOException {

        BufferedReader br=FileUtils.getBufferedUTF8Reader(transactionsStream);
        // read transactions file
        for (String line=br.readLine();line!=null&&!line.isEmpty();line=br.readLine()){
            Transaction t=new Transaction(line);

            if (transactionsSet.adjustOrPutValue(t, 1, 1) == 1) {
                // add it to the map
                for (int i:t.getItems()){
                    items2transactions.put(i,t);
                }
            }

        }


    }

    public static String writeToTmpFile(Collection<Transaction> transactions) throws IOException {
        File temporaryTransactions = File.createTempFile("rule_mining", "negativeTransactions.txt");
        BufferedWriter bw = FileUtils.getBufferedUTF8Writer(temporaryTransactions);
        for (Transaction t : transactions) {
            for (int i = 0; i < t.getCount(); i++) {
                bw.write(t.toString());
                bw.newLine();
            }

        }
        bw.flush();
        bw.close();
        temporaryTransactions.deleteOnExit();
        System.out.println(temporaryTransactions.getAbsoluteFile());
        return temporaryTransactions.getAbsolutePath();

    }

    private Set<Transaction> getNegativeExamples(AssocRuleString rule) {


        // transactions contain body
        int [] body=rule.getItemset1();
        Set<Transaction> transactions=new HashSet<>(items2transactions.get(body[0]));
        for (int i=1;i<body.length;i++){
            transactions = new HashSet(Sets.intersection(transactions, items2transactions.get(body[i])));
        }

        // exclude transactions contain head
        int [] head=rule.getItemset2();
        for (int i=0;i<head.length;i++){
            transactions = new HashSet(Sets.difference(transactions, items2transactions.get(head[i])));
        }

        for (Transaction transaction:transactions){
            int transactionCount = this.transactionsSet.get(transaction);
            transaction.setCout(transactionCount);

        }
        return transactions;
    }


    public Itemsets mineExceptions(AssocRuleString rule) throws IOException {

        Set<Transaction> negativeTransactions = getNegativeExamples(rule);

        String negativeTransactionsFilePath = writeToTmpFile(negativeTransactions);

        getClosedFrequentItemSets(negativeTransactionsFilePath);


    return null;
    }

    private void getClosedFrequentItemSets(String negativeTransactionsFilePath) {
        MainTestDCharm_bitset_saveToMemory

    }


    public static void main(String[] args) throws IOException {


        List<Transaction> ts = new ArrayList<>();
        ts.add(new Transaction("1 2 3", 3));
        ts.add(new Transaction("1 2"));
        ts.add(new Transaction("1 2"));
        ts.add(new Transaction("1 2 4", 10));

        ExceptionMining.writeToTmpFile(ts);

    }




}
