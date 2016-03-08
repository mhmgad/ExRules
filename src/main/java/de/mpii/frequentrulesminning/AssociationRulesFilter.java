package de.mpii.frequentrulesminning;

import de.mpii.frequentrulesminning.utils.Item;
import de.mpii.yagotools.YagoTaxonomy;
import de.mpii.yagotools.utils.YagoRelations;
import mpi.tools.javatools.util.FileUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

/**
 * Created by gadelrab on 2/17/16.
 */
public class AssociationRulesFilter {
    private final double upperThreshold;
    private final double lowerThreshold;
    BufferedReader inputFile;
    YagoTaxonomy yt;
    BufferedWriter outputFile;

    public AssociationRulesFilter(String inFilePath,String outFilePath,double lowerThreshold,double upperThreshold) {
        yt=YagoTaxonomy.getInstance();
        this.lowerThreshold=lowerThreshold;
        this.upperThreshold=upperThreshold;
        try {
            inputFile=FileUtils.getBufferedUTF8Reader(inFilePath);
            outputFile= FileUtils.getBufferedUTF8Writer(outFilePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }



    public void filterRules(){
        String line=null;
        try {
            while((line=inputFile.readLine())!=null&&!line.trim().isEmpty()){
                AssociationRule rule=AssociationRule.fromString(line);

                if(rule.getConfidence()<lowerThreshold||rule.getConfidence()>upperThreshold||!interesting(rule))
                    continue;


                outputFile.write(rule.toString());
                outputFile.newLine();

            }
            outputFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private boolean interesting(AssociationRule rule) {
        Item head=rule.getHead();
        if (!head.getPredicate().equals( YagoRelations.TYPE))
            return true;
        for (Item b :rule.getBody()) {
            if(!b.getPredicate().equals( YagoRelations.TYPE))
                continue;
            Set<String> parents=yt.getParents(b.getObject());
            if(parents.contains(head.object))
                return false;

        }

        return true;

    }

    public static void main(String[]args){

        double upperThreshold=100;
        if (args.length<3){
            System.out.println("filter_rules <input_file_path> <output_file_path> <lowerThreshold> <upperThreshold>");

        }

        double lowerThreshold=Double.parseDouble(args[2]);
        if (args.length>3) {
            upperThreshold = Double.parseDouble(args[3]);
        }
        AssociationRulesFilter am=new AssociationRulesFilter(args[0],args[1],lowerThreshold,upperThreshold);
        System.out.println("Filtering Rules ... ");
        am.filterRules();
        System.out.println("Done!");


        //am.readFrequentItems("data/patterns-3_pretty_filtered.txt");


        //am.generateRules("data/rules_pretty_filtered.txt");
    }


}
