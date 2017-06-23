package de.mpii.yagotools;

import com.google.common.collect.Multimap;
import de.mpii.yagotools.utils.YagoDataReader;
import de.mpii.yagotools.utils.YagoRelations;

import java.util.*;


/**
 * Created by gadelrab on 2/8/16.
 */
public class YagoTaxonomy {

    //private static final String SUB_CLASS_OF = "rdfs:subClassOf";
    String TAXONOMY_FILE_PATH="resources/bigData/yagoTaxonomy_withGeo.tsv";

    private static YagoTaxonomy instance;

    private Multimap<String,String> typesParents;
    private Map<String,Set<String>> transitiveParents;

    private YagoTaxonomy(){
        typesParents= YagoDataReader.loadDataInMap(TAXONOMY_FILE_PATH,new String[]{YagoRelations.SUB_CLASS_OF}, YagoDataReader.MapType.SUBJ_2_OBJ);
        loadTransitiveParents();
    }

    private void loadTransitiveParents() {
        transitiveParents=new HashMap<>();
    }


    public static  YagoTaxonomy getInstance(){
        if (instance==null){
            instance=new YagoTaxonomy();
        }
        return instance;
    }


    private Set<String> getTransitiveParents(String key){

        Set<String> parents=new HashSet<>();
        TreeSet<String> notExplored=new TreeSet<>();
        notExplored.add(key);

        while(!notExplored.isEmpty()){
            String current=notExplored.pollFirst();
            parents.add(current);
            Collection<String> parentsListTmp=typesParents.get(current);
            if (parentsListTmp!=null){
                for(String e:parentsListTmp){
                    if(!parents.contains(e))
                        notExplored.add(e);
                }
            }

        }
        return parents;

    }

    public Set<String> getParents(String key){
        Set<String> parents= transitiveParents.get(key);
        if(parents!=null)
            return parents;
        //System.out.printf("Recompute");
        parents=getTransitiveParents(key);
        transitiveParents.put(key,parents);

        return parents;
    }

    public Set<String> getParents(Collection<String> keys){

        Set<String> parents=new HashSet<>();

        keys.forEach((k)-> parents.addAll(getParents(k)));

        return parents;

    }


    public static void main (String [] args){
        YagoTaxonomy yt= YagoTaxonomy.getInstance();

        System.out.println(yt.getParents(args[0]));

//        System.out.println(yt.getParents("<wikicat_American_film_directors>"));
//        System.out.println(yt.getParents("<wikicat_Short_films>"));
//        System.out.println(yt.getParents("<wikicategory_K._Lierse_S.K._players>"));
//        System.out.println(yt.getParents("<wordnet_hearer_110165448>"));
//        System.out.println(yt.getParents("<wikicat_English_fantasy_writers>"));
//        System.out.println(yt.getParents("<wikicat_People_from_Monterrey>"));
//        System.out.println(yt.getParents("<wordnet_journalist_110224578>"));
    }


}

