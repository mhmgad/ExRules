package de.mpii.frequentrulesminning;

import de.mpii.frequentrulesminning.utils.AssocRulesExtended;

/**
 * Created by gadelrab on 4/13/16.
 */
public class SystemConfig {


    boolean materialization;
    boolean weight;
    boolean order;
    private AssocRulesExtended.SortingType processingOrder= AssocRulesExtended.SortingType.LIFT;

    public SystemConfig(boolean materialization, boolean weight, boolean order) {
        this.materialization = materialization;
        this.weight = weight;
        this.order = order;
    }

    @Override
    public String toString() {
        return "SystemConfig{" +
                "materialization=" + materialization +
                ", weight=" + weight +
                ", order=" + order +
                ", processingOrder=" + processingOrder +
                '}';
    }

    public boolean isOrder() {
        return order;
    }

    public boolean isWeight() {
        return weight;
    }

    public boolean isMaterialization() {
        return materialization;
    }

    public void setMaterialization(boolean materialization) {
        this.materialization = materialization;
    }

    public void setWeight(boolean weight) {
        this.weight = weight;
    }

    public void setOrder(boolean order) {
        this.order = order;
    }


    public AssocRulesExtended.SortingType getProcessingOrder() {
        return processingOrder;
    }
}
