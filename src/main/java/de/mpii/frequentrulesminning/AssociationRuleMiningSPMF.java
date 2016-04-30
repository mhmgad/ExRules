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

    boolean encode; boolean decode; boolean filter; boolean withExceptions; double exceptionMinSupp; boolean materialize; boolean level2Filter;

    AssocRulesExtended.SortingType sortType;
    boolean showRulesWithExceptionsOnly;



    private AlgoFPGrowth fpgrowth ;
    AlgoAgrawalFaster94 algoAgrawal;
    RDF2IntegerTransactionsConverter rdf2TransactionsConverter;
    private String temporarySubjectsMappingFile;

    @Override
    public String toString() {
        return "AssociationRuleMiningSPMF{" +
                "minsupp=" + minsupp +
                ", minconf=" + minconf +
                ", maxconf=" + maxconf +
                ", encode=" + encode +
                ", decode=" + decode +
                ", filter=" + filter +
                ", withExceptions=" + withExceptions +
                ", exceptionMinSupp=" + exceptionMinSupp +
                ", materialize=" + materialize +
                ", level2Filter=" + level2Filter +
                ", configuration=" + configuration +
                ", cautiousMatrializationThreshold=" + cautiousMatrializationThreshold +
                ", exceptionRanking=" + exceptionRanking +
                ", outputSortType;"+sortType+
                ", showRulesWithExceptionsOnly"+showRulesWithExceptionsOnly+
                '}';
    }

    private String temperorayTransactiosFile;
    private String temporaryMappingFile;
    private YagoTaxonomy yagoTaxonomy;
    private boolean debugMatherialization;
    private String debugMaterializationFile;
    private SystemConfig configuration;
    private double cautiousMatrializationThreshold;

    public ExceptionRanker.Order getExceptionRanking() {
        return exceptionRanking;
    }

    public void setExceptionRanking(ExceptionRanker.Order exceptionRanking) {
        this.exceptionRanking = exceptionRanking;
    }

    private ExceptionRanker.Order exceptionRanking= ExceptionRanker.Order.LIFT;

