package com.itobuz.android.easybmicalculator;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static java.lang.Math.pow;
import org.apache.commons.math3.stat.regression.SimpleRegression;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsTabFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPageNo;

    ArrayAdapter<CharSequence> adapter;
    DatabaseHelper mdb;
    Context context;
    ViewPager viewPager;

    private AppCompatEditText editAgeText;
    private AppCompatEditText editWeightText;
    private Spinner editWeightunit;
    private AppCompatEditText editHeightText;
    private AppCompatEditText editHeightInchText;
    private Spinner editHeightunit;
    private RadioButton editMale;
    private RadioButton editFemale;
    private Button calculateBtn;
    private Button cancelBtn;
    private Switch removeAd;
    private TextView versionAbout;
//    private Button btn_bluetooth;

    private int result_bmi;
    private  int userId;
    private String userAge;
    private String userSex;
    private double userWeight;
    private String userWeightUnit;
    private int userWeightUnitPos;
    private double userHight;
    private double userHightInch;
    private String userHeightUnit;
    private int userHeightUnitPos;
    private String strDate;
    private int userStatus;
    private String classify;

    SharedPreferences pref;
    private static final String REMOVE_AD = "remove_ad";
    SharedPreferences.Editor edt;

    private float bmiResult, glucose_result, finalGLUCOSE;
    double finalOutputGlucose;

    /**Arjun**/
    private Button btn_bluetooth, btn_testsample, btn_sample_glucose, btn_filter_glucose, btn_train_polyfit, btn_train_PCA;
    private EditText editGlucoseText;
    private GlucoseFilter train = new GlucoseFilter();
    private PrincipleComponentAnalysis data_PCA = new PrincipleComponentAnalysis();

    private static final int NUM_SAMPLES = 10;
    private static final int SAMPLE_LENGTH = NUM_SAMPLES;
    private static final int PCA_COMPONENTS = 5;
    private static final double SKIN_THICKNESS = 1.5;
    private int readings = 0;
    private int size = 0;
    private double actual_glucose_values [] = new double[NUM_SAMPLES];
    private double glucose_database[][] = new double[NUM_SAMPLES][SAMPLE_LENGTH];

    private double skin_database[][] = new double[NUM_SAMPLES][SAMPLE_LENGTH];

    private double train_filtered_gluc[][];
    private double train_filtered_skin[][];
    private double absorptions[][];
    private double coefficients[], current_sample_gluc[],current_sample_skin[], test_filtered[], coefficientsBEER;

    private double skin_readings[][] = new double[NUM_SAMPLES][SAMPLE_LENGTH];
    int gluc_finish = 0;

    // Victor Code
    private static final String TAG = "BluetoothFragment";
    // private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter btAdapter = null;
    Set<BluetoothDevice> pairedDevices = null;
    ArrayList<Bluetooth> deviceList;
    ArrayList<String> deviceAddress;
    // ArrayAdapter<String> deviceList;
    ConnectThread connectThread;
    Handler mHandler;
    private Boolean btConnected = false;
    public Boolean ctAlive = false;
    public Boolean noninAlive = false;

    //Arjun
    public static float glucose_array[] = new float[SAMPLE_LENGTH];
    public static float skin_array[] = new float[SAMPLE_LENGTH];
    private SimpleRegression simpleRegression = new SimpleRegression(true);

    public int data_cnt = 0;
    private int test_flag = 0;
    private double test_glucose_value = 0;

    private static final UUID MY_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static final int MESSAGE_READ = 0;

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothLeScanner mLEScanner;

    private ScanCallback mScanCallback;
    private BluetoothAdapter.LeScanCallback mLeScanCallback;

    private ScanSettings settings;
    private List<ScanFilter> filters;
    private int REQUEST_ENABLE_BT = 1;

    // UUID
    private static final UUID MEASUREMENT_SERVICE_UUID = UUID
            .fromString("46a970e0-0d5f-11e2-8b5e-0002a5d5c51b");
    private static final UUID MEASUREMENT_CHARACTERISTIC_UUID = UUID
            .fromString("0aad7ea0-0d60-11e2-8e3c-0002a5d5c51b");
    private static final UUID CLIENT_CHARACTERISTIC_CONFIG = UUID
            .fromString("00002902-0000-1000-8000-00805f9b34fb");

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    private static final int REQUEST_LOCATION_PERMISSION = 2;
    private int mConnectionState = STATE_DISCONNECTED;
    // UI
    private TextView mFieldDeviceName, mFieldConnectionStatus, mFieldSpo2,
            mFieldPR;
    private String kDeviceName, kSpo2, kPulseRate;
    private Button mDisconnectButton;

    public static SettingsTabFragment newInstance(int pageNo) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNo);
        SettingsTabFragment settingsFragment = new SettingsTabFragment();
        settingsFragment.setArguments(args);
        return settingsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNo = getArguments().getInt(ARG_PAGE);

        deviceList =  new ArrayList<>();
        deviceAddress = new ArrayList<>();
        // deviceList = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, devices);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        viewPager = (ViewPager) getActivity().findViewById(R.id.view_pager);

        editAgeText = (AppCompatEditText) view.findViewById(R.id.editAge);
        editWeightText =  (AppCompatEditText) view.findViewById(R.id.editWidth);
        editWeightunit =  (Spinner) view.findViewById(R.id.editWeight_unit);
        editHeightText = (AppCompatEditText) view.findViewById(R.id.editHeight);
        editHeightInchText = (AppCompatEditText) view.findViewById(R.id.editHeightInch);
        editHeightunit =  (Spinner) view.findViewById(R.id.editHeight_unit);

        editMale = (RadioButton) view.findViewById(R.id.editRadioMale);
        editFemale = (RadioButton) view.findViewById(R.id.editRadioFemale);
        versionAbout = (TextView) view.findViewById(R.id.about_text2);
        calculateBtn = (Button) view.findViewById(R.id.editCalculate);
        btn_testsample = (Button) view.findViewById(R.id.test_sample);

        /** Arjun **/
        btn_sample_glucose = (Button) view.findViewById(R.id.sample_glucose_btn);
        btn_filter_glucose = (Button) view.findViewById(R.id.filter_btn);
