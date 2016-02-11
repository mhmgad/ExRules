package de.mpii.yagotools;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import de.mpii.yagotools.utils.YagoDataReader;
import de.mpii.yagotools.utils.YagoRelations;
import javatools.test.NewUTF8Reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * Created by gadelrab on 2/8/16.
 */
public class YagoTaxonomy {

    //private static final String SUB_CLASS_OF = "rdfs:subClassOf";
    String TAXONOMY_FILE_PATH="data/yagoTaxonomy.tsv";

    private static YagoTaxonomy instance;

    private Multimap<String,String> typesParents;


    private YagoTaxonomy(){
        typesParents= YagoDataReader.loadSubjectObjectMap(TAXONOMY_FILE_PATH,new String[]{YagoRelations.SUB_CLASS_OF});

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

