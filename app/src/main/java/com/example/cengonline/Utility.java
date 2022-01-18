package com.example.cengonline;

import android.app.Activity;
import android.util.TypedValue;

public class Utility {

    private static Utility instance;

    private Utility(){}


    public int DPtoPX(int dps, Activity activity){
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dps, activity.getResources().getDisplayMetrics()));
    }


    public static Utility getInstance(){

        if(instance == null){
            instance = new Utility();
        }

        return instance;
    }
}
