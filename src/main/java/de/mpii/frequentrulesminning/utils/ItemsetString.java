package de.mpii.frequentrulesminning.utils;

import com.google.common.base.Joiner;
import com.google.common.primitives.Ints;

import java.util.Arrays;
import java.util.List;

/**
 * Created by gadelrab on 2/29/16.
 */
public class ItemsetString {//extends AbstractOrderedItemset {


    /** the array of items **/
    public int[] itemset;

    /** the array of items **/
    public Item[] itemsetItem;

    /**  the support of this itemset */
    public int support = 0;

    /**  the support of this itemset */
    public int totalCount = 1;

    public ItemsetString(Item [] itemsetItem,int [] items,int support, int totalCount) {
        this(itemsetItem,items, support);
        this.totalCount=totalCount;
    }

    public void setItemset(int[] itemset) {
        this.itemset=itemset;
        Arrays.sort(this.itemset);
    }

    /**
     * Constructor
     * @param items an array of items that should be added to the new itemset
     */
    public ItemsetString(int[] items) {
        setItemset( items);
    }

    /**
     * Get the items as array
     * @return the items
     */
    public int[] getItems() {
        return itemset;
    }

    /**
     * Constructor
     */
    public ItemsetString(int size){
        itemset = new int[size];
        itemsetItem = new Item[size];
    }



    /**
     * Constructor
     * @param items an array of items that should be added to the new itemset
     * @param support the support of the itemset
     */
    public ItemsetString(int [] items,int support){
        this(items);
        this.support=support;
    }

    /**
     * Constructor
     * @param items an array of items that should be added to the new itemset
     * @param support the support of the itemset
     */
    public ItemsetString(Item [] itemsetItem,int [] items,int support){
        this(items,support);
        this.itemsetItem=itemsetItem;
    }

    /**
     * Constructor
     * @param itemset a list of Integer representing items in the itemset
     * @param support the support of the itemset
     */
    public ItemsetString(List<Integer> itemset, int support){
       this(Ints.toArray(itemset),support);

    }

    /**
     * Get the support of this itemset
     */
    public int getAbsoluteSupport(){
        return support;
    }

    /**
     * Get the size of this itemset
     */
    public int size() {
        return itemset.length;
    }

    /**
     * Get the item at a given position in this itemset
     */
    public Integer get(int position) {
        return itemset[position];
    }


    /**
     * Set the support of this itemset
     * @param support the support
     */
    public void setAbsoluteSupport(Integer support) {
        this.support = support;
    }

    /**
     * Increase the support of this itemset by 1
     */
    public void increaseTransactionCount() {
        this.support++;
    }

    @Override
    public String toString() {
        return "[" + Joiner.on(' ').join(itemsetItem) + "]\tsupp: " + String.format("%.4f",this.getRelativeSupport(this.totalCount)) + "";
    }

    public double getRelativeSupport(int nbObject) {
        return (double)this.getAbsoluteSupport() / (double)nbObject;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(itemset);
    }

    @Override
    public boolean equals(Object obj) {
        return Arrays.equals(((ItemsetString)obj).getItems(),getItems());
    }


    public double getRelativeSupport() {
        return this.getRelativeSupport(this.totalCount);
    }
}
