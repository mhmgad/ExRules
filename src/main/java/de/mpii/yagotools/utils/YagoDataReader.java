package de.mpii.yagotools.utils;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import mpi.tools.basics3.Fact;
import mpi.tools.basics3.FactSource;
import mpi.tools.javatools.filehandlers.UTF8Reader;
import java.net.URL;


import java.io.File;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import java.lang.*;
import java.io.*;
/**
 * Created by gadelrab on 2/11/16.
 */
public class YagoDataReader {

    public enum MapType{SUBJ_2_OBJ,PRED_OBJ_2_SUBJ}


    public static Multimap<String,String> loadDataInMap(String filePath, String [] relations, MapType type) {
        // add them to set for searching
        ImmutableSet<String> relationsSet=null;
        if (relations!=null)
            relationsSet=ImmutableSet.copyOf(relations);

        Multimap<String,String> subjectObjectMap= HashMultimap.create();

        UTF8Reader fileReader;
        try {



            //String line;

           for(Fact f: FactSource.from(filePath))
                if(relationsSet==null||relationsSet.contains(f.getRelation())){
                    String key=null;
                    String value=null;
                    switch (type){
                        case SUBJ_2_OBJ:
                            key=f.getSubject();
                            value=f.getObject();
                            break;
                        case PRED_OBJ_2_SUBJ:
                            key=f.getRelation()+"\t"+f.getObject();
                            value=f.getSubject();
                            break;
                    }
                    subjectObjectMap.put(key,value);}

            System.out.println( "Dictionary size: "+ subjectObjectMap.size());

            return subjectObjectMap;


        } catch (MalformedURLException e2) {
            e2.printStackTrace();
        }

        return null;

    }



//    public static Multimap<String,String> loadPredicateObject2subjectMap(String filePath, String [] relations) {
//        // add them to set for searching
//        ImmutableSet<String> relationsSet=null;
//        if (relations!=null)
//            relationsSet=ImmutableSet.copyOf(relations);
//
//        Multimap<String,String> subjectObjectMap= HashMultimap.create();
//
//        UTF8Reader fileReader;
//        try {
//            fileReader=new UTF8Reader(new File(filePath),"Loading Data");
//
//
//            String line;
//
//            while((line=fileReader.readLine())!=null){
//                String[] lineParts=line.split("\t");
//                int subIndex=0,predIndex=1,objIndex=2;
//                if (lineParts.length>3){
//                    subIndex++;predIndex++;objIndex++;
//                }
//                if(relationsSet==null||relationsSet.contains(lineParts[predIndex])){
//                    subjectObjectMap.put(lineParts[predIndex] + lineParts[objIndex],lineParts[subIndex]);
//                }
//
//            }
//            System.out.println( "Dictionary size: "+ subjectObjectMap.size());
//
//            return subjectObjectMap;
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e1) {
//            e1.printStackTrace();
//        }
//
//        return null;
//
//    }


}
