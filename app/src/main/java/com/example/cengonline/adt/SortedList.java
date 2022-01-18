package com.example.cengonline.adt;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SortedList<T extends  Comparable> extends AbstractList<T> {

    private List<T> list = new ArrayList<T>();

    @Override
    public void add(int position, T element){
        list.add(element);
        Collections.sort(list);
    }

    @Override
    public T get(int index) {
        return list.get(index);
    }

    @Override
    public int size() {
        return list.size();
    }
}
