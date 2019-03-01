package com.sp.neto;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class neto extends AppCompatActivity {

    private TextView userID;
    private TextView userMobileNo;
    private FirebaseUser currUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.neto_main);
        currUser = FirebaseAuth.getInstance().getCurrentUser();
        //Intent intent = getIntent();

        userID = (TextView)findViewById(R.id.userID);
        userMobileNo = (TextView)findViewById((R.id.userMobileNo));

        if(currUser != null) {
            userID.setText(currUser.getDisplayName());
            userMobileNo.setText(currUser.getPhoneNumber());
        }



    }
}
