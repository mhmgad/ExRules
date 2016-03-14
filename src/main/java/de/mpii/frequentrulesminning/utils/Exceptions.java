package de.mpii.frequentrulesminning.utils;

import java.util.*;
import java.util.function.Consumer;

/**
 * Created by gadelrab on 3/11/16.
 */
public class Exceptions implements Iterable<ExceptionItem>{

    List<ExceptionItem> exceptions;

    public Exceptions() {
        exceptions=new ArrayList<>();
    }

    public Exceptions(List<ExceptionItem> exceptions) {
        this();
        this.exceptions = exceptions;
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
        Collections.sort(this.exceptions,(x, y)-> y.getAbsoluteSupport()-x.getAbsoluteSupport());
    }

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        exceptions.forEach((ex)-> sb.append(ex+"\n"));
        return sb.toString();
    }
}
