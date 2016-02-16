package de.mpii.yagotools.utils;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import mpi.tools.basics3.Fact;
import mpi.tools.basics3.FactSource;
import mpi.tools.javatools.filehandlers.UTF8Reader;


import java.io.File;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Created by gadelrab on 2/11/16.
 */
public class YagoDataReader {


    public static Multimap<String,String> loadSubject2ObjectMap(String filePath, String [] relations) {
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
                    subjectObjectMap.put(f.getSubject(),f.getObject());}

            System.out.println( "Dictionary size: "+ subjectObjectMap.size());

            return subjectObjectMap;


        } catch (MalformedURLException e2) {
            e2.printStackTrace();
        }

        return null;

    }

    public static Multimap<String,String> loadPredicateObject2subjectMap(String filePath, String [] relations) {
        // add them to set for searching
        ImmutableSet<String> relationsSet=null;
        if (relations!=null)
            relationsSet=ImmutableSet.copyOf(relations);

        Multimap<String,String> subjectObjectMap= HashMultimap.create();

        UTF8Reader fileReader;
        try {
            fileReader=new UTF8Reader(new File(filePath),"Loading Data");


            String line;

            while((line=fileReader.readLine())!=null){
                String[] lineParts=line.split("\t");
                int subIndex=0,predIndex=1,objIndex=2;
                if (lineParts.length>3){
                    subIndex++;predIndex++;objIndex++;
                }
                if(relationsSet==null||relationsSet.contains(lineParts[predIndex])){
                    subjectObjectMap.put(lineParts[predIndex] + lineParts[objIndex],lineParts[subIndex]);
                }

            }
            System.out.println( "Dictionary size: "+ subjectObjectMap.size());

            return subjectObjectMap;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return null;

    }


}
