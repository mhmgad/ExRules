package de.mpii.yagotools;

import com.google.common.collect.Multimap;
import de.mpii.yagotools.utils.YagoDataReader;
import de.mpii.yagotools.utils.YagoRelations;




/**
 * Created by gadelrab on 2/8/16.
 */
public class YagoTypes {

    String TAXONOMY_FILE_PATH="data/yagoTypes.tsv";

    private static YagoTypes instance;

    private Multimap<String,String> entityTypes;


    private YagoTypes(){
        entityTypes= YagoDataReader.loadDataInMap(TAXONOMY_FILE_PATH,new String[]{YagoRelations.TYPE}, YagoDataReader.MapType.SUBJ_2_OBJ);
    }



    public static YagoTypes getInstance(){
        if (instance==null){
            instance=new YagoTypes();
        }

        return instance;

    }





    public static void main (String [] args){
        YagoTypes yt= YagoTypes.getInstance();
    }


}

