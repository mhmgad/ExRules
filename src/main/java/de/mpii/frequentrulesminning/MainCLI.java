package de.mpii.frequentrulesminning;

import com.google.common.base.Joiner;
import de.mpii.frequentrulesminning.utils.AssocRulesExtended;
import org.apache.commons.cli.*;

/**
 * Created by gadelrab on 3/22/16.
 */
public class MainCLI {
    private  Options helpOptions;
    private DefaultParser parser;
    Options options;


    Option shouldEncodeOp;
    private Option shouldDecodeOp;
    private Option mineExceptionsOp;
    private Option minSupportOp;
    private Option minConfOp;
    private Option maxConfOp;
    private Option inputFileOp;
    private Option outputFileOp;
    private Option mappingFileOp;
    private Option excpMinSupportOp;
    private Option sortingOp;
    private Option outputRulesWithExceptionsOnlyOp;
    private Option helpOp;
    private Option debugMaterializationOp;
    private Option partialMaterializationOp;
    private Option filterOp;
    private Option filter2Op;
    private Option exceptionRankingOp;
    private Option weightsOp;

    public MainCLI() {
         options= new Options();
         helpOptions = new Options();
        parser = new DefaultParser();


    }

    public void defineOptions(){

        helpOp =new Option("h",false,"Show Help");
        helpOp.setLongOpt("help");
        helpOptions.addOption(helpOp);

        shouldEncodeOp =new Option("en",false,"Encode the input");
        options.addOption(shouldEncodeOp);

        shouldDecodeOp =new Option("de",false,"Decode the output");
        options.addOption(shouldDecodeOp);

        mineExceptionsOp =new Option("ex",false,"Mine the output");
        options.addOption(mineExceptionsOp);



        minSupportOp= Option.builder("minS").hasArg().desc("Minimum support for the rule(default=0.0001)").argName("MIN_SUPP_RATIO").build();
        options.addOption(minSupportOp);

        minConfOp=Option.builder("minConf").hasArg().desc("Minimum Confidence for the rule (default=0.001)").argName("MIN_CONF_RATIO").build();
        options.addOption(minConfOp);

        maxConfOp=Option.builder("maxConf").hasArg().desc("Maximum Confidence for the rule (default=1.0)").argName("MAX_CONF_RATIO").build();
        options.addOption(maxConfOp);


        inputFileOp=Option.builder("i").longOpt("input").hasArg().desc("Input file inform of RDF or Integer transactions").argName("file").required().build();
        options.addOption(inputFileOp);

        outputFileOp=Option.builder("o").longOpt("output").hasArg().desc("Input file inform of RDF or Integer transactions").argName("file").required().build();
        options.addOption(outputFileOp);

        mappingFileOp=Option.builder("m").longOpt("mapping_file").hasArg().desc("Mapping RDF to Integer").argName("file").build();
        options.addOption(mappingFileOp);


        excpMinSupportOp= Option.builder("exMinSup").hasArg().desc("Exception Minimum support for the rule").argName("EXCEPTION_MIN_SUPP_RATIO").build();
        options.addOption(excpMinSupportOp);

        sortingOp=Option.builder("s").longOpt("sorting").hasArg().desc("Output sorting("+ Joiner.on("|").join(AssocRulesExtended.SortingType.values())+")").argName("order").build();
        options.addOption(sortingOp);

        outputRulesWithExceptionsOnlyOp =new Option("expOnly",false,"Output rules with exceptions only");
        options.addOption(outputRulesWithExceptionsOnlyOp);





        debugMaterializationOp=Option.builder("dPM").longOpt("Debug_materialization").hasArg().desc("debug Materialization file").argName("file").build();
        options.addOption(debugMaterializationOp);

        partialMaterializationOp=Option.builder("pm").longOpt("materialization").hasArg(false).desc(" Use partial materialization" ).build();
        options.addOption(partialMaterializationOp);



        filterOp=Option.builder("f1").longOpt("first_filter").hasArg(false).desc(" first filter based on size (4 body atoms at most, 1 head and Max conf) " ).build();
        options.addOption(filterOp);

        filter2Op=Option.builder("f2").longOpt("second_filter").hasArg(false).desc(" Second filter based on type hierarchy " ).build();
        options.addOption(filter2Op);

        weightsOp=Option.builder("w").longOpt("weighted_transactions").hasArg(false).desc("Count transactions with weights. Only useful with Materialization" ).build();
        options.addOption(weightsOp);


        exceptionRankingOp=Option.builder("exRank").longOpt("exception_ranking").hasArg().desc("Exception ranking method("+ Joiner.on("|").join(ExceptionRanker.Order.values())+")").argName("order").build();
        options.addOption(exceptionRankingOp);


    }


