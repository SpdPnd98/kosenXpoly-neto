package com.sp.neto;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import static com.sp.neto.R.*;

public class chatRoom extends AppCompatActivity {

    private EditText editText;
    private DatabaseReference dbRef;
    private ImageView sendButton;
    private String topicName;
    private FirebaseListAdapter<ChatMessages> adapter;
    private ListView listView;
    private ChatMessages newEntry = new ChatMessages();
    private String uid;
    private String displayName;
    private TextView roomID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.chat_room);
        uid = getIntent().getExtras().getString("UID");
        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(uid)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            displayName = task.getResult().getData().get("displayName").toString();
                        }
                    }
                });

        editText = (EditText) findViewById(id.messageText);
        sendButton = (ImageView) findViewById(id.sendButton);
        sendButton.setOnClickListener(onSendMessage);
        topicName = getIntent().getExtras().getString("KEY");
        dbRef = FirebaseDatabase.getInstance().getReference().getRoot();
        roomID = (TextView) findViewById(id.roomID);
        roomID.setText("Room ID: " + topicName);
        displayChatMessages();
    }

    public void displayChatMessages(){
        listView = (ListView)findViewById(id.messageList);
        newEntry = new ChatMessages(editText.getText().toString(),
                displayName);
        adapter = new FirebaseListAdapter<ChatMessages>(
                this,
                ChatMessages.class,
                R.layout.message,
                FirebaseDatabase.getInstance().getReference().child(topicName)) {
            @Override
            protected void populateView(View v, ChatMessages model, int position) {
                TextView messageMain = (TextView)v.findViewById(id.message_text);
                TextView messageUser = (TextView)v.findViewById(id.message_user);
                TextView messageTime = (TextView)v.findViewById(id.message_time);

                // Seeing the values in model!
                Log.i("ModelText", model.getMessageText());
                Log.i("ModelUser", model.getMessageUser());
                Log.i("ModelTime", model.getMessageTime());

                // Set the above text views
                messageMain.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());
                messageTime.setText(model.getMessageTime());
            }
        };
        listView.setAdapter(adapter);
    }

    public View.OnClickListener onSendMessage = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            newEntry = new ChatMessages(editText.getText().toString(),
                    displayName);
            HashMap<String,Object> hashMap = new HashMap<>();
            hashMap.put(newEntry.getMessageTime(),newEntry);
            dbRef.child(topicName).updateChildren(hashMap);
            editText.setText("");
        }
    };



}