package de.mpii.frequentrulesminning.utils;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import mpi.tools.javatools.util.FileUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by gadelrab on 3/8/16.
 */
public class TransactionsDatabase {
    HashMap<ItemsArray, Transaction> transactionsSet;

    //    TObjectIntHashMap<Transaction> transactionsSet;
    int distinctId;
    SetMultimap<Integer, Transaction> items2transactions;
    SetMultimap<Integer, Transaction> predictedItems2transactions;
    private int[] items;


    public TransactionsDatabase() {
        this.items2transactions = HashMultimap.create();
//        this.transactionsSet = new TObjectIntHashMap<>();
        this.transactionsSet = new HashMap<>();
        this.predictedItems2transactions = HashMultimap.create();
        distinctId = 0;

    }


    public TransactionsDatabase(String transactionFilePath) throws IOException {

        this(new FileInputStream(transactionFilePath));


    }


    public TransactionsDatabase(InputStream transactions) throws IOException {
        this();

        loadTransactions(transactions);


    }

    public static double getTransactionsCount(Collection<Transaction> transactions) {
        return getTransactionsCount(transactions,null,null,false);
        //return transactions.stream().mapToInt(Transaction::getCount).sum();
    }

    public static Set<Transaction> filterBetterQualityRulesPredictions(Collection<Transaction> transactions, AssocRuleWithExceptions rule,boolean negated){
        // Restrict on transactions of higher quality
        return  transactions.stream().filter((t)-> t.allPredictionsFromBetterQualityRules(rule)).collect(Collectors.toSet());
    }

    /**
     * Keep the transactions to only those generated with other rules
     * @param transactions
     * @param rule
     * @return
     */
    public static Set<Transaction> filterOtherRulesPredictions(Collection<Transaction> transactions,AssocRuleWithExceptions rule){
        // Restrict on transactions of higher quality
        return  transactions.stream().filter((t)-> t.allPredicationFromDifferentRules(rule)).collect(Collectors.toSet());
    }

    /**
     * Return the transactions total count either nor weighted or weighted with respect to set of Items positive and negatives
     * @param transactions
     * @param positiveItems
     * @param negativeItems
     * @param weighted
     * @return
     */
    public static double getTransactionsCount(Collection<Transaction> transactions,int[]positiveItems,int[]negativeItems,boolean weighted) {

        return transactions.stream().mapToDouble((t)->{
            if(weighted)
                return t.getWeightedCount(positiveItems,negativeItems);
            else
            return t.getCount();}).sum();
    }

    public synchronized int getNextId() {
        distinctId++;
        return distinctId;

    }

    public void loadTransactions(InputStream transactionsStream) throws IOException {

        BufferedReader br = FileUtils.getBufferedUTF8Reader(transactionsStream);
        // read transactions file
        for (String line = br.readLine(); line != null && !line.isEmpty(); line = br.readLine()) {


            ItemsArray items = new ItemsArray(Transaction.parseIntItems(line));

            Transaction t = transactionsSet.get(items);
            // new Transactions
            if (t == null) {
                t = new Transaction(items.getItems());
                t.setId(getNextId());
                transactionsSet.put(items, t);
                final Transaction tr = t;
                // add to items
                Arrays.stream(items.getItems()).forEach((i) -> items2transactions.put(i, tr));
            } else {

                t.incrementCount();
            }


        }
        System.out.println(transactionsSet.values().size());


    }

    public Set<Transaction> getTransactionsWithItem(int itemId, boolean withPredictions) {

        if (!withPredictions)
            return items2transactions.get(itemId);
        else
            return Sets.union(items2transactions.get(itemId), predictedItems2transactions.get(itemId));
    }


    /**
     * Collects the transactions that contain items in withItems array and do not contain items from withoutItems.  For example to get transactions of (h <- b, not e). The input should be
     *
     * @param withItems contains the positive items {b,h}
     * @param withoutItems contains negated items {e}
     * @param withPredictions if we should consider predictions from other rules. We do not check predictions while find negated items transactions. Therefore, if a transaction has predicted e it will remain. Only, transactions with original e are excluded.
     *
     *
     * @return
     */
    public Set<Transaction> getTransactions(int[] withItems, int[] withoutItems, boolean withPredictions) {

        Set<Transaction> transactions = getTransactionsWithItem(withItems[0], withPredictions);
        transactions = filterTransactionsWith(transactions, withItems, 1, withPredictions);

        //TODO if withPredictions is set, that means we need to KEEP the predictions.. so do not remove transactions if without Item exists in predictions
        // It is safer to fix it to false here
        transactions = filterOutTransactionsWith(transactions, withoutItems, 0, false/*!withPredictions*/);

//        return new HashSet<>(transactions);
        return transactions;
    }

    public Set<Transaction> filterTransactionsWith(Set<Transaction> transactions, int[] withItems, boolean withPredictions) {
        return filterTransactionsWith(transactions, withItems, 0, withPredictions);
    }

    public Set<Transaction> filterTransactionsWith(Set<Transaction> transactions, int[] withItems, int startIndex, boolean withPredictions) {
        if (withItems != null) {
            for (int i = startIndex; i < withItems.length && transactions.size() > 0; i++) {
                transactions = Sets.intersection(transactions, getTransactionsWithItem(withItems[i], withPredictions));
            }
            //transactions=new HashSet<>(transactions);
        }
        return transactions;
    }

    /**
     * @param transactions
     * @param excludedItems
     * @param evenInPrediction exclude transactions of the item even if it is in the prediction
     * @return
     */
    public Set<Transaction> filterOutTransactionsWith(Set<Transaction> transactions, int[] excludedItems, boolean evenInPrediction) {
        return filterOutTransactionsWith(transactions, excludedItems, 0, evenInPrediction);
    }

    /**
     * @param transactions
     * @param excludedItems
     * @param startIndex
     * @param evenInPrediction exclude transactions of the item even if it is in the prediction
     * @return
     */
    public Set<Transaction> filterOutTransactionsWith(Set<Transaction> transactions, int[] excludedItems, int startIndex, boolean evenInPrediction) {

        if (excludedItems != null) {
            for (int i = startIndex; i < excludedItems.length && transactions.size() > 0; i++) {


                // TODO (Recheck) currently, we say if evenInPrediction is set remove the transaction
                transactions = Sets.difference(transactions, getTransactionsWithItem(excludedItems[i], evenInPrediction));

            }

        }
        return transactions;
    }



    public void addPrediction(int item, Transaction transactionWithPrediction) {
        predictedItems2transactions.put(item, transactionWithPrediction);
    }

    /**
     * add transaction to set of predicted items in the materialization map
     *
     * @param items
     * @param transaction
     */
    public void addPredictions(int[] items, Transaction transaction) {
        Arrays.stream(items).forEach((i) -> addPrediction(i, transaction));
    }

    public Set<Transaction> getTransactionsFromExtendedDB(int[] withItems, int[] withoutItems) {

        return getTransactions(withItems, withoutItems, true);

    }

    static class ItemsArray {
        int[] items;

        public ItemsArray(int[] items) {
            setItems(items);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(items);
        }

        @Override
        public boolean equals(Object obj) {
            return Arrays.equals(((ItemsArray) obj).items, items);
        }

        public int[] getItems() {
            return items;
        }

        public void setItems(int[] items) {
            this.items = items;
            Arrays.sort(this.items);
        }
    }



}
