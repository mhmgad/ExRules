package de.mpii.frequentrulesminning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by gadelrab on 2/17/16.
 */
public class AssociationRule {


    String head;
    List<String> body;
    double confidence;

    public AssociationRule(String head, List<String> body, double confidence) {
        this.head = head;
        this.body = body;
        this.confidence = confidence;
    }

    @Override
    public String toString() {
        return String.format("%.4f", this.confidence) + "\t" + this.head + "\t" + this.body;
    }


    public static AssociationRule fromString(String rule) {

        if (rule.trim().isEmpty())
            return null;

        String[] parts = rule.split("\t");
        return new AssociationRule(parts[1], getListFromString(parts[2]), Double.parseDouble(parts[0]));

    }

    private static List<String> getListFromString(String str) {
        str = str.substring(1, str.length() - 1);
        String[] elements = str.split(", ");
        return Arrays.asList(elements);
    }


    public double getConfidence() {
        return confidence;
    }

    public String getHead() {
        return head;
    }

    public List<String> getBody() {
        return body;
    }
}