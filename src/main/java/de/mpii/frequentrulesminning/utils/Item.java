package de.mpii.frequentrulesminning.utils;

/**
 * Created by gadelrab on 2/18/16.
 */
public class Item implements  Comparable<Item>{

    private final  int hashCode;
    String predicate;

    public int getHashCode() {
        return hashCode;
    }

    String object;
    int id;


    public Item(String predicate, String object) {
        this.predicate = predicate;
        this.object = object;
        this.hashCode= toString().hashCode();
        this.id=-1;
    }

    @Override
    public int hashCode() {
        return (hashCode);
    }

    public static Item fromString(String itemString){
        try {
            int splitIndex = itemString.indexOf(',');
            String pred = itemString.substring(1, splitIndex);
            String obj = itemString.substring(splitIndex + 1, itemString.length() - 1);
            return new Item(pred, obj);
        }catch (Exception e){
            System.out.println(itemString);
            e.printStackTrace();
        }

        return null;


    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return '('+predicate+','+object+')';
    }


    public String getObject() {
        return object;
    }

    public static void main(String[]args){
        Item m=Item.fromString("(<wasCreatedOnDate>,\"1966-##-##\")");
        System.out.println(m.predicate);
        System.out.println(m.object);

    }

    @Override
    public boolean equals(Object obj) {
        if(obj==null)
            return false;
        return predicate.equals(((Item)obj).predicate)&&object.equals(((Item)obj).object) ;
    }

    public String getPredicate() {
        return predicate;
    }




    public int getId() {
        return id;
    }

    @Override
    public int compareTo(Item o) {
        int idsDiff=this.id-o.id;
        if(this.id!=-1&&o.id!=-1&&idsDiff!=0)
            return idsDiff;

        int predicateDiff=this.predicate.compareTo(o.predicate);
        if(predicateDiff!=0)
            return predicateDiff;

        return this.object.compareTo(o.object);
    }
}
