package com.sp.neto;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity {

    private EditText loginID;
    private EditText password;
    private Button loginButton;
    private Button signUpButton;
    private FirebaseAuth appAuth; // Need to check whenever the user is signed in
    private ProgressBar progressBarLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.neto_login); //Login Page

        loginID = (EditText) findViewById(R.id.loginID);
        password = (EditText) findViewById(R.id.password);
        loginButton = (Button) findViewById(R.id.loginButton);
        signUpButton = (Button) findViewById(R.id.signUpButton);
        progressBarLogin = (ProgressBar) findViewById(R.id.progressBarLogin);
        progressBarLogin.setVisibility(View.INVISIBLE);

        loginButton.setOnClickListener(onLogin);
        signUpButton.setOnClickListener(onSignUp);
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBarLogin.setVisibility(View.GONE);
    }

    private View.OnClickListener onLogin = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            progressBarLogin.setVisibility(View.VISIBLE);
            loginUser();
            progressBarLogin.setVisibility(View.VISIBLE);
        }
    };

    private View.OnClickListener onSignUp = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            //Some code to redirect to Sign Up page
            progressBarLogin.setVisibility(View.VISIBLE);
            Intent i = new Intent(login.this, signUp.class);
            startActivity(i);
            progressBarLogin.setVisibility(View.VISIBLE);
        }
    };

    private void loginUser(){
        //Reading string from login ID
        String user = loginID.getText().toString();
        //Reading Password from Password
        String pass = password.getText().toString();

        if (user.isEmpty()){
            loginID.setError("Please Enter your UID");
            loginID.requestFocus();
            return;
        }else if(pass.isEmpty()){
            password.setError("Password cannot be empty");
            password.requestFocus();
            return;
        }else if(pass.length()<6){
            password.setError("Password invalid");
            password.requestFocus();
            return;
        }

        appAuth = FirebaseAuth.getInstance();
        appAuth.signInWithEmailAndPassword(user,pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent i = new Intent(login.this, netoMainMap.class);
                    i.putExtra("loginKey", appAuth.getCurrentUser().getUid());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.putExtra("Email", appAuth.getCurrentUser().getEmail());
                    i.putExtra("UID", appAuth.getCurrentUser().getUid());
                    //Toast.makeText(login.this,"login UID is: " + appAuth.getCurrentUser().getUid(),Toast.LENGTH_SHORT).show();
                    //FirebaseUser loginUser = appAuth.getCurrentUser();
                    startActivity(i);
                    finish();
                } else if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                    Toast.makeText(getApplicationContext(),"Password or UID incorrect",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
