package de.mpii.frequentrulesminning;

import com.google.common.base.Joiner;


import de.mpii.frequentrulesminning.utils.AssocRulesExtended;
import org.apache.commons.cli.*;

/**
 * Created by gadelrab on 3/22/16.
 */
public class MainCLI {
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

    public MainCLI() {
         options= new Options();
        parser = new DefaultParser();


    }

    public void defineOptions(){
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

        sortingOp=Option.builder("s").longOpt("sorting").longOpt("sorting").hasArg().desc("Output sorting("+ Joiner.on("|").join(AssocRulesExtended.SortingType.values())+")").argName("file").build();
        options.addOption(sortingOp);

        outputRulesWithExceptionsOnlyOp =new Option("expOnly",false,"Output rules with exceptions only");
        options.addOption(outputRulesWithExceptionsOnlyOp);


        helpOp =new Option("expOnly",false,"Show Help");
        options.addOption(helpOp);




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


        AssociationRuleMiningSPMF miner=new AssociationRuleMiningSPMF(minsupp,minconf,maxconf);
        AssocRulesExtended rulesStrings = miner.getFrequentAssociationRules(inputFile, rdf2idsMappingFile, encode, decode, true, withExceptions, excepminSupp);
        miner.exportRules(rulesStrings, outputFilePath,outputSorting,showRulesWithExceptionsOnly);



    }

    public static void main(String [] args) throws Exception {


        MainCLI instance=new MainCLI();
        instance.defineOptions();
        instance.run(instance.parse(args));

//        if(args.length<4){
//            System.out.println("Usage: rules_spmf.sh <infile> <outFile>  <minsupp> <minConf> <maxConf> [<Sorting (CONF|HEAD_CONF)> <encode(1|0)> <decode(1|0)> <rdf2intMapping file>] <withExceptions(0|1)> <Exception Minsupp>");
//            System.exit(0);
//        }

//        String inputFile=args[0];
//        String outputFilePath=args[1];
//
//
//

//        double excepminSupp =0.5D;


//        boolean encode=true;
//        boolean decode=true;
//        boolean withExceptions=false;

//        String rdf2idsMappingFile=null;
//        AssocRulesExtended.SortingType outputSorting= AssocRulesExtended.SortingType.CONF;
//
//        if(args.length>5){
//            outputSorting=AssocRulesExtended.SortingType.valueOf(args[5]);
//        }

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

//        boolean showRulesWithExceptionsOnly=false;
//
//        if(args.length>11){
//            showRulesWithExceptionsOnly=args[11].equals("1");
//
//        }


//        AssociationRuleMiningSPMF miner=new AssociationRuleMiningSPMF(minsupp,minconf,maxconf);

//        AssocRulesExtended rulesStrings=miner.getFrequentAssociationRules(inputFile,rdf2idsMappingFile,encode,decode,true, withExceptions,excepminSupp);
//        miner.exportRules(rulesStrings, outputFilePath,outputSorting,showRulesWithExceptionsOnly);


    }

    private CommandLine parse(String[] args) throws ParseException {
        return parser.parse(options,args);
    }
}
