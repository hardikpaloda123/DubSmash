package com.dekhoapp.android.app.base;



import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.afollestad.materialcamera.MaterialCamera;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.DecimalFormat;

public class RecordingActivity extends BaseActivity {

    private static final String TAG = RecordingActivity.class.getSimpleName();

    private final static int CAMERA_RQ = 6969;
    private final static int PERMISSION_RQ = 84;

    private static final String VIDEO_STORAGE_PATH = "Hardik/DubsMashVideos/";

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private Button recordButton;
    private Context mContext;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getApplicationContext();
        setContentView(R.layout.recording_activity);
        recordButton = (Button) findViewById(R.id.btn_recording);
        recordButton.setOnClickListener(mClickListener);

        firebaseStorage = FirebaseStorage.getInstance();//Used to access from the firebaseStorage
        storageReference = firebaseStorage.getReference();
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d(TAG,"onClick");
            if(view.getId() == R.id.btn_recording){
                Log.d(TAG,"Recording Button Clicked");
                if (ContextCompat.checkSelfPermission(RecordingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // Request permission to save videos in external storage
                    ActivityCompat.requestPermissions(RecordingActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_RQ);
                }

                File saveDir = null;

                if (ContextCompat.checkSelfPermission(RecordingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // Only use external storage directory if permission is granted, otherwise cache directory is used by default
                    saveDir = new File(Environment.getExternalStorageDirectory(), "DekhoApp");
                    saveDir.mkdirs();
                }

                MaterialCamera materialCamera = new MaterialCamera(RecordingActivity.this)
                        .saveDir(saveDir)
                        .showPortraitWarning(false)
                        .allowRetry(true)
                        .defaultToFrontFacing(true)
                        .allowRetry(true)
                        .autoSubmit(false)
                        .videoPreferredAspect(16f / 9f)
                        .primaryColorAttr(R.attr.cardBackgroundColor)
                        .labelConfirm(R.string.mcam_use_video)
                        .qualityProfile(MaterialCamera.QUALITY_480P)
                        .videoFrameRate(30)
                        .maxAllowedFileSize(1024 * 1024 * 15) // 15Mb max file size
                        .countdownMinutes(1f)
                        ;
                materialCamera.start(CAMERA_RQ);
                Log.d(TAG, "material Camera started succesfully ");
            }
        }
    };


    private String readableFileSize(long size) {
        if (size <= 0) return size + " B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    private String fileSize(File file) {
        return readableFileSize(file.length());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Received recording or error from MaterialCamera
        if (requestCode == CAMERA_RQ) {
            if (resultCode == RESULT_OK) {
                final File file = new File(data.getData().getPath());
                Toast.makeText(this, String.format("Saved to: %s, size: %s",
                        file.getAbsolutePath(), fileSize(file)), Toast.LENGTH_LONG).show();
                setResult(RESULT_OK,data);

                // Stop the Song
                MediaPlayerManager.getInstance(mContext).stopSong();

                Uri selectedVideoUri = data.getData();
                uploadData(selectedVideoUri,selectedVideoUri.getLastPathSegment());

                Log.d(TAG,"recordingActivityUri :"+selectedVideoUri);
                //Intent viewCreatedVideo = new Intent(this, ViewCreatedVideo.class);
                //viewCreatedVideo.putExtra("PUT_EXTRA",selectedVideoUri.toString());
                //startActivity(viewCreatedVideo);


            } else if (data != null) {
                Exception e = (Exception) data.getSerializableExtra(MaterialCamera.ERROR_EXTRA);
                if (e != null) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    private void uploadData(Uri filePathUri, final String fileName) {
        Log.d(TAG, "uploadData: filePathUri = " + filePathUri + "fileName = " + fileName);
        String storagePath = VIDEO_STORAGE_PATH;
        Log.d(TAG, "storagePath = " + storagePath);
        StorageReference filestorageReference = storageReference.child(storagePath + fileName);

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("video/mp4")
                .build();
        
        progressDialog = new ProgressDialog(RecordingActivity.this);
        progressDialog.setMessage("Uploading Video...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);

        progressDialog.show();



        UploadTask uploadTask = filestorageReference.putFile(filePathUri, metadata);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "onFailure : exception = " + exception.getMessage());
                if(progressDialog!=null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                Toast.makeText(RecordingActivity.this, "Video Upload Failed",
                        Toast.LENGTH_LONG).show();
                // This finishes this activity after execution and removes it from back stack
                finish();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "onSuccess : taskSnapshot = " + taskSnapshot);

                if(progressDialog!=null && progressDialog.isShowing()){

                    progressDialog.dismiss();
                }
                Toast.makeText(RecordingActivity.this, "Video Upload Successful",
                        Toast.LENGTH_LONG).show();
                // This finishes this activity after execution and removes it from back stack
                finish();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            // Sample was denied WRITE_EXTERNAL_STORAGE permission
            Toast.makeText(this, "Videos will be saved in a cache directory instead of an external storage directory since permission was denied.", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
       Log.d(TAG,"onPause");
        // MediaPlayerManager.getInstance(mContext).stopSong();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG,"onBackPressed");
        MediaPlayerManager.getInstance(mContext).stopSong();
        finish();
    }


}
