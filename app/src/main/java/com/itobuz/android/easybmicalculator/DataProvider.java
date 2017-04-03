package com.itobuz.android.easybmicalculator;

/**
 * Created by itobuz-01 on 26/10/16.
 */


public class DataProvider {
    private String name;
    private int age;
    private String sex;
    private double weight;
    private String weightunit;
    private double height;
    private double heightinch;
    private String heightunit;
    private int wpos;
    private int hpos;

    public DataProvider(){
        // Empty Constractor
    }


//    public DataProvider(int id, String date, String age, Float result){
//        this.id = id;
//        this.date = date;
//        this.age = age;
//        this.result = result;
//    }

    public DataProvider(String name, int age, String sex, double weight, String weightunit, double height, double heightinch, String heightunit, int wpos, int hpos){
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.weight = weight;
        this.weightunit = weightunit;
        this.height = height;
        this.heightunit =heightunit;
        this.heightinch =heightinch;
        this.wpos = wpos;
        this.hpos = hpos;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getSex() {
        return sex;
    }

    public double getWeight() {
        return weight;
    }

    public String getWeightunit() {
        return weightunit;
    }

    public double getHeight() {
        return height;
    }

    public double getHeightinch() {
        return heightinch;
    }

    public String getHeightunit() {
        return heightunit;
    }

    public int getWpos() {
        return wpos;
    }

    public int getHpos() {
        return hpos;
    }
}