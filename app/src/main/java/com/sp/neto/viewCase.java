package com.sp.neto;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class viewCase extends AppCompatActivity {

    private String key;
    private Uri photoURI;
    private File photoFile = null;
    String mCurrentPhotoPath;
    private ImageView downloadView;
    private String uid;
    private ProgressBar progressBar;
    private FloatingActionButton getJoinChat;
    private TextView latcase,loncase,information,date,time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_case);

        Bundle extras = getIntent().getExtras();

        FirebaseFirestore dbRef= FirebaseFirestore.getInstance();
        dbRef.collection("casesLatLng").document(extras.getString("KEY")).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();

                            String lat = document.getString("Lat");
                            String lon = document.getString("Lon");
                            String dateStr = document.getString("Date");
                            String timeStr = document.getString("Time");
                            String details = document.getString("Details");

                            latcase = (TextView) findViewById(R.id.latCaseView);
                            latcase.setText(lat);
                            loncase = (TextView) findViewById(R.id.lonCaseView);
                            loncase.setText(lon);
                            information = (TextView) findViewById(R.id.infoCaseView);
                            information.setText(details);
                            date = (TextView) findViewById(R.id.dateCaseView);
                            date.setText(dateStr);
                            time = (TextView) findViewById(R.id.timeCaseView);
                            time.setText(timeStr);

                        }
                    }
                });
        downloadView = (ImageView) findViewById(R.id.downloadView);
        progressBar = (ProgressBar) findViewById(R.id.loadingImage);
        getJoinChat = (FloatingActionButton) findViewById(R.id.getJoinChat);

        key = getIntent().getExtras().getString("KEY");
        Log.d("KEY value:",key);

        uid = getIntent().getExtras().getString("UID");

        getJoinChat.setOnClickListener(onJoinChat);

        findViewById(R.id.viewCaseLayout).post(new Runnable() {
            @Override
            public void run() {
                boolean downloaded = false;
                try {
                    photoFile = createImageFile();
                    downloaded=false;
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    Log.i("MagickEvent","Some magick has happened!");
                    String imageFileName = key + ".jpg";
                    File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    File image = new File(URI.create("file://" + storageDir.getAbsolutePath() + "/" + imageFileName));
                    downloaded = true;
                }
                if (photoFile != null) {
                    photoURI = FileProvider.getUriForFile(viewCase.this,"com.sp.neto.fileprovider",photoFile);
                }
                if(!downloaded) {
                    Log.i("EmptyFile", "Created***********");
                    FirebaseStorage picDB = FirebaseStorage.getInstance();
                    StorageReference picDBRef = picDB.getReference().child(key + ".jpg");
                    picDBRef.getFile(photoFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(@NonNull FileDownloadTask.TaskSnapshot task) {
                            displayImage();
                        }
                    });
                }else{
                    displayImage();
                }
            }

        });


    }

    protected void onResume(){
        super.onResume();

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = key;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = new File(URI.create("file://" + storageDir +"/"+imageFileName+".jpg"));
        if (image.exists()){
            photoFile = image;
            throw new IOException("This is the end of the line, eat this!");
        }
        image.createNewFile();

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
                Toast.makeText(viewCase.this, "File deleted @ " + String.valueOf(photoFile.getPath()),Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(viewCase.this, "ERROR! File not deleted @ " + String.valueOf(photoFile.getPath()),Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(viewCase.this, "ERROR! File does not exist @ " + String.valueOf(photoFile.getPath()),Toast.LENGTH_LONG).show();

        }
    }

    private void displayImage(){
        progressBar.setVisibility(View.GONE);
        int targetW = downloadView.getWidth();
        int targetH = downloadView.getHeight();
        Log.i("Dimensions", String.valueOf(targetH) + " , " + String.valueOf(targetW));

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath()/*, bmOptions*/);
        int photoW = bitmap.getWidth();
        int photoH = bitmap.getHeight();

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / (targetW / 2), photoH / (targetH / 2));

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = (scaleFactor);

        // Set a scaled down image
        mCurrentPhotoPath = photoFile.getAbsolutePath();
        bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        Log.i("mCurrentPhotoPath", mCurrentPhotoPath);
        if (bitmap == null) {
            Log.i("BitmapProblem", "Why this bitmap is null?");
        }
        downloadView.setImageBitmap(bitmap);
    }

    private View.OnClickListener onJoinChat = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Create a chatroom instance in Realtime DB
            Intent i = new Intent(viewCase.this,chatRoom.class);
            i.putExtra("KEY",key);
            i.putExtra("UID",uid);
            startActivity(i);
        }
    };


}
