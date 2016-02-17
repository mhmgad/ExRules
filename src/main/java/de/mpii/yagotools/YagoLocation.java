package de.mpii.yagotools;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import de.mpii.yagotools.utils.YagoDataReader;
import de.mpii.yagotools.utils.YagoRelations;
import mpi.tools.javatools.util.FileUtils;

import java.lang.*;
import java.io.*;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.net.URL;


/**
 * Created by gadelrab on 2/8/16.
 */
public class YagoLocation {

    //private static final String SUB_CLASS_OF = "rdfs:subClassOf";
    String LOCATION_FILE_PATH ="resources/bigData/isLocatedInData.tsv";
    final static String COUNTRIES_FILE="resources/countries.tsv";

    private static YagoLocation instance;

    private Multimap<String,String> typesParents;
    ImmutableSet<String> countriesSet;


    private YagoLocation(){
        loadCountries();
        try {
            typesParents = YagoDataReader.loadDataInMap(LOCATION_FILE_PATH, new String[]{YagoRelations.IS_LOCATED_IN}, YagoDataReader.MapType.SUBJ_2_OBJ);

        }catch (Exception e) {
            e.printStackTrace();}
    }

    private void loadCountries() {
        try {
            countriesSet= ImmutableSet.copyOf(FileUtils.getFileContentasList(COUNTRIES_FILE));
        } catch (Exception e) {
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

