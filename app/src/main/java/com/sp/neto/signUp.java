package com.sp.neto;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.FirebaseDatabase;

public class signUp extends AppCompatActivity {

    private Button signUpConfirm;
    private EditText email;
    private EditText password;
    private EditText displayName;
    private EditText mobileNo;
    private FirebaseAuth appAuth;
    private ProgressBar progressBarSignUp;
    private FirebaseDatabase userdb = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.neto_sign_up);
        signUpConfirm = (Button) findViewById(R.id.signUpConfirm);
        email = (EditText) findViewById(R.id.emailSignUp);
        password = (EditText) findViewById(R.id.passwordSignUp);
        displayName = (EditText)findViewById(R.id.displayNameSignUp);
        mobileNo = (EditText) findViewById(R.id.mobileNoSignUp);
        appAuth = FirebaseAuth.getInstance();
        progressBarSignUp = (ProgressBar) findViewById(R.id.progressBarSignUp);
        progressBarSignUp.setVisibility(View.GONE);

        signUpConfirm.setOnClickListener(onSignUp);
    }

    private View.OnClickListener onSignUp = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            registerUser(); // method to register user
            progressBarSignUp.setVisibility(View.GONE);
        }

    };

    private void registerUser(){
        String newEmail= email.getText().toString();
        String newPassword = password.getText().toString();
        String newDisplayName = displayName.getText().toString();
        String newMobileNo = mobileNo.getText().toString();

        if (newDisplayName.isEmpty()){
            displayName.setError("Display name is required!");
            displayName.requestFocus();
            return;
        }else if (newEmail.isEmpty()||!(Patterns.EMAIL_ADDRESS.matcher(newEmail).matches())){
            email.setError("Email is invalid!");
            email.requestFocus();
            return;
        }else if (newMobileNo.isEmpty()){
            mobileNo.setError("Mobile number is required!");
            mobileNo.requestFocus();
            return;
        }else if (newPassword.isEmpty()||(newPassword.length()<6)){
            password.setError("Password is invalid!");
            password.requestFocus();
            return;
        }

        startActivityForResult(new Intent(signUp.this, termsAndConditions.class),1);

    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data){
        Bundle extras = data.getExtras();
        Toast.makeText(signUp.this, "Entered onActivityResult, DB success", Toast.LENGTH_SHORT);
        if(requestCode==1 && resultCode == RESULT_OK){
                progressBarSignUp.setVisibility(View.VISIBLE);

                // create account, saving email and password

                appAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){
                            //Toast.makeText(getApplicationContext(),"User Registered Successful",Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(signUp.this, netoMainMap.class);
                            i.putExtra("UID", appAuth.getCurrentUser().getUid());
                            i.putExtra("Email",appAuth.getCurrentUser().getEmail());
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                            finish();

                            // calls activity to save to firebase
                            Intent saveDisplayNameAndMobileNo = new Intent(signUp.this,readWriteUserDetails.class);
                            saveDisplayNameAndMobileNo.putExtra("UID",appAuth.getCurrentUser().getUid());
                            Toast.makeText(signUp.this,"Your UID is: " + appAuth.getCurrentUser().getUid(),Toast.LENGTH_LONG).show();
                            saveDisplayNameAndMobileNo.putExtra("DisplayName",displayName.getText().toString());
                            saveDisplayNameAndMobileNo.putExtra("MobileNo",mobileNo.getText().toString());
                            saveDisplayNameAndMobileNo.putExtra("EmailAddress",email.getText().toString());
                            saveDisplayNameAndMobileNo.putExtra("Password",password.getText().toString());
                            saveDisplayNameAndMobileNo.putExtra("New",true);

                            sendBroadcast(saveDisplayNameAndMobileNo);
                        }
                        else if(task.getException() instanceof FirebaseAuthUserCollisionException){
                            Toast.makeText(getApplicationContext(), "This Email has already been registered", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"An Error has occurred", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        }

        else if (requestCode==1 && resultCode ==RESULT_CANCELED){
            email.setText("");
            password.setText("");
            displayName.setText("");
            mobileNo.setText("");
        }

        else{
            Toast.makeText(getApplicationContext(),"How did you get here?", Toast.LENGTH_SHORT);
        }
    }

}
