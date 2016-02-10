package de.mpii.yagotools;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import javatools.test.NewUTF8Reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * Created by gadelrab on 2/8/16.
 */
public class YagoTaxonomy {

    private static final String SUB_CLASS_OF = "rdfs:subClassOf";
    String TAXONOMY_FILE_PATH="data/yagoTaxonomy.tsv";

    private static YagoTaxonomy instance;

    private Multimap<String,String> typesParents;


    private YagoTaxonomy(){
        typesParents= HashMultimap.create();
        loadData();
    }

    private void loadData() {
        NewUTF8Reader fileReader;
        try {
            fileReader=new NewUTF8Reader(new File(TAXONOMY_FILE_PATH),"Loading Data");


            String line;

            while((line=fileReader.readLine())!=null){
                String[] lineParts=line.split("\t");
                int subIndex=0,predIndex=1,objIndex=2;
                if (lineParts.length>3){
                    subIndex++;predIndex++;objIndex++;
                }
                if(lineParts[predIndex].equals(SUB_CLASS_OF)){
                    typesParents.put(lineParts[subIndex],lineParts[objIndex]);
                }

            }
            System.out.println( "Dictionary size: "+typesParents.size());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }



    }


    public static  YagoTaxonomy getInstance(){
        if (instance==null){
            instance=new YagoTaxonomy();
        }

        return instance;

    }


    public static void main (String [] args){
        YagoTaxonomy yt= YagoTaxonomy.getInstance();

    }


}

