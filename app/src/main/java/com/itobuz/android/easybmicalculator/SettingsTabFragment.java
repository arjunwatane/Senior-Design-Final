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
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static java.lang.Math.pow;
import org.apache.commons.math3.stat.regression.SimpleRegression;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsTabFragment extends Fragment
{
    public static final String ARG_PAGE = "ARG_PAGE";

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
    private TextView compareGluc;

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
    private String classify;
    private int testing_DEMO = 0;
    private int error = 0;

    private float glucose_result;

    private EditText editGlucoseText;

    private static final int NUM_SAMPLES = 2;

    private int size = 0;
    private double actual_glucose_values[] = new double[NUM_SAMPLES];


    public double double_glucose[] = new double[NUM_SAMPLES];
    int gluc_finish = 0;

    private BluetoothAdapter btAdapter = null;
    Set<BluetoothDevice> pairedDevices = null;
    ArrayList<Bluetooth> deviceList;
    ArrayList<String> deviceAddress;
    ConnectThread connectThread;
    Handler mHandler;
    public Boolean ctAlive = false;

    //Arjun
    public float glucose_array[] = new float[NUM_SAMPLES];
    private SimpleRegression simpleRegression = new SimpleRegression(true);

    public int clear_database = 0, clear_database2 = 0;
    public int data_cnt = 0;
    private int test_flag = 0;
    private double test_glucose_value = 0;

    private static final UUID MY_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static final int MESSAGE_READ = 0;
    private int REQUEST_ENABLE_BT = 1;



    public static SettingsTabFragment newInstance(int pageNo)
    {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNo);
        SettingsTabFragment settingsFragment = new SettingsTabFragment();
        settingsFragment.setArguments(args);
        return settingsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        deviceList = new ArrayList<>();
        deviceAddress = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        viewPager = (ViewPager) getActivity().findViewById(R.id.view_pager);

        editAgeText = (AppCompatEditText) view.findViewById(R.id.editAge);
        editWeightText = (AppCompatEditText) view.findViewById(R.id.editWidth);
        editWeightunit = (Spinner) view.findViewById(R.id.editWeight_unit);
        editHeightText = (AppCompatEditText) view.findViewById(R.id.editHeight);
        editHeightInchText = (AppCompatEditText) view.findViewById(R.id.editHeightInch);
        editHeightunit = (Spinner) view.findViewById(R.id.editHeight_unit);

        editMale = (RadioButton) view.findViewById(R.id.editRadioMale);
        editFemale = (RadioButton) view.findViewById(R.id.editRadioFemale);
        TextView versionAbout = (TextView) view.findViewById(R.id.about_text2);
        calculateBtn = (Button) view.findViewById(R.id.editCalculate);
        Button btn_testsample = (Button) view.findViewById(R.id.test_sample);
        Button btn_compare = (Button) view.findViewById(R.id.compare);

        /** Arjun **/
        Button btn_sample_glucose = (Button) view.findViewById(R.id.sample_glucose_btn);
        Button btn_filter_glucose = (Button) view.findViewById(R.id.filter_btn);
        editGlucoseText = (AppCompatEditText) view.findViewById(R.id.editGlucose);
        compareGluc = (TextView) view.findViewById(R.id.compare_result);
        /*
      Arjun
     */
        Button btn_bluetooth = (Button) view.findViewById(R.id.bluetooth_btn);

        mHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                if (msg.what == MESSAGE_READ)
                {
                    if (gluc_finish == 0)
                    {
                        if (data_cnt < NUM_SAMPLES)
                        {
                            String message = msg.obj.toString();
                            float readMessage = Float.valueOf(message);
                            System.out.println(message);
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            //readMessage = 50.0f;
                            glucose_array[data_cnt] = readMessage;
                            double_glucose[data_cnt] = (double) glucose_array[data_cnt];
                            System.out.println("Reading = " + glucose_array[data_cnt] + " and " + double_glucose[data_cnt]);


                            if (data_cnt == NUM_SAMPLES)
                            {
                                Toast.makeText(getActivity(), "Got enough samples.", Toast.LENGTH_SHORT).show();
                                gluc_finish = 1;
                                //data_cnt = 0;

                            }

                        }


                    }

                    if(test_flag == 1)
                    {
                        String message = msg.obj.toString();
                        float readMessage = Float.valueOf(message);
                        test_glucose_value = (double) readMessage;
                        System.out.println("Reading = " + test_glucose_value);
                        System.out.println(message);
                        test_flag = 0;

//                        test_glucose_value = 51.0;

                    }
                    else if (gluc_finish == 1)
                        Toast.makeText(getActivity(), "Already have enough calibration data.", Toast.LENGTH_SHORT).show();
                }
            }
        };

        btn_sample_glucose.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