    public void run(CommandLine cmd) throws Exception{


        // Defualts
        double minsupp = 0.0001;
        double minconf = 0.01;//0.01D;
        double maxconf = 1;//0.01D;



        if(cmd.hasOption(minSupportOp.getOpt())){
            minsupp = Double.valueOf(cmd.getOptionValue(minSupportOp.getOpt()));
        }

        if(cmd.hasOption(minConfOp.getOpt())){
            minconf = Double.valueOf(cmd.getOptionValue(minConfOp.getOpt()));
        }

        if(cmd.hasOption(maxConfOp.getOpt())){
            maxconf = Double.valueOf(cmd.getOptionValue(maxConfOp.getOpt()));
        }

        String inputFile=cmd.getOptionValue(inputFileOp.getOpt());

        String outputFilePath=cmd.getOptionValue(outputFileOp.getOpt());

        String rdf2idsMappingFile=cmd.getOptionValue(mappingFileOp.getOpt(),null);



        boolean encode=cmd.hasOption(shouldEncodeOp.getOpt());
        boolean decode=cmd.hasOption(shouldDecodeOp.getOpt());

        boolean withExceptions=cmd.hasOption(mineExceptionsOp.getOpt());

        double excepminSupp=0.01;

        if(cmd.hasOption(excpMinSupportOp.getOpt()))
            excepminSupp=Double.valueOf(cmd.getOptionValue(excpMinSupportOp.getOpt()));


        boolean showRulesWithExceptionsOnly=cmd.hasOption(outputRulesWithExceptionsOnlyOp.getOpt());


        AssocRulesExtended.SortingType outputSorting= AssocRulesExtended.SortingType.CONF;

        if(cmd.hasOption(sortingOp.getOpt()))
            outputSorting=AssocRulesExtended.SortingType.valueOf(cmd.getOptionValue(sortingOp.getOpt(),"CONF"));

        String debugMaterializationFile=null;
        if(cmd.hasOption(debugMaterializationOp.getOpt()))
            debugMaterializationFile=cmd.getOptionValue(debugMaterializationOp.getOpt());

        boolean materialize = cmd.hasOption(partialMaterializationOp.getOpt());

        boolean filter = cmd.hasOption(filterOp.getOpt());
        boolean level2Filter = cmd.hasOption(filter2Op.getOpt());

        ExceptionRanker.Order exceptionOrdering= ExceptionRanker.Order.valueOf(cmd.getOptionValue(sortingOp.getOpt(),ExceptionRanker.Order.LIFT.toString()));

        AssociationRuleMiningSPMF miner=new AssociationRuleMiningSPMF(minsupp,minconf,maxconf);
        miner.setDebugMaterialization(debugMaterializationFile!=null,debugMaterializationFile);


        boolean useWeightedTransactions= cmd.hasOption(weightsOp.getOpt());

        SystemConfig sConf=new SystemConfig(materialize,useWeightedTransactions,true);
        miner.setConfiguration(sConf);
        miner.setExceptionRanking(exceptionOrdering);
        AssocRulesExtended rulesStrings = miner.getFrequentAssociationRules(inputFile, rdf2idsMappingFile, encode, decode, filter, withExceptions, excepminSupp, materialize, level2Filter);
        miner.exportRules(rulesStrings, outputFilePath,outputSorting,showRulesWithExceptionsOnly);



    }

    public static void main(String [] args) throws Exception {


        MainCLI instance=new MainCLI();
        instance.defineOptions();
        instance.run(instance.parse(args));


    }

    private CommandLine parse(String[] args) throws ParseException {
//        CommandLine cmdHelp=parser.parse(helpOptions,args);
//        if(cmdHelp.hasOption(helpOp.getOpt())){
//            HelpFormatter formatter = new HelpFormatter();
//            formatter.printHelp( "mine_rules.sh", options );
//            System.exit(0);
//        }


        return parser.parse(options,args);
    }
}
