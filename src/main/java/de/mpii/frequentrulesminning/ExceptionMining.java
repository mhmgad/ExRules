package de.mpii.frequentrulesminning;

import ca.pfv.spmf.algorithms.frequentpatterns.charm.AlgoDCharm_Bitset;
import ca.pfv.spmf.input.transaction_database_list_integers.TransactionDatabase;
import ca.pfv.spmf.patterns.itemset_array_integers_with_tids_bitset.Itemset;
import ca.pfv.spmf.patterns.itemset_array_integers_with_tids_bitset.Itemsets;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import de.mpii.frequentrulesminning.utils.*;
import gnu.trove.map.hash.TIntIntHashMap;
import mpi.tools.javatools.util.FileUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by gadelrab on 2/26/16.
 */
public class ExceptionMining {

    private  double exceptionMinSupp;
    private  RDF2IntegerTransactionsConverter converter;


    TransactionsDatabase transactionDB;

    public ExceptionMining(InputStream transactions, RDF2IntegerTransactionsConverter rdf2TransactionsConverter,double exceptionMinSupp) throws IOException {
        this(new TransactionsDatabase(transactions),rdf2TransactionsConverter,exceptionMinSupp);
    }




    public ExceptionMining(String transactionsFile,double exceptionMinSupp) throws IOException {

        this(new FileInputStream(transactionsFile),null,exceptionMinSupp);


    }

    public ExceptionMining(String transactionsFilePath, RDF2IntegerTransactionsConverter rdf2TransactionsConverter,double exceptionMinSupp) throws IOException {
        this( new FileInputStream(transactionsFilePath),rdf2TransactionsConverter,exceptionMinSupp);



    }

    public ExceptionMining(String transactionsFilePath, String rdf2IdMappingFilePath, double exceptionMinSupp) throws IOException {
        this(transactionsFilePath,new RDF2IntegerTransactionsConverter(),exceptionMinSupp);
        this.converter.loadPredicateMappingFromFile(rdf2IdMappingFilePath);

    }

    public ExceptionMining(String transactionsFilePath, RDF2IntegerTransactionsConverter rdf2TransactionsConverter) throws IOException {
        this(transactionsFilePath,rdf2TransactionsConverter,0.5D);
    }

