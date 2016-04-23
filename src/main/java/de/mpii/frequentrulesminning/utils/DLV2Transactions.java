package de.mpii.frequentrulesminning.utils;

import mpi.tools.javatools.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by gadelrab on 4/21/16.
 */
public class DLV2Transactions {



    static Pattern singleModelPattern = Pattern.compile(Pattern.quote("{") + "(.*?)" + Pattern.quote("}"));
    public static String[] getModelFactsStrings(String dlvOutputFile) throws IOException {

        //TODO only one answer
        String outputFileContent= FileUtils.getFileContent(new File(dlvOutputFile));
        Matcher subjectMatcher=singleModelPattern.matcher(outputFileContent);

        String[] modelPredicatesString=new String[0];
        if(subjectMatcher.find()) {
            String modelString = subjectMatcher.group(1);

            System.out.println("Model Found");

            modelPredicatesString = modelString.split(",");
        }else
        {
            System.out.println("No Model was Found");
        }
        return modelPredicatesString;
    }


//TODO move all processing related to DLV here
    public static void main(String [] args) throws IOException {

        // TODO use CLI
        RDF2IntegerTransactionsConverter cv=new RDF2IntegerTransactionsConverter();


        cv.loadPredicateMappingFromFile(args[2]);
        cv.loadSubjectMappingFromFile(args[3]);


        String[] modelStrings=getModelFactsStrings(args[1]);
        cv.parseDLVOutput(modelStrings);



        switch (RDF2IntegerTransactionsConverter.EncodingType.valueOf(args[0])){
            case SPMF:
                cv.exportTransactions(args[4]);
                break;
            case RDF:
                cv.exportToRDF(args[4]);
                break;
            case PrASP:
                cv.exportAsPrASP(args[4]);
                break;


        }
        computeConflictsStats(modelStrings);


    }

    private static void computeConflictsStats( String[] modelStrings) {

        IntSummaryStatistics summary = Arrays.stream(modelStrings).filter(s -> s.contains("number_of_conflicts")).mapToInt(DLV2Transactions::extractCount).summaryStatistics();
        System.out.println(summary.toString())


    }



    static Pattern countPattern = Pattern.compile(Pattern.quote("(") + "(.*?)" + Pattern.quote(")"));
    private static int extractCount(String fact) {
        Matcher countMatcher=countPattern.matcher(fact);

        if(countMatcher.find()) {
            return Integer.valueOf( countMatcher.group(1));
        }

        return 0;

    }
}
