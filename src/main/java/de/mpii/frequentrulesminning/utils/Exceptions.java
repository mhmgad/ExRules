package de.mpii.frequentrulesminning.utils;

import de.mpii.frequentrulesminning.ExceptionRanker;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by gadelrab on 3/11/16.
 */
public class Exceptions implements Iterable<ExceptionItem>{

    List<ExceptionItem> exceptions;

    public List<ExceptionItem> getExceptions() {
        return exceptions;
    }



    public Exceptions() {
        exceptions=new ArrayList<>();
    }

    public Exceptions(List<ExceptionItem> exceptions) {
        this();
        this.setExceptions(exceptions);
        
    }public void setExceptions(List<ExceptionItem> exception) {
        this.exceptions = exception;
        //sortOnSupport();
        //sortOnLiftThenInvConflict();
    }

    @Override
    public Iterator<ExceptionItem> iterator() {
        return exceptions.iterator();
    }

    @Override
    public void forEach(Consumer<? super ExceptionItem> action) {
        exceptions.forEach(action);
    }

    @Override
    public Spliterator<ExceptionItem> spliterator() {
        return exceptions.spliterator();
    }

    public void add(ExceptionItem exceptionItem){
        this.exceptions.add(exceptionItem);
    }

    public int size() {
        return exceptions.size();
    }

    public void sortOnSupport(){
        Collections.sort(this.exceptions,(ExceptionItem x,ExceptionItem  y)-> y.getAbsoluteSupport()-x.getAbsoluteSupport());
    }

    public void sortOnLift(){
        Collections.sort(this.exceptions,Comparator.comparing(ExceptionItem::getLift).reversed());
    }

    public void sortOnLiftThenInvConflict(){
        Collections.sort(this.exceptions,Comparator.comparing(ExceptionItem::getLift).reversed().thenComparing(ExceptionItem::getInvertedConflictScore));
    }

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        exceptions.forEach((ex)-> sb.append(ex+"\n"));
        return sb.toString();
    }


    public int[] getExceptionsInts(){
        return exceptions.stream().mapToInt((x) -> x.getFirstItems()).toArray();
    }


    public void sortOnPosNegConfidence() {
        Collections.sort(this.exceptions,Comparator.comparing(ExceptionItem::getPosNegConfidence).reversed());
    }

    public void sortOnConfidence() {
        Collections.sort(this.exceptions,Comparator.comparing(ExceptionItem::getConfidence).reversed());
    }

    public List<ExceptionItem> getExceptions(double minimumSupport) {
        return exceptions.stream().filter((e)->e.getRelativeSupport()>=minimumSupport).collect(Collectors.toList());
    }

    public int[] getExceptionsInts(double minimumSupport) {
        return getExceptions( minimumSupport).stream().mapToInt(ExceptionItem::getFirstItems).toArray();

    }

    public int[] getTopKExceptionsInts(int numberOfEceptions) {

        return exceptions.subList(0,numberOfEceptions).stream().mapToInt(ExceptionItem::getFirstItems).toArray();
    }

    public Item [] getTopKExceptions(int numberOfEceptions) {
        int size= Math.min(numberOfEceptions,exceptions.size());
        Item[] output=new Item[0];

        for(int i=0;i<size;i++){
            output= ArrayUtils.addAll(output,exceptions.get(i).getItemsetItem());
        }

        return  output;

        //exceptions.subList(0,numberOfEceptions).stream().flatMap(ExceptionItem::getItemsetItem).
       // return ArrayUtils.addAll(new Item[0],exceptions.subList(0,numberOfEceptions).stream().map(ExceptionItem::getItemsetItem).collect(Collectors.toList()));

    }
}
