package de.mpii.frequentrulesminning.utils;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by gadelrab on 2/26/16.
 */
public class Transaction{
    int [] items;
    int count;

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
        this(Arrays.stream(transLine.split(" ")).map(String::trim).mapToInt(Integer::parseInt).toArray(), count);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(items);
    }

    @Override
    public boolean equals(Object obj) {
        return Arrays.equals(((Transaction)obj).getItems(),getItems());
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

//    public void removeItems(int[] excludingList) {
//        Set<Integer> transDiff= Sets.difference(ImmutableSet.copyOf(this.getItemsAsList()),ImmutableSet.copyOf(Ints.asList(excludingList)));
//        setItems(Ints.toArray(transDiff));
//    }

}