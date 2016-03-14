package de.mpii.frequentrulesminning.utils;

import ca.pfv.spmf.input.transaction_database_list_integers.TransactionDatabase;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import gnu.trove.map.hash.TByteByteHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import mpi.tools.javatools.util.FileUtils;

import java.io.*;
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
        for (int i=1;i<withItems.length;i++){
            transactions = Sets.intersection(transactions, getTransactionsWithItem(withItems[i]));
        }

        if(withoutItems!=null){
            for (int i=0;i<withoutItems.length;i++){
                transactions = Sets.difference(transactions, getTransactionsWithItem(withoutItems[i]));
            }
        }

        return new HashSet<Transaction>(transactions);
    }


    public int getTransactionsCount(int[] withItems,int [] withoutItems) {
        return getTransactions(withItems,withoutItems).size();

    }
}
