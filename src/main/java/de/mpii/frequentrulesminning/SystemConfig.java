package de.mpii.frequentrulesminning;

/**
 * Created by gadelrab on 4/13/16.
 */
public class SystemConfig {


    boolean materialization;
    boolean weight;
    boolean order;

    public SystemConfig(boolean materialization, boolean weight, boolean order) {
        this.materialization = materialization;
        this.weight = weight;
        this.order = order;
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
}
