package com.example.antonio.cameraapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

public class MainActivity extends AppCompatActivity {
    private final int RC_PICTURE_TAKEN = 1111;
    private final int RC_PERMISSIONS = 2222;
    private static final int REQUEST_WRITE_PERMISSION = 20;
    Context context;
    Button btn;
    TextView textV;
    private static final double SMILE_PROB = 0.5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.button);
        textV = (TextView) findViewById(R.id.textView);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.CAMERA}, RC_PERMISSIONS);
            finish();
        }

//        FaceDetector detector = new FaceDetector.Builder(context)
//                .setTrackingEnabled(false)
//                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
//                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
//                .build();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(MainActivity.this, new
                        String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, RC_PICTURE_TAKEN);
            }
        });


    }
    //  @Override
//    protected void onResume() {
//        super.onResume();
//        // when the user clicks the button… {
//        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(takePicture, RC_PICTURE_TAKEN);
//    }

    // this method gets called when you return from the camera application, with the picture included within
    // the data object
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        boolean smiling;
        if (requestCode == RC_PICTURE_TAKEN && resultCode == RESULT_OK) {
            // the newly taken photo is now stored in a Bitmap object
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            // handle the facial recognition etc. here
            FaceDetector detector = new FaceDetector.Builder(getApplicationContext())
                    .setTrackingEnabled(false)
                    .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                    .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                    .setProminentFaceOnly(true).build();

// Copy and create the SafeFaceDetector class from the link on the next page
            Detector<Face> safeDetector = new SafeFaceDetector(detector);

// Create a frame object from the bitmap and run face detection on the frame.
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<Face> faces = safeDetector.detect(frame);
            textV.setText(null);
            for (int index = 0; index < faces.size(); ++index) {
                Face face = faces.valueAt(index);
                textV.setText("Smiling probability" + String.valueOf(face.getIsSmilingProbability()) + "\n");
                if (face.getIsSmilingProbability() > SMILE_PROB) {
                    Intent intent = new Intent(this, Main2Activity.class);
                    startActivity(intent);

                }
// Number of faces detected (there should be only one with .setProminentFaceOnly(true)
                Log.d("sda", "faces detected: " + faces.size());
// Get the first face in the faces array (you might have to add a check here that the array has any faces!)

                //use the face object and its method to get the details, and launch a new activity if smile probability high enough …

// release the objects for reuse
                detector.release();
                bitmap.recycle();

            }
            if (requestCode == RC_PERMISSIONS && resultCode == RESULT_OK) {
                // restart the activity if you arrive here from the permission dialog
                Intent reboot = new Intent(this, MainActivity.class);
                startActivity(reboot);
            }
        }
    }
}
