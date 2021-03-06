package de.mpii.frequentrulesminning.utils;

import com.google.common.collect.*;
import mpi.tools.javatools.util.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by gadelrab on 4/21/16.
 */
public class DLV2Transactions {


    SetMultimap<Item,String> conflict2subject=HashMultimap.create();
    SetMultimap<Integer,Item> count2Conflict =HashMultimap.create();

    // TODO use CLI
    RDF2IntegerTransactionsConverter cv=new RDF2IntegerTransactionsConverter();




    static Pattern singleModelPattern = Pattern.compile(Pattern.quote("{") + "(.*?)" + Pattern.quote("}"));
    private HashMultimap<Integer, String> items2Subjects;
    private HashMultimap<Integer,String> negativeItems2Subjects=HashMultimap.create();

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

        converter.loadNegations(modelStrings);

        if(args.length>4){
            if(args[4].equals("cautious")){
                converter.removeConflicts();
            }
        }

        converter.exportResults(RDF2IntegerTransactionsConverter.EncodingType.valueOf(args[0]),args[1]);



        String exportStatsFile = args[1] + ".stats";
        converter.computeConflictsStats(exportStatsFile);




    }

    private void removeConflicts() {
        Set<Integer> KeysIntersection=Sets.intersection(items2Subjects.keySet(),negativeItems2Subjects.keySet());

        for (Integer key:KeysIntersection) {
            for (String s:negativeItems2Subjects.get(key))
                items2Subjects.remove(key,s);

        }


    }

    private void loadNegations(String[] modelStrings) {
        Arrays.stream(modelStrings).filter(s -> s.contains("not_")).forEach( confString -> {
                    negativeItems2Subjects.put(cv.fromDLV2ItemId(confString),cv.fromDLV2Subject(confString));
                }

        );
    }

    private void exportResults(RDF2IntegerTransactionsConverter.EncodingType encodingType,String inputFile) {

        switch (encodingType){
            case SPMF:
                cv.exportTransactions(inputFile+".transactions");
                break;
            case RDF:
                cv.exportToRDF(inputFile+".tsv");
                break;
            case PrASP:
                cv.exportAsPrASP(inputFile+".prasp");
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

    private  void computeConflictsStats(String exportFile) throws  IOException{


        StringBuilder bf=new StringBuilder();

        Set<Integer> KeysIntersection=Sets.intersection(items2Subjects.keySet(),negativeItems2Subjects.keySet());

        for (Integer key:KeysIntersection) {
            Set<String> valueIntersection=Sets.intersection(items2Subjects.get(key),negativeItems2Subjects.get(key));
            int size=valueIntersection.size();
            if(size>0){
                count2Conflict.put(size,cv.convertInteger2Item(key));
            }

        }

        IntSummaryStatistics conflictsSummary= count2Conflict.keys().stream().mapToInt(Integer::intValue).summaryStatistics();

        

        long positivePredictionsCount=items2Subjects.size();//Arrays.stream(modelStrings).filter(s -> !(s.contains("conflict")||s.trim().startsWith("not_"))).count();
        long negativePredictionsCount=negativeItems2Subjects.size();//Arrays.stream(modelStrings).filter(s -> s.trim().startsWith("not_")).count();

        long totalPredictionsCount=positivePredictionsCount+negativePredictionsCount;


        bf.append("#Conflicts\t");
        bf.append("#Predictions\t");
        bf.append("#Pos Predict\t");
        bf.append("#Neg Predict\t");
        bf.append("Max Conflict\t");
        bf.append("Min Conflict\t");
        bf.append("Avg Conflict\t");
        bf.append("uniq. PredicatesWithConflict\t");
        bf.append("uniq. Predictions\t");
        bf.append("uniq. Pos_Predictions\t");
        bf.append("uniq. Neg_Predictions\t");

        bf.append('\n');
        bf.append(conflictsSummary.getSum()+"\t");
        bf.append(totalPredictionsCount+"\t");
        bf.append(positivePredictionsCount+"\t");
        bf.append(negativePredictionsCount+"\t");
        bf.append(count2Conflict.size()==0? 0:conflictsSummary.getMax()+"\t");
        bf.append(count2Conflict.size()==0? 0:conflictsSummary.getMin()+"\t");
        bf.append(conflictsSummary.getAverage()+"\t");
        bf.append(conflictsSummary.getCount()+"\t");
        bf.append(Sets.union(items2Subjects.keySet(),negativeItems2Subjects.keySet()).size()+"\t");
        bf.append(items2Subjects.keySet().size()+"\t");
        bf.append(negativeItems2Subjects.keySet().size()+"\t");
        bf.append('\n');



        bf.append("Max_Conflict_Predicate\t"+ count2Conflict.get(conflictsSummary.getMax()));
        bf.append('\n');
        bf.append("Min_Conflict_Predicate\t"+ count2Conflict.get(conflictsSummary.getMin()));

        bf.append('\n');


        System.out.println(bf.toString());

        if(exportFile!=null) {
            BufferedWriter bw = FileUtils.getBufferedUTF8Writer(exportFile);
            bw.write(bf.toString());
            bw.close();
        }






    }
//
//    private Multimap<Integer,String> multimapIntersection(SetMultimap<Integer, String> map1, SetMultimap<Integer, String> map2) {
//
//
//
//
//
//
//    }


    static Pattern countPattern = Pattern.compile(Pattern.quote("(") + "(.*?)" + Pattern.quote(")"));
    private static int extractCount(String fact) {
        Matcher countMatcher=countPattern.matcher(fact);

        if(countMatcher.find()) {
            return Integer.valueOf( countMatcher.group(1));
        }

        return 0;

    }
}
