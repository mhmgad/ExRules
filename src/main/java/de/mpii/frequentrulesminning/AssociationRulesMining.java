package de.mpii.frequentrulesminning;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import de.mpii.frequentrulesminning.utils.Item;
import mpi.tools.javatools.filehandlers.UTF8Writer;
import mpi.tools.javatools.filehandlers.UTF8Reader;

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





    //private TObjectIntCustomHashMap<ImmutableSet<String>> frequentitems;
    private HashMap<ImmutableSet<Item>,Integer> frequentitems;

    public AssociationRulesMining(){
      //  frequentitems=new TObjectIntCustomHashMap<>();
        this.frequentitems=Maps.newHashMap();
        this.generatedRulesCount =0;
    }


    public void readFrequentItems(String frequentItemsPath){
        UTF8Reader fileReader;
        try {
            fileReader=new UTF8Reader(new File(frequentItemsPath),"Loading Frequent Items Sets");


            String line;

            while((line=fileReader.readLine())!=null&&!line.trim().isEmpty()){
                String[] lineParts=line.split("\\[");
                int count=Integer.parseInt(lineParts[0].trim());


                ImmutableSet<Item> elementsImmutable = ImmutableSet.copyOf(parseItemsSet(lineParts[1]));
//                System.out.println(elementsImmutable+" "+elementsImmutable.hashCode());
                frequentitems.put(elementsImmutable,count);
//                ImmutableSet<Item> elementsImmutable2 = ImmutableSet.copyOf(parseItemsSet(lineParts[1]));
//                System.out.println(elementsImmutable2+" "+elementsImmutable.hashCode());
//                System.out.println(frequentitems.containsKey(elementsImmutable2));
//                System.out.println(elementsImmutable.equals(elementsImmutable2));


//                if (frequentitems.containsKey(elementsImmutable)){
//                    System.out.println("Duplicate");
//                    count+=frequentitems.get(elementsImmutable);
//                }

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

    private List<Item> parseItemsSet(String linePart) {
        String [] elements =linePart.substring(0,linePart.length()-1).split(" \\(");
        List<Item> items=new ArrayList<>(elements.length);
        for (int i=0;i<elements.length;i++){
            if(i!=0) {
                elements[i] = "(" + elements[i];
            }
            items.add(Item.fromString(elements[i]));


        }
        //System.out.println("list: "+Arrays.asList(elements));
        // l= Arrays.asList(elements);

        return items;
    }

    public void generateRules(String outputFilePath){
        try {
            generateRules(new UTF8Writer(outputFilePath) );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void generateRules(UTF8Writer outputWriter) throws IOException {

        Set<Map.Entry<ImmutableSet<Item>, Integer>> entries=this.frequentitems.entrySet();
        int itemsSetCount=entries.size();
        int processed=0;
        for (Map.Entry<ImmutableSet<Item>, Integer> ruleValuePair:entries) {
            processed++;
            ArrayList<AssociationRule> rules= generateRule(ruleValuePair);
            if (processed%1000==0) {
                System.out.println(processed + "/" + itemsSetCount);
            }

            for (AssociationRule r:rules ) {
                outputWriter.writeln(r.toString());

            }

        }
        System.out.println("Total Generate Rules Count: "+this.generatedRulesCount);
    }


    private ArrayList<AssociationRule> generateRule(Map.Entry<ImmutableSet<Item>, Integer> ruleValuePair) {
        ArrayList<AssociationRule> rules =new ArrayList<>();
        int itemsSupp=ruleValuePair.getValue();
        ImmutableSet<Item> itemsSet=ruleValuePair.getKey();
        //System.out.println(itemsSet);
        if (itemsSet.size()<2)
            return rules;
        Item[] itemsList=new Item[itemsSet.size()];
        itemsSet.toArray(itemsList);

        Set<Item> items=new HashSet<Item>(itemsSet) ;

//        /Set<String> examinedHeads=new HashSet<>();
        //Set<String> tmp ;
        for(Item headPredicate:itemsSet.asList()){

            // Create body and Immutable set.. then restore item set
            items.remove(headPredicate);
            ImmutableSet<Item> body=ImmutableSet.copyOf(items);
            items.add(headPredicate);

            Integer bodySupp=frequentitems.get(body);

            if (bodySupp==null||bodySupp==0)
                continue;

            double confidence= (itemsSupp+0.0)/bodySupp;

            AssociationRule frqRule=new AssociationRule(headPredicate,body.asList(),confidence);
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
