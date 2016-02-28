package de.mpii.frequentrulesminning.utils;

import com.google.common.base.Joiner;
import com.google.common.primitives.Ints;

import java.util.Arrays;

/**
 * Created by gadelrab on 2/26/16.
 */
public class Transaction{
    int [] items;
    int count;


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


//    public static Transaction fromString(String trasaction){
//
//
//    }
}