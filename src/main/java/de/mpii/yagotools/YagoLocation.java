package de.mpii.yagotools;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import de.mpii.yagotools.utils.YagoDataReader;
import de.mpii.yagotools.utils.YagoRelations;
import mpi.tools.javatools.util.FileUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


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


    public Set<String> getParentCountries(String entity) {

        Set<String> o = new HashSet<>();
        o.add(entity);
        if (countriesSet.contains(entity))
            return o;

        Collection<String> parents=typesParents.get(entity);
        //System.out.println(parents);
        Set<String> countryParents=Sets.intersection(ImmutableSet.copyOf(parents), countriesSet);
        //System.out.println(countryParents);

        if (countryParents == null || countryParents.isEmpty()) {

            return o;
        }
        else
            return countryParents;
    }

    public Collection<String> getParents(String entity){
        Collection<String> parents=typesParents.get(entity);
        if (parents==null)
            return new HashSet<String>();
        else
            return parents;
    }


    public static void main (String [] args) {
        YagoLocation yt= YagoLocation.getInstance();
        System.out.println(yt.getParents("<Sohag>"));
        System.out.println(yt.getParents("<Berlin>"));
        System.out.println(yt.getParents("<Qatar>"));
    }


}

