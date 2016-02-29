package de.mpii.frequentrulesminning.utils;

import ca.pfv.spmf.patterns.AbstractOrderedItemset;
import com.google.common.base.Joiner;
import com.google.common.primitives.Ints;
import de.mpii.frequentrulesminning.Item;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by gadelrab on 2/29/16.
 */
public class ItemsetString extends AbstractOrderedItemset {


    /** the array of items **/
    public int[] itemset;

    /** the array of items **/
    public Item[] itemsetItem;

    /**  the support of this itemset */
    public int support = 0;

    /**
     * Constructor
     * @param items an array of items that should be added to the new itemset
     */
    public ItemsetString(int[] items) {
        this.itemset=items;
        Arrays.sort(items);
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
    public ItemsetString(){
        itemset = new int[]{};
        itemsetItem = new Item[]{};
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
        return "["+Joiner.on(' ').join(itemsetItem)+"]";
    }
}
