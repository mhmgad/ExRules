package de.mpii.frequentrulesminning.utils;

import com.google.common.base.Joiner;
import com.google.common.primitives.Ints;

import java.util.Arrays;
import java.util.List;

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

}