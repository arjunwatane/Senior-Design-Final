package com.itobuz.android.easybmicalculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeTabFragment extends Fragment {

    private GaugeView mGaugeView;
    private TextView userInfo, infoResult, infoSuggest;
    private ImageView imageView;

    SharedPreferences pref;
    boolean removeAdIsChecked;
    private AdView mAdView;
    public static final String REMOVE_AD = "remove_ad";

    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPageNo;

    private float bmi_result;
    private String userAge;
    private double userWeight;
    private String userWeightUnit;
    private double userHeight;
    private int userHeightInch;
    private String userHeightUnit;

    DatabaseHelper mdb;
    ArrayAdapter<CharSequence> adapter;
    Context context;
    BmiHelper bh;

    public static HomeTabFragment newInstance(int pageNo) {

        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNo);
        HomeTabFragment fragment = new HomeTabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNo = getArguments().getInt(ARG_PAGE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        imageView = (ImageView) view.findViewById(R.id.expression);
        mGaugeView = (GaugeView) view.findViewById(R.id.gauge_view);
        userInfo = (TextView) view.findViewById(R.id.user_info);
        infoResult = (TextView) view.findViewById(R.id.info_result);
        infoSuggest = (TextView) view.findViewById(R.id.result_suggest);


        // Initialize and request AdMob ad.
//        mAdView = (AdView) view.findViewById(R.id.adView);

        return view;
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        bh = new BmiHelper();
        mdb = new DatabaseHelper(this.getContext());
        showUserDetails();
//        }

    }

    @Override
    public void onPause() {
//        if (mAdView != null) {
//            mAdView.pause();
//        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (mAdView != null) {
//            mAdView.resume();
//        }
    }

    @Override
    public void onDestroy() {
//        if (mAdView != null) {
//            mAdView.destroy();
//        }
        super.onDestroy();
    }

    /**
     * showUserDetails() method.
     * user details and gauge meter create.
     */

    //todo add userbio info retrival if needed
    private void showUserDetails(){
        //cursor initialize to fetch last records from db.
        //Cursor c = mdb.lastRecords();
        //c.moveToLast();

        //get user bio
        DataProvider dp = mdb.searchInfo();
        //check cursor not equals null
        if (dp != null) {
            //get value from database
            userAge = (String.valueOf(dp.getAge()));
            userWeight = dp.getWeight();
            userWeightUnit = dp.getWeightunit();
            userHeight = dp.getHeight();
            userHeightInch = (int)(dp.getHeightinch() + .0000001);
            userHeightUnit = dp.getHeightunit();

            bmi_result = mdb.lastGluResult();

            //set gauge meter value
            if(bmi_result > 50) mGaugeView.setTargetValue(49);
            else    mGaugeView.setTargetValue(bmi_result);

            // show result in text view
            infoResult.setText(""+bmi_result);

            if(bmi_result < 15) {
                infoResult.setTextColor(Color.rgb(0, 153, 232));
                imageView.setImageResource(R.drawable.ic_expressions_blue);
            } else if (bmi_result < 35){
                infoResult.setTextColor(Color.rgb(0, 174, 74));
                imageView.setImageResource(R.drawable.ic_expressions_green);
            } else if (bmi_result < 50) {
                infoResult.setTextColor(Color.rgb(224, 25, 43));
                imageView.setImageResource(R.drawable.ic_expressions_red);
            } else {
                infoResult.setTextColor(Color.rgb(224, 25, 43));
                imageView.setImageResource(R.drawable.ic_expressions_red);
            }

            infoSuggest.setText("Your glucose level is "+bh.getBMIClassification(bmi_result));

            //user information text setup
            if(userHeightUnit.equals("Cm")) {
                userInfo.setText(userAge+" Yr | "+userWeight+" "+userWeightUnit+" | "+userHeight+" "+userHeightUnit);
            }else{
                userInfo.setText(userAge+" Yr | "+userWeight+" "+userWeightUnit+" | "+(int) userHeight+"' "+userHeightInch+"\"");
            }
        }//end if
    } //end showDetails()
}