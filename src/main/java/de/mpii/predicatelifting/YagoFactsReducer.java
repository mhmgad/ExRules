package de.mpii.predicatelifting;

import com.google.common.collect.ImmutableSet;
import de.mpii.yagotools.YagoSimpleTypes;
import mpi.tools.basics3.Fact;
import mpi.tools.basics3.FactSource;
import mpi.tools.javatools.filehandlers.UTF8Reader;
import mpi.tools.javatools.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.util.Set;

/**
 * Created by gadelrab on 2/11/16.
 */
public class YagoFactsReducer {


    YagoSimpleTypes yst;



    public YagoFactsReducer(){
        yst=YagoSimpleTypes.getInstance();



    }



    public void reduceToType(String factSourceFilePath, String []relations) {
        try {
            reduceToType( FactSource.from(factSourceFilePath), relations);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }


    public void reduceToType(FactSource factSource, String []relations){

        ImmutableSet<String> relationsSet=ImmutableSet.copyOf(relations);


        for( Fact f:factSource)


            if(relationsSet.contains(f.getRelation())){
                reduceFact(f);
            }

    }







    public Fact reduceFact(Fact orgFact) {

        return orgFact;

    }




}
