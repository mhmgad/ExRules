package de.mpii.predicatelifting;

import com.google.common.collect.Multimap;
import de.mpii.yagotools.utils.YagoDataReader;
import mpi.tools.basics3.Fact;
import mpi.tools.javatools.util.FileUtils;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * Created by mhmgad on 14.02.16.
 */
public class YagoFactsFilter {


    Multimap<String,String> predicateObject2Subjects;

    public YagoFactsFilter(String reducedFactsPath){

        predicateObject2Subjects= YagoDataReader.loadDataInMap(reducedFactsPath,null, YagoDataReader.MapType.PRED_OBJ_2_SUBJ);



    }

    public void predicateObjectsCount(String outputFile,int threshold){
        try {
            BufferedWriter bw=FileUtils.getBufferedUTF8Writer(outputFile);

            for(String key : predicateObject2Subjects.keySet()){
                Collection<String> values=predicateObject2Subjects.get(key);
                int size=values.size();
                if(size>=threshold) {
                    String[]items=key.split("\t");
                    values.forEach((v) -> {
                        try {
                            bw.write(new Fact(v, items[0], items[1]).toTsvLine());

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    ;
                    //bw.write(size+"\t" + entry.getKey());

                }
                else
                    System.out.println(key+": "+values.size());
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public static void main(String[]args){
//        args=new String[3];
//        args[0]="data/facts_to_mine.tsv";
//        args[1]="data/combined_predicates_filtered_count_100.tsv";
        if(args.length<3)
        {
            System.out.println("Usage: filter_facts.sh <inFilePath> <outFilePath> <minimum support (integer)>");
            System.exit(0);
        }
//        System.out.println(Arrays.toString(args));

        int threshold=Integer.parseInt(args[2]);
        YagoFactsFilter yf=new YagoFactsFilter(args[0]);
        yf.predicateObjectsCount(args[1],threshold);
    }






}
