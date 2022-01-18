package com.example.cengonline.model;


import android.util.Log;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

public class MyTimestamp extends Timestamp implements Serializable {

    public MyTimestamp(){
        super(new Date().getTime());
    }

    public MyTimestamp(Date date){
        super(date.getTime());
    }

    private String toMonth(int monthInt){

        String monthStr = "";
        switch(monthInt){
            case 1: monthStr = "Jan"; break;
            case 2: monthStr = "Feb"; break;
            case 3: monthStr = "Mar"; break;
            case 4: monthStr = "Apr"; break;
            case 5: monthStr = "May"; break;
            case 6: monthStr = "Jun"; break;
            case 7: monthStr = "Jul"; break;
            case 8: monthStr = "Aug"; break;
            case 9: monthStr = "Sep"; break;
            case 10: monthStr = "Oct"; break;
            case 11: monthStr = "Nov"; break;
            case 12: monthStr = "Dec"; break;
        }
        return monthStr;
    }

    public String toStringDate(){

        try{
            String str = super.toString();
            String month = str.substring(5, 7);
            String day = str.substring(8, 10);
            String year = str.substring(0, 4);
            Log.w("ASDASDa", str);
            return Integer.parseInt(month) + "/" +Integer.parseInt(day) + "/" + Integer.parseInt(year);
        }
        catch(Exception ex){
            return "01/01/1970";
        }
    }

    public String toStringTime(){

        try{
            return super.toString().substring(11, 16);
        }
        catch (Exception ex){
            return "00:00";
        }
    }

    public String toStringAssignmentDue(){

        try{
            String str = super.toString();
            String monthStr = toMonth(Integer.parseInt(str.substring(5, 7)));
            String day = str.substring(8, 10);
            String time = str.substring(11, 16);

            return "Due " + monthStr + " " + Integer.parseInt(day) + ", " + time;
        }
        catch(Exception ex){
            return super.toString();
        }
    }

    public String superToString(){
        return super.toString();
    }

    @Override
    public String toString(){

        try{
            String str = super.toString();
            int monthInt = Integer.parseInt(str.substring(5, 7));
            int dayInt = Integer.parseInt(str.substring(8, 10));
            String monthStr = toMonth(monthInt);
            int yearInt = Integer.parseInt(str.substring(0, 4));
            int nowYear = Integer.parseInt(new Timestamp(new Date().getTime()).toString().substring(0, 4));
            if(nowYear != yearInt){
                return monthStr + " " + str.substring(8, 10) + ", " + yearInt;
            }
            else{
                int nowDay = Integer.parseInt(new Timestamp(new Date().getTime()).toString().substring(8, 10));
                if(nowDay != dayInt){
                    return monthStr + " " + dayInt;
                }
                else{
                    return str.substring(11, 16);
                }
            }
        }
        catch(Exception ex){
            return super.toString();
        }
    }
}
