package com.sp.neto;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class termsAndConditions extends AppCompatActivity {

    private Button acceptTC;
    private Button rejectTC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.neto_terms_and_conditions);

        acceptTC = (Button) findViewById(R.id.agreeTC);
        rejectTC = (Button) findViewById(R.id.rejectTC);

        acceptTC.setOnClickListener(onAccept);
        rejectTC.setOnClickListener(onReject);
    }

    private View.OnClickListener onAccept = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent();
            setResult(termsAndConditions.RESULT_OK, i);
            finish();
        }
    };

    private View.OnClickListener onReject = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent();
            setResult(termsAndConditions.RESULT_CANCELED, i);
            finish();
        }
    };
}
