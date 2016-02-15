package de.mpii.predicatelifting;

import com.google.common.collect.ImmutableSet;
import de.mpii.yagotools.YagoSimpleTypes;
import mpi.tools.javatools.filehandlers.UTF8Reader;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by gadelrab on 2/11/16.
 */
public class YagoFactsReducer {


    YagoSimpleTypes yst;

    public YagoFactsReducer(){
        yst=YagoSimpleTypes.getInstance();


    }



    public void reduceToType(UTF8Reader fileReader,String [] relations){

        ImmutableSet<String> relationsSet=ImmutableSet.copyOf(relations);
        try {

            String line;
            while((line=fileReader.readLine())!=null){
                String[] lineParts=line.split("\t");
                int subIndex=0,predIndex=1,objIndex=2;
                if (lineParts.length>3){
                    subIndex++;predIndex++;objIndex++;
                }
                if(relationsSet.contains(lineParts[predIndex])){

                }

            }


    } catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException e1) {
        e1.printStackTrace();
    }


}






}
