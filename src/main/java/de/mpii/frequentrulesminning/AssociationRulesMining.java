package de.mpii.frequentrulesminning;

import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import javatools.filehandlers.UTF8Writer;
import javatools.test.NewUTF8Reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * Created by gadelrab on 2/9/16.
 *
 * This class is conserned with generating w
 */
public class AssociationRulesMining {

    private int generatedRulesCount;


    class Rule{
        String head;
        List<String> body;
        double confidence;

        public Rule(String head, List<String> body, double confidence) {
            this.head = head;
            this.body = body;
            this.confidence = confidence;
        }

        @Override
        public String toString() {
            return String.format( "%.4f",this.confidence)+"\t"+this.head+"\t"+this.body;
        }
    }


    //private TObjectIntCustomHashMap<ImmutableSet<String>> frequentitems;
    private HashMap<ImmutableSet<String>,Integer> frequentitems;

    public AssociationRulesMining(){
      //  frequentitems=new TObjectIntCustomHashMap<>();
        this.frequentitems=Maps.newHashMap();
        this.generatedRulesCount =0;
    }


    public void readFrequentItems(String frequentItemsPath){
        NewUTF8Reader fileReader;
        try {
            fileReader=new NewUTF8Reader(new File(frequentItemsPath),"Loading Frequent Items Sets");


            String line;

            while((line=fileReader.readLine())!=null){
                String[] lineParts=line.split("\t\\[");
                int count=Integer.parseInt(lineParts[0].trim());


                ImmutableSet<String> elementsImmutable = ImmutableSet.copyOf(lineParts[1].substring(0,lineParts[1].length()-1).split("\t"));
                //ImmutableSet<String> elementsImmutable2 = ImmutableSet.copyOf(lineParts[1].substring(0,lineParts[1].length()-1).split("\t"));
                //System.out.println(elementsImmutable.equals(elementsImmutable2));
                frequentitems.put(elementsImmutable,count);
                //System.out.println(frequentitems.get(elementsImmutable2));
                //break;

            }
            System.out.println( "Dictionary size: "+frequentitems.size());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void generateRules(String outputFilePath){
        try {
            generateRules(new UTF8Writer(outputFilePath) );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void generateRules(UTF8Writer outputWriter) throws IOException {

        Set<Map.Entry<ImmutableSet<String>, Integer>> entries=this.frequentitems.entrySet();
        int itemsSetCount=entries.size();
        int processed=0;
        for (Map.Entry<ImmutableSet<String>, Integer> ruleValuePair:entries) {
            processed++;
            ArrayList<Rule> rules= generateRule(ruleValuePair);
            System.out.println(processed+"/"+itemsSetCount);

            for (Rule r:rules ) {
                outputWriter.writeln(r.toString());

            }

        }
        System.out.println("Total Generate Rules Count: "+this.generatedRulesCount);
    }


    private ArrayList<Rule> generateRule(Map.Entry<ImmutableSet<String>, Integer> ruleValuePair) {
        ArrayList<Rule> rules =new ArrayList<>();
        int itemsSupp=ruleValuePair.getValue();
        ImmutableSet<String> itemsSet=ruleValuePair.getKey();
        //System.out.println(itemsSet);
        if (itemsSet.size()<2)
            return rules;
        String[] itemsList=new String[itemsSet.size()];
        itemsSet.toArray(itemsList);

        Set<String> items=new HashSet<String>(itemsSet) ;

//        /Set<String> examinedHeads=new HashSet<>();
        //Set<String> tmp ;
        for(String headPredicate:itemsSet.asList()){

            // Create body and Immutable set.. then restore item set
            items.remove(headPredicate);
            ImmutableSet<String> body=ImmutableSet.copyOf(items);
            items.add(headPredicate);

            Integer bodySupp=frequentitems.get(body);

            if (bodySupp==null||bodySupp.intValue()==0)
                continue;

            double confidence= (itemsSupp+0.0)/bodySupp.intValue();

            Rule frqRule=new Rule(headPredicate,body.asList(),confidence);
            rules.add(frqRule);

        }
        // Count gnerated rules
        this.generatedRulesCount +=rules.size();
        return rules;

    }



    public static void main(String[]args){
        AssociationRulesMining am=new AssociationRulesMining();
        if (args.length<2){
            System.out.println("Incorrect params");
        }

        am.readFrequentItems(args[0]);
        System.out.println("Generating Rules ... ");
        am.generateRules(args[1]);
        System.out.println("Done!");
        //am.readFrequentItems("data/patterns-3_pretty_filtered.txt");


        //am.generateRules("data/rules_pretty_filtered.txt");
    }
}