    public ExceptionMining(TransactionsDatabase transactionsDatabase,RDF2IntegerTransactionsConverter rdf2TransactionsConverter, double exceptionMinSupp) {
        this.transactionDB=transactionsDatabase;
        this.exceptionMinSupp=exceptionMinSupp;
        this.converter=rdf2TransactionsConverter;
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

    private Set<Transaction> getNegativeTransactions(AssocRuleWithExceptions rule) {
        // transactions contain body
        Set<Transaction> transactions;
        if(rule.getBodyTransactions()==null ||rule.getHeadTransactions()==null) {

            int[] body = rule.getItemset1();
            int[] head = rule.getItemset2();

            transactions = transactionDB.getTransactions(body, head,false);
        }
        else
        {
            transactions=Sets.difference(rule.getBodyTransactions(),rule.getHeadTransactions());
        }


        return transactions;
    }


    private Set<Transaction> getPositiveTransactions(AssocRuleWithExceptions rule) {

        // transactions contain body
        Set<Transaction> transactions;
        if(rule.getHornRuleTransactions()==null){
        int [] body=rule.getItemset1();
        int [] head=rule.getItemset2();
            transactions = transactionDB.getTransactions(ArrayUtils.addAll(body,head),null,false);}
        else
        {
            transactions= rule.getHornRuleTransactions();
        }
        return transactions;
    }

    private List<Transaction> removeBodyItemsFromTransactions(AssocRuleWithExceptions rule,Set<Transaction> transactions) {
        int [] body=rule.getItemset1();
        // adding transactions to list after removing body items
        Set<Integer> bodyset= ImmutableSet.copyOf(Ints.asList(body));

        List<Transaction> out=new ArrayList<>(transactions.size());


        for (Transaction transaction:transactions) {
            Set <Integer> transDiff= Sets.difference(ImmutableSet.copyOf(transaction.getItemsAsList()),bodyset);
            if(!transDiff.isEmpty()){
                out.add(new Transaction(Ints.toArray(transDiff),transaction.getCount()));

            }
        }
        return out;
    }



    public  List<ItemsetString> mineExceptionsAsFrequentSetMining(AssocRuleWithExceptions rule) throws IOException {

        Set<Transaction> negativeTransactions = getNegativeTransactions(rule);
        Collection filteredNegTrans=removeBodyItemsFromTransactions(rule,negativeTransactions);

        List<List<Itemset>> patterns =getClosedFrequentItemSets(filteredNegTrans).getLevels();
        List<Itemset> patternsFlat =  patterns.stream().flatMap(List::stream).collect(Collectors.toList());
        List<ItemsetString> patternsFlatItems=new ArrayList<>(patternsFlat.size());
        for (Itemset it:patternsFlat) {
            patternsFlatItems.add(new ItemsetString(converter.convertIntegers2Strings(it.getItems()),it.getItems(),it.getAbsoluteSupport()));
        }
        


    return patternsFlatItems;
    }


    public List<ExceptionItem> mineExceptionsAsSetManipulation(AssocRuleWithExceptions rule) throws IOException {

        // Get negative transactions and remove the body items
        Set<Transaction> negativeTransactions = getNegativeTransactions(rule);


        // Get positive transactions and remove the body items
        Set<Transaction> PositiveTransactions = getPositiveTransactions(rule);



        return mineExceptionsAsSetManipulation(PositiveTransactions,negativeTransactions);
    }

    /**
     * mine exception candidates starting from positive and negative transactions
     * @param PositiveTransactions
     * @param negativeTransactions
     * @return
     * @throws IOException
     */
    public List<ExceptionItem> mineExceptionsAsSetManipulation(Set<Transaction> PositiveTransactions, Set<Transaction> negativeTransactions) throws IOException {

        // Get negative transactions and remove the body items
       // Set<Transaction> negativeTransactions = getNegativeTransactions(rule);
        // Disable filtering .. not required
        //Collection filteredNegTrans=removeBodyItemsFromTransactions(rule, negativeTransactions);


        // Get positive transactions and remove the body items

        //Set<Transaction> PositiveTransactions = getPositiveTransactions(rule);
        // Disable filtering .. not required
       // Collection filteredPosTrans=removeBodyItemsFromTransactions(rule, PositiveTransactions);

        // count items
        // Disable filtering .. not required
//        List<ExceptionItem> negTransItems = getItemsWithCount(filteredNegTrans);
//        List<ExceptionItem> posTransItems = getItemsWithCount(filteredPosTrans);

        // count items
        List<ExceptionItem> negTransItems = getItemsWithCount(negativeTransactions);
        List<ExceptionItem> posTransItems = getItemsWithCount(PositiveTransactions);


        // Get whatever exists in negative but not head
        Set<ExceptionItem> diff=Sets.difference(ImmutableSet.copyOf(negTransItems),ImmutableSet.copyOf(posTransItems));


        List<ExceptionItem> diffList=new ArrayList<>(diff);
        // filter based on support
        diffList.removeIf((x)-> x.getRelativeSupport()<this.exceptionMinSupp);

        return diffList;
    }




    private List<ExceptionItem> getItemsWithCount(Collection<Transaction> transactions) {
        TIntIntHashMap itemsCount=new TIntIntHashMap();
        int totalCount=0;
        for(Transaction t:transactions){
            int count=t.getCount();
            totalCount+=count;
            for(int i: t.getItems()){
                itemsCount.adjustOrPutValue(i,count,count);
            }
        }
        List<ExceptionItem> output=new ArrayList<>(itemsCount.size());

        for (int itemId:itemsCount.keys()) {
            output.add(new ExceptionItem(new Item[]{converter.convertInteger2Item(itemId)},new int[]{itemId},itemsCount.get(itemId),totalCount));

        }
        return output;

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


    public static void main(String[] args) throws IOException {


        List<Transaction> ts = new ArrayList<>();
        ts.add(new Transaction("1 2 3", 3));
        ts.add(new Transaction("1 2"));
        ts.add(new Transaction("1 2"));
        ts.add(new Transaction("1 2 4", 10));

        ExceptionMining.writeToTmpFile(ts);

    }




}
