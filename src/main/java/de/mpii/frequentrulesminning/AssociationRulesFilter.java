package de.mpii.frequentrulesminning;

import com.google.common.collect.ImmutableSet;
import de.mpii.yagotools.YagoTaxonomy;
import mpi.tools.javatools.filehandlers.UTF8Reader;
import mpi.tools.javatools.filehandlers.UTF8Writer;
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

    private final double threshold;
    BufferedReader inputFile;
    YagoTaxonomy yt;
    BufferedWriter outputFile;

    public AssociationRulesFilter(String inFilePath,String outFilePath,double threshold) {
        yt=YagoTaxonomy.getInstance();
        this.threshold=threshold;
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

                if(rule.getConfidence()<threshold||!interesting(rule))
                    continue;


                outputFile.write(rule.toString());
                outputFile.newLine();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private boolean interesting(AssociationRule rule) {
        String head=rule.getHead();
        for (String b :rule.getBody()) {
            Set<String> parents=yt.getParents(b);
            if(parents.contains(head))
                return false;

        }

        return true;

    }

    public static void main(String[]args){
        if (args.length<3){
            System.out.println("filter_rules <input_file_path> <output_file_path> <threshold>");
        }

        double threshold=Double.parseDouble(args[2]);

        AssociationRulesFilter am=new AssociationRulesFilter(args[0],args[1],threshold);
        System.out.println("Filtering Rules ... ");
        am.filterRules();
        System.out.println("Done!");


        //am.readFrequentItems("data/patterns-3_pretty_filtered.txt");


        //am.generateRules("data/rules_pretty_filtered.txt");
    }


}
