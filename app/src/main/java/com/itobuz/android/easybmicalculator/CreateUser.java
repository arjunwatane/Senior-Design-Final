package com.itobuz.android.easybmicalculator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreateUser extends AppCompatActivity
{
    DatabaseHelper helper = new DatabaseHelper(this);
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        Button btnSignUp = (Button) findViewById(R.id.btnSignUp);  // change to create

        btnSignUp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EditText tfName = (EditText) findViewById(R.id.tfName);
                EditText tfPassword1 = (EditText) findViewById(R.id.tfPassword1);
                EditText tfPassword2 = (EditText) findViewById(R.id.tfPassword2);

                String namestr = tfName.getText().toString();
                String pass1 = tfPassword1.getText().toString();
                String pass2 = tfPassword2.getText().toString();

                if(!pass1.equals(pass2))
                {
                    // popup msg
                    Toast.makeText(getApplicationContext(), "The passwords do not match.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    // insert details into database
                    //User u = new User(namestr,pass1);
                    //u.setName(namestr);
                    //u.setPassword(pass1);
                    helper.insertUser(new User(namestr,pass1));
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);
                    finish();
                    Toast.makeText(getApplicationContext(), "You have created a new user.", Toast.LENGTH_SHORT).show();

                    //set new user shared preferences
                    SharedPreferences.Editor editor = getSharedPreferences(namestr, MODE_PRIVATE).edit();
                    editor.putBoolean("newuser",true);
                    editor.commit();
                }
            }
        });
    }
}