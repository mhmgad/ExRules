package de.mpii.predicatelifting;

import com.google.common.collect.ImmutableSet;
import de.mpii.yagotools.YagoLocation;
import de.mpii.yagotools.YagoTaxonomy;
import de.mpii.yagotools.YagoTypes;
import mpi.tools.basics3.Fact;
import mpi.tools.basics3.FactSource;
import mpi.tools.javatools.util.FileUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by gadelrab on 2/11/16.
 */
public class YagoFactsReducer {


    private YagoTaxonomy yTax;

    public enum FactType{LOCATION,DATE,PERSON,ORGANIZATION,ARTIFACT,HAS_TYPE}

    public static final String LOCATION_RELATIONS_FILE="resources/yago_location_relations.tsv";
    ImmutableSet<String> locationRelations;
    YagoTypes yTypes;
    YagoLocation yLoc;



    public YagoFactsReducer(){
        //yst=YagoTypes.getInstance();
        yLoc=YagoLocation.getInstance();
        yTax=YagoTaxonomy.getInstance();
        yTypes=YagoTypes.getInstance();


        try {

            locationRelations = ImmutableSet.copyOf(FileUtils.getFileContentasList(LOCATION_RELATIONS_FILE));


        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    public void reduceFacts(String factSourceFilePath, String outputFile, String []relations, FactType fType) {
        try {
            BufferedWriter bw=FileUtils.getBufferedUTF8Writer(outputFile);
            reduceFacts( FactSource.from(factSourceFilePath),bw ,relations,fType);
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void reduceFacts(FactSource factSource, BufferedWriter outputWriter, String []relations, FactType fType){
        ImmutableSet<String> relationsSet=null;
        if(relations!=null)
            relationsSet=ImmutableSet.copyOf(relations);


        for( Fact f:factSource) {
            if (relationsSet==null||relationsSet.contains(f.getRelation())) {
                List<Fact> reducedFacts = reduceFact(f, fType);

                    reducedFacts.forEach((reducedFact) -> {
                        try {
                            outputWriter.write(reducedFact.toTsvLine());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
            }

        }

    }



    public List<Fact> reduceFact(Fact orgFact,FactType factType) {
        List<Fact>  reducedFacts=new ArrayList<>();
        if (factType==null){
            if(locationRelations.contains(orgFact)){
                return reduceLocationFact(orgFact);
            }
        }
        else{
            switch (factType){
                case LOCATION:
                    return reduceLocationFact(orgFact);
                case DATE:
                    return reduceDateFact(orgFact);
                case HAS_TYPE:
                    return reduceBasedOnType(orgFact);

            }
        }
        reducedFacts.add(orgFact);
        return reducedFacts;

    }

    private List<Fact> reduceBasedOnType(Fact orgFact) {
        List<Fact>  reducedFacts=new ArrayList<>();

        String entity=orgFact.getObject();
        Collection<String> parents=yTax.getParents(yTypes.getType(entity));

        parents.forEach((p)-> reducedFacts.add(new Fact(orgFact.getSubject(),orgFact.getRelation(),p)));

        return reducedFacts;
    }

    private List<Fact> reduceDateFact(Fact orgFact) {
        List<Fact>  reducedFacts=new ArrayList<>();
        String date="0000";
        try {
             date = orgFact.getObjectAsJavaString();
        }
        catch (Exception e){
            System.out.println(orgFact.toString());
        }
        //System.out.println(date);
        //String[] dateParts=date.split("-");
        String reduced=date.substring(0,3)+"s";
        reducedFacts.add(new Fact(orgFact.getSubject(),orgFact.getRelation(),reduced));
        return reducedFacts;
    }


    private List<Fact> reduceLocationFact(Fact orgFact) {
        List<Fact>  reducedFacts=new ArrayList<>();
        String entity=orgFact.getObject();
        Collection<String> reduced=yLoc.getParents(entity);
        reduced.forEach((rf)-> reducedFacts.add(new Fact(orgFact.getSubject(),orgFact.getRelation(),rf)));
        reducedFacts.add(orgFact);
        return reducedFacts;

    }


    public static void main(String [] args){


        if(args.length<2){
            System.out.println("Incorrect params: fact_reducer <InputFile> <outputFile> [Type<LOCATION|DATE|HAS_TYPE>]");
            System.exit(1);
        }

        YagoFactsReducer fr=new YagoFactsReducer();



        FactType type=null;
        if (args.length>2)
            type=FactType.valueOf(args[2]);
        fr.reduceFacts(args[0],args[1],null,type);

    }



}
