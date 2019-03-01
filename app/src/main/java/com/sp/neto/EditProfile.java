package com.sp.neto;

import android.arch.lifecycle.ViewModelStoreOwner;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EditProfile extends AppCompatActivity {

    private Button submitChanges;
    private Button cancelChanges;
    private EditText editDisplayName;
    private EditText editEmail;
    private EditText editContactNumber;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        uid = getIntent().getExtras().getString("UID");

        submitChanges = (Button) findViewById(R.id.submitChanges);
        cancelChanges = (Button) findViewById(R.id.cancel);

        editDisplayName = (EditText) findViewById(R.id.editDisplayName);
        editContactNumber = (EditText)findViewById(R.id.editContactNumber);

        editDisplayName.setText(getIntent().getExtras().getString("DisplayName"));
        editContactNumber.setText(getIntent().getExtras().getString("MobileNumber"));
        //editEmail.setText(getIntent().getExtras().getString("Email"));

        submitChanges.setOnClickListener(onSubmitChange);
        cancelChanges.setOnClickListener(onCancel);
    }


    public View.OnClickListener onSubmitChange = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(EditProfile.this, viewProfile.class);
            i.putExtra("DisplayName",editDisplayName.getText().toString());
            i.putExtra("MobileNumber", editContactNumber.getText().toString());
            i.putExtra("UID",uid);
            //i.putExtra("Email", editEmail.getText().toString());

            Intent editDB = new Intent(EditProfile.this,readWriteUserDetails.class);
            editDB.putExtra("DisplayName",editDisplayName.getText().toString());
            editDB.putExtra("MobileNumber", editContactNumber.getText().toString());
            editDB.putExtra("UID",uid);
            //editDB.putExtra("Email", editEmail.getText().toString());
            editDB.putExtra("Edit",true);

            sendBroadcast(editDB);
            startActivity(i);
            finish();
        }
    };

    public View.OnClickListener onCancel = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };
}
