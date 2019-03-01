package com.sp.neto;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class readWriteUserDetails extends BroadcastReceiver {

    private FirebaseFirestore DNPhDB = FirebaseFirestore.getInstance();

public void onReceive(final Context context, Intent intent){

    final Bundle extras = intent.getExtras();
    if(extras.getBoolean("New") == true){
        String id = extras.getString("UID");
        Users user = new Users(id, extras.getString("DisplayName"),extras.getString("Email"),extras.getString("MobileNo"),extras.getString("Password"));
        DNPhDB.collection("Users").document(id).set(user.userInfo);
    }

    else if(extras.getBoolean ("Edit") == true){
        //TODO: edit the DB
        String id = extras.getString("UID");
        final DocumentReference document = DNPhDB.collection("Users").document(id);
        document.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    document.update("displayName", extras.getString("DisplayName"));
                    document.update("MobileNo", extras.getString("MobileNumber"));
                }
            }
        });
    }else if(extras.getBoolean("read") == true){
        //TODO: reading from DB, only called before entering viewProfile
        final String id = extras.getString("UID");
             DNPhDB.collection("Users").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    String displayName = task.getResult().getString("displayName");
                    String email = task.getResult().getString("Email");
                    String mobileNo = task.getResult().getString("MobileNo");
                    Log.d("DisplayName", displayName);
                    //Log.d("email", email);
                    Log.d("mobileNo", mobileNo);

                    Intent returnIntent = new Intent (context.getApplicationContext(), viewProfile.class);
                    returnIntent.putExtra("DisplayName",displayName);
                    returnIntent.putExtra("Email",email);
                    returnIntent.putExtra("MobileNumber", mobileNo);
                    returnIntent.putExtra("UID",id);
                    returnIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(returnIntent);
                } else {
                    Log.w("ReadDB", "Error getting documents: ", task.getException());
                }
            }
        });


    }

}
}