//    private TransactionsDatabase transactionsDB;


    public AssociationRuleMiningSPMF(double minsupp,double minconf,double maxconf) {
        this.fpgrowth = new AlgoFPGrowth();
        this.minsupp=minsupp;
        this.minconf=minconf;
        this.maxconf=maxconf;
        this.algoAgrawal = new AlgoAgrawalFaster94();
        this.rdf2TransactionsConverter=new RDF2IntegerTransactionsConverter();
        this.yagoTaxonomy=YagoTaxonomy.getInstance();



    }

    public Itemsets getFrequentItemsets(String inputIntegerTransactionsFilePath) throws IOException {
        System.out.println("Start Mining Items... minSupport=" + minsupp);
        long startTime = System.nanoTime();
        Itemsets patterns = fpgrowth.runAlgorithm(inputIntegerTransactionsFilePath, null, minsupp);
        long estimatedTime = System.nanoTime() - startTime;
        fpgrowth.printStats();
        System.out.println("Start Rule Mining ... estimatedTime= "+estimatedTime);
        return patterns;

    }

    public int getDatabaseSize(){
        int databaseSize = fpgrowth.getDatabaseSize();
        return databaseSize;
    }


    public AssocRulesExtended getFrequentAssociationRules(Itemsets frequentItemsets) throws IOException {
        System.out.println("Start Rule Mining ...");
        long startTime = System.nanoTime();
        List<AssocRule> rulesGenerated = algoAgrawal.runAlgorithm(frequentItemsets, null, getDatabaseSize(), minconf).getRules();
        AssocRulesExtended rules=new AssocRulesExtended();

        rulesGenerated.forEach((r)-> rules.addRule(new AssocRuleWithExceptions(AssocRuleWithExceptions.getNextID(),r.getItemset1(),r.getItemset2(),r.getCoverage(),r.getAbsoluteSupport(),r.getConfidence(),r.getLift())));
        algoAgrawal.printStats();
        long estimatedTime = System.nanoTime() - startTime;
        System.out.println("Done Rule Mining ... estimatedTime= "+estimatedTime);
        return rules;
    }


    public AssocRulesExtended getFrequentAssociationRules(String inputRDFFile, String mappingFilePath/*, boolean encode, boolean decode, boolean filter, boolean withExceptions, double exceptionMinSupp, boolean materialize, boolean level2Filter*/) throws Exception {
        System.out.println("-----------------------------------------------------");
        System.out.println("Start: "+toString());
        long startTime = System.nanoTime();
        System.out.println("-----------------------------------------------------");
        // encode if required and get data file path
        String transactionsFilePath=encodeData(inputRDFFile,encode);

        TransactionsDatabase transactionsDB=new TransactionsDatabase(transactionsFilePath);

        // mine Frequent patterns
        Itemsets frequentPatterns=getFrequentItemsets(transactionsFilePath);

        // generate Association rules
        AssocRulesExtended rules=getFrequentAssociationRules(frequentPatterns);

        if(filter) {
            filterRules(rules);

        }

        // decode
        decodeRules( rules, mappingFilePath,decode,!encode);

        if(decode&&level2Filter){
            filterRulesMimickingHierarchy(rules);

        }

       // distribute Transactions

        computeSupportingTransactions(rules,transactionsDB);
        rulesQuality(rules,transactionsDB);

        //rules.sort(AssocRulesExtended.SortingType.HEAD_CONF);
        if(withExceptions){
            mineExceptions(rules,transactionsDB,exceptionMinSupp);
        }


        // materialize predictions of the rules
        if(materialize) {
            materialize(rules, transactionsDB);
        }


        if(withExceptions) {
            // predictable transactions
//            computeSafePredictableTransactions(rules,transactionsDB);
            rankException(rules,transactionsDB);
        }

        revisetedRuleQuality(rules,transactionsDB);

        long estimatedTime = System.nanoTime() - startTime;
        System.out.println("Done! -----------------------------------------------------  estimatedTime= "+estimatedTime);
        return rules;
    }

    private void revisetedRuleQuality(AssocRulesExtended rules, TransactionsDatabase transactionsDB) {
        Evaluator eval=new Evaluator(transactionsDB);
        rules.getRules().parallelStream().forEach((r)->{
            final ExceptionItem e=r.getTopException();
            r.setRevisedConfidence(e==null? r.getConfidence():eval.confidence(r,e));
            r.setRevisedLift(e==null? r.getLift():eval.lift(r,e));
            r.setRevisedJaccardCoefficient(e==null? r.getJaccardCoefficient():eval.JaccardCoefficient(r,e));
        });

    }

    public void materialize(AssocRulesExtended rules,TransactionsDatabase transactionsDB) throws Exception {
        System.out.println("Materialization ... ");
        long startTime = System.nanoTime();
        Materializer materializer=new Materializer(transactionsDB,cautiousMatrializationThreshold,debugMatherialization,debugMaterializationFile);
        materializer.materialize(rules.getRules(),true,true);
        long estimatedTime = System.nanoTime() - startTime;


        System.out.println("Done Materialization ! estimatedTime= "+estimatedTime);

    }

