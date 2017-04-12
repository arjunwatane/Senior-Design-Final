package com.itobuz.android.easybmicalculator;
//todo need to make user bio persistent

import android.content.ContentValues;
import android.content.Context;
import net.sqlcipher.database.SQLiteOpenHelper;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.Cursor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static java.security.AccessController.getContext;
//todo//just need to store user bio data...do not need to do anything with it
/**
 * Created by Debasis on 26/9/16.
 * Modified by Justyn
 */
public class DatabaseHelper extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 1;//SCHEMA
    private static final String DATABASE_NAME = "users.db";
    private static final String TABLE_USERS = "users";
    public static final String TABLE_USERS_BIO = "usersbio";
    //bio data
    public static final String COLUMN_ID_B = "id";
    private static final String COLUMN_NAME = "name";
    public static final String COLUMN_AGE = "age";
    private static final String COLUMN_SEX = "sex";
    private static final String COLUMN_WEIGHT = "weight";
    private static final String COLUMN_WEIGHT_UNIT = "weightunit";
    private static final String COLUMN_HEIGHT = "height";
    private static final String COLUMN_HEIGHT_INCH= "heightinch";
    private static final String COLUMN_HEIGHT_UNIT = "heightunit";
    private static final String COLUMN_RESULT = "result";
    public static final String COLUMN_DATE = "created_at";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_WEIGHT_POS = "wpos";
    private static final String COLUMN_HEIGHT_POS = "hpos";
    //user info for glucose storage
    private static final String TABLE_INFO = "userinfo";
    private static final String COLUMN_ID = "ID";
    private static final String COLUMN_INFOID = "infoID";
    private static final String COLUMN_USERID = "userID";
    private static final String COLUMN_USER = "user";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_GLUCOSE = "glucose_reading";
    private static final String COLUMN_TIMESTAMP = "time_of_reading";

    private SQLiteDatabase db;
    private Context context;

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    private int lastId;
    private float lastResult;
    private String lastAge, lastWeight, lastWeightUnit, lastHeight, lastHeightUnit, lastHeightInch;
    private String lastRadio;
    private Cursor cursor;

    //create user bio table//removed excess parts
    private static final String TABLE_CREATE_USER_BIOS = "CREATE TABLE " + TABLE_USERS_BIO + " ("+
            COLUMN_ID_B+" INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME+" VARCHAR, " +
            COLUMN_AGE +" VARCHAR, "+ COLUMN_SEX +" VARCHAR, "+ COLUMN_WEIGHT +" DOUBLE, "+
            COLUMN_WEIGHT_UNIT +" VARCHAR, "+ COLUMN_HEIGHT +" DOUBLE, "+
            COLUMN_HEIGHT_INCH +" DOUBLE, "+ COLUMN_HEIGHT_UNIT +" VARCHAR, "+ COLUMN_WEIGHT_POS +
            " INTEGER, " + COLUMN_HEIGHT_POS + " INTEGER);";

    //create user table
    private static final String TABLE_CREATE_USERS = "CREATE TABLE "+ TABLE_USERS + " (ID integer primary key not null, " +
            "user text not null, password text not null);";

    //create info table
    private static final String TABLE_CREATE_INFO = "CREATE TABLE "+ TABLE_INFO + " (infoID integer primary key not null, " +
            "userID integer not null, user text not null, glucose_reading integer not null, time_of_reading datetime not null, status not null);";

    //create a db helper based on context
    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.loadLibs(context);
        db.execSQL(TABLE_CREATE_USERS);
        db.execSQL(TABLE_CREATE_INFO);
        db.execSQL(TABLE_CREATE_USER_BIOS);
        this.db=db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_USERS_BIO);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_INFO);

        this.onCreate(db);
    }

    //update user age and sex to also be used in policy check//two fragment
    public void updateBio2Policy(String sex, int age){
        load();
        db = getReadableDatabase("test");
        ContentValues values = new ContentValues();
        values.put(COLUMN_AGE, age);
        values.put(COLUMN_SEX, sex);

        db.update(TABLE_USERS_BIO, values, "name = " + Hold.getName(), null);
        db.close();
    }

    //update user weights also to be used in policy check//three fragment
    public void updateBio3Policy(double weight, String wunit, int pos){
        load();
        db = getReadableDatabase("test");
        ContentValues vals = new ContentValues();
        vals.put(COLUMN_WEIGHT,weight);
        vals.put(COLUMN_WEIGHT_UNIT,wunit);
        vals.put(COLUMN_WEIGHT_POS,pos);

        db.update(TABLE_USERS_BIO, vals, "name = " + Hold.getName(), null);
        db.close();
    }

    //update user heights also to be used in policy check//four fragment
    public void updateBio4Policy(double height, double inch, String hunit, int pos){
        load();
        db = getReadableDatabase("test");
        ContentValues vals = new ContentValues();
        vals.put(COLUMN_HEIGHT, height);
        vals.put(COLUMN_HEIGHT_INCH, inch);
        vals.put(COLUMN_HEIGHT_UNIT, hunit);
        vals.put(COLUMN_HEIGHT_POS, pos);

        db.update(TABLE_USERS_BIO, vals, "name = " + Hold.getName(), null);
        db.close();
    }

    //todo: update all bio values
    boolean updateBioAll(int age, String sex, double weight, String wunit, double height,
            double inch, String hunit, int wpos, int hpos){
        updateBio2Policy(sex,age);
        updateBio3Policy(weight,wunit,wpos);
        updateBio4Policy(height,inch,hunit,hpos);

        return true;
    }

    //insert user bio data into table
    public boolean insertUserBio(int age, String sex, double weight, String wunit, double height,
            double inch, String hunit, int wpos, int hpos){
        load();
//        context = getContext().getApplicationContext();

//        System.out.println(this);
        db = this.getWritableDatabase("test");
        //create new table if needed
//        db.execSQL(TABLE_CREATE_USER_BIOS);
        ContentValues values = new ContentValues();
        //String query = "SELECT * FROM " + TABLE_USERS_BIO;

        //Cursor cursor = db.rawQuery(query,null);
        //int count = cursor.getCount();
        //auto add id
        values.put(COLUMN_NAME, Hold.getName());
        values.put(COLUMN_AGE, age);
        values.put(COLUMN_SEX, sex);
        values.put(COLUMN_WEIGHT, weight);
        values.put(COLUMN_WEIGHT_UNIT,wunit);
        values.put(COLUMN_HEIGHT,height);
        values.put(COLUMN_HEIGHT_INCH,inch);
        values.put(COLUMN_HEIGHT_UNIT,hunit);
        values.put(COLUMN_WEIGHT_POS,wpos);
        values.put(COLUMN_HEIGHT_POS,hpos);

        db.insert(TABLE_USERS_BIO,null,values);
        db.close();
        return true;
    }

    //insert user into db
    public void insertUser(User u){
        load();
        db = this.getWritableDatabase("test");
        ContentValues values = new ContentValues();

        String query = "SELECT * FROM " + TABLE_USERS;
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();

        values.put(COLUMN_ID, count);
        values.put(COLUMN_USER, u.getName());
        values.put(COLUMN_PASSWORD, u.getPassword());

        db.insert(TABLE_USERS, null, values);
        db.close();
    }

    public String searchPass(String username){
        load();
        db = this.getReadableDatabase("test");
        String query = "SELECT user, password FROM " + TABLE_USERS;
        Cursor cursor = db.rawQuery(query, null);
        String user, pass;

        pass = randomString(15);
        if (cursor.moveToFirst()) {
            do {
                user = cursor.getString(0);

                if (user.equals(username)) {
                    pass = cursor.getString(1);
                    break;
                }
            }
            while (cursor.moveToNext());
        }
        db.close();
        cursor.close();
        return pass;
    }

    public int searchID(String username, String pass) {
        load();
        db = this.getReadableDatabase("test");
        String query = "SELECT ID FROM " + TABLE_USERS + " where user = ? and password = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username, pass});
        int ID;

        cursor.moveToFirst();

        ID = cursor.getInt(0);

        db.close();
        cursor.close();
        return ID;
    }

    public ArrayList<QuerySet> search(String username, int ID) {
        //   load(); //not sure why this was removed
        db = this.getReadableDatabase("test");
        String query = "SELECT glucose_reading, time_of_reading FROM " + TABLE_INFO + " WHERE user = ? AND userID = ? ORDER BY time_of_reading DESC";
        Cursor cursor = db.rawQuery(query, new String[]{username, String.valueOf(ID)});

        ArrayList<QuerySet> queryResults = new ArrayList<QuerySet>();

        if(cursor.moveToFirst()){
            do {
                QuerySet queryHolder = new QuerySet();
                queryHolder.glucoseReading = cursor.getString(0);
                queryHolder.timestamp = cursor.getString(1);
                queryHolder.state = cursor.getString(2);
                queryHolder.id = ID;
                queryResults.add(queryHolder);
            }
            while (cursor.moveToNext());
        }
        db.close();
        cursor.close();

        return queryResults;
    }

    //search the bio info table and return the row
    public DataProvider searchInfo(){
        load();
        db = this.getReadableDatabase("test");
        //String query = "SELECT name, age, sex, weight, weightunit, height, heightinch, heightunit, wpos, hpos FROM " +
        String query = "SELECT * FROM " +
                TABLE_USERS_BIO + " WHERE name = ?";

        Cursor cursor = db.rawQuery(query, new String[]{Hold.getName()});

        db.close();

        if(cursor.getCount()==0){
            cursor.close();
            return null;
        }

        DataProvider dp = new DataProvider(cursor.getString(0),cursor.getInt(1),cursor.getString(2),cursor.
                getDouble(3),cursor.getString(4),cursor.getInt(5),cursor.getInt(6),cursor.getString
                (7),cursor.getInt(8),cursor.getInt(9));

        cursor.close();
        return dp;
    }

    //replaces insertBmiRow()
    //insert a glucose reading into the log
    public boolean insertGlucLog(String username, int userID, int glucoseValue, String status){
        load();
        db = this.getWritableDatabase("test");
        ContentValues values = new ContentValues();

        String query = "SELECT * FROM " + TABLE_INFO;
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();

        values.put(COLUMN_INFOID, count);
        values.put(COLUMN_USERID, userID);
        values.put(COLUMN_USER, username);
        values.put(COLUMN_GLUCOSE, glucoseValue);
        values.put(COLUMN_TIMESTAMP, getDateTime());
        values.put(COLUMN_STATUS,status);

        db.insert(TABLE_INFO, null, values);
        db.close();

        return true;
    }

    // Getting Glu History Count
    public int getGluHistoryCount(){
        load();
        String query = "SELECT ID FROM " + TABLE_INFO + " WHERE user = ?";
        //String query = "SELECT  FROM " + TABLE_UzSERS + " where user = ? and password = ?";
        db = this.getReadableDatabase("test");
        Cursor cursor = db.rawQuery(query, new String[]{Hold.getName()});
        int count = cursor.getCount();
        db.close();
        return count;
    }

    /**
     * Method lastBmiId
     * @return int lastId
     */
    public int lastGluId(){
        load();
        db = this.getReadableDatabase("test");
        String status="1";
        String query = "SELECT " + COLUMN_ID +" FROM "+ TABLE_INFO +" WHERE " + COLUMN_STATUS +" = " + status + " AND user = ? ' ORDER BY "+COLUMN_ID+"  DESC limit 1";
        db = this.getReadableDatabase("test");
        cursor = db.rawQuery(query, new String[]{Hold.getName()});
        if (cursor != null && cursor.moveToFirst()) {
            lastId = cursor.getInt(0); //The 0 is the column index, we only have 1 column, so the index is 0
        }
        return lastId;
    }

    /**
     * Method lastBmiResult
     * @return float lastResult
     */
    public float lastGluResult(){
        load();
        db = this.getReadableDatabase("test");
        String status="1";
        String query = "SELECT " + COLUMN_RESULT +" FROM "+ TABLE_INFO+" WHERE " + COLUMN_STATUS +" = '" + status + "' ORDER BY "+COLUMN_ID+"  DESC limit 1";
        db = this.getReadableDatabase("test");
        cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            lastResult = cursor.getInt(0); //The 0 is the column index, we only have 1 column, so the index is 0
        }
        return lastResult;
    }

    /**
     * Method LastRecords
     * @return cursor
     */
    public Cursor lastRecords( ){
        load();
        db = this.getReadableDatabase("test");
        String getlastquery = "SELECT " + COLUMN_INFOID +", "+ COLUMN_USERID +", " + COLUMN_USER+", " + COLUMN_GLUCOSE +", " + COLUMN_TIMESTAMP +", " + COLUMN_STATUS +" FROM "+ TABLE_INFO+";";
        db = this.getReadableDatabase("test");
        cursor = db.rawQuery(getlastquery, null);
        return cursor;
    }

    /**
     * Method getBmiInformation
     * @return cursor
     */
    public Cursor getBmiInformation(){
        //String status="1";
        //String allQuery = "SELECT "+COLUMN_INFOID+", "+COLUMN_ID+ ", " +COLUMN_AGE+" , "+COLUMN_RESULT+" FROM " + TABLE_USERS_BIO+" WHERE "+ COLUMN_STATUS +" = '" + status + "' ORDER BY "+COLUMN_ID+"  DESC";
        //db = this.getReadableDatabase("test");
        //cursor = db.rawQuery(allQuery, null);
        return lastRecords();//cursor;
    }

    //remove a glucose test
    public void remgluItem(int id){
        load();
        db = this.getWritableDatabase("test");
//        String sId = String.valueOf(id);
//        String status="1";
        db.execSQL("DELETE FROM "+TABLE_INFO+" WHERE "+COLUMN_INFOID+" = " + id); // + "' AND " + COLUMN_STATUS +" = '" + status + "'"
    }

    String randomString(int len){
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }

    //load the db by provided context
    public void load(){ SQLiteDatabase.loadLibs(context); }

    public static class QuerySet{
        public String glucoseReading;
        public String timestamp;
        public String state;
        public int id;
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    //converts 1D array to 2D to write to file
    public void writefile(String name, double[] vals){
        double[][] values = new double[1][vals.length];
        values[0] = vals;
        //writefile(name,values);
    }

    //todo call readfile before write
    //takes filename and adds user name to front
    //todo: add user name to filename
    //create a file
    //// TODO: 1/30/2017 change to accpet a filename and data
    // // TODO: 2/1/2017 changet to writefile
    //delete the file if adding to it if necessary
    //public void writefile(String name, double[][] d){
    //// TODO: 2/2/2017 trainvals glucdata trainres strings
    //assume input array has data
    //public void writefile(String name, double[][] d){
    public void writefile(){
        System.out.println("Writing file: ");
        String name = Hold.getName()+"trainvals"; //name = Hold.getName() + name;
        //file will have been read already--input array has data in it
        try{
            deletefile("name");//// TODO: 2/2/2017 will need to keep this line for later
            FileOutputStream fos = context.openFileOutput(name, Context.MODE_APPEND);

            double d[][] = new double[1][20];
            double[] t = {1.0, 2.5555555, 3.0215};
            d[0] = t;

            StringBuilder sb = new StringBuilder();
            System.out.println(d.length);

            for(int i=0; i<d.length; i++) {
                for(int j = 0; j < d[i].length; j++){
                    //start of a line
                    if(j == 0) sb.append(d[i][j]);
                        //not start of line
                    else sb.append(" "+d[i][j]);
                    //end of a line
                    if(j==d[i].length-1) sb.append('\n');
                }
            }

            System.out.println(sb.toString().getBytes() + "'\n"+sb);
            //write bytestring to file
            fos.write(sb.toString().getBytes());
            //close the out stream
            fos.close();
            System.out.println("words written");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //todo: add user name to filename
    //accept filename as input to read a file
    //returns output doubles
    public double[][] readfile(String name){
    //public void readfile(){
        String fname = Hold.getName()+name;
        System.out.println("Reading file: ");
        BufferedReader reader;
        try{
            reader = new BufferedReader(new InputStreamReader(context.openFileInput(fname)));
            String in;
            StringBuilder sb = new StringBuilder();

            //scan in each line of input
            while((in = reader.readLine()) != null) sb.append(in + "\n");
            System.out.println(sb.toString());
            System.out.println(charcount(sb)+"=----------"+reader.toString());
            reader.close();

            //split each row of the file
            String splitter[] = sb.toString().split("\n");
            double[][] ret = new double[30][30];// TODO: 2/2/2017 reconsider return size
            //run for each row of splitter
            for(int i=0; i<splitter.length; i++){
                //split the row into doubles
                String row[] = splitter[i].split(" ");
                //fill each row of the double array
                for(int j=0; j<row.length; j++){
                    //take the each double from the row
                    ret[i][j]=Double.parseDouble(row[j]);
                }
            }
            reader.close();

            for(int i=0; i<ret.length; i++){
                System.out.println();
                for(int j=0; j<ret[i].length; j++){
                    System.out.print(ret[i][j]+" ");
                }
            }

            //return the read file
            //todo need to account for 1d array when it is returned to caller--handle at call
//            if(fname.contains("trainvals")){
//                double[][] r = new double[1][ret[0].length];
//                double[] q = r;
//                return r;
//            }
            return ret;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //silence error
        return (new double[1][1]);
    }

    //delete a file by it filename
    public void deletefile(String fname){
        String user;
        //checks if user name needs to be added to file name
        if(!fname.contains(Hold.getName())){
            user = Hold.getName() + fname;
        }
        else user = fname;

        //context.deleteFile(user);
        System.out.println(user+" deleted: "+context.deleteFile(user));
    }

    public int charcount(StringBuilder sb){
        int count=0;
        for(int i=0; i<sb.length(); i++) if(sb.charAt(i)=='\n') count++;
        return count;
    }
//}//trainacutal is 1D
//glucosedata and result is 2D
}