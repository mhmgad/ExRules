package de.mpii.frequentrulesminning.utils;

import com.google.common.base.Joiner;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import mpi.tools.basics3.Fact;
import mpi.tools.basics3.FactSource;
import mpi.tools.javatools.filehandlers.UTF8Reader;
import mpi.tools.javatools.util.FileUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by gadelrab on 2/22/16.
 */
public class RDF2IntegerTransactionsConverter {





    private HashBiMap<Item,Integer> items2Ids;
    private BiMap<Integer,Item> id2Item;
    private SetMultimap<String, Integer> subjects2ItemsIds;
    private int itemsIDs;


    public RDF2IntegerTransactionsConverter() {

        items2Ids = HashBiMap.create();
        subjects2ItemsIds = HashMultimap.create();
        itemsIDs=0;
    }

    private void loadRDFFile(String filePath) {
        UTF8Reader fileReader;
        try {
            //String line;

            for (Fact f : FactSource.from(filePath)) {
                String value = f.getSubject();

                //String key=f.getRelation()+"\t"+f.getObject();
                Item key=new Item(f.getRelation(),f.getObject());
                if (!items2Ids.containsKey(key)) {
                    itemsIDs++;
                    items2Ids.put(key,itemsIDs);
                }



                int id= items2Ids.get(key);
                subjects2ItemsIds.put(value, id);
            }
             id2Item= items2Ids.inverse();
            System.out.println("Items size: " + items2Ids.size());
            System.out.println("Transactions Size: "+subjects2ItemsIds.size());

        } catch (MalformedURLException e2) {
            e2.printStackTrace();
        }
    }

    public void exportTransactions(String outputFilePath) {
//        Map<String, Collection<Integer>> entriesMap = subjects2ItemsIds.asMap();
        try {
            BufferedWriter bw = FileUtils.getBufferedUTF8Writer(outputFilePath);

            for (String t : subjects2ItemsIds.keys()) {
                String transactionText = Joiner.on(' ').join(subjects2ItemsIds.get(t));
                bw.write(transactionText);
                bw.newLine();
            }
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void exportIds(String idsFilePath) {
        try {
            BufferedWriter bw = FileUtils.getBufferedUTF8Writer(idsFilePath);
            //List<String> keysList= new ArrayList<>(items2Ids.keySet());
            List<Item> keysList= new ArrayList<>(items2Ids.keySet());
            Collections.sort(keysList);
           // for (String item : keysList) {
            for (Item item : keysList) {
                String itemText = items2Ids.get(item) + "\t" + item;//.toString();
                bw.write(itemText);
                bw.newLine();
            }
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void convert(String rdfFilePath){
        System.out.println("Loading RDF data ...");
        loadRDFFile(rdfFilePath);
        System.out.println("Done!");


    }

    public void convertandSave(String rdfFilePath,String outputTransactionsFilePath,String outputMappingPath){
        convertandSave( rdfFilePath, outputTransactionsFilePath);

        System.out.println("Writing Mapping ...");
        exportIds(outputMappingPath);
        System.out.println("Done!");

    }

    private void convertandSave(String rdfFilePath, String outputTransactionsFilePath) {
        convert(rdfFilePath);

        System.out.println("Writing Transactions ...");
        exportTransactions(outputTransactionsFilePath);
        System.out.println("Done!");

    }

//    public void convertIntegers2Strings(String inputTransactionFilePath, String inputMappingFilePath,String outputTransactionFilePath){
//
//        loadMappingFromFile(inputMappingFilePath);
//
//        convertIntegers2Strings(inputTransactionFilePath,outputTransactionFilePath);
//
//
//    }

    public Item[]convertIntegers2Strings(int [] itemsList){

        Item[] itemsStrList=new Item[itemsList.length];
        for (int i=0; i<itemsList.length;i++){
            itemsStrList[i]=convertInteger2Item(itemsList[i]);

        }

        return itemsStrList;

    }

    public int[]convertItems2Integer(Item [] itemsList){

        int[] itemsInt=new int[itemsList.length];
        for (int i=0; i<itemsList.length;i++){
            itemsInt[i]= convertItem2Integer(itemsList[i]);

        }

        return itemsInt;

    }


    public int convertItem2Integer(Item item){
        return items2Ids.get(item);
    }

    public Item convertInteger2Item(int i){
        return id2Item.get(i);
    }




//    private void convertIntegers2Strings(String inputTransactionFilePath, String outputTransactionFilePath) {
//        try {
//            BiMap<Integer,Item> id2Item=items2Ids.inverse();
//            System.out.println("Reading Mapping ...");
//            BufferedReader br= FileUtils.getBufferedUTF8Reader(inputTransactionFilePath);
//            for(String line=br.readLine();line!=null;line=br.readLine()){
//               // String[] parts=line.split("\\s");
//                //Collections.
////                ArrayList<Item> itemsList=new ArrayList<>(parts.length);
////                for (String part:parts) {
////                    itemsList.add(id2Item.get(Integer.getInteger(part)));
////
////                }
//                String [] parts=splitLine(line);
//
//            }
//            System.out.println("Done!");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }

    private String[] splitLine(String line) {
    String [] parts=new String[2];


        String pattern="(\\s)*(\\d+\\s)+(==>)(\\d+\\)";
        return parts;
    }

    public void loadMappingFromFile(String inputMappingFilePath) {
        try {
            System.out.println("Reading Mapping ...");
            BufferedReader br= FileUtils.getBufferedUTF8Reader(inputMappingFilePath);
            for(String line=br.readLine();line!=null;line=br.readLine()){
                String[] parts=line.split("\t");
                items2Ids.put(Item.fromString(parts[1]),Integer.valueOf(parts[0]));

            }
            id2Item= items2Ids.inverse();
            System.out.println("Done!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





    private void convertToPrASP(String rdfInputFile, String PrASPFile) {
        loadRDFFile(rdfInputFile);

        try {
            BufferedWriter bw = FileUtils.getBufferedUTF8Writer(PrASPFile);

            for (String t : subjects2ItemsIds.keys()) {
                final String predicateName=Item.readableSubject(t);
                List<String> itemsAsPrASP=subjects2ItemsIds.get(t).stream().map((i)-> id2Item.get(i).toStringPrASPWithPredicate(predicateName)).collect(Collectors.toList());
                String transactionText = Joiner.on(". ").join(itemsAsPrASP);
                bw.write(transactionText);
                bw.newLine();
            }
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void main(String [] args){

        RDF2IntegerTransactionsConverter cv=new RDF2IntegerTransactionsConverter();

        if(args[0].equals("spmf"))
        cv.convertandSave(args[1],args[2],args[3]);
        else
        if(args[0].equals("prasp"))
        cv.convertToPrASP(args[1],args[2]);

        //cv.convertandSave("data/facts_to_mine.tsv","data/facts_to_mine_integer_transactions.tsv","data/facts_to_mine_mapping.tsv");
    }


}
