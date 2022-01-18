package com.example.cengonline.model;

public enum FileType{
    PDF(1),
    TXT(2);

    private int value;
    private FileType(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }
}