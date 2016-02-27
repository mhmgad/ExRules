package de.mpii.frequentrulesminning;

import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemset;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemsets;
import com.google.common.collect.*;
import com.google.common.primitives.Ints;
import de.mpii.frequentrulesminning.utils.AssocRuleString;
import de.mpii.frequentrulesminning.utils.AssocRulesExtended;
import de.mpii.frequentrulesminning.utils.Transaction;
import gnu.trove.map.hash.TIntObjectHashMap;
import mpi.tools.javatools.util.FileUtils;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * Created by gadelrab on 2/26/16.
 */
public class ExceptionMining {

    Multiset<Transaction> transactionsSet;

    SetMultimap<Integer,Transaction> items2transactions;


    public ExceptionMining(InputStream transactions) throws IOException {
        this.items2transactions = HashMultimap.create();
        this.transactionsSet=HashMultiset.create();
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
            if(!transactionsSet.contains(t)){
                // add it to the map
                for (int i:t.getItems()){
                    items2transactions.put(i,t);
                }
            }
            transactionsSet.add(t);
        }


    }

    private List<Transaction> getNegativeExamples(AssocRuleString rule){
        int [] body=rule.getItemset1();
        Set<Transaction> transactions=new HashSet<>(items2transactions.get(body[0]));
        for (int i=1;i<body.length;i++){
            transactions=Sets.intersection(transactions,items2transactions.get(body[i]));
        }

        int [] head=rule.getItemset2();
        for (int i=0;i<head.length;i++){
            transactions=Sets.difference(transactions,items2transactions.get(head[i]));
        }

        for (Transaction transaction:transactions){
            this.transactionsSet.
        }
    }


    public Itemsets mineExceptions(AssocRuleString rule){


    return null;
    }

}
