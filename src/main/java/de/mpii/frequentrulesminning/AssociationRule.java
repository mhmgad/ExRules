package de.mpii.frequentrulesminning;

import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by gadelrab on 2/17/16.
 */
public class AssociationRule {


    Item head;
    List<Item> body;
    double confidence;

    public AssociationRule(Item head, List<Item> body, double confidence) {
        this.head = head;
        this.body = body;
        this.confidence = confidence;
    }

    @Override
    public String toString() {
        return String.format("%.4f", this.confidence) + "\t" + this.head + "\t[" + Joiner.on("\t").join(this.body)+"]";
    }


    public static AssociationRule fromString(String rule) {

        if (rule.trim().isEmpty())
            return null;

        String[] parts = rule.split("\t");
        return new AssociationRule(Item.fromString(parts[1]), getListFromString(parts[2]), Double.parseDouble(parts[0]));

    }

    private static List<Item> getListFromString(String str) {
        str = str.substring(1, str.length() - 1);
        String[] elements = str.split("\t");
        ArrayList<Item> items=new ArrayList<>(elements.length);
        for (int i=0;i<elements.length;i++) {
            items.add(Item.fromString(elements[i]));
        }

        return items;
    }


    public double getConfidence() {
        return confidence;
    }

    public Item getHead() {
        return head;
    }

    public List<Item> getBody() {
        return body;
    }
}