package de.mpii.yagotools;

import com.google.common.collect.Multimap;
import de.mpii.yagotools.utils.YagoDataReader;
import de.mpii.yagotools.utils.YagoRelations;




/**
 * Created by gadelrab on 2/8/16.
 */
public class YagoSimpleTypes {

    String TAXONOMY_FILE_PATH="data/yagoSimpleTypes.tsv";

    private static YagoSimpleTypes instance;

    private Multimap<String,String> entityTypes;


    private YagoSimpleTypes(){
        entityTypes= YagoDataReader.loadSubject2ObjectMap(TAXONOMY_FILE_PATH,new String[]{YagoRelations.TYPE});
    }



    public static YagoSimpleTypes getInstance(){
        if (instance==null){
            instance=new YagoSimpleTypes();
        }

        return instance;

    }


    public static void main (String [] args){
        YagoSimpleTypes yt= YagoSimpleTypes.getInstance();
    }


}

