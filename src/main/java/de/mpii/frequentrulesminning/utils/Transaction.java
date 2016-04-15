package de.mpii.frequentrulesminning.utils;

import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.primitives.Ints;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

/**
 * Created by gadelrab on 2/26/16.
 */
public class Transaction{


    int [] items;

    int count;
    private int id;

//    TIntObjectHashMap<ItemMetaData> predictedItems;


    public Transaction(int[] items) {
        this(items, 1);

    }

    public Transaction(int[] items, int count) {
//        items2Weights =new TIntDoubleHashMap();
//        items2Weights = HashMultimap.create();
        items2Weights = new HashMap<>();
        setItems(items);
        this.count = count;


    }

    public Transaction(String transLinet) {
        this(transLinet, 1);
    }

    public Transaction(String transLine, int count) {
        this(parseIntItems(transLine), count);
    }


//    @Override
//    public int hashCode() {
//        return Arrays.hashCode(items);
//    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

//    @Override
//    public boolean equals(Object obj) {
//        return Arrays.equals(((Transaction)obj).getItems(),getItems());
//    }

    @Override
    public boolean equals(Object obj) {
        return ((Transaction)obj).getId()==getId();
    }

    public List<Integer> getItemsAsList() {
        return Ints.asList(items);
    }

    public int[] getItems() {
        return items;
    }

    public int getCount() {
        return count;
    }

    public void setItems(int[] items) {
        this.items = items;
        Arrays.sort(items);
        // adds item to weights
//        Arrays.stream(items).forEach((i)-> addItemWithWeight(i,1));
        Arrays.stream(items).forEach((i)-> addItemWithWeight(i,Weight.INDEPENDENT));
    }



    public void setCout(int count){
        this.count=count;
    }

    @Override
    public String toString() {
        return Joiner.on(' ').join(Ints.asList(items));
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public static int[] parseIntItems(String transLine) {
        return Arrays.stream(transLine.split(" ")).map(String::trim).mapToInt(Integer::parseInt).toArray();
    }

    public void incrementCount() {
        count++;
    }


//    public void removeItems(int[] excludingList) {
//        Set<Integer> transDiff= Sets.difference(ImmutableSet.copyOf(this.getItemsAsList()),ImmutableSet.copyOf(Ints.asList(excludingList)));
//        setItems(Ints.toArray(transDiff));
//    }



    //Predictions
    HashMap<Integer,Weight> items2Weights;

    public void addItemWithWeight(int item, Weight weight){
//        if(items2Weights.containsKey(item)){
//            double oldWeight= items2Weights.get(item);
//            // Currently take the maximum
//            weight=Math.max(oldWeight,weight);
//        }
        // Do not replace procedure
        if(!items2Weights.containsKey(item))
            items2Weights.put(item,weight);
    }


    public Weight getItemWeight(int item){
        Weight   w =  items2Weights.get(item);
        return w==null? Weight.INDEPENDENT:w;

    }

    public boolean contains(int item){
            //return items2Weights.containsKey(item);

        // check the original transaction Items only
        return ArrayUtils.contains(items,item);
    }

    public boolean contains(int[] body) {
        return Arrays.stream(body).allMatch((i)->contains(i));
    }

    public void addItemsWithWeight(int[] items, Weight weight) {
        Arrays.stream(items).forEach((i)-> addItemWithWeight(i,weight));
    }


//    public double getWeight(int[] with, int[] without) {
//        return   getWeight( with, without,  0.0) ;
//    }



    public double getWeight(int[] positiveItems, int[] negativeItems) {
        // TODO .. Wraning: the method is NOT suitable for no-order

    // TODO needs more thinking about accumulating weights in case of dependency


        double tranWeight=1;

        // Currently we assume that there is no rule chaining ... so multiplying the weights is enough

        if(positiveItems!=null) {
            for (int i : positiveItems) {

                Weight itemWeight = items2Weights.get(i);
                // if any of the item has quality less than our threshold


                if (itemWeight.isIndependent())
                    tranWeight *= itemWeight.getFinalWeight();
                else
                    tranWeight *= itemWeight.getRuleWeight();
            }
        }

        if(negativeItems!=null) {
            for (int i : negativeItems) {
                Weight itemWeight = items2Weights.get(i);
                // if not in the transactions (we are good) just * 1 otherwise * (1-tranWeight)
                if (itemWeight == null)
                    tranWeight *= 1;
                else
                    tranWeight *= (1 - itemWeight.getFinalWeight());

            }
        }



        return  tranWeight;
    }

    public boolean containsAny(int[] items) {
        return Arrays.stream(items).anyMatch((i)->contains(i));

    }




    /**
     * Gets the transaction count based on weight of items
     * @param positiveItems
     * @param negativeItems
     * @return
     */
    public  double getWeightedCount(int[] positiveItems, int[] negativeItems/*,boolean noFractions*/) {
        double weight= getWeight(positiveItems,  negativeItems);
            return weight*getCount();
//        return (weight>0 && noFractions? 1:weight)*getCount();

    }

    /**
     * Checks if the predicted facts in the transaction are generated from higher quality rules
     * @param rule
     * @return
     */
    public boolean allPredictionsFromBetterQualityRules(AssocRuleWithExceptions rule) {
        return Arrays.stream(rule.getBody()).allMatch((i)->getItemWeight(i).predictedWithBetterQualityRules(rule));

    }
}