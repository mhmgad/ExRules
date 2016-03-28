package de.mpii.frequentrulesminning.utils;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import gnu.trove.map.hash.TObjectIntHashMap;
import mpi.tools.javatools.util.FileUtils;

import java.io.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by gadelrab on 3/8/16.
 */
public class TransactionsDatabase {

    TObjectIntHashMap<Transaction> transactionsSet;

    SetMultimap<Integer,Transaction> items2transactions;

    public TransactionsDatabase(){
        this.items2transactions = HashMultimap.create();
        this.transactionsSet = new TObjectIntHashMap<>();
    }

    public TransactionsDatabase(String transactionFilePath) throws IOException {

        this(new FileInputStream(transactionFilePath));


    }

    public TransactionsDatabase(InputStream transactions) throws IOException {
        this();

        loadTransactions(transactions);


    }

    public void loadTransactions(InputStream transactionsStream) throws IOException {

        BufferedReader br= FileUtils.getBufferedUTF8Reader(transactionsStream);
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

        transactionsSet.keySet().forEach((Transaction x)->  x.setCout(this.transactionsSet.get(x)));

    }

    public Set<Transaction> getTransactionsWithItem(int itemId){
        return items2transactions.get(itemId);
    }

    public Set<Transaction> getTransactions(int[] withItems,int [] withoutItems){

        Set<Transaction> transactions=new HashSet<>(getTransactionsWithItem(withItems[0]));
        transactions = filterTransactionsWith(transactions, withItems, 1);

        transactions = filterOutTransactionsWith(transactions,withoutItems,0 );

        return new HashSet<>(transactions);
    }

    public Set<Transaction> filterTransactionsWith(Set<Transaction> transactions, int[] withItems){
        return filterTransactionsWith(transactions,withItems,0);
    }

    public Set<Transaction> filterTransactionsWith(Set<Transaction> transactions, int[] withItems, int startIndex) {
        if(withItems!=null) {
            for (int i = startIndex; i < withItems.length && transactions.size() > 0; i++) {
                transactions = Sets.intersection(transactions, getTransactionsWithItem(withItems[i]));
            }
        transactions=new HashSet<>(transactions);
        }
        return transactions;
    }

    public Set<Transaction> filterOutTransactionsWith(Set<Transaction> transactions, int[] excludedItems){
        return filterOutTransactionsWith(transactions,excludedItems,0);
    }

    public Set<Transaction> filterOutTransactionsWith(Set<Transaction> transactions, int[] excludedItems, int startIndex) {

        if(excludedItems!=null){
            for (int i=startIndex;i<excludedItems.length&&transactions.size()>0;i++){
                transactions = Sets.difference(transactions, getTransactionsWithItem(excludedItems[i]));

            }
            transactions=new HashSet<>(transactions);
        }
        return transactions;
    }


    public int getTransactionsCount(int[] withItems,int [] withoutItems) {
        return TransactionsDatabase.getTransactionsCount(getTransactions(withItems,withoutItems));//.stream().mapToInt(Transaction::getCount).sum();

    }

    public static int getTransactionsCount(Collection<Transaction> transactions){
        return  transactions.stream().mapToInt(Transaction::getCount).sum();
    }

}