//    private void computeSafePredictableTransactions(AssocRulesExtended rules, TransactionsDatabase transactionsDB) {
//        rules.getRules().parallelStream().forEach(r -> {
//            r.setSafePredictableTransactions(transactionsDB.filterOutTransactionsWith(r.getPredicatableTransactions(), r.getExceptionsCandidatesInts(),false));
//
//        });
//
//    }

    private void rulesQuality(AssocRulesExtended rules, TransactionsDatabase transactionsDB) {
        System.out.println("Rules Quality.. ");
        long startTime = System.nanoTime();

        Evaluator eval=new Evaluator(transactionsDB);
        rules.getRules().parallelStream().forEach(r -> {
                  r.setLift(eval.lift(r));
//            r.setCoverage(eval.coverage(r));
            r.setNegConfidence(eval.negativeRuleConfidence(r));
            r.setJaccardCoefficient(eval.JaccardCoefficient(r));

        });

        long estimatedTime = System.nanoTime() - startTime;
        System.out.println("Done Rules  Quality! .... estimatedTime= "+estimatedTime);

    }

    private void computeSupportingTransactions(AssocRulesExtended rules, TransactionsDatabase transactionsDB) {
        System.out.println("Resolving Transactions.. ");
        long startTime = System.nanoTime();

//        Evaluator eval=new Evaluator(transactionsDB);
        rules.getRules().parallelStream().forEach(r -> {
            r.setBodyTransactions(transactionsDB.getTransactions(r.getBody(),null,false));
            r.setHeadTransactions(transactionsDB.getTransactions(r.getHead(),null,false));
            r.setHornRuleTransactions(transactionsDB.filterTransactionsWith(r.getBodyTransactions(),r.getHead(),false));
//            r.setPredictableTransactions(transactionsDB.filterOutTransactionsWith(r.getHornRuleTransactions(), r.getHead(),false));

//            r.setLift(eval.lift(r));
////            r.setCoverage(eval.coverage(r));
//            r.setNegConfidence(eval.negativeRuleConfidence(r));
//            r.setJaccardCoefficient(eval.JaccardCoefficient(r));

        });

        long estimatedTime = System.nanoTime() - startTime;
        System.out.println("Done Resolving Transactions! .... estimatedTime= "+estimatedTime);

    }



    private void rankException(AssocRulesExtended rules, TransactionsDatabase transactionsDB)
    {
        System.out.println("Re-rank Exceptions.. ");
        long startTime = System.nanoTime();

        Evaluator evaluator=new Evaluator(transactionsDB);
        evaluator.setCountPrediction(configuration.isMaterialization());
        evaluator.setUseWeights(configuration.isWeight());
        evaluator.setUseOrder(configuration.isOrder());


        ExceptionRanker ranker=new ExceptionRanker(evaluator,this.exceptionRanking);

        ranker.rankExceptions(rules);

        long estimatedTime = System.nanoTime() - startTime;
        System.out.println("Done Re-rank Exceptions! ... estimatedTime= "+estimatedTime);

    }

    private void mineExceptions( AssocRulesExtended rules,  TransactionsDatabase transactionsDB,double exceptionMinSupp) throws IOException {
        System.out.println("Start Mining Exception Candidates ...");
        long startTime = System.nanoTime();
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
        long estimatedTime = System.nanoTime() - startTime;
        System.out.println("Done Mining Exception Candidates! ..  estimatedTime= "+estimatedTime);
    }




    private void filterRulesMimickingHierarchy(AssocRulesExtended rules) {
        System.out.println("Start Filtering 2...");
        long startTime = System.nanoTime();
        //rules.getRules().removeIf(rule -> isMimickingHierarchy((AssocRuleWithExceptions) rule));
        rules.removeRulesIf((AssocRuleWithExceptions rule) -> isMimickingHierarchy(rule));

        long estimatedTime = System.nanoTime() - startTime;
        System.out.println("Rules after filtering2: "+rules.getRulesCount());

        System.out.println("Done Filtering 2! ... estimatedTime= "+estimatedTime);
    }

    private void filterRules(AssocRulesExtended rules) {

        System.out.println("Start Filtering...");
        long startTime = System.nanoTime();
//        rules.getRules().removeIf(rule -> (rule.getConfidence() >= maxconf || rule.getItemset2().length>1||rule.getItemset1().length>4));
        rules.removeRulesIf(rule -> (rule.getConfidence() >= maxconf || rule.getItemset2().length>1||rule.getItemset1().length>4));
        removeContainingRules(rules);
        long estimatedTime = System.nanoTime() - startTime;

        System.out.println("Rules after filtering1: "+rules.getRulesCount());
        System.out.println("Done Filtering! ... estimatedTime= "+estimatedTime);

    }

    private void removeContainingRules(AssocRulesExtended rules) {

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



    public void exportRules(AssocRulesExtended rules, String outputFilePath/*, AssocRulesExtended.SortingType sortType,boolean showRulesWithExceptionsOnly*/) throws IOException {
        BufferedWriter bw=FileUtils.getBufferedUTF8Writer(outputFilePath);
        bw.write(rules.toString(sortType,showRulesWithExceptionsOnly));
        bw.close();


    }

    private void decodeRules(AssocRulesExtended rules, String mappingFilePath, boolean decode, boolean loadMapping) {

        if(!decode) {
            System.out.println("Skip Decoding ...");

        }

        if(loadMapping)
            rdf2TransactionsConverter.loadPredicateMappingFromFile(mappingFilePath);

        System.out.println("Start Decoding ...");
        long startTime = System.nanoTime();

        rules.getRules().parallelStream().forEach((r)->{
            r.setBodyItems(rdf2TransactionsConverter.convertIntegers2Strings(r.getBody()));
            r.setHeadItems(rdf2TransactionsConverter.convertIntegers2Strings(r.getHead()));
        });
//        for(AssocRuleWithExceptions r:rules.getRules()) {
//            r.setBodyItems(rdf2TransactionsConverter.convertIntegers2Strings(r.getItemset1()));
//            r.setHeadItems(rdf2TransactionsConverter.convertIntegers2Strings(r.getItemset2()));
//
//        }
        long estimatedTime = System.nanoTime() - startTime;
        System.out.println("Done Decoding! ... estimatedTime= "+estimatedTime);
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
            this.temporarySubjectsMappingFile=tmpDataFolder+"mapping_subjects_"+date+".tsv";

            transactionsFilePath=temperorayTransactiosFile;
            System.out.println("Start Encoding ...");
            rdf2TransactionsConverter.convertandSave(inFilePath,transactionsFilePath , temporaryMappingFile,temporarySubjectsMappingFile);
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

    public void setConfiguration(SystemConfig configuration) {
        this.configuration = configuration;
    }

    public void setCautiousMatrializationThreshold(double cautiousMatrializationThreshold) {
        this.cautiousMatrializationThreshold = cautiousMatrializationThreshold;
    }


    public void exportRulesForPrASP(AssocRulesExtended rules, String outputFilePath) throws IOException {
        BufferedWriter bw=FileUtils.getBufferedUTF8Writer(outputFilePath);
        bw.write(rules.toStringPrASP(sortType,1,showRulesWithExceptionsOnly));
        bw.close();


    }

    public void showStatistics(AssocRulesExtended rules,boolean export, String fileName) throws Exception {

        long revisedRulesCount=rules.getRevisedRuleCount();

        StringBuilder st=new StringBuilder();
        st.append(toString());
        st.append('\n');




        st.append("topK\ttype\tAvgConf\tdiff\tAvgConfRO\tdiff\tAvgLIFT\tdiff\tAvgLIFTRO\tdiff\tAvgJaccardCof\tdiff");
        st.append('\n');

        StringBuilder stAll=new StringBuilder();
        stAll.append("TopK\tMeasurement\tAll Rules\tRevised Rules Only\n");



        for(int i=1;i<=10;i++) {
            int k=(int)Math.ceil((i*0.1)*rules.size());


            double orgAvgConf= rules.getConfidenceStats(k, false,false).getAverage();
            double orgAvgConfRO= rules.getConfidenceStats(k, false,true).getAverage();

            double orgAvgLift = rules.getLiftStats(k, false,false).getAverage();
            double orgAvgLiftRO = rules.getLiftStats(k, false,true).getAverage();
            double orgAvgJaccardCoefficient = rules.getJaccardCoefficientStats(k, false,false).getAverage();;


            st.append(k+"\tBefore\t");
            st.append(String.format("%.5f",orgAvgConf)+"\t__\t");
            st.append(String.format("%.5f",orgAvgConfRO)+"\t__\t");
            st.append(String.format("%.6f", orgAvgLift)+"\t__\t");
            st.append(String.format("%.6f", orgAvgLiftRO)+"\t__\t");
            st.append(String.format("%.5f", orgAvgJaccardCoefficient)+"\t__\t");
            st.append('\n');

            double newAvgConfidence = rules.getConfidenceStats(k, true, false).getAverage();
            double newAvgConfidenceRO = rules.getConfidenceStats(k, true, true).getAverage();
            double newAvgLift = rules.getLiftStats(k, true,false).getAverage();
            double newAvgLiftRO = rules.getLiftStats(k, true,true).getAverage();


            double newAvgJaccardCoefficient = rules.getJaccardCoefficientStats(k, true,false).getAverage();;

            st.append(k+"\tAfter\t");
            st.append(String.format("%.5f", newAvgConfidence)+"\t");
            st.append(String.format("%.5f", newAvgConfidence-orgAvgConf)+"\t");
            st.append(String.format("%.5f", newAvgConfidenceRO)+"\t");
            st.append(String.format("%.5f", newAvgConfidenceRO-orgAvgConfRO)+"\t");

            st.append(String.format("%.6f", newAvgLift)+"\t");
            st.append(String.format("%.6f", newAvgLift-orgAvgLift)+"\t");
            st.append(String.format("%.6f", newAvgLiftRO)+"\t");
            st.append(String.format("%.6f", newAvgLiftRO-orgAvgLiftRO)+"\t");
            st.append(String.format("%.5f", newAvgJaccardCoefficient)+"\t");
            st.append(String.format("%.5f", newAvgJaccardCoefficient-orgAvgJaccardCoefficient)+"\t");
            st.append('\n');

            st.append("--------------------------------------------------------------------\n");


            st.append('\n');


            //-------------------

            stAll.append(k+"\t");
            stAll.append("Confidence\t");
            stAll.append(rules.getConfidenceDiffStats(k,false).toString().replaceAll("DoubleSummaryStatistics","")+"\t");
            stAll.append(rules.getConfidenceDiffStats(k,true).toString().replaceAll("DoubleSummaryStatistics","")+"\n");
            stAll.append("\tLift\t");
            stAll.append(rules.getLiftDiffStats(k,false).toString().replaceAll("DoubleSummaryStatistics","")+"\t");
            stAll.append(rules.getLiftDiffStats(k,true).toString().replaceAll("DoubleSummaryStatistics","")+"\n");
            stAll.append("\tJaccard\t");
            stAll.append(rules.getJaccardDiffStats(k,false).toString().replaceAll("DoubleSummaryStatistics","")+"\t");
            stAll.append(rules.getJaccardDiffStats(k,true).toString().replaceAll("DoubleSummaryStatistics","")+"\n");



            stAll.append("--------------------------------------------------------------------\n");



        }

        st.append("RO: revised only\n");




//        System.out.println(st.toString());
//        System.out.println(stAll.toString());
        if(export){
            BufferedWriter bw=FileUtils.getBufferedUTF8Writer(fileName);
            bw.write(st.toString());
            bw.close();

            BufferedWriter bw2=FileUtils.getBufferedUTF8Writer(fileName+".all");
            bw2.write(stAll.toString());
            bw2.close();
        }



    }


    public void showStatisticsRevisedRules(AssocRulesExtended rules,boolean export, String fileName) throws Exception {

        long revisedRulesCount=rules.getRevisedRuleCount();

        StringBuilder st=new StringBuilder();
        st.append(toString());
        st.append('\n');




        st.append("topK\ttype\tAvgConfRO\tdiff\tAvgLIFTRO\tdiff");
        st.append('\n');

        StringBuilder stAll=new StringBuilder();
        stAll.append("TopK\tMeasurement\tRevised Rules Only\n");



        for(int i=1;i<=10;i++) {
            int k=(int)Math.ceil((i*0.1)*revisedRulesCount);

            double orgAvgConfRO= rules.getRevisedRulesConfidenceStats(k, false).getAverage();
            double orgAvgLiftRO = rules.getRevisedRulesLiftStats(k, false).getAverage();



            st.append(k+"\tBefore\t");
            st.append(String.format("%.5f",orgAvgConfRO)+"\t__\t");
            st.append(String.format("%.6f", orgAvgLiftRO)+"\t__\t");
            st.append('\n');


            double newAvgConfidenceRO = rules.getRevisedRulesConfidenceStats(k, true).getAverage();

            double newAvgLiftRO = rules.getRevisedRulesLiftStats(k, true).getAverage();

            st.append(k+"\tAfter\t");
            st.append(String.format("%.5f", newAvgConfidenceRO)+"\t");
            st.append(String.format("%.5f", newAvgConfidenceRO-orgAvgConfRO)+"\t");
            st.append(String.format("%.6f", newAvgLiftRO)+"\t");
            st.append(String.format("%.6f", newAvgLiftRO-orgAvgLiftRO)+"\t");

            st.append('\n');

            st.append("--------------------------------------------------------------------\n");


            st.append('\n');


            //-------------------

            stAll.append(k+"\t");
            stAll.append("Confidence\t");
            stAll.append(rules.getRevisedRulesConfidenceDiffStats(k).toString().replaceAll("DoubleSummaryStatistics","")+"\n");
            stAll.append("\tLift\t");

            stAll.append(rules.getRevisedRulesLiftDiffStats(k).toString().replaceAll("DoubleSummaryStatistics","")+"\n");

            stAll.append("\tExceptions RO\t");
            stAll.append(rules.getExceptionsStats(k,true).toString().replaceAll("IntSummaryStatistics","")+"\n");

            int[] numArray=rules.getRules().stream().filter(AssocRuleWithExceptions::hasExceptions).mapToInt(AssocRuleWithExceptions::getExceptionCandidatesSize).toArray();
            Arrays.sort(numArray);
            double median;
            if (numArray.length % 2 == 0)
                median = ((double)numArray[numArray.length/2] + (double)numArray[numArray.length/2 - 1])/2;
            else
                median = (double) numArray[numArray.length/2];
//            double medianValue = median.evaluate(//?????);
            stAll.append("\tMedian\t"+median+"\n");
            stAll.append("\tExceptions all\t");
            stAll.append(rules.getExceptionsStats((int)Math.ceil((i*0.1)*rules.size()),false).toString().replaceAll("IntSummaryStatistics","")+"\n");
            stAll.append("--------------------------------------------------------------------\n");



        }

        st.append("RO: revised only\n");






//        System.out.println(st.toString());
//        System.out.println(stAll.toString());
        if(export){
            BufferedWriter bw=FileUtils.getBufferedUTF8Writer(fileName);
            bw.write(st.toString());
            bw.close();

            BufferedWriter bw2=FileUtils.getBufferedUTF8Writer(fileName+".all");
            bw2.write(stAll.toString());
            bw2.close();
        }



    }


    public double getMinsupp() {
        return minsupp;
    }

    public void setMinsupp(double minsupp) {
        this.minsupp = minsupp;
    }

    public double getMinconf() {
        return minconf;
    }

    public void setMinconf(double minconf) {
        this.minconf = minconf;
    }

    public double getMaxconf() {
        return maxconf;
    }

    public void setMaxconf(double maxconf) {
        this.maxconf = maxconf;
    }

    public boolean isEncode() {
        return encode;
    }

    public void setEncode(boolean encode) {
        this.encode = encode;
    }

    public boolean isDecode() {
        return decode;
    }

    public void setDecode(boolean decode) {
        this.decode = decode;
    }

    public boolean isFilter() {
        return filter;
    }

    public void setFilter(boolean filter) {
        this.filter = filter;
    }

    public boolean isWithExceptions() {
        return withExceptions;
    }

    public void setWithExceptions(boolean withExceptions) {
        this.withExceptions = withExceptions;
    }

    public double getExceptionMinSupp() {
        return exceptionMinSupp;
    }

    public void setExceptionMinSupp(double exceptionMinSupp) {
        this.exceptionMinSupp = exceptionMinSupp;
    }

    public boolean isMaterialize() {
        return materialize;
    }

    public void setMaterialize(boolean materialize) {
        this.materialize = materialize;
    }

    public boolean isLevel2Filter() {
        return level2Filter;
    }

    public void setLevel2Filter(boolean level2Filter) {
        this.level2Filter = level2Filter;
    }

    public double getCautiousMatrializationThreshold() {
        return cautiousMatrializationThreshold;
    }

    public AssocRulesExtended.SortingType getSortType() {
        return sortType;
    }

    public void setSortType(AssocRulesExtended.SortingType sortType) {
        this.sortType = sortType;
    }

    public boolean isShowRulesWithExceptionsOnly() {
        return showRulesWithExceptionsOnly;
    }

    public void setShowRulesWithExceptionsOnly(boolean showRulesWithExceptionsOnly) {
        this.showRulesWithExceptionsOnly = showRulesWithExceptionsOnly;
    }

    public void exportRulesForDLV(AssocRulesExtended rules, String dlvOutputFile) throws IOException {
        BufferedWriter bw=FileUtils.getBufferedUTF8Writer(dlvOutputFile);

        bw.write(rules.toStringdlvSafe(sortType,1,showRulesWithExceptionsOnly,true));
        bw.close();

        //Negated Facts
        BufferedWriter bwNeg=FileUtils.getBufferedUTF8Writer(dlvOutputFile+".neg");
        bwNeg.write(rules.toStringdlvSafe(sortType,1,showRulesWithExceptionsOnly,false));
        bwNeg.close();



    }

    public void exportDLVConflict(AssocRulesExtended rules, String dlvConflictFile)throws IOException {

        BufferedWriter bw=FileUtils.getBufferedUTF8Writer(dlvConflictFile);
        bw.write(rules.toStringdlvConflict());
        bw.close();
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
