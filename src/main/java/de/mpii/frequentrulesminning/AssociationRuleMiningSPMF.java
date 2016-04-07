package de.mpii.frequentrulesminning;

import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AlgoAgrawalFaster94;
import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AssocRule;
import ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth.AlgoFPGrowth;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemsets;
import de.mpii.frequentrulesminning.utils.*;
import de.mpii.yagotools.YagoTaxonomy;
import mpi.tools.javatools.util.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;


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
    private boolean debugMatherialization;
    private String debugMaterializationFile;

//    private TransactionsDatabase transactionsDB;


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
        List<AssocRule> rulesGenerated = algoAgrawal.runAlgorithm(frequentItemsets, null, getDatabaseSize(), minconf).getRules();
        AssocRulesExtended rules=new AssocRulesExtended();
        rulesGenerated.forEach((r)-> rules.addRule(new AssocRuleWithExceptions(r.getItemset1(),r.getItemset2(),r.getCoverage(),r.getAbsoluteSupport(),r.getConfidence(),r.getLift())));
        algoAgrawal.printStats();
        System.out.println("Done Rule Mining ...");
        return rules;
    }


    public AssocRulesExtended getFrequentAssociationRules(String inputRDFFile, String mappingFilePath, boolean encode, boolean decode, boolean filter, boolean withExceptions, double exceptionMinSupp) throws Exception {
        // encode if required and get data file path
        String transactionsFilePath=encodeData(inputRDFFile,encode);

        // mine Frequent patterns
        Itemsets frequentPatterns=getFrequentItemsets(transactionsFilePath);

        // generate Association rules
        AssocRulesExtended rules=getFrequentAssociationRules(frequentPatterns);

        if(filter) {
            filterRules(rules);
            System.out.println("Rules after filtering1: "+rules.getRulesCount());
        }

        // decode
        decodeRules( rules, mappingFilePath,decode,!encode);

        if(decode&&filter){
            filterAfterDecoding(rules);
            System.out.println("Rules after filtering2: "+rules.getRulesCount());
        }



        TransactionsDatabase transactionsDB=new TransactionsDatabase(transactionsFilePath);

        // distribute Transactions

        computeSupportingTransactions(rules,transactionsDB);

        //rules.sort(AssocRulesExtended.SortingType.HEAD_CONF);
        if(withExceptions){
            mineExceptions(rules,transactionsDB,exceptionMinSupp);
        }


        materialize(rules, transactionsDB);

        // predictable transactions
        computeSafePredictableTransactions(rules,transactionsDB);


        if(withExceptions) {
            evaluateRules(rules,transactionsDB);
        }


        return rules;
    }

    public void materialize(AssocRulesExtended rules,TransactionsDatabase transactionsDB) throws Exception {
        System.out.println("Materialization ... ");
        Materializer materializer=new Materializer(transactionsDB,debugMatherialization,debugMaterializationFile);
        materializer.materialize(rules.getRules(),true,true);
        System.out.println("Done Materialization !");

    }

    private void computeSafePredictableTransactions(AssocRulesExtended rules, TransactionsDatabase transactionsDB) {
        rules.getRules().parallelStream().forEach(r -> {
            r.setSafePredictableTransactions(transactionsDB.filterOutTransactionsWith(r.getPredicatableTransactions(), r.getExceptionsCandidatesInts(),false));

        });

    }

    private void computeSupportingTransactions(AssocRulesExtended rules, TransactionsDatabase transactionsDB) {
        System.out.println("Resolving Transactions.. ");
        rules.getRules().parallelStream().forEach(r -> {
            r.setBodyTransactions(transactionsDB.getTransactions(r.getBody(),null,false));
            r.setHeadTransactions(transactionsDB.getTransactions(r.getHead(),null,false));
            r.setHornRuleTransactions(transactionsDB.filterTransactionsWith(r.getBodyTransactions(),r.getHead(),false));
            r.setPredictableTransactions(transactionsDB.filterOutTransactionsWith(r.getHornRuleTransactions(), r.getHead(),false));
//            r.setSafePredictableTransactions(transactionsDB.filterOutTransactionsWith(r.getPredicatableTransactions(), r.getExceptionsCandidatesInts()));
            r.computeQualityMeasurements();
        });
        System.out.println("Done Resolving Transactions!");

    }

    private void evaluateRules(AssocRulesExtended rules, TransactionsDatabase transactionsDB) {
        System.out.println("Re-evaluate rules with Exceptions.. ");
        RulesEvaluator  evaluator=new RulesEvaluator(transactionsDB);


        // Evaluate individual Rules
        rules.evaluateIndividuals(evaluator);

        // Groups Evaluation
        //rules.evaluateHeadGroups(evaluator);

        System.out.println("Done Re-evaluating rules!");

    }

    private void mineExceptions( AssocRulesExtended rules,  TransactionsDatabase transactionsDB,double exceptionMinSupp) throws IOException {
        System.out.println("Start Mining Exception Candidates ...");
        ExceptionMining em=new ExceptionMining(transactionsDB,rdf2TransactionsConverter,exceptionMinSupp);

        rules.getRules().stream().parallel().forEach((rule)-> {
            List<ExceptionItem> exceptionCandidates= null;
            try {
                exceptionCandidates = em.mineExceptions2(rule);
                rule.setExceptionCandidates(exceptionCandidates);
            } catch (IOException e) {
                e.printStackTrace();
            }

            });
        System.out.println("Done Mining Exception Candidates!");
    }




    private void filterAfterDecoding(AssocRulesExtended rules) {
        System.out.println("Start Filtering 2...");
        this.yagoTaxonomy=YagoTaxonomy.getInstance();
        //rules.getRules().removeIf(rule -> isMimickingHierarchy((AssocRuleWithExceptions) rule));
        rules.removeRulesIf((AssocRuleWithExceptions rule) -> isMimickingHierarchy(rule));
        System.out.println("Done Filtering 2!");
    }

    private void filterRules(AssocRulesExtended rules) {

        System.out.println("Start Filtering...");

//        rules.getRules().removeIf(rule -> (rule.getConfidence() >= maxconf || rule.getItemset2().length>1||rule.getItemset1().length>4));
        rules.removeRulesIf(rule -> (rule.getConfidence() >= maxconf || rule.getItemset2().length>1||rule.getItemset1().length>4));
        removeContainingRules(rules);

        System.out.println("Done Filtering!");

    }

    private void removeContainingRules(AssocRulesExtended rules) {
        //Collections.sort(rules.getRules(),(c1, c2) -> ((AssocRule)c2).getItemset1().length - ((AssocRule)c1).getItemset1().length);
        rules.sortByBodyLength();

        for(int i=0;i<rules.getRules().size();i++){
            final int  c=i;
//            rules.getRules().removeIf(assocRule -> isSubsetBody(rules.getRules().get(c),assocRule));
            rules.removeRulesIf(assocRule -> isSubsetBody(assocRule,rules.getRules().get(c)));
        }

    }

    /**
     * Checks is body predicates are just subclassof the head predicate ..
     * currently supports only single predicate head rules
     * @param rule
     * @return
     */
    private boolean isMimickingHierarchy(AssocRuleWithExceptions rule) {
            Item head=rule.getHeadItems()[0];

        for (Item b :rule.getbodyItems()) {
            Set<String> parents=yagoTaxonomy.getParents(b.getObject());
            // Check body vs head
            if(b.getPredicate().equals( head.getPredicate())) {
                if(parents.contains(head.getObject()))
                    return true;
            }

            // check body vs other body elements
            if(Arrays.stream(rule.getbodyItems()).anyMatch((b2)-> ((b2!=b)&&b.getPredicate().equals( b2.getPredicate())&&(parents.contains(b2.getObject())))))
                return true;



        }
        return false;
    }

    /**
     * Checks if the second is subset from the first (should be flipped)
     * @param assocRule
     * @param superRule
     * @return
     */
    private boolean isSubsetBody(AssocRuleWithExceptions assocRule, AssocRuleWithExceptions superRule) {
        if(assocRule==superRule)
            return false;

        if(assocRule.getConfidence()!=superRule.getConfidence())
            return false;

        return assocRule.isSubsetOf(superRule);
    }



    public void exportRules(AssocRulesExtended rules, String outputFilePath, AssocRulesExtended.SortingType sortType,boolean showRulesWithExceptionsOnly) throws IOException {
        BufferedWriter bw=FileUtils.getBufferedUTF8Writer(outputFilePath);
        bw.write(rules.toString(sortType,showRulesWithExceptionsOnly));
        bw.close();


    }

    private void decodeRules(AssocRulesExtended rules, String mappingFilePath, boolean decode, boolean loadMapping) {

        if(!decode) {
            System.out.println("Skip Decoding ...");

        }

        if(loadMapping)
            rdf2TransactionsConverter.loadMappingFromFile(mappingFilePath);

        System.out.println("Start Decoding ...");
        for(AssocRuleWithExceptions r:rules.getRules()) {
            r.setBodyItems(rdf2TransactionsConverter.convertIntegers2Strings(r.getItemset1()));
            r.setHeadItems(rdf2TransactionsConverter.convertIntegers2Strings(r.getItemset2()));

        }
        System.out.println("Done Decoding!");
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
            temperorayTransactiosFile=tmpDataFolder+"transactions_"+date+".txt";
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

    public void setDebugMaterialization(boolean debugMatherialization, String debugMaterializationFile) {
        this.debugMatherialization=debugMatherialization;
        this.debugMaterializationFile=debugMaterializationFile;
    }


//    public static  void main(String [] args) throws IOException {
//
//
//        if(args.length<4){
//            System.out.println("Usage: rules_spmf.sh <infile> <outFile>  <minsupp> <minConf> <maxConf> [<Sorting (CONF|HEAD_CONF)> <encode(1|0)> <decode(1|0)> <rdf2intMapping file>] <withExceptions(0|1)> <Exception Minsupp>");
//            System.exit(0);
//        }
//
//        String inputFile=args[0];
//        String outputFilePath=args[1];
//
//
//        double minsupp = Double.valueOf(args[2]);
//        double minconf = Double.valueOf(args[3]);//0.01D;
//        double maxconf = Double.valueOf(args[4]);//0.01D;
//
//        double excepminSupp =0.5D;
//
//
//        boolean encode=true;
//        boolean decode=true;
//        boolean withExceptions=false;
//
//        String rdf2idsMappingFile=null;
//        AssocRulesExtended.SortingType outputSorting= AssocRulesExtended.SortingType.CONF;
//
//        if(args.length>5){
//            outputSorting=AssocRulesExtended.SortingType.valueOf(args[5]);
//        }
//
//        if(args.length>6){
//            encode=args[6].equals("1");
//        }
//        if(args.length>7){
//            decode=args[7].equals("1");
//        }
//        if(args.length>8){
//            rdf2idsMappingFile=args[8];
//        }
//
//        if(args.length>9){
//            withExceptions=args[9].equals("1");
//
//        }
//
//        if(args.length>10){
//            excepminSupp=Double.valueOf(args[10]);
//        }
//
//        boolean showRulesWithExceptionsOnly=false;
//
//        if(args.length>11){
//            showRulesWithExceptionsOnly=args[11].equals("1");
//
//        }
//
//
//        AssociationRuleMiningSPMF miner=new AssociationRuleMiningSPMF(minsupp,minconf,maxconf);
//
//        AssocRulesExtended rulesStrings=miner.getFrequentAssociationRules(inputFile,rdf2idsMappingFile,encode,decode,true, withExceptions,excepminSupp);
//        miner.exportRules(rulesStrings, outputFilePath,outputSorting,showRulesWithExceptionsOnly);
//
//    }


}
