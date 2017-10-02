package com.dekhoapp.android.app.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;


public class SongListActivity extends AppCompatActivity {

    private static final String TAG = SongListActivity.class.getSimpleName() + "_LOG";
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        mContext = this.getApplicationContext();
        setContentView(R.layout.activity_song_list);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        SongCategoryFragment songCategoryFragment = new SongCategoryFragment();

        fragmentTransaction.replace(R.id.fragmentContainer, songCategoryFragment);

        fragmentTransaction.commit();

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: requestCode = " + requestCode + "resultCode = " + resultCode);

        if (requestCode == SongListFragment.CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String filePath = data.getData().getPath();
                Log.d(TAG, "file path = " + filePath);
                final File file = new File(data.getData().getPath());

            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


}
