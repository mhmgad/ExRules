package de.mpii.yagotools;

import com.google.common.collect.Multimap;
import de.mpii.yagotools.utils.YagoDataReader;
import de.mpii.yagotools.utils.YagoRelations;


/**
 * Created by gadelrab on 2/8/16.
 */
public class YagoLocation {

    //private static final String SUB_CLASS_OF = "rdfs:subClassOf";
    String TAXONOMY_FILE_PATH="data/yagoTaxonomy.tsv";

    private static YagoLocation instance;

    private Multimap<String,String> typesParents;


    private YagoLocation(){
        typesParents= YagoDataReader.loadSubject2ObjectMap(TAXONOMY_FILE_PATH,new String[]{YagoRelations.SUB_CLASS_OF});

    }


    public static YagoLocation getInstance(){
        if (instance==null){
            instance=new YagoLocation();
        }
        return instance;
    }


    public static void main (String [] args){
        YagoLocation yt= YagoLocation.getInstance();
    }


}

