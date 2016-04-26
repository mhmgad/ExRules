package de.mpii.yagotools;

import com.google.common.collect.Multimap;
import de.mpii.yagotools.utils.YagoDataReader;
import de.mpii.yagotools.utils.YagoRelations;

import java.util.Collection;
import java.util.HashSet;


/**
 * Created by gadelrab on 2/8/16.
 */
public class YagoTypes {

    String TAXONOMY_FILE_PATH="data/yagoSimpleTypes.tsv";

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


    public Collection<String> getType(String entity){
        Collection<String> parents=entityTypes.get(entity);
        return (entityTypes==null)? new HashSet<String>():parents;
    }


    public static void main (String [] args){
        YagoTypes yt= YagoTypes.getInstance();
        System.out.println();
        System.out.println(yt.getType("<Aaron_Sorkin>"));
    }


}

