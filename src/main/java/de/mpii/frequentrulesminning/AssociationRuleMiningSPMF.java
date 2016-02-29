package de.mpii.frequentrulesminning;

import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AlgoAgrawalFaster94;
import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AssocRule;


import ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth.AlgoFPGrowth;

import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemsets;

import ca.pfv.spmf.patterns.itemset_array_integers_with_tids_bitset.Itemset;
import com.google.common.collect.*;

import com.google.common.primitives.Ints;
import de.mpii.frequentrulesminning.utils.AssocRuleString;
import de.mpii.frequentrulesminning.utils.AssocRulesExtended;
import de.mpii.frequentrulesminning.utils.ItemsetString;
import de.mpii.frequentrulesminning.utils.RDF2IntegerTransactionsConverter;

import de.mpii.yagotools.YagoTaxonomy;
import de.mpii.yagotools.utils.YagoRelations;
import mpi.tools.javatools.util.FileUtils;

import java.io.BufferedWriter;
import java.io.File;

import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by gadelrab on 2/19/16.
 */
public class AssociationRuleMiningSPMF {


    double minsupp;
    double minconf;
    double maxconf;



    private AlgoFPGrowth fpgrowth ;
    AlgoAgrawalFaster94 algoAgrawal;
    RDF2IntegerTransactionsConverter rdf2TransactionsConverter;
    private String temperorayTransactiosFile;
    private String temporaryMappingFile;
    private YagoTaxonomy yagoTaxonomy;


    public AssociationRuleMiningSPMF(double minsupp,double minconf,double maxconf) {
        this.fpgrowth = new AlgoFPGrowth();
        this.minsupp=minsupp;
        this.minconf=minconf;
        this.maxconf=maxconf;
        this.algoAgrawal = new AlgoAgrawalFaster94();
        this.rdf2TransactionsConverter=new RDF2IntegerTransactionsConverter();


    }

    public Itemsets getFrequentItemsets(String inputIntegerTransactionsFilePath) throws IOException {
        System.out.println("Start Mining Items...");
        Itemsets patterns = fpgrowth.runAlgorithm(inputIntegerTransactionsFilePath, (String)null, minsupp);
        System.out.println("Start Rule Mining ...");
        fpgrowth.printStats();
        return patterns;

    }

    public int getDatabaseSize(){
        int databaseSize = fpgrowth.getDatabaseSize();
        return databaseSize;
    }


    public AssocRulesExtended getFrequentAssociationRules(Itemsets frequentItemsets) throws IOException {
        System.out.println("Start Rule Mining ...");
        AssocRulesExtended rules=new AssocRulesExtended(algoAgrawal.runAlgorithm(frequentItemsets, null,getDatabaseSize(), minconf));
        algoAgrawal.printStats();
        System.out.println("Done Rule Mining ...");
        return rules;
    }


    public AssocRulesExtended getFrequentAssociationRules(String inputRDFFile,String mappingFilePath,boolean encode,boolean decode, boolean filter, boolean withExceptions) throws IOException {
        // encode if required and get data file path
        String transactionsFilePath=encodeData(inputRDFFile,encode);

        // mine Frequent patterns
        Itemsets frequentPatterns=getFrequentItemsets(transactionsFilePath);

        // generate Association rules
        AssocRulesExtended rules=getFrequentAssociationRules(frequentPatterns);

        if(filter)
            filterRules(rules);

        // decode
        rules=decodeRules( rules, mappingFilePath,decode,!encode);

        if(decode&&filter){
            filterAfterDecoding(rules);
        }


        //rules.sort(AssocRulesExtended.SortingType.HEAD_CONF);
        mineExceptions(transactionsFilePath,rules);

        return rules;
    }

    private void mineExceptions(String transactionsFilePath, AssocRulesExtended rules) throws IOException {
        System.out.println("Start Mining Exception Candidates ...");
        ExceptionMining em=new ExceptionMining(transactionsFilePath,rdf2TransactionsConverter);

        int i=0;
        for (AssocRule rule: rules.getRules()) {
            i++;
            List<ItemsetString> exceptionCandidates=em.mineExceptions(rule);


            ((AssocRuleString)rule).setExceptionCandidates(exceptionCandidates);


            if(i%1000==0)
                System.out.println(i+"/"+rules.getRules().size());

        }
        System.out.println("Done Mining Exception Candidates!");
    }


    private void filterAfterDecoding(AssocRulesExtended rules) {
        System.out.println("Start Filtering 2...");
        this.yagoTaxonomy=YagoTaxonomy.getInstance();
        rules.getRules().removeIf(rule -> isMimickingHierarchy((AssocRuleString) rule));
        System.out.println("Done Filtering 2!");
    }

    private void filterRules(AssocRulesExtended rules) {

        System.out.println("Start Filtering...");

        rules.getRules().removeIf(rule -> (rule.getConfidence() >= maxconf || rule.getItemset2().length>1||rule.getItemset1().length>4));

        removeContainingRules(rules);

        System.out.println("Done Filtering!");

    }

    private void removeContainingRules(AssocRulesExtended rules) {
        //Collections.sort(rules.getRules(),(c1, c2) -> ((AssocRule)c2).getItemset1().length - ((AssocRule)c1).getItemset1().length);
        rules.sortByBodyLength();

        for(int i=0;i<rules.getRules().size();i++){
            final int  c=i;
            rules.getRules().removeIf(assocRule -> isSubsetBody(rules.getRules().get(c),assocRule));
        }

    }

