package de.mpii.frequentrulesminning;

import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AssocRule;
import ca.pfv.spmf.algorithms.frequentpatterns.charm.AlgoDCharm_Bitset;
import ca.pfv.spmf.input.transaction_database_list_integers.TransactionDatabase;


import ca.pfv.spmf.patterns.itemset_array_integers_with_tids_bitset.Itemset;
import ca.pfv.spmf.patterns.itemset_array_integers_with_tids_bitset.Itemsets;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import de.mpii.frequentrulesminning.utils.AssocRuleString;
import de.mpii.frequentrulesminning.utils.ItemsetString;
import de.mpii.frequentrulesminning.utils.RDF2IntegerTransactionsConverter;
import de.mpii.frequentrulesminning.utils.Transaction;
import gnu.trove.map.hash.TObjectIntHashMap;
import mpi.tools.javatools.util.FileUtils;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by gadelrab on 2/26/16.
 */
public class ExceptionMining {

    private  double exceptionMinSupp;
    private  RDF2IntegerTransactionsConverter converter;
    TObjectIntHashMap<Transaction> transactionsSet;

    SetMultimap<Integer,Transaction> items2transactions;


    public ExceptionMining(InputStream transactions,double exceptionMinSupp) throws IOException {
        this.items2transactions = HashMultimap.create();
        this.transactionsSet = new TObjectIntHashMap<>();
        loadTransactions( transactions);
        this.exceptionMinSupp=exceptionMinSupp;
    }


    public ExceptionMining(String transactionsFile,double exceptionMinSupp) throws IOException {

        this(new FileInputStream(transactionsFile),exceptionMinSupp);


    }

    public ExceptionMining(String transactionsFilePath, RDF2IntegerTransactionsConverter rdf2TransactionsConverter,double exceptionMinSupp) throws IOException {
        this(transactionsFilePath,exceptionMinSupp);

        this.converter=rdf2TransactionsConverter;

    }

    public ExceptionMining(String transactionsFilePath, String rdf2IdMappingFilePath, double exceptionMinSupp) throws IOException {
        this(transactionsFilePath,new RDF2IntegerTransactionsConverter());
        this.converter.loadMappingFromFile(rdf2IdMappingFilePath);

    }

    public ExceptionMining(String transactionsFilePath, RDF2IntegerTransactionsConverter rdf2TransactionsConverter) throws IOException {
        this(transactionsFilePath,rdf2TransactionsConverter,0.5D);
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

    private Set<Transaction> getNegativeExamples(AssocRule rule) {


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

        // adding transactions to list after removing body items
        Set<Integer> bodyset= ImmutableSet.copyOf(Ints.asList(body));
        Iterator<Transaction> itr = transactions.iterator();
        while (itr.hasNext()) {


            Transaction transaction=itr.next();
            Set <Integer> transDiff=Sets.difference(ImmutableSet.copyOf(transaction.getItemsAsList()),bodyset);
            if(transDiff.size()==0){
                itr.remove();
            }
            else {
                transaction.setItems(Ints.toArray(transDiff));
                int transactionCount = this.transactionsSet.get(transaction);
                transaction.setCout(transactionCount);
            }

        }
        return transactions;
    }


    public  List<ItemsetString> mineExceptions(AssocRule rule) throws IOException {

        Set<Transaction> negativeTransactions = getNegativeExamples(rule);

        //String negativeTransactionsFilePath = writeToTmpFile(negativeTransactions);

        List<List<Itemset>> patterns =getClosedFrequentItemSets(negativeTransactions).getLevels();
        List<Itemset> patternsFlat =  patterns.stream().flatMap(List::stream).collect(Collectors.toList());
        List<ItemsetString> patternsFlatItems=new ArrayList<>(patternsFlat.size());
        for (Itemset it:patternsFlat) {
            patternsFlatItems.add(new ItemsetString(converter.convertIntegers2Strings(it.getItems()),it.getItems(),it.getAbsoluteSupport()));

        }
        


    return patternsFlatItems;
    }


    private TransactionDatabase getTransactionDatabase(Collection<Transaction> transactions){
        TransactionDatabase tDb=new TransactionDatabase();

        for (Transaction t:transactions) {
            List<Integer> transAsList=t.getItemsAsList();
            int count=t.getCount();
            for (int i = 0; i < count; i++) {
                tDb.addTransaction(transAsList);
            }
        }
        return tDb;
    }

    private Itemsets getClosedFrequentItemSets(Collection<Transaction> negativeTransactions) throws IOException {

        TransactionDatabase tDb=getTransactionDatabase(negativeTransactions);

        AlgoDCharm_Bitset algo = new AlgoDCharm_Bitset();
        Itemsets patterns = algo.runAlgorithm((String)null, tDb, this.exceptionMinSupp, true, (negativeTransactions.size()+10)/10);
        //algo.printStats();

        return patterns;

    }
//
//    private Itemsets getClosedFrequentItemSets(Collection<Transaction> negativeTransactions) throws IOException {
//
//        FPClose
//        TransactionDatabase tDb=getTransactionDatabase(negativeTransactions);
//
//        AlgoDCharm_Bitset algo = new AlgoDCharm_Bitset();
//        Itemsets patterns = algo.runAlgorithm((String)null, tDb, 0.5D, true, (negativeTransactions.size()+10)/10);
//        algo.printStats();
//
//        return patterns;
//
//    }


    public static void main(String[] args) throws IOException {


        List<Transaction> ts = new ArrayList<>();
        ts.add(new Transaction("1 2 3", 3));
        ts.add(new Transaction("1 2"));
        ts.add(new Transaction("1 2"));
        ts.add(new Transaction("1 2 4", 10));

        ExceptionMining.writeToTmpFile(ts);

    }




}