//                data_cnt = 0;
                try
                {
                    sample(editGlucoseText.getText().toString());

                } catch (Exception e)
                {
                    Toast.makeText(getActivity(), "Please enter a valid number."
                            , Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_filter_glucose.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                train();
            }
        });

        btn_compare.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                compare();
            }
        });
        btn_bluetooth.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                disconnectBluetooth();
                connectBluetooth();
            }
        });

        mdb = new DatabaseHelper(this.getContext());

        versionAbout.setText("Build Version: " + BuildConfig.VERSION_NAME);

        // if select height spinner inch field setup
        settingsHeightSpinners();

        editAgeText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (s.toString().trim().length() == 0)
                {
                    calculateBtn.setEnabled(false);
                } else
                {
                    calculateBtn.setEnabled(true);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                // TODO Auto-generated method stub
            }
        });

        editWeightText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

                if (s.toString().trim().length() == 0)
                {
                    calculateBtn.setEnabled(false);
                } else
                {
                    calculateBtn.setEnabled(true);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                // TODO Auto-generated method stub
            }
        });

        editHeightText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (s.toString().trim().length() == 0)
                {
                    calculateBtn.setEnabled(false);
                } else
                {
                    calculateBtn.setEnabled(true);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                // TODO Auto-generated method stub
            }
        });

        //Calculate button click action
        calculateBtn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View arg0)
            {

                if ((actual_glucose_values.length - data_cnt) <= 0 && testing_DEMO == 0)
                {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    strDate = sdf.format(new Date());

                    userAge = editAgeText.getText().toString();
                    userSex = (editMale.isChecked()) ? "Male" : (editFemale.isChecked()) ? "Female" : "";

                    try
                    {
                        userWeight = Double.valueOf(editWeightText.getText().toString());
                    } catch (NumberFormatException e)
                    {
                        userWeight = 0;
                    }

                    userWeightUnit = editWeightunit.getSelectedItem().toString();
                    userWeightUnitPos = editWeightunit.getSelectedItemPosition();

                    try
                    {
                        userHight = Double.valueOf(editHeightText.getText().toString());
                    } catch (NumberFormatException e)
                    {
                        userHight = 0;
                    }

                    try
                    {
                        userHightInch = Double.valueOf(editHeightInchText.getText().toString());
                    } catch (NumberFormatException e)
                    {
                        userHightInch = 0;
                    }

                    userHeightUnit = editHeightunit.getSelectedItem().toString();
                    userHeightUnitPos = editHeightunit.getSelectedItemPosition();

                    GlucHelper gh = new GlucHelper();


                    glucose_result = (float) simpleRegression.predict(test_glucose_value);
                    System.out.println("Predicting glucose value concentration based on the reading = " + test_glucose_value);
                    System.out.println("GLUCOSE RESULT = " + glucose_result);

                    if (mdb.updateBioAll(Integer.parseInt(userAge), userSex, userWeight, userWeightUnit,
                            userHight, userHightInch, userHeightUnit, 0, 0) && mdb.insertGlucLog
                            (Hold.getName(), Hold.getId(), (int) glucose_result, gh
                                    .getGlucClassification(glucose_result)))
                    {
                        context = getContext().getApplicationContext();

                        classify = gh.getGlucClassification(glucose_result);
                        Toast.makeText(context, "Your glucose is " + classify, Toast.LENGTH_SHORT).show();

                        viewPager.setCurrentItem(0);
                    }
                }
                else if(actual_glucose_values.length - data_cnt <= 0 && testing_DEMO == 1){
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    strDate = sdf.format(new Date());

                    userAge = editAgeText.getText().toString();
                    userSex = (editMale.isChecked()) ? "Male" : (editFemale.isChecked()) ? "Female" : "";

                    try
                    {
                        userWeight = Double.valueOf(editWeightText.getText().toString());
                    } catch (NumberFormatException e)
                    {
                        userWeight = 0;
                    }

                    userWeightUnit = editWeightunit.getSelectedItem().toString();
                    userWeightUnitPos = editWeightunit.getSelectedItemPosition();

                    try
                    {
                        userHight = Double.valueOf(editHeightText.getText().toString());
                    } catch (NumberFormatException e)
                    {
                        userHight = 0;
                    }

                    try
                    {
                        userHightInch = Double.valueOf(editHeightInchText.getText().toString());
                    } catch (NumberFormatException e)
                    {
                        userHightInch = 0;
                    }

                    userHeightUnit = editHeightunit.getSelectedItem().toString();
                    userHeightUnitPos = editHeightunit.getSelectedItemPosition();

                    GlucHelper gh = new GlucHelper();


//                    glucose_result = (float) simpleRegression.predict(test_glucose_value);
                    glucose_result = (float)test_glucose_value;
                    System.out.println("Predicting glucose value concentration based on the reading = " + test_glucose_value);
                    System.out.println("GLUCOSE RESULT = " + glucose_result);

                    if (mdb.updateBioAll(Integer.parseInt(userAge), userSex, userWeight, userWeightUnit,
                            userHight, userHightInch, userHeightUnit, 0, 0) && mdb.insertGlucLog
                            (Hold.getName(), Hold.getId(), (int) glucose_result, gh
                                    .getGlucClassification(glucose_result)))
                    {
                        context = getContext().getApplicationContext();

                        classify = gh.getGlucClassification(glucose_result);
                        Toast.makeText(context, "Your glucose is " + classify, Toast.LENGTH_SHORT).show();

                        viewPager.setCurrentItem(0);
                    }
                }
                else
                {
                    Toast.makeText(getActivity(), "Add more samples", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Test Sample button onclick action
        btn_testsample.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View arg0)
            {

                if(actual_glucose_values.length - data_cnt <= 0 && testing_DEMO == 0) {
                    test_flag = 1;
                    String a = "A";

                    if (connectThread != null)
                        connectThread.connectedThread.write(a.getBytes());

//                    test_glucose_value = 80.0;

                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            System.out.println("Tested sample. Reading = " + test_glucose_value);
                            System.out.println("Test Flag: " + test_flag);
                            test_flag = 0;
                        }
                    }, 2000);


                }
                else if(actual_glucose_values.length - data_cnt <= 0 && testing_DEMO == 1){
                    test_glucose_value = 100;
                }
                else
                    Toast.makeText(getActivity(), "Add more samples", Toast.LENGTH_SHORT).show();


            }
        });

        return view;
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState)
    {
        super.onViewStateRestored(savedInstanceState);
        showUserValue();
    }

    //todo remove cursor or change to use different records
    //just need to find the user's bio stats
    private void showUserValue()
    {
        //Cursor c = mdb.lastRecords();
        DataProvider c = mdb.searchInfo();
        //c.moveToLast();
        if (c != null)
        {
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

            if (c.getHeightunit().equals("Cm"))
            {
                //do something
                editHeightInchText.setVisibility(View.GONE);
            } else
            {
                editHeightInchText.setText(Double.toString(c.getHeightinch()));
                editHeightInchText.setVisibility(View.VISIBLE);
            }

            String sex = (c.getSex());

            if (sex.equals("Male"))
            {
                editMale.setChecked(true);
            } else if (sex.equals("Female"))
            {
                editFemale.setChecked(true);
            }
        } else System.out.println("Failure of the system show");
    }

    public void settingsHeightSpinners()
    {
        editHeightunit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView parent, View view, int position, long id)
            {
                //height spinner CM is selected I would like to hide the second height edittext field
                if (position == 0)
                {
                    editHeightInchText.setVisibility(View.GONE);

                } else
                {
                    editHeightInchText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                // Do Something.
            }
        });
    }


    /**
     * GOLD FUNCTIONS
     **/
    private void disconnectBluetooth()
    {
        if (ctAlive)
            connectThread.connectedThread.cancel();
        if (connectThread != null)
            connectThread.cancel();
    }

    private void connectBluetooth()
    {
        int msptracker = 0;
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null)
        {
            Toast.makeText(getActivity(), "Bluetooth is not supported on this device.", Toast.LENGTH_SHORT).show();
        } else
        {
            if (!btAdapter.isEnabled())
            {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            } else
            {
                pairedDevices = btAdapter.getBondedDevices();
                if (pairedDevices.size() > 0)
                {
                    for (BluetoothDevice device : pairedDevices)
                    {
                        Bluetooth bt = new Bluetooth(device.getName(), device.getAddress());
                        deviceList.add(bt);
                        for (int i = 0; i < deviceList.size(); i++)
                        {
                            if (deviceList.get(i).getName().startsWith("HC"))
                            {
                                msptracker = i;
                                Toast.makeText(getActivity(), "Bluetooth is connected.", Toast.LENGTH_SHORT).show();
                            }

                        }

                    }
                }
                // need to move this inside the if loop
                BluetoothDevice remoteDevice = btAdapter.getRemoteDevice(deviceList.get(msptracker).getAddress());
                connectThread = new ConnectThread(remoteDevice);
                connectThread.start();
                Toast.makeText(getActivity(), "Bluetooth is now enabled.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        int msptracker = 0;
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                pairedDevices = btAdapter.getBondedDevices();

                if (pairedDevices.size() > 0) {
                    for (BluetoothDevice device : pairedDevices)
                    {
                        Bluetooth bt = new Bluetooth(device.getName(), device.getAddress());
                        deviceList.add(bt);
                        for (int i = 0; i < deviceList.size(); i++)
                        {
                            if (deviceList.get(i).getName().startsWith("HC-06"))
                                msptracker = i;

                        }
                    }

                    BluetoothDevice remoteDevice = btAdapter.getRemoteDevice(deviceList.get(msptracker).getAddress());
                    connectThread = new ConnectThread(remoteDevice);
                    connectThread.start();
                    Toast.makeText(getActivity(), "Bluetooth is now enabled.", Toast.LENGTH_SHORT).show();
                }
            }
            if (resultCode == RESULT_CANCELED)
            {
                Toast.makeText(getActivity(), "Bluetooth needs to be enabled to continue.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class ConnectThread extends Thread
    {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        ConnectedThread connectedThread;

        public ConnectThread(BluetoothDevice device)
        {
            currentThread().setName("ConnectedThread");
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try
            {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e)
            {
            }
            mmSocket = tmp;
        }

        public void run()
        {
            // Cancel discovery because it will slow down the connection
            btAdapter.cancelDiscovery();

            try
            {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
            } catch (IOException connectException)
            {
                // Unable to connect; close the socket and get out
                try
                {
                    ctAlive = false;
                    mmSocket.close();
                } catch (IOException closeException)
                {
                }
                return;
            }

            // Do work to manage the connection (in a separate thread)
            connectedThread = new ConnectedThread(mmSocket);
            connectedThread.start();
            ctAlive = true;
        }

        /**
         * Will cancel an in-progress connection, and close the socket
         */
        public void cancel()
        {
            try
            {
                mmSocket.close();
            } catch (IOException e)
            {
            }
        }
    }

    public class ConnectedThread extends Thread
    {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket)
        {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try
            {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e)
            {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public synchronized void run()
        {
            currentThread().setName("ConnectedThread");
            byte[] buffer;  // buffer store for the stream
            int bytes; // bytes returned from read()
            int availableBytes = 0;
            ByteBuffer bb = ByteBuffer.allocate(1024);

            // Keep listening to the InputStream until an exception occurs
            while (true)
            {
                try
                {
                    // Read from the InputStream
                    availableBytes = mmInStream.available();
                    if (availableBytes > 0)
                    {
                        buffer = new byte[availableBytes];
//                        Log.d("Before buffer read;", new String(buffer));
                        bytes = mmInStream.read(buffer);
//                        Log.d("After buffer read;", new String(buffer));
                        // Wrap the byte array as a float number ordering the bytes using little endian.
                        bb.position(0);
                        float test = bb.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                        String s = Float.toString(test);

                        // Send the obtained bytes to the UI activity
                        if (test > 0.001)
                        {
                            mHandler.obtainMessage(MESSAGE_READ, bytes, -1, s).sendToTarget();
                        }
                    } else
                    {
                        Thread.sleep(100);
                    }
                } catch (IOException e)
                {
                    break;
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                    ctAlive = false;
                } catch (BufferUnderflowException e)
                {
                    bb.position(0);
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes)
        {
            try
            {
                mmOutStream.write(bytes);
            } catch (IOException e)
            {
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel()
        {
            try
            {
                mmSocket.close();
            } catch (IOException e)
            {
            }
        }
    }


    /**
     * Get one sample data (skin and glucose) from the hardware module, and add it to the database
     **/
    public void sample(final String known_glucose_string)
    {
        if(known_glucose_string.length() == 0)
            Toast.makeText(getActivity(), "Enter valid number.", Toast.LENGTH_SHORT).show();
        else if (actual_glucose_values.length - data_cnt > 0 && testing_DEMO == 0)
        {
            error = 0;
            clear_database = 0;
            String a = "A";
            if (connectThread != null)
                connectThread.connectedThread.write(a.getBytes());

            new Timer().schedule(new TimerTask()
            {
                @Override
                public void run()
                {
                    //if(data_cnt == 0) data_cnt ++;
                    if(double_glucose[data_cnt] > 0) {
                        compareGluc.setText("Got glucose.");
                        actual_glucose_values[data_cnt] = Double.parseDouble(known_glucose_string);
                        System.out.println("Sample " + (data_cnt + 1) + ": " + actual_glucose_values[data_cnt] + ", " + double_glucose[data_cnt]);
                        data_cnt = data_cnt + 1;
//                    size++;
                        //System.out.println(size + ": Waited 2 seconds and saved measurement: " + double_glucose[data_cnt]);
                        //System.out.println("Samples left: " + String.valueOf(actual_glucose_values.length - size));
//                    Toast.makeText(getActivity(), "Samples left: " + String.valueOf(actual_glucose_values.length - size), Toast.LENGTH_SHORT).show();
                    }
                    else
                        System.out.println("Error in sampling. Redo.");
                }
            }, 2000);

            if(error == 1)
                Toast.makeText(getActivity(), "Error in sampling. Redo.", Toast.LENGTH_SHORT).show();
        }
        else if(actual_glucose_values.length - data_cnt > 0 && testing_DEMO == 1)
        {
            clear_database = 0;
            actual_glucose_values[data_cnt] = Double.parseDouble(known_glucose_string);
            if(data_cnt == 0)
                double_glucose[data_cnt] = 50;
            if(data_cnt == 1)
                double_glucose[data_cnt] = 100;
            data_cnt = data_cnt + 1;

        }
        else
            Toast.makeText(getActivity(), "Database is full. Cannot add more samples.", Toast.LENGTH_SHORT).show();
    }


    //train glucose and skin
    public void train()
    {

        if(data_cnt == 0)
            Toast.makeText(getActivity(), "Databases is empty, get samples.", Toast.LENGTH_SHORT).show();
        else if(clear_database > 1 || clear_database2 > 1 || actual_glucose_values.length - data_cnt < 0){
            data_cnt = 0;
            size = 0;
            gluc_finish = 0;
            clear_database = 0;
            clear_database2 = 0;
            Toast.makeText(getActivity(), "Databases are emptied.", Toast.LENGTH_SHORT).show();
        }
        else if(actual_glucose_values.length - data_cnt > 0 || clear_database2 == 1)
        {
            Toast.makeText(getActivity(), "Click again to empty database.", Toast.LENGTH_SHORT).show();
            clear_database ++;
            clear_database2 += 2;
        }
        else if(actual_glucose_values.length - data_cnt == 0)
        {
            train_polyfit();
            Toast.makeText(getActivity(), "Databases are filtered and trained.", Toast.LENGTH_SHORT).show();
            size++;
            clear_database = 0;
            clear_database2 ++;
        }


    }


    /**
     * Create polynomial fitting of training data
     **/
    public void train_polyfit()
    {

        for (int i = 0; i < actual_glucose_values.length; i++) {
            simpleRegression.addData(double_glucose[i], actual_glucose_values[i]);
            System.out.println("Added data:  Actual = " + actual_glucose_values[i] + "   Reading = " + double_glucose[i] + " or " + glucose_array[i]);
        }

        System.out.println("slope = " + simpleRegression.getSlope());
        System.out.println("intercept = " + simpleRegression.getIntercept());

    }

    public void compare()
    {
        if(actual_glucose_values.length - data_cnt == 0){

            if(double_glucose[0] > double_glucose[1])
                compareGluc.setText("1st sample is glucose.");
            else
                compareGluc.setText(("2nd sample is glucose."));
        }
    }
}



