package de.mpii.predicatelifting;

import com.google.common.collect.ImmutableSet;
import de.mpii.yagotools.YagoLocation;
import de.mpii.yagotools.YagoSimpleTypes;
import mpi.tools.basics3.Fact;
import mpi.tools.basics3.FactSource;
import mpi.tools.javatools.util.FileUtils;

import java.io.*;
import java.net.URL;

/**
 * Created by gadelrab on 2/11/16.
 */
public class YagoFactsReducer {


    public enum FactType{LOCATION,DATE,PERSON,ORGANIZATION,ARTIFACT}

    public static final String LOCATION_RELATIONS_FILE="resources/yago_location_relations.tsv";
    ImmutableSet<String> locationRelations;
    YagoSimpleTypes yst;
    YagoLocation yLoc;



    public YagoFactsReducer(){
        //yst=YagoSimpleTypes.getInstance();
        yLoc=YagoLocation.getInstance();


        try {

            locationRelations = ImmutableSet.copyOf(FileUtils.getFileContentasList(LOCATION_RELATIONS_FILE));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    public void reduceFacts(String factSourceFilePath, String outputFile, String []relations, FactType fType) {
        try {
            reduceFacts( FactSource.from(factSourceFilePath),FileUtils.getBufferedUTF8Writer(outputFile) ,relations,fType);

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
                Fact reducedfact = reduceFact(f, fType);
                try {
                    outputWriter.write(reducedfact.toTsvLine());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }

    }








    public Fact reduceFact(Fact orgFact,FactType factType) {
        if (factType==null){
            if(locationRelations.contains(orgFact)){
                return reduceLocationFact(orgFact);
            }
        }
        else{
            switch (factType){
                case LOCATION:
                    return reduceLocationFact(orgFact);

            }
        }

        return orgFact;

    }


    private Fact reduceLocationFact(Fact orgFact) {
        String entity=orgFact.getObject();
        String reduced=yLoc.getParentCountry(entity);

        return new Fact(orgFact.getSubject(),orgFact.getRelation(),reduced);

    }


    public static void main(String [] args){


        if(args.length<2){
            System.out.println("Incorrect params: fact_reducer <InputFile> <outputFile> [Type<LOCATION>]");
            System.exit(1);
        }

        YagoFactsReducer fr=new YagoFactsReducer();



        FactType type=null;
        if (args.length>2)
            type=FactType.valueOf(args[2]);
        fr.reduceFacts(args[0],args[1],null,type);

    }



}
