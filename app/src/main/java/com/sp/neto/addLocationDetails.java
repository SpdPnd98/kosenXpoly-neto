package com.sp.neto;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class addLocationDetails extends AppCompatActivity {

    private ImageButton camera;
    private Button submitCase;
    private Button cancelCase;
    private EditText dateDetails;
    private EditText timeDetails;
    private EditText caseDetails;
    private Uri photoURI;
    private FirebaseFirestore caseDB = FirebaseFirestore.getInstance();
    private File photoFile = null;
    private String key = "";

    static final int REQUEST_TAKE_PHOTO = 5;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go

            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Log.d("Instantiated photoFile",photoFile.toString());
                photoURI = FileProvider.getUriForFile(this,
                        "com.sp.neto.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_location_details);

        camera = (ImageButton)findViewById(R.id.Camera);
        submitCase = (Button)findViewById(R.id.submitCase);
        cancelCase = (Button)findViewById(R.id.cancelCase);
        caseDetails = (EditText)findViewById(R.id.detailsContent);
        timeDetails = (EditText)findViewById(R.id.timeDetails);
        dateDetails = (EditText)findViewById(R.id.dateDetails);

        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        SimpleDateFormat caseTime = new SimpleDateFormat("HH:mm");
        String currentTime = caseTime.format(calendar.getTime());
        dateDetails.setText(currentDate);
        timeDetails.setText(currentTime);

        submitCase.setOnClickListener(submitOnClick);
        cancelCase.setOnClickListener(cancelOnClick);
        camera.setOnClickListener(cameraOnclick);
    }

    public View.OnClickListener submitOnClick = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            ProgressBar progressBarSubmit = findViewById(R.id.progressBarSubmit);
            progressBarSubmit.setVisibility(View.VISIBLE);
            final double latitude = Double.valueOf(getIntent().getExtras().getString("Lat"));
            final double longitude = Double.valueOf(getIntent().getExtras().getString("Lon"));
            String caseDetailText = caseDetails.getText().toString();

            // Create a new case object with Latitude, Longitude (This document only has LatLng field!)
            // TODO: create a new document with picture and image link, with the same key id
            Map<String, Object> newCase = new HashMap<>();
            newCase.put("Lat",String.valueOf(latitude));
            newCase.put("Lon", String.valueOf(longitude));
            newCase.put("Details", caseDetailText);
            newCase.put("Date",String.valueOf(dateDetails.getText().toString()));
            newCase.put("Time",String.valueOf(timeDetails.getText().toString()));
            final Intent resultIntent = new Intent(addLocationDetails.this, netoMainMap.class);

            resultIntent.putExtra("Lat",latitude);
            resultIntent.putExtra("Lon",longitude);
            resultIntent.putExtra("Details",caseDetailText);
            resultIntent.putExtra("Date",String.valueOf(dateDetails.getText().toString()));
            resultIntent.putExtra("Time",String.valueOf(timeDetails.getText().toString()));

            // Add a document with a generated ID...
            caseDB.collection("casesLatLng").add(newCase).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Log.d("Marker added!", String.valueOf(latitude) + " ," + String.valueOf(longitude));
                    setResult(RESULT_OK,resultIntent);
                    Log.d("onSuccess", "Finished triggered");
                    key = documentReference.getId();
                    createChatroom();

                    // Upload Image here
                    if(photoFile != null){
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        String fileName = documentReference.getId() + ".jpg";
                        Log.d("FileName",fileName);
                        StorageReference pathCursor = storage.getReference();
                        pathCursor = pathCursor.child(fileName);
                        UploadTask uploadImg = pathCursor.putFile(Uri.fromFile(photoFile));
                        uploadImg.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                Toast.makeText(addLocationDetails.this,"Upload Image FAILED!",Toast.LENGTH_SHORT);
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                // ...
                                Toast.makeText(addLocationDetails.this,"Upload Image SUCCESS!",Toast.LENGTH_SHORT);
                            }
                        });
                    }

                    removeTempphoto();
                    finish();


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("Marker not added!", "Somehow you are here.....");
                    Toast.makeText(addLocationDetails.this,"Location not added, do you have active WiFi connection?",Toast.LENGTH_LONG).show();
                    setResult(RESULT_CANCELED,resultIntent);
                    Log.d("onFailed", "Finished triggered");
                    removeTempphoto();
                    finish();
                }
             });

        }
    };

    public View.OnClickListener cancelOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (photoURI != null){
                removeTempphoto();
            }
            setResult(RESULT_CANCELED,new Intent(addLocationDetails.this,netoMainMap.class));
            Log.d("onCancel", "Finished triggered");
            finish();
        }
    };

    public View.OnClickListener cameraOnclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dispatchTakePictureIntent();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

            int targetW = camera.getWidth();
            int targetH = camera.getHeight();

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW/(targetW/2), photoH/(targetH/2));

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = (scaleFactor);

            // Set a scaled down image
            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            camera.setImageBitmap(bitmap);
            //camera.setImageURI(photoURI);
        }
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void removeTempphoto(){
        // After Setting the image, it will be removed from temporary file
        // get full path with cursors...

        /*Cursor cursor = getContentResolver().query(photoURI, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);

        File fdelete = new File(cursor.getString(idx));*/
        Log.d("Original path", photoURI.getPath());
        Log.d("Path name: ", photoFile.toString());
        if (photoFile.exists()) {
            if(photoFile.delete()){
                photoFile.delete();
                Toast.makeText(addLocationDetails.this, "File deleted @ " + String.valueOf(photoFile.getPath()),Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(addLocationDetails.this, "ERROR! File not deleted @ " + String.valueOf(photoFile.getPath()),Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(addLocationDetails.this, "ERROR! File does not exist @ " + String.valueOf(photoFile.getPath()),Toast.LENGTH_LONG).show();

        }
    }

    public void createChatroom(){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().getRoot().child(key);
        ChatMessages chatRoomInit = new ChatMessages("Welcome to new room, document what you see at the scene of accident",
                "Admin");
        dbRef.child(chatRoomInit.getMessageTime()).setValue(chatRoomInit);
    }

}
