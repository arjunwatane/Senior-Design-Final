package com.itobuz.android.easybmicalculator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity
{
    DatabaseHelper helper = new DatabaseHelper(this);
    EditText tfUsername, tfPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        helper.load();

        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        Button btnCreateUser = (Button) findViewById(R.id.btnCreateUser); // change this to create user
        tfUsername = (EditText)findViewById(R.id.TFusername);
        tfPassword = (EditText)findViewById(R.id.TFpassword);

        //login as a previous user
        btnLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                String str = tfUsername.getText().toString();

                String pass = tfPassword.getText().toString();

                String password = helper.searchPass(str);

                if(pass.equals(password))
                {
                    int ID = helper.searchID(str, pass);
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    i.putExtra("Username", str);
                    i.putExtra("ID", ID);
                    startActivity(i);
                    //temp hold user login
                    new Hold(str,password,ID);
                    finish();
                }
                else
                {
                    Toast loginToast = Toast.makeText(getApplicationContext(), "The login credentials are incorrect.", Toast.LENGTH_SHORT);
                    loginToast.show();
                }
            }
        });

        //create a new user
        btnCreateUser.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                tfUsername.setText("");
                tfPassword.setText("");
                Intent i = new Intent(getApplicationContext(), CreateUser.class);
                startActivity(i);
            }
        });
    }
}