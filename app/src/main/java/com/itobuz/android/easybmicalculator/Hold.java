package com.itobuz.android.easybmicalculator;

/**
 * Created by Justyn on 2/27/2017.
 */

//class to temp hold user specs
public class Hold{
    private static String name;
    private static String pass;
    private static int id;
    //private static boolean newuser;
    private static int age;
    private static String sex;
    private static double weight;
    private static String wunit;
    private static int wpos=0;
    private static double height;
    private static double inch;
    private static String hunit;
    private static int hpos=0;

    //no arg consctor
    Hold(){}

    Hold(String name, String pass, int id){
        Hold.name = name;
        Hold.pass = pass;
        Hold.id = id;
        //Hold.newuser = true;
    }

    public static String getName() {
        return name;
    }

    public static String getPass() {
        return pass;
    }

    //public static boolean getNewUser(){ return newuser; }

    public static int getId() { return id; }

    //public static void setNewUser(){ Hold.newuser = false;}

    public static int getAge() {
        return age;
    }

    public static void setAge(int age) {
        Hold.age = age;
    }

    public static String getSex() {
        return sex;
    }

    public static void setSex(String sex) {
        Hold.sex = sex;
    }

    public static double getWeight() {
        return weight;
    }

    public static void setWeight(double weight) {
        Hold.weight = weight;
    }

    public static String getWunit() {
        return wunit;
    }

    public static void setWunit(String wunit) {
        Hold.wunit = wunit;
    }

    public static int getWpos() {
        return wpos;
    }

    public static void setWpos(int wpos) {
        Hold.wpos = wpos;
    }

    public static double getHeight() {
        return height;
    }

    public static void setHeight(double height) {
        Hold.height = height;
    }

    public static double getInch() {
        return inch;
    }

    public static void setInch(double inch) {
        Hold.inch = inch;
    }

    public static String getHunit() {
        return hunit;
    }

    public static void setHunit(String hunit) {
        Hold.hunit = hunit;
    }

    public static int getHpos() {
        return hpos;
    }

    public static void setHpos(int hpos) {
        Hold.hpos = hpos;
    }
}