    /**
     * Checks is body predicates are just subclassof the head predicate ..
     * currently supports only single predicate head rules
     * @param rule
     * @return
     */
    private boolean isMimickingHierarchy(AssocRuleString rule) {
            Item head=rule.getHeadItems()[0];

        for (Item b :rule.getbodyItems()) {
            if(!b.getPredicate().equals( head.getPredicate()))
                continue;
            Set<String> parents=yagoTaxonomy.getParents(b.getObject());
            if(parents.contains(head.getObject()))
                return true;
        }
        return false;
    }


    private boolean isSubsetBody(AssocRule assocRule, AssocRule assocRule1) {
        if(assocRule==assocRule1)
            return false;

        if(assocRule.getConfidence()!=assocRule1.getConfidence())
            return false;

        Set<Integer> intersec=Sets.intersection(ImmutableSet.copyOf(Ints.asList(assocRule.getItemset1())),(ImmutableSet.copyOf(Ints.asList(assocRule1.getItemset1()))));
        if (intersec.size()==assocRule1.getItemset1().length)
            return true;

        return false;
    }



    public void exportRules(AssocRulesExtended rules, String outputFilePath, AssocRulesExtended.SortingType sortType) throws IOException {
        if(sortType!=null)
            rules.sort(sortType);
        BufferedWriter bw=FileUtils.getBufferedUTF8Writer(outputFilePath);
        bw.write(rules.toString(getDatabaseSize()));
        bw.close();


    }

    private AssocRulesExtended decodeRules(AssocRulesExtended rules, String mappingFilePath, boolean decode, boolean loadMapping) {

        if(!decode) {
            System.out.println("Skip Decoding ...");
            return rules;
        }

        if(loadMapping)
            rdf2TransactionsConverter.loadMappingFromFile(mappingFilePath);

        System.out.println("Start Decoding ...");
        AssocRulesExtended rulesStrings=new AssocRulesExtended("Frequent Rules");
        for(AssocRule r:rules.getRules()) {
            AssocRuleString rstr=new AssocRuleString(rdf2TransactionsConverter.convertIntegers2Strings(r.getItemset1()),rdf2TransactionsConverter.convertIntegers2Strings(r.getItemset2()),r.getItemset1(),r.getItemset2(),r.getCoverage(),r.getAbsoluteSupport(),r.getConfidence(),r.getLift());
            rulesStrings.addRule(rstr);
        }
        System.out.println("Done Decoding!");
        return rulesStrings;

    }


    public String encodeData(String inFilePath,boolean encode){

        String transactionsFilePath;
        // encoding from RDF to
        if(encode) {

            File inFile=new File(inFilePath);
            String tmpDataFolder=inFile.getParent()+File.separator+"tmp"+File.separator;


            File tmpDir=new File(tmpDataFolder);
            if (!tmpDir.exists()) {
                System.out.println("creating directory: " + tmpDataFolder);
                boolean result = false;

                try{
                    tmpDir.mkdir();
                    result = true;
                }
                catch(SecurityException se){
                    //handle it
                }
                if(result) {
                    System.out.println("DIR created");
                }
            }


            String date=new SimpleDateFormat("yyyyMMddhhmm").format(new Date());
            this.temperorayTransactiosFile=tmpDataFolder+"transactions_"+date+".txt";
            this.temporaryMappingFile=tmpDataFolder+"mapping_"+date+".tsv";

            transactionsFilePath=temperorayTransactiosFile;
            System.out.println("Start Encoding ...");
            rdf2TransactionsConverter.convertandSave(inFilePath,transactionsFilePath , temporaryMappingFile);
            System.out.println("Done Encoding!");
        }else
        {
            System.out.println("Skip encoding ...");
            transactionsFilePath=inFilePath;

        }
        return transactionsFilePath;
    }


    public static  void main(String [] args) throws IOException {


        if(args.length<4){
            System.out.println("Usage: rules_spmf.sh <infile> <outFile>  <minsupp> <minConf> <maxConf> [<Sorting (CONF|HEAD_CONF)> <encode(1|0)> <decode(1|0)> <rdf2intMapping file>] <withExceptions(0|1)>");
            System.exit(0);
        }

        String inputFile=args[0];
        String outputFilePath=args[1];


        double minsupp = Double.valueOf(args[2]);
        double minconf = Double.valueOf(args[3]);//0.01D;
        double maxconf = Double.valueOf(args[4]);//0.01D;


        boolean encode=true;
        boolean decode=true;
        boolean withExceptions=false;

        String rdf2idsMappingFile=null;
        AssocRulesExtended.SortingType outputSorting= AssocRulesExtended.SortingType.CONF;

        if(args.length>5){
            outputSorting=AssocRulesExtended.SortingType.valueOf(args[5]);
        }

        if(args.length>6){
            encode=args[6].equals("1");
        }
        if(args.length>7){
            decode=args[7].equals("1");
        }
        if(args.length>8){
            rdf2idsMappingFile=args[8];
        }

        if(args.length>9){
            withExceptions=args[9].equals("1");
        }


        AssociationRuleMiningSPMF miner=new AssociationRuleMiningSPMF(minsupp,minconf,maxconf);

        AssocRulesExtended rulesStrings=miner.getFrequentAssociationRules(inputFile,rdf2idsMappingFile,encode,decode,true, withExceptions);
        miner.exportRules(rulesStrings, outputFilePath,outputSorting);

    }

//    public String getTemperorayTransactiosFile() {
//        return temperorayTransactiosFile;
//    }

//    public String getTemporaryMappingFile() {
//        return temporaryMappingFile;
//    }
}
