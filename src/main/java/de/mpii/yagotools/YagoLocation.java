package de.mpii.yagotools;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import de.mpii.yagotools.utils.YagoDataReader;
import de.mpii.yagotools.utils.YagoRelations;
import mpi.tools.javatools.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;


/**
 * Created by gadelrab on 2/8/16.
 */
public class YagoLocation {

    //private static final String SUB_CLASS_OF = "rdfs:subClassOf";
    String LOCATION_FILE_PATH ="data/isLocatedInData.tsv";
    final static String COUNTRIES_FILE="src/resources/countries.tsv";

    private static YagoLocation instance;

    private Multimap<String,String> typesParents;
    ImmutableSet<String> countriesSet;


    private YagoLocation(){
        typesParents= YagoDataReader.loadSubject2ObjectMap(LOCATION_FILE_PATH,new String[]{YagoRelations.IS_LOCATED_IN});
        loadCountries();
    }

    private void loadCountries() {
        try {
            String fileContect= FileUtils.getFileContent(new File(COUNTRIES_FILE));
            countriesSet= ImmutableSet.copyOf(fileContect.split("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    public static YagoLocation getInstance(){
        if (instance==null){
            instance=new YagoLocation();
        }
        return instance;
    }


    public  String getParentCountry(String entity){

        String parentCountry;
        if (countriesSet.contains(entity))
            return entity;

        Collection<String> parents=typesParents.get(entity);
        //System.out.println(parents);
        Set<String> countryParents=Sets.intersection(ImmutableSet.copyOf(parents), countriesSet);
        //System.out.println(countryParents);

        if(countryParents==null||countryParents.isEmpty())
            return entity;
        else
            return (String) countryParents.toArray()[0];
    }


    public static void main (String [] args) {
        YagoLocation yt= YagoLocation.getInstance();
        System.out.println(yt.getParentCountry("<Sohag>"));
        System.out.println(yt.getParentCountry("<Berlin>"));
        System.out.println(yt.getParentCountry("<Qatar>"));
    }


}