//        btn_train_polyfit = (Button) view.findViewById(R.id.train_polyfit);
//        btn_train_PCA = (Button) view.findViewById(R.id.train_PCA);
        editGlucoseText = (AppCompatEditText) view.findViewById(R.id.editGlucose);
        btn_bluetooth = (Button) view.findViewById(R.id.bluetooth_btn);

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                if (msg.what == MESSAGE_READ){
                    if(gluc_finish == 0){
                        if(data_cnt < SAMPLE_LENGTH){
                            String message = msg.obj.toString();
                            float readMessage = Float.valueOf(message);
                            System.out.println(message);
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            glucose_array[data_cnt] = readMessage;
                            data_cnt = data_cnt+1;

                        }
                        if (data_cnt == SAMPLE_LENGTH)
                        {
                            Toast.makeText(getActivity(), "Got enough samples.", Toast.LENGTH_SHORT).show();
                            gluc_finish = 1;
                            //data_cnt = 0;

                        }
                    }
                    else if(gluc_finish == 1)
                    {
                        Toast.makeText(getActivity(), "Already have enough calibration data.", Toast.LENGTH_SHORT).show();

                        /*if(data_cnt < SAMPLE_LENGTH)
                        {


                            String message = msg.obj.toString();
                            float readMessage = Float.valueOf(message);
                            System.out.println(readMessage);
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            skin_array[data_cnt] = readMessage;
                            data_cnt = data_cnt + 1;
                            if (data_cnt == SAMPLE_LENGTH){
                                gluc_finish = 0;
                                data_cnt = 0;
                            }
                        }*/

                    }
                    if(test_flag == 1)
                    {
                        String message = msg.obj.toString();
                        float readMessage = Float.valueOf(message);
                        test_glucose_value =  (double) readMessage;
                        System.out.println(message);
                        test_flag = 0;

                        test_glucose_value = 50.0;

                    }
                }
            }
        };

        btn_sample_glucose.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                data_cnt = 0;
                try{
                    sample(editGlucoseText.getText().toString());

                }
                catch (Exception e){
                    Toast.makeText(getActivity(), "Please enter a valid number."
                            , Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_filter_glucose.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                train();
            }
        });

        btn_bluetooth.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                disconnectBluetooth();
                connectBluetooth();
            }
        });

        mdb = new DatabaseHelper(this.getContext());
