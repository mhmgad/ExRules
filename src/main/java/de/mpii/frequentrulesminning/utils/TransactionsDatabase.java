package de.mpii.frequentrulesminning.utils;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import gnu.trove.map.hash.TIntDoubleHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import mpi.tools.javatools.util.FileUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by gadelrab on 3/8/16.
 */
public class TransactionsDatabase {
    private int[] items;

//    TObjectIntHashMap<Transaction> transactionsSet;

    static class  ItemsArray{
        int [] items;
        public ItemsArray(int [] items) {
            setItems(items);
        }

        public void setItems(int[] items) {
            this.items = items;
            Arrays.sort(this.items);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(items);
        }

        @Override
        public boolean equals(Object obj) {
            return Arrays.equals(((ItemsArray)obj).items,items);
        }

        public int[] getItems() {
            return items;
        }
    }

    HashMap<ItemsArray,Transaction> transactionsSet;


    int distinctId;

    SetMultimap<Integer,Transaction> items2transactions;


    SetMultimap<Integer,Transaction> predictedItems2transactions;


    public TransactionsDatabase(){
        this.items2transactions = HashMultimap.create();
//        this.transactionsSet = new TObjectIntHashMap<>();
        this.transactionsSet=new HashMap<>();
        this. predictedItems2transactions=HashMultimap.create();
        distinctId=0;

    }


    public synchronized int getNextId(){
        distinctId++;
        return distinctId;

    }

    public TransactionsDatabase(String transactionFilePath) throws IOException {

        this(new FileInputStream(transactionFilePath));


    }

    public TransactionsDatabase(InputStream transactions) throws IOException {
        this();

        loadTransactions(transactions);


    }

    public void loadTransactions(InputStream transactionsStream) throws IOException {

        BufferedReader br = FileUtils.getBufferedUTF8Reader(transactionsStream);
        // read transactions file
        for (String line = br.readLine(); line != null && !line.isEmpty(); line = br.readLine()) {


//            Transaction t=new Transaction(line);
//
//            if (transactionsSet.adjustOrPutValue(t, 1, 1) == 1) {
//                // add it to the map
//                for (int i:t.getItems()){
//                    items2transactions.put(i,t);
//                }
//            }

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
        // setCount and ID
//        transactionsSet.keySet().forEach((Transaction x)->  x.setCout(this.transactionsSet.get(x)));
//        for (Transaction t:transactionsSet.keySet()) {
//            t.setCout(this.transactionsSet.get(t));
//            t.setId(getNextId());
//        }


    }

    public Set<Transaction> getTransactionsWithItem(int itemId,boolean withPredictions){

        if(!withPredictions)
            return items2transactions.get(itemId);
        else
            return Sets.union(items2transactions.get(itemId),predictedItems2transactions.get(itemId));
    }

//    public Set<Transaction> getTransactions(int[] withItems,int [] withoutItems){
//        return getTransactions(withItems,withoutItems,false);
//    }

    public Set<Transaction> getTransactions(int[] withItems,int [] withoutItems, boolean withPredictions){

        Set<Transaction> transactions=getTransactionsWithItem(withItems[0],withPredictions);
        transactions = filterTransactionsWith(transactions, withItems, 1, withPredictions);

        //TODO if withPredictions is set, thats mean we need to KEEP the predictions.. so do not remove transactions if without exists in predictions
        transactions = filterOutTransactionsWith(transactions,withoutItems,0, !withPredictions );

//        return new HashSet<>(transactions);
        return transactions;
    }

    public Set<Transaction> filterTransactionsWith(Set<Transaction> transactions, int[] withItems,boolean withPredictions){
        return filterTransactionsWith(transactions,withItems,0,withPredictions);
    }

    public Set<Transaction> filterTransactionsWith(Set<Transaction> transactions, int[] withItems, int startIndex, boolean withPredictions) {
        if(withItems!=null) {
            for (int i = startIndex; i < withItems.length && transactions.size() > 0; i++) {
                transactions = Sets.intersection(transactions, getTransactionsWithItem(withItems[i],withPredictions));
            }
        //transactions=new HashSet<>(transactions);
        }
        return transactions;
    }

    public Set<Transaction> filterOutTransactionsWith(Set<Transaction> transactions, int[] excludedItems,boolean evenInPrediction){
        return filterOutTransactionsWith(transactions,excludedItems,0,evenInPrediction);
    }

    public Set<Transaction> filterOutTransactionsWith(Set<Transaction> transactions, int[] excludedItems, int startIndex, boolean evenInPrediction) {

        if(excludedItems!=null){
            for (int i=startIndex;i<excludedItems.length&&transactions.size()>0;i++){
                //transactions = Sets.difference(transactions, getTransactionsWithItem(excludedItems[i],withPredictions));

                // TODO (Recheck) currently, we say if evenInPrediction is set remove the transaction
                transactions = Sets.difference(transactions, getTransactionsWithItem(excludedItems[i],evenInPrediction));

            }
            //transactions=new HashSet<>(transactions);
        }
        return transactions;
    }


//    public int getTransactionsCount(int[] withItems,int [] withoutItems) {
//        return TransactionsDatabase.getTransactionsCount(getTransactions(withItems,withoutItems));//.stream().mapToInt(Transaction::getCount).sum();
//
//    }

    public static int getTransactionsCount(Collection<Transaction> transactions){
        return  transactions.stream().mapToInt(Transaction::getCount).sum();
    }


    public void addPrediction(int item,Transaction transactionWithPrediction){
        predictedItems2transactions.put(item,transactionWithPrediction);
    }

    /**
     * add transaction to set of predicted items in the materialization map
     * @param items
     * @param transaction
     */
    public void addPredictions(int[] items, Transaction transaction) {
        Arrays.stream(items).forEach((i)->addPrediction(i,transaction));
    }


    public Set<Transaction> getTransactionsFromExtendedDB(int [] withItems,int[] withoutItems){

        return getTransactions(withItems,withoutItems,true);

    }


}
