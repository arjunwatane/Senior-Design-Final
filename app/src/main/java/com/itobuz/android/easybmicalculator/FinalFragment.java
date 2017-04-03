package com.itobuz.android.easybmicalculator;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class FinalFragment extends Fragment {

    private static final String USER_AGE = "userAge";
    private static final String USER_SEX ="userSex";

    private static final String USER_WEIGHT = "userWeight";
    private static final String USER_WEIGHT_UNIT = "userWeightUnit";
    private static final String USER_WEIGHT_UNIT_POS = "userWeightUnitPos";

    private static final String USER_HEIGHT = "userHeight";
    private static final String USER_HEIGHT_INCH = "userHeightInch";
    private static final String USER_HEIGHT_UNIT = "userHeightUnit";
    private static final String USER_HEIGHT_UNIT_POS = "userHeightUnitPos";

    private static final String ARG_LAYOUT_RES_ID = "layoutResId";

    private TextView resultBmi;

    private int layoutResId;
    private float bmiResult;

    private int result_bmi;
    private int userAge;
    private String userSex;
    private double userWeight;
    private String userWeightUnit;
    private int userWeightUnitPos;
    private double userHight;
    private double userHightInch;
    private String userHeightUnit;
    private int userHeightUnitPos;
    private  String strDate;
    //private int userStatus;

    DatabaseHelper mydb;
    Context context;

    public static FinalFragment newInstance(int layoutResId) {
        FinalFragment finalFragment = new FinalFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_RES_ID, layoutResId);
        finalFragment.setArguments(args);

        return finalFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(ARG_LAYOUT_RES_ID)) {
            layoutResId = getArguments().getInt(ARG_LAYOUT_RES_ID);
        }

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View resultBmiView = inflater.inflate(R.layout.final_fragment, container,false);

        resultBmi = (TextView) resultBmiView.findViewById(R.id.result_bmi);

        return resultBmiView;
    }

    //todo: remove shared pref retrival and change to sqldb retrieval
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //DatabaseHelper db = new DatabaseHelper(this.getContext());
            context = getContext().getApplicationContext();

            mydb = new DatabaseHelper(this.context);
//            mydb.load();
            if(mydb.insertUserBio(Hold.getAge(),Hold.getSex(),Hold.getWeight(),Hold.getWunit(),Hold.
                    getHeight(),Hold.getInch(),Hold.getHunit(),Hold.getWpos(),Hold.getHpos())){
                context = getContext().getApplicationContext();

            }
            //insertUserBio(int age, String sex, double weight, String wunit, double height,
            //double inch, String hunit, int wpos, int hpos)

            //get the user info
            //DataProvider dp = mydb.searchInfo();

            //no user bio
/*            if(dp == null){
                mydb.insertUserBio(0,"empty",0,"empty",0,0,"empty",0,0);
            }

            //db entry exists at least partially
            else{
                userAge = dp.getAge() > 0 ? dp.getAge() : 0;
                userSex = dp.getSex().length()>0 ? dp.getSex() : "empty";
                userWeight = dp.getWeight() > 0 ? dp.getWeight() : 0;
                userWeightUnit = dp.getWeightunit().length()>0 ? dp.getWeightunit() : "empty";
                userWeightUnitPos = dp.getWpos()!=0 ? dp.getWpos() : 0;
                userHight = dp.getHeight()>0 ? dp.getHeight() : 0;
                userHightInch = dp.getHeightinch()>0 ? dp.getHeightinch() : 0;
                userHeightUnit = dp.getHeightunit().length()>0 ? dp.getHeightunit() : "empty";
                userHeightUnitPos  = dp.getHpos()>0 ? dp.getHpos() : 0;
                mydb.insertUserBio(userAge,userSex,userWeight,userWeightUnit,userHight,userHightInch,
                        userHeightUnit,userWeightUnitPos,userHeightUnitPos);
            }
            */
/*
            SharedPreferences pref = getActivity().getPreferences(0);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            strDate = sdf.format(new Date());

            userAge = pref.getString(USER_AGE, "0");
            userSex = pref.getString(USER_SEX, "empty");

            userWeight = Double.parseDouble(pref.getString(USER_WEIGHT, "0"));
            userWeightUnit = pref.getString(USER_WEIGHT_UNIT, "empty");
            userWeightUnitPos = pref.getInt(USER_WEIGHT_UNIT_POS, 0);

            userHight = Double.parseDouble(pref.getString(USER_HEIGHT, "0"));
            userHightInch = Double.parseDouble(pref.getString(USER_HEIGHT_INCH, "0"));
            userHeightUnit = pref.getString(USER_HEIGHT_UNIT, "empty");
            userHeightUnitPos = pref.getInt(USER_HEIGHT_UNIT_POS, 0);
            //userStatus = 1;
*/

//            GlucHelper gh = new GlucHelper();
//
//            if(userWeightUnitPos==0 && userHeightUnitPos==0 ){
//                bmiResult = (float) gh.getBMIKg(userHight,userWeight);
//            }else if(userWeightUnitPos==1 && userHeightUnitPos==0 ) {
//                bmiResult = (float) gh.getBMIKg(userHight,gh.lbToKgConverter(userWeight));
//            }else if(userWeightUnitPos==0 && userHeightUnitPos==1 ) {
//                bmiResult = (float) gh.getBMIKg(gh.feetInchToCmConverter(userHight, userHightInch),userWeight);
//            }else if(userWeightUnitPos==1 && userHeightUnitPos==1 ) {
//                bmiResult = (float) gh.getBMILb(userHight, userHightInch, userWeight);
//            }

            //todo this might be where result is displayed--will need to change to gluc
            //result_bmi = Math.round(mydb.lastGluResult());
            result_bmi = 0;
          //  animateTextView(0,result_bmi,resultBmi);

//            mydb.insertUserBio(0,"empty",0,"empty",0,0,"empty",0,0);
            Toast.makeText(context, "Details Saved Successfully", Toast.LENGTH_SHORT).show();
/*
            //todo; change form bmirow--this is obsolete now
            if(mydb.insertBmiRow(userAge, userSex, userWeight,userWeightUnit, userHight, userHightInch, userHeightUnit, bmiResult,strDate, userStatus)) {

                context = getContext().getApplicationContext();
                Toast.makeText(context, "Details Saved Successfully", Toast.LENGTH_SHORT).show();
            }
*/
        }
    }

    public void animateTextView(int initialValue, int finalValue, final TextView textview) {
        DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator(0.8f);
        int start = Math.min(initialValue, finalValue);
        int end = Math.max(initialValue, finalValue);
        int difference = ( finalValue - initialValue); //Math.abs
        Handler handler = new Handler();
        for (int count = start; count <= end; count++) {
            int time = Math.round(decelerateInterpolator.getInterpolation((((float) count) / difference)) * 100) *  count;
            final int finalCount = ((initialValue > finalValue) ? initialValue - count : count);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    textview.setText(finalCount + "");
                }
            }, time);
        }
    }
}