//        userId= mdb.lastBmiId();
        //showUserValue();
        //set version in about section
        versionAbout.setText("Build Version: "+ BuildConfig.VERSION_NAME);

        // if select height spinner inch field setup
        settingsHeightSpinners();

        editAgeText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().length()==0){
                    calculateBtn.setEnabled(false);
                } else {
                    calculateBtn.setEnabled(true);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

        editWeightText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().trim().length()==0){
                    calculateBtn.setEnabled(false);
                } else {
                    calculateBtn.setEnabled(true);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

        editHeightText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().length()==0){
                    calculateBtn.setEnabled(false);
                } else {
                    calculateBtn.setEnabled(true);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

        //Calculate button click action
        calculateBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                int testing_DEMO = 0;

                if((actual_glucose_values.length - size)<=0 && testing_DEMO == 0){
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    strDate = sdf.format(new Date());

                    userAge = editAgeText.getText().toString();
                    userSex = (editMale.isChecked()) ? "Male" : (editFemale.isChecked()) ? "Female" : "";

                    try{
                        userWeight = Double.valueOf(editWeightText.getText().toString());
                    } catch (NumberFormatException e){
                        userWeight = 0;
                    }

                    userWeightUnit = editWeightunit.getSelectedItem().toString();
                    userWeightUnitPos = editWeightunit.getSelectedItemPosition();

                    try{
                        userHight = Double.valueOf(editHeightText.getText().toString());
                    } catch (NumberFormatException e){
                        userHight = 0;
                    }

                    try{
                        userHightInch = Double.valueOf(editHeightInchText.getText().toString());
                    } catch (NumberFormatException e){
                        userHightInch = 0;
                    }

                    userHeightUnit = editHeightunit.getSelectedItem().toString();
                    userHeightUnitPos = editHeightunit.getSelectedItemPosition();
                    userStatus = 1;

//                    BmiHelper bh = new BmiHelper();
                    GlucHelper gh = new GlucHelper();

//                if(userWeightUnitPos==0 && userHeightUnitPos==0 ){
//                    bmiResult = (float) bh.getBMIKg(userHight,userWeight);
//                }else if(userWeightUnitPos==1 && userHeightUnitPos==0 ) {
//                    bmiResult = (float) bh.getBMIKg(userHight,bh.lbToKgConverter(userWeight));
//                }else if(userWeightUnitPos==0 && userHeightUnitPos==1 ) {
//                    bmiResult = (float) bh.getBMIKg(bh.feetInchToCmConverter(userHight, userHightInch),userWeight);
//                }else if(userWeightUnitPos==1 && userHeightUnitPos==1 ) {
//                    bmiResult = (float) bh.getBMILb(userHight, userHightInch, userWeight);
//                }

//                    filterTesting();
//                    finalGLUCOSE = testPolyFit();

//                    finalGLUCOSE = testBeerLambert();
//                    glucose_result = test_PCA();
//                    bmiResult = 55.0f;
//                    glucose_result = 55.0f;
//                    finalGLUCOSE = 95.0f;
//                    System.out.println("Prediction for 1.5 = " + simpleRegression.predict(1.5));

                    glucose_result = (float)simpleRegression.predict(test_glucose_value);
                    System.out.println("GLUCOSE RESULT = " + glucose_result);
//                    glucose_result = 100.0f;
                    if(mdb.updateBioAll(Integer.parseInt(userAge),userSex,userWeight,userWeightUnit,
                                userHight,userHightInch,userHeightUnit,0,0) && mdb.insertGlucLog
                                (Hold.getName(),Hold.getId(),(int)glucose_result,gh
                                .getGlucClassification(glucose_result))){
                        context = getContext().getApplicationContext();

                        classify = gh.getGlucClassification(glucose_result);
                        Toast.makeText(context, "Your glucose is " + classify, Toast.LENGTH_SHORT).show();

                        viewPager.setCurrentItem(0);
                    }
                }
                else if(testing_DEMO == 1){
//                    finalGLUCOSE = glucose_array[1]*100;
//                    data_cnt = 0;
                    float v1 = glucose_array[0];
                    float v2 = glucose_array[1];
                    System.out.println(v1 + " " + v2);
//                    Toast.makeText(getActivity(), Float.toString(v1*100), Toast.LENGTH_SHORT).show();
//                    Toast.makeText(getActivity(), Float.toString(v2*100), Toast.LENGTH_SHORT).show();

                    if(v1 > v2)
                        finalGLUCOSE = 2;
                    else if (v2 > v1)
                        finalGLUCOSE = 1;
                    else
                        finalGLUCOSE = 0;

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    strDate = sdf.format(new Date());

                    userAge = editAgeText.getText().toString();
                    userSex = (editMale.isChecked()) ? "Male" : (editFemale.isChecked()) ? "Female" : "";

                    try{
                        userWeight = Double.valueOf(editWeightText.getText().toString());
                    } catch (NumberFormatException e){
                        userWeight = 0;
                    }

                    userWeightUnit = editWeightunit.getSelectedItem().toString();
                    userWeightUnitPos = editWeightunit.getSelectedItemPosition();

                    try{
                        userHight = Double.valueOf(editHeightText.getText().toString());
                    } catch (NumberFormatException e){
                        userHight = 0;
                    }

                    try{
                        userHightInch = Double.valueOf(editHeightInchText.getText().toString());
                    } catch (NumberFormatException e){
                        userHightInch = 0;
                    }

                    userHeightUnit = editHeightunit.getSelectedItem().toString();
                    userHeightUnitPos = editHeightunit.getSelectedItemPosition();
                    userStatus = 1;

                    GlucHelper gh = new GlucHelper();
                    if(mdb.updateBioAll(Integer.parseInt(userAge),userSex,userWeight,userWeightUnit,
                            userHight,userHightInch,userHeightUnit,0,0) || mdb.insertGlucLog(Hold.getName(),Hold.getId(),(int)glucose_result,gh.getGlucClassification(glucose_result))){

                        context = getContext().getApplicationContext();

                        classify = gh.getGlucClassification(glucose_result);
                        Toast.makeText(context, "Your glucose is " + classify, Toast.LENGTH_SHORT).show();

                        viewPager.setCurrentItem(0);
                    }
                }
                else{
                    Toast.makeText(getActivity(), "Add more samples", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Test Sample button onclick action
        btn_testsample.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                int testing_DEMO = 0;
                test_flag = 0;
                if(testing_DEMO == 1)
                {

                    String a = "A";
                    if(connectThread != null)
                        connectThread.connectedThread.write(a.getBytes());
//                    if(readings == 0)
//                    {
//                        Toast.makeText(getActivity(), Float.toString(glucose_array[readings]*100), Toast.LENGTH_SHORT).show();
//                        readings++;
//                    }
//                    else
//                        Toast.makeText(getActivity(), Float.toString(glucose_array[readings]*100), Toast.LENGTH_SHORT).show();

                }else
                {
                    test_flag = 1;
                    String a = "A";
                    if(connectThread != null)
                        connectThread.connectedThread.write(a.getBytes());

//                    data_cnt = 0;
//                    if ((actual_glucose_values.length - size) <= 0)
//                    {
//                        float data_gluc[] = glucose_array;
//                        double spec_double_gluc[] = new double[data_gluc.length];
//                        for (int i = 0; i < spec_double_gluc.length; i++)
//                        {
//                            spec_double_gluc[i] = (double) data_gluc[i];
//                        }
//
//                        float data_skin[] = skin_array;
//                        double spec_double_skin[] = new double[data_skin.length];
//                        for (int i = 0; i < spec_double_skin.length; i++)
//                        {
//                            spec_double_skin[i] = (double) data_skin[i];
//                        }
//
//                        current_sample_gluc = spec_double_gluc;
//                        current_sample_skin = spec_double_skin;
//
//                    } else
//                        Toast.makeText(getActivity(), "Add more samples", Toast.LENGTH_SHORT).show();


                }
            }
        });

        return view;
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        showUserValue();
    }

    //todo remove cursor or change to use different records
    //just need to find the user's bio stats
    private void showUserValue(){
        //Cursor c = mdb.lastRecords();
        DataProvider c = mdb.searchInfo();
        //c.moveToLast();
        if (c != null){
            System.out.println("Showing user info");
            editAgeText.setText(Integer.toString(c.getAge()));
            editWeightText.setText(Double.toString(c.getWeight()));
            editHeightText.setText(Double.toString(c.getHeight()));

            //set weight spinner position
            adapter = ArrayAdapter.createFromResource(this.getContext(), R.array.weightArray, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            editWeightunit.setAdapter(adapter);
            int weightSpinnerPos = adapter.getPosition(c.getWeightunit());
            editWeightunit.setSelection(weightSpinnerPos);

            // set height spinner position;
            adapter = ArrayAdapter.createFromResource(this.getContext(), R.array.heightArray, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            editHeightunit.setAdapter(adapter);
            int heightSpinnerPos = adapter.getPosition(c.getHeightunit());
            editHeightunit.setSelection(heightSpinnerPos);

            if(c.getHeightunit().equals("Cm")){
                //do something
                editHeightInchText.setVisibility(View.GONE);
            }else{
                editHeightInchText.setText(Double.toString(c.getHeightinch()));
                editHeightInchText.setVisibility(View.VISIBLE);
            }

            String sex = (c.getSex());

            if(sex.equals("Male")){
                editMale.setChecked(true);
            }else if(sex.equals("Female")) {
                editFemale.setChecked(true);
            }
        }
        else System.out.println("Failure of the system show");
    }

    public void settingsHeightSpinners(){
        editHeightunit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView parent, View view, int position, long id) {
                //height spinner CM is selected I would like to hide the second height edittext field
                if (position == 0){
                    editHeightInchText.setVisibility(View.GONE);

                } else {
                    editHeightInchText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do Something.
            }
        });
    }

    /** GOLD FUNCTIONS **/
    private void disconnectBluetooth(){
        btConnected = false;
        if(ctAlive)
            connectThread.connectedThread.cancel();
        if(connectThread != null)
            connectThread.cancel();
    }

    private void connectBluetooth(){
        int msptracker = 0;
        int nonintracker = 1;
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(btAdapter == null){
            Toast.makeText(getActivity(), "Bluetooth is not supported on this device.", Toast.LENGTH_SHORT).show();
        }
        else{
            if(!btAdapter.isEnabled()){
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
//                btnCT.setEnabled(false);
            }
            else{
                pairedDevices = btAdapter.getBondedDevices();
                if(pairedDevices.size() > 0){
                    for(BluetoothDevice device : pairedDevices){
                        Bluetooth bt = new Bluetooth(device.getName(), device.getAddress());
                        deviceList.add(bt);
                        // deviceAddress.add(device.getAddress());
                        for(int i = 0; i < deviceList.size(); i++){
                            if(deviceList.get(i).getName().startsWith("HC")){
                                msptracker = i;
                                Toast.makeText(getActivity(), "Bluetooth is connected.", Toast.LENGTH_SHORT).show();
                            }
                            if(deviceList.get(i).getName().startsWith("Nonin")){
                                nonintracker = i;
                            }
                        }

//                        tvDev.setText((deviceList.get(msptracker).getName()));
//                        tvDev.refreshDrawableState();
                        // deviceList.notifyDataSetChanged();
                        // lvDeviceList.setAdapter(deviceList);
                    }
                }
                // need to move this inside the if loop
                BluetoothDevice remoteDevice = btAdapter.getRemoteDevice(deviceList.get(msptracker).getAddress());
                connectThread = new ConnectThread(remoteDevice);
                connectThread.start();

                BluetoothDevice noninDevice = btAdapter.getRemoteDevice(deviceList.get(nonintracker).getAddress());
                initiateNonin(noninDevice);
//                btnDC.setEnabled(true);
//                btnSA.setEnabled(true);
//                btnCT.setEnabled(false);
                btConnected = true;
                Toast.makeText(getActivity(), "Bluetooth is now enabled.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        int msptracker = 0;
        if(requestCode == REQUEST_ENABLE_BT){
            if(resultCode == RESULT_OK){
                pairedDevices = btAdapter.getBondedDevices();
                if(pairedDevices.size() > 0){
                    for(BluetoothDevice device : pairedDevices){
                        Bluetooth bt = new Bluetooth(device.getName(), device.getAddress());
                        deviceList.add(bt);
                        // deviceAddress.add(device.getAddress());
                        for(int i = 0; i < deviceList.size(); i++){
                            if(deviceList.get(i).getName().startsWith("HC-06")){
                                msptracker = i;
                            }
                        }
//                        tvDev.setText((deviceList.get(msptracker).getName()));
//                        tvDev.refreshDrawableState();
                        // deviceList.notifyDataSetChanged();
                        // lvDeviceList.setAdapter(deviceList);
                    }

                    BluetoothDevice remoteDevice = btAdapter.getRemoteDevice(deviceList.get(msptracker).getAddress());
                    connectThread = new ConnectThread(remoteDevice);
                    connectThread.start();
                    // need to introduce some delay for BT to connect here
//                    btnDC.setEnabled(true);
//                    btnSA.setEnabled(true);
//                    btnCT.setEnabled(false);
                    btConnected = true;
                    Toast.makeText(getActivity(), "Bluetooth is now enabled.", Toast.LENGTH_SHORT).show();
                }
            }
            if(resultCode == RESULT_CANCELED){
                Toast.makeText(getActivity(), "Bluetooth needs to be enabled to continue.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        ConnectedThread connectedThread;

        public ConnectThread(BluetoothDevice device) {
            currentThread().setName("ConnectedThread");
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) { }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            btAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    ctAlive = false;
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }

            // Do work to manage the connection (in a separate thread)
            connectedThread = new ConnectedThread(mmSocket);
            connectedThread.start();
            ctAlive = true;
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    public class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public synchronized void run() {
            currentThread().setName("ConnectedThread");
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            int availableBytes = 0;
            ByteBuffer bb = ByteBuffer.allocate(1024);

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    availableBytes = mmInStream.available();
                    if(availableBytes > 0){
                        buffer = new byte[availableBytes];
//                        Log.d("Before buffer read;", new String(buffer));
                        bytes = mmInStream.read(buffer);
//                        Log.d("After buffer read;", new String(buffer));
                        // Wrap the byte array as a float number ordering the bytes using little endian.
                        bb.position(0);
                        float test = bb.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                        String s = Float.toString(test);

                        // Send the obtained bytes to the UI activity
                        if(test > 0.001){
                            mHandler.obtainMessage(MESSAGE_READ, bytes, -1, s).sendToTarget();
                        }
                    }
                    else{
                        Thread.sleep(100);
                    }
                } catch (IOException e) {
                    break;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    ctAlive = false;
                }
                catch (BufferUnderflowException e) {
                    bb.position(0);
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    public void initiateNonin(BluetoothDevice bt){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int locationPermission = ContextCompat.checkSelfPermission(getActivity(),
                    android.Manifest.permission.ACCESS_COARSE_LOCATION);
            if (locationPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_LOCATION_PERMISSION);
            }
        }

        if (btAdapter == null || !btAdapter.isEnabled()) {
            //Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            if (Build.VERSION.SDK_INT >= 21) {
                mLEScanner = btAdapter.getBluetoothLeScanner();

                // startscan() settings defaults to SCAN_MODE_LOW_POWER
                // To decrease scanning time, set settings to SCAN_MODE_LOW_LATENCY
                settings = new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .build();
                filters = new ArrayList<ScanFilter>();
            }
            scanLeDevice(true, bt);
            //  pairedDevices = btAdapter.getBondedDevices();
        }
    }

    private void scanLeDevice(final boolean enable, BluetoothDevice bt) {
        if (enable) {
            Log.i("TAG","Scan started");
            if (Build.VERSION.SDK_INT < 21) {
                //  initLeScanCallBack();
                btAdapter.startLeScan(mLeScanCallback);
            } else {
                initScanCallback(bt);
//                mLEScanner.startScan(filters, settings, mScanCallback);
            }
        } else {
            if (Build.VERSION.SDK_INT < 21) {
                //    btAdapter.stopLeScan(mLeScanCallback);
            } else {
                //       mLEScanner.stopScan(mScanCallback);
            }
        }
    }

    /**
     * @desc This method initializes ScanCallback use for devices running API 21 and higher
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void initScanCallback(BluetoothDevice btDevice) {    // the onScanResult is not returning anything, and this is the reason why the nonin is not connecting
//        mScanCallback = new ScanCallback() {
//            @Override
//            public void onScanResult(int callbackType, ScanResult result) {
//                Log.i("callbackType", String.valueOf(callbackType));
//                Log.i("result", result.toString());
//
//                if (result.getDevice().getName() != null) {
//                    BluetoothDevice btDevice = result.getDevice();
//
//                    // Check if the device name has prefix 'Nonin' and it's already in
//                    // the list
//                    if (btDevice.getName().startsWith("Nonin")) {
//                        Log.i("Tag", "Device Name: " + btDevice.getName());
//                        // stop scanning
//                        scanLeDevice(false);
//                        // get the device name
//                        kDeviceName = btDevice.getName();
//                        // connect to the device
//                        connect(btDevice.getAddress());
//                    }
//                }
//            }
//        };

        scanLeDevice(false, btDevice);
        // get the device name
        kDeviceName = btDevice.getName();
        // connect to the device
        connect(btDevice.getAddress());
    }

    /**
     * @desc This method initializes LeScanCallback use for devices running API 18-20
     */
//    public void initLeScanCallBack() {
//        mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
//            @Override
//            public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
//                // Check if the device name has prefix 'Nonin' and it's already in
//                // the list
//                if (device.getName().startsWith("Nonin")) {
//                    Log.i("Tag", "Device Name: " + device.getName());
//                    // stop scanning
//                    scanLeDevice(false);
//                    // get the device name
//                    kDeviceName = device.getName();
//                    // connect to the device
//                    connect(device.getAddress());
//                }
//            }
//        };
//    }

    /**
     * @desc This method tries to connect to the selected the device
     * @param address - Bluetooth LE device address
     */
    public boolean connect(final String address) {
        if (btAdapter == null || address == null) {
            Log.i("Tag",
                    "BluetoothAdapter not initialized or unspecified address");
            return false;
        }

        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        if (device == null) {
            Log.i("Tag", "Device not found. Unable to connect");
            return false;
        }

        // Connect to the device
        connectGatt(device);
        Log.i("Tag", "Connecting to " + device.getName());
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    /**
     * @desc connects to the GATT server hosted by this device
     * @param device - the about to connect Bluetooth device
     */
    private void connectGatt(final BluetoothDevice device) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBluetoothGatt = device.connectGatt(getActivity().getApplication(), false,
                        mGattCallback);
            }
        });
    }

    /**
     * @desc Implements callback methods for GATT events that the app cares
     *       about. For example, connection change and services discovered.
     */
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        /*
         * @desc Callback invoked when the list of remote services, charcters
         * and descriptors for the remote device have been updated, i.e. new
         * service have been discovered
         * @param gatt - Gat client invoked discoverServices()
         * @param status - GATT_SUCCESS if the remote device has been explored
         * successfully
         */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.i("Tag", "Inside service dicovered");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i("Tag", "read device for service and characteristics");
                noninAlive = true;
                readDevice(gatt);
            } else {
                noninAlive = false;
                Log.i("Tag", "onServicesDiscovered not sucess: " + status);
            }
        }

        /**
         * @desc Callback triggered as a result of a remote characteristic
         *       notification Also, the received data are parsed here into measurements
         * @param gatt - GATT client the characteristic is associated with
         * @param characteristic
         *            Characteristic that has been updated as a result of remote notification event
         */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                BluetoothGattCharacteristic characteristic) {
            Log.i("Tag", "Measurements recieved!");

            // Indicates the current device status
            final int status = characteristic.getIntValue(
                    BluetoothGattCharacteristic.FORMAT_UINT8, 1);
            // Indicates that the display is synchronized to the SpO2 and pulse
            // rate values contained in this packet
            final int kSyncIndication = (status & 0x1);
            // Average amplitude indicates low or marginal signal quality
            final int kWeakSignal = (status & 0x2) >> 1;
            // Used to indicate that the data successfully passed the SmartPoint
            // Algorithm
            final int kSmartPoint = (status & 0x4) >> 2;
            // An absence of consecutive good pulse signal
            final int kSearching = (status & 0x8) >> 3;
            // CorrectCheck technology indicates that the finger is placed
            // correctly in the oximeter
            final int kCorrectCheck = (status & 0x10) >> 4;
            // Low or critical battery is indicated on the device
            final int kLowBattery = (status & 0x20) >> 5;
            // indicates whether Bluetooth connection is encrypted
            final int kEncryption = (status & 0x40) >> 6;

            // Voltage level of the batteries in use in .1 volt increments
            // [decivolts]
            final int decivoltage = characteristic.getIntValue(
                    BluetoothGattCharacteristic.FORMAT_UINT8, 2);
            // Value that indicates the relative strength of the pulsatile
            // signal. Units 0.01% (hundreds of a precent)
            final int paiValue = (characteristic.getIntValue(
                    BluetoothGattCharacteristic.FORMAT_UINT8, 3) * 256 + characteristic
                    .getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 4));
            // Value that indicates that number of seconds since the device went
            // into run mod (between 0-65535)
            final int secondCnt = (characteristic.getIntValue(
                    BluetoothGattCharacteristic.FORMAT_UINT8, 5) * 256 + characteristic
                    .getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 6));
            // SpO2 percentage 0-100 (127 indicates missing)
            final int spo2 = characteristic.getIntValue(
                    BluetoothGattCharacteristic.FORMAT_UINT8, 7);
            // Pulse Rate in beats per minute, 0-325. (511 indicates missing)
            final int pulseRate = (characteristic.getIntValue(
                    BluetoothGattCharacteristic.FORMAT_UINT8, 8) * 256 + characteristic
                    .getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 9));

            // A value of 127 indicates no data for SpO2
            if (spo2 == 127) {
                kSpo2 = "--";
            } else {
                kSpo2 = String.format(" %d", spo2);
            }
            // A value of 511 indicates no data for pulse
            if (pulseRate == 511) {
                kPulseRate = "--";
            } else {
                kPulseRate = String.format(" %d", pulseRate);
            }
            // Display the measurement(Only Spo2 and PulseRate is shown in the
            // UI for this demo)
            updateUI();
        }

        /**
         * @desc Callback indicating when GATT client has connected/disconnected
         *       to/from a remote GATT server
         * @param gatt - GATT client
         * @param status - status of the connect or disconnect operation
         * @param newState - returns the new connection state
         */
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.i("Tag", "ConnectionStateChanged");
            // connected to a GATT server
            if (status == BluetoothGatt.GATT_SUCCESS
                    && newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i("Tag", "Attempting to start service discovery:"
                        + mBluetoothGatt.discoverServices());
                updateUIConnectionStatus("Connected!");
                mConnectionState = STATE_CONNECTED;
                // disconnected from a GATT server
            } else if (status == BluetoothGatt.GATT_SUCCESS
                    && newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i("Tag", "Disconnected from GATT server.");
                if (mConnectionState != STATE_DISCONNECTED) {
                    disconnect();
                }

                // GATT failure
            } else if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.i("Tag", "GATT Failure");
                noninAlive = false;
                if (mConnectionState != STATE_DISCONNECTED) {
                    disconnect();
                }
            }
        }

        /**
         * @desc reads the specified characteristic of the service and enables
         *       notification
         * @param gatt - GATT client
         */
        public void readDevice(BluetoothGatt gatt) {
            BluetoothGattCharacteristic characteristic;

            characteristic = mBluetoothGatt
                    .getService(MEASUREMENT_SERVICE_UUID).getCharacteristic(
                            MEASUREMENT_CHARACTERISTIC_UUID);

            mBluetoothGatt.readCharacteristic(characteristic);
			/*
			 * Once the notification are enabled for a characteristics,
			 * onCharacteristicsChnaged() callback is triggered if the
			 * characteristic changes on the remote device.
			 */
            mBluetoothGatt.setCharacteristicNotification(characteristic, true);

            BluetoothGattDescriptor desc = characteristic
                    .getDescriptor(CLIENT_CHARACTERISTIC_CONFIG);
            desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(desc);

            Log.i("Tag", "Success while reading");
        }
    };

    /**
     * @desc update the UI with the measurements
     */
    private void updateUI() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // display device name
