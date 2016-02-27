package de.mpii.frequentrulesminning.utils;

import java.util.Arrays;

/**
 * Created by gadelrab on 2/26/16.
 */
public class Transaction{
    int [] items;
    int count;


    public Transaction(int[] items) {
        setItems(items);
        count=1;

    }

    public Transaction(String transLine) {
        this(Arrays.stream(transLine.split(" ")).map(String::trim).mapToInt(Integer::parseInt).toArray());
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

    public void setItems(int[] items) {
        this.items = items;
        Arrays.sort(items);
    }

    public void setCout(int count){
        this.count=count;
    }
}