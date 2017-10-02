package com.dekhoapp.android.app.base;


import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;



import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadActivity extends AppCompatActivity  {

    private static final String TAG = UploadActivity.class.getSimpleName() + "_LOG";

    private static final int FILE_SELECT_REQUEST_CODE = 100;
    private static final int SONG_TYPE_HINDI = 0;
    private static final int SONG_TYPE_ENGLISH = 1;
    private static final int SONG_TYPE_MELODY = 2;
    private static final String HINDI_SONGS_PATH = "Hardik/Songs/Hindi/";
    private static final String ENGLISH_SONGS_PATH = "Hardik/Songs/English/";
    private static final String MELODY_SONGS_PATH = "Hardik/Songs/Melody/";
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private DatabaseReference mDatabaseReference;
    private Button buttonHindiUpload;
    private Button buttonEnglishUpload;
    private Button buttonMelodyUpload;
    private Button viewSongList;

    ProgressDialog progressDialog;

    private int selectedSongType = -1;
    private View.OnClickListener btnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d(TAG, "onClick");

            switch (view.getId()) {
                case R.id.buttonChooseHindi:
                    selectedSongType = SONG_TYPE_HINDI;
                    break;
                case R.id.buttonChooseEnglish:
                    selectedSongType = SONG_TYPE_ENGLISH;
                    break;
                case R.id.buttonChooseMelody:
                    selectedSongType = SONG_TYPE_MELODY;
                    break;
                case R.id.showSongsList:
                    Intent i = new Intent(UploadActivity.this, SongListActivity.class);
                    startActivity(i);
                    return;
            }
            Log.d(TAG, "selectedSongType = " + selectedSongType);
            showFileChooser();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        firebaseStorage = FirebaseStorage.getInstance();//Used to access from the firebaseStorage
        storageReference = firebaseStorage.getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        setContentView(R.layout.activity_upload_music);

        //getting views from layout
        buttonHindiUpload = (Button) findViewById(R.id.buttonChooseHindi);
        buttonEnglishUpload = (Button) findViewById(R.id.buttonChooseEnglish);
        buttonMelodyUpload = (Button) findViewById(R.id.buttonChooseMelody);
        viewSongList = (Button) findViewById(R.id.showSongsList);
        //attaching listener
        buttonHindiUpload.setOnClickListener(btnClickListener);
        buttonEnglishUpload.setOnClickListener(btnClickListener);
        buttonMelodyUpload.setOnClickListener(btnClickListener);
        viewSongList.setOnClickListener(btnClickListener);
    }

    private String getStoragePath(int songType) {
        Log.d(TAG, "getStoragePath: songType = " + songType);
        switch (songType) {
            case SONG_TYPE_HINDI:
                return HINDI_SONGS_PATH;

            case SONG_TYPE_ENGLISH:
                return ENGLISH_SONGS_PATH;

            case SONG_TYPE_MELODY:
                return MELODY_SONGS_PATH;

        }
        return "";
    }

    private void showFileChooser() {
        Log.d(TAG, "showFileChooser");

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

        intent.setType("audio/mpeg");

        intent.addCategory(Intent.CATEGORY_OPENABLE);



        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to UploadActivity"),
                    FILE_SELECT_REQUEST_CODE);

        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: requestCode : " + requestCode + "resultCode = " + resultCode);
        switch (requestCode) {
            case FILE_SELECT_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected realFileUri
                    Uri fileUri = data.getData();
                    Log.d(TAG, "File Uri: " + fileUri.toString());
                    Log.d(TAG, "fileUri.getPath()= " + fileUri.getPath());
                    Log.d(TAG, "fileUri.getLastPathSegment() = " + fileUri.getLastPathSegment());

                    String realfileName = getRealNameFromURI(fileUri);

                    if (realfileName != null) {
                        Log.d(TAG, "realfileName = " + realfileName);
                        uploadData(fileUri, realfileName);
                    } else {
                        Log.e(TAG, "realFileName is null.. uplaod with name unknown");
                        uploadData(fileUri, "unknown");

                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public String getRealNameFromURI(Uri contentUri) {
        Log.d(TAG, "getRealNameFromURI");
        String result = null;
        Log.d(TAG, "Uri Scheme = " + contentUri.getScheme());
        if (contentUri.getScheme().equalsIgnoreCase("content")) {
            Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        } else if (contentUri.getScheme().equalsIgnoreCase("file")) {
            result = contentUri.getLastPathSegment();
        } else {
            Log.d(TAG, "Unknown scheme");
        }
        return result;
    }

    private void uploadData(Uri filePathUri, final String fileName) {

        Log.d(TAG, "uploadData: filePathUri = " + filePathUri + "fileName = " + fileName);

        String storagePath = getStoragePath(selectedSongType);
        Log.d(TAG, "storagePath = " + storagePath);
        StorageReference filestorageReference = storageReference.child(storagePath + fileName);



        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("audio/mpeg")
                .build();

        progressDialog = new ProgressDialog(UploadActivity.this);
        progressDialog.setMessage("Uploading Song...");
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
                Toast.makeText(UploadActivity.this, "Song Upload Failed",
                        Toast.LENGTH_LONG).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if(progressDialog!=null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                Log.d(TAG, "onSuccess : taskSnapshot = " + taskSnapshot);
                // taskSnapshot.getMetadata() contains fileUri metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                String databaseUrl = getStoragePath(selectedSongType);
                Toast.makeText(UploadActivity.this, "Song Successfully Uploaded",
                        Toast.LENGTH_LONG).show();
                Log.d(TAG, "onComplete : upload" + downloadUrl);
                SongDataItem songDataItem = new SongDataItem();
                songDataItem.setSongUrl(downloadUrl.toString());
                songDataItem.setSongName(fileName);

                DatabaseReference databaseReference = mDatabaseReference.child(getStoragePath(selectedSongType));
                String userId = databaseReference.push().getKey();
                databaseReference.child(userId).setValue(songDataItem);
                Log.d(TAG, "databaseReference:  " + databaseReference + " databaseUrl: " + databaseUrl + " valuesToBeSet :" + songDataItem);
            }
        });
    }

}