//                tvNoninName.setText(kDeviceName);
                // display Spo2 value
//                tvSpo2.setText(kSpo2);
                // display Pulse Rate value
//                tvPR.setText(kPulseRate);
            }
        });
    }

    /**
     * @desc update the UI with the measurements
     * @param status - represents the status of the current connection, "Connected" or "Scanning"
     */
    private void updateUIConnectionStatus(final String status) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // update the status field
//                tvConnectionStatus.setText(status);
                // connected
                if (status == "Connected!") {
                    // show disconnect button
                    //    mDisconnectButton.setVisibility(View.VISIBLE);
                    // not connected
                } else {
                    // hide the disconnect button
                    //  mDisconnectButton.setVisibility(View.GONE);
                    // empty the UI field
//                    tvNoninName.setText("");
//                    tvSpo2.setText("");
//                    tvPR.setText("");
                    // Rescan for Bluetooth LE Devices
                    // scanLeDevice(true);
                }
            }
        });
    }

    /**
     * @desc Disconnects an existing connection or cancel a pending connection.
     *       The disconnection result is reported asynchronously through the
     *       {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *       callback.
     */
    public void disconnect() {
        mConnectionState = STATE_DISCONNECTED;
        // disconnect
        mBluetoothGatt.disconnect();
        close();
        // reScan
        updateUIConnectionStatus("Scanning...");
    }

    public void closeNonin(){ close(); }

    /**
     * @desc After using a given BLE device, the app must call this method to
     *       ensure resources are released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /** Get one sample data (skin and glucose) from the hardware module, and add it to the database **/
    public void sample(String known_glucose_string){

        if(actual_glucose_values.length-size != 0)
        {
            String a = "A";
            if(connectThread != null)
                connectThread.connectedThread.write(a.getBytes());

            double known_glucose = Double.parseDouble(known_glucose_string);

            glucose_array[size] = 50.0f;
            actual_glucose_values[size] = known_glucose;
//            glucose_database[size] = gluc_double;

            //skin
//            float data_skin[] = skin_array;
//            double skin_double[] = new double[data_skin.length];
//            for (int i = 0; i < skin_double.length; i++) {
//                skin_double[i] = (double) data_skin[i];
//            }
//            skin_database[size] = skin_double;

            size++;
            System.out.println(size);
            Toast.makeText(getActivity(), "Samples left: " + String.valueOf(actual_glucose_values.length - size), Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(getActivity(), "Database is full. Cannot add more samples.", Toast.LENGTH_SHORT).show();
    }

    //train glucose and skin
    public void train() {
        readings = 0;
        int testing_DEMO = 0;
        if (testing_DEMO == 1) {
            data_cnt = 0;
//            System.out.println(glucose_array);
            glucose_array = new float[SAMPLE_LENGTH];
//            System.out.println(glucose_array);
        } else {
                if (actual_glucose_values.length - size == 0) {
    //            filter();
                    train_polyfit();
    //            train_PCA();

    //            trainBeerLambert();
                    Toast.makeText(getActivity(), "Databases are filtered and trained.", Toast.LENGTH_SHORT).show();
                    size++;

                }
                else if(actual_glucose_values.length - size < 0){
                    data_cnt = 0;
                    glucose_array = new float[SAMPLE_LENGTH];
                    size = 0;
                    gluc_finish = 0;
                    Toast.makeText(getActivity(), "Databases are emptied.", Toast.LENGTH_SHORT).show();
                }
            }
        }


    /** TRAIN DATA and return derived + smoothed database **/
    public void filter() {
        train_filtered_gluc = glucose_database;

        //1st derivative
        train_filtered_gluc = train.firstDerivative(train_filtered_gluc);

        //smooth data
        train_filtered_gluc = train.smooth(train_filtered_gluc);

        //smooth skin, not taking derivative
        train_filtered_skin = train.smooth(skin_database);
//            for(int i = 0; i < train_filtered_gluc.length; i ++)
//                System.out.println(actual_glucose_values[i] + " " + Arrays.toString(train_filtered_gluc[i]));
    }

    /** Create polynomial fitting of training data **/
    public void train_polyfit(){
//        coefficients = train.polynomialFit(train_filtered_gluc, actual_glucose_values);




        double double_glucose[] = new double[glucose_array.length];
        for (int i = 0; i < double_glucose.length; i++)
        {
            double_glucose[i] = (double) glucose_array[i];
        }

//        simpleRegression.addData(new double[][] { actual_glucose_values, double_glucose});
//        simpleRegression.addData(new double[][] { double_glucose,  actual_glucose_values});
//        double addingData[][] = new double[][]{actual_glucose_values, double_glucose};
//        simpleRegression.addData(addingData);
        for (int i = 0; i < actual_glucose_values.length; i ++)
        {
            simpleRegression.addData(actual_glucose_values[i], double_glucose[i]);
        }

        System.out.println("slope = " + simpleRegression.getSlope());
        System.out.println("intercept = " + simpleRegression.getIntercept());

    }

    /** Principle Component Analysis of training data **/
    public void train_PCA(){
        data_PCA.setup(train_filtered_gluc.length, train_filtered_gluc[0].length);

        for (int i = 0; i < train_filtered_gluc.length; i++)
            data_PCA.addSample(train_filtered_gluc[i]);

        data_PCA.computeBasis(PCA_COMPONENTS);
    }

    /** Test data against Training Data **/
    public void filterTesting(){
        if((actual_glucose_values.length - size)<=0){
//            float data[] = glucose_array;
////            float data[] = {1,2,3,4,5,6,7,8,9,10};
//            double gluc_double[] = new double[data.length];
//            for(int i=0; i<gluc_double.length; i++) {
//                gluc_double[i] = (double)data[i];
//            }

            GlucoseFilter test = new GlucoseFilter();

            test_filtered = test.firstDerivative(current_sample_gluc);
            test_filtered = test.smooth(test_filtered);
        }
        else
            Toast.makeText(getActivity(), "Add more samples", Toast.LENGTH_SHORT).show();
    }

    public float testPolyFit(){
        if((actual_glucose_values.length - size)<=0){
            double sum = 0, average, exp;
            float result=0;

            for(int i=0; i < test_filtered.length; i++) {
                sum += test_filtered[i];
            }
            average = sum / test_filtered.length;
//            average = 2;
            exp = coefficients.length;
            //for(int i = coefficients.length-1; i >= 0; i--){
            for(int i = 0; i < coefficients.length; i ++){
                exp --;
                result += coefficients[i] * pow(average,exp);
//            System.out.println(String.valueOf(result));
                //System.out.println(coefficients[i]);
            }

            System.out.println(String.valueOf(result));
            return result;
        }
        else{
            Toast.makeText(getActivity(), "Add more samples", Toast.LENGTH_SHORT).show();
            return 0.0f;
        }
    }

    /** Test data by checking PCA membership **/
    public float test_PCA(){
        if((actual_glucose_values.length - size)<=0)
        { //data_PCA.errorMembership(test_filtered);
            //data_PCA.sampleToEigenSpace(test_filtered);
            double x = data_PCA.response(test_filtered);
//            outputGlucose.setText(String.valueOf(x));
            return (float)x;
        }
        else{
            Toast.makeText(getActivity(), "Add more samples", Toast.LENGTH_SHORT).show();
            return 0.0f;
        }
    }

    public void trainBeerLambert(){
        absorptions = train_filtered_skin;

        for(int i = 0; i < absorptions.length; i ++)
            for(int j = 0; j < absorptions[i].length; j ++)
                absorptions[i][j] = absorptions[i][j]/SKIN_THICKNESS;

        for(int i = 0; i < absorptions.length; i ++)
            for(int j = 0; j < absorptions[i].length; j ++)
                absorptions[i][j] = absorptions[i][j]/actual_glucose_values[i];

        coefficientsBEER = train.polynomialFitSkin(absorptions, actual_glucose_values);
    }

    public float testBeerLambert(){
        double sum = 0, averageReading;
        for (int i = 0; i < current_sample_gluc.length; i ++ )
            sum += current_sample_gluc[i];
        averageReading = sum/current_sample_gluc.length;

        finalOutputGlucose =  averageReading/(coefficientsBEER* glucose_result);
        return (float)finalOutputGlucose;
    }
}