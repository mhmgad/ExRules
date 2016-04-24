package de.mpii.frequentrulesminning.utils;

import com.google.common.collect.*;
import mpi.tools.javatools.util.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.IntSummaryStatistics;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by gadelrab on 4/21/16.
 */
public class DLV2Transactions {


    Multimap<Integer,Item> conflicts2Count=HashMultimap.create();

    // TODO use CLI
    RDF2IntegerTransactionsConverter cv=new RDF2IntegerTransactionsConverter();




    static Pattern singleModelPattern = Pattern.compile(Pattern.quote("{") + "(.*?)" + Pattern.quote("}"));
    private HashMultimap<Integer, String> items2Subjects;
    private HashMultimap<Item,String> negativeItems2Subjects=HashMultimap.create();

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


        String[] modelStrings=getModelFactsStrings(args[1]);

        DLV2Transactions converter=new DLV2Transactions();
        converter.loadMappings(args[2],args[3]);
        converter.parseDLVOutput(modelStrings);

        converter.exportResults(RDF2IntegerTransactionsConverter.EncodingType.valueOf(args[0]),args[4]);

        converter.loadNegations(modelStrings);

        String exportStatsFile = args[1] + ".stats";
        converter.computeConflictsStats(modelStrings,exportStatsFile);


    }

    private void loadNegations(String[] modelStrings) {
        Arrays.stream(modelStrings).filter(s -> s.contains("not_")).forEach( confString -> {
                    negativeItems2Subjects.put(cv.fromDLVToItem(confString),cv.fromDLVSubject(confString));
                }

        );
    }

    private void exportResults(RDF2IntegerTransactionsConverter.EncodingType encodingType,String outputFile) {

        switch (encodingType){
            case SPMF:
                cv.exportTransactions(outputFile);
                break;
            case RDF:
                cv.exportToRDF(outputFile);
                break;
            case PrASP:
                cv.exportAsPrASP(outputFile);
                break;
            case NONE:
                break;

        }
    }

    private void parseDLVOutput(String[] modelStrings) throws IOException {

        cv.parseDLVOutput(modelStrings);

         items2Subjects= HashMultimap.create();
        Multimaps.invertFrom(cv.getSubjects2ItemsIds(),items2Subjects);

           }

    private void loadMappings(String predicateMapping, String subjectMapping) {
        cv.loadPredicateMappingFromFile(predicateMapping);
        cv.loadSubjectMappingFromFile(subjectMapping);


    }

    private  void computeConflictsStats( String[] modelStrings,String exportFile) throws  IOException{


        StringBuilder bf=new StringBuilder();


        Arrays.stream(modelStrings).filter(s -> s.contains("number_of_conflicts")).forEach( confString -> {
                    conflicts2Count.put(DLV2Transactions.extractCount(confString),cv.fromDLVToItem(confString));
                }

        );

        IntSummaryStatistics conflictsSummary=conflicts2Count.keys().stream().mapToInt(Integer::intValue).summaryStatistics();

        

        long positivePredictionsCount=Arrays.stream(modelStrings).filter(s -> !(s.contains("conflict")||s.startsWith("not_"))).count();
        long negativePredictionsCount=Arrays.stream(modelStrings).filter(s -> s.startsWith("not_")).count();

        long totalPredictionsCount=positivePredictionsCount+negativePredictionsCount;


        bf.append("#Conflicts\t");
        bf.append("#Predictions\t");
        bf.append("#Pos Predict\t");
        bf.append("#Neg Predict\t");
        bf.append("Max Conflict\t");
        bf.append("Min Conflict\t");
        bf.append("Avg Conflict\t");
        bf.append("uniq. PredicatesWithConflict");
        bf.append("uniq. Pos_Predictions");
        bf.append("uniq. Neg_Predictions");

        bf.append('\n');
        bf.append(conflictsSummary.getSum()+"\t");
        bf.append(totalPredictionsCount+"\t");
        bf.append(positivePredictionsCount+"\t");
        bf.append(negativePredictionsCount+"\t");
        bf.append(conflictsSummary.getMax()+"\t");
        bf.append(conflictsSummary.getMin()+"\t");
        bf.append(conflictsSummary.getAverage()+"\t");
        bf.append(conflictsSummary.getCount()+"\t");
        bf.append(items2Subjects.keySet().size()+"\t");
        bf.append(negativeItems2Subjects.keySet().size()+"\t");
        bf.append('\n');



        bf.append("Max_Conflict_Predicate\t"+conflicts2Count.get(conflictsSummary.getMax()));
        bf.append('\n');
        bf.append("Min_Conflict_Predicate\t"+conflicts2Count.get(conflictsSummary.getMin()));

        bf.append('\n');


        System.out.println(bf.toString());

        if(exportFile!=null) {
            BufferedWriter bw = FileUtils.getBufferedUTF8Writer(exportFile);
            bw.write(bf.toString());
            bw.close();
        }






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
