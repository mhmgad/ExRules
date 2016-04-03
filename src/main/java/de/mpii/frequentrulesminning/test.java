package de.mpii.frequentrulesminning;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.Collection;

/**
 * Created by gadelrab on 4/3/16.
 */
public class test {



     static class Key{

        int k;



        public Key(int k) {
            this.k = k;
        }

        @Override
        public int hashCode() {
            return Integer.hashCode(k);
        }

        @Override
        public boolean equals(Object obj) {
            return k==((Key)obj).k;
        }
    }


    public static void main(String [] args){

        Multimap<Key, Integer> t= HashMultimap.create();

        Key x=new Key(1);

        t.put(x,1);
        t.put(new Key(1),2);

        t.put(new Key(2),2);

        Collection<Integer> l=t.get(new Key(1));

        Collection<Integer> l2=t.get(x);
        System.out.println(l);

        t.put(new Key(1),3);
        t.put(new Key(1),4);

        System.out.println(l);

        t.remove(new Key(1),2);

        System.out.println(l);
        System.out.println(l2);
        Collection<Integer> l3=t.get(new Key(3));
        System.out.println(l3);
        t.put(new Key(3),50);
System.out.println(l3);


    }
}
