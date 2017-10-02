package com.dekhoapp.android.app.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class SongListFragment extends Fragment {

    public static final String SONG_TYPE_KEY = "SONG_TYPE_KEY";
    public final static int CAMERA_REQUEST_CODE = 1000;
    private static final String TAG = SongListFragment.class.getSimpleName();
    private static final int SONG_TYPE_HINDI = 0;
    private static final int SONG_TYPE_ENGLISH = 1;
    private static final int SONG_TYPE_MELODY = 2;

    private static final String HINDI_SONGS_PATH = "Hardik/Songs/Hindi";
    private static final String ENGLISH_SONGS_PATH = "Hardik/Songs/English";
    private static final String MELODY_SONGS_PATH = "Hardik/Songs/Melody";

    List<SongDataItem> mSongDataList = new ArrayList<>();
    SongListAdapter mSongListAdapter;
    private Context mContext;
    private ListView mSongListView;
    private int mSongCategory = -1;
    private DatabaseReference mDatabase;
    private AdapterView.OnItemClickListener mItemClickListener;
    private ProgressDialog progressDialog;
    private ValueEventListener mValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Log.d(TAG, "onDataChange: dataSnapshot = " + dataSnapshot);
            //clearing the previous artist list
            mSongDataList.clear();
            //iterating through all the nodes
            Log.d(TAG, "dataSnapshot size = " + dataSnapshot.getChildrenCount());

            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                //getting songDataItem
                Log.d(TAG, "postSnapshot = " + postSnapshot);
                String key = postSnapshot.getKey();
                Log.d(TAG, "key = " + key);
                SongDataItem songDataItem = postSnapshot.getValue(SongDataItem.class);

                Log.d(TAG, "songDataItem Name = " + songDataItem.getSongName());
                Log.d(TAG, "songDataItem Url = " + songDataItem.getSongUrl());

                //adding songDataItem to the list
                mSongDataList.add(songDataItem);
            }

            Log.d(TAG, "mSongDataList size = " + mSongDataList.size());

            if (mSongDataList.size() > 0) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                //creating adapter
                mSongListAdapter = new SongListAdapter(mContext, mSongDataList);
                //attaching adapter to the listview
                mSongListView.setAdapter(mSongListAdapter);
                //
            } else {
                Toast.makeText(mContext, "No Songs Found",
                        Toast.LENGTH_LONG).show();
            }


        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.d(TAG, "onCancelled: databaseError = " + databaseError.getDetails());

            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            Toast.makeText(mContext, "Error Occured while retrieving songs",
                    Toast.LENGTH_LONG).show();

        }
    };

    {
        mItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long idl) {
                Log.d(TAG, "onItemClick : position = " + position);
                // Toast.makeText(mContext, mListStrings.get(position) + " clicked ", Toast.LENGTH_LONG).show();
                String songurl = mSongDataList.get(position).getSongUrl();

                MediaPlayerManager.getInstance(mContext).playSong(songurl);

                Intent recordActivityIntent = new Intent(mContext, RecordingActivity.class);
                recordActivityIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(recordActivityIntent);
                //getActivity().startActivityForResult(recordActivityIntent,CAMERA_REQUEST_CODE);

                Log.d(TAG, "activity started successfully");

            }

        };
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        mContext = getActivity().getApplicationContext();
        setRetainInstance(true);

        Bundle b = getArguments();
        mSongCategory = b.getInt(SONG_TYPE_KEY, -1);
        Log.d(TAG, "mSongCategory = " + mSongCategory);

        Log.d(TAG, "data base path = " + getStoragePath(mSongCategory));

        mDatabase = FirebaseDatabase.getInstance().getReference(getStoragePath(mSongCategory));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_song_list, container, false);
        mSongListView = (ListView) view.findViewById(R.id.song_list_view);
        mSongListView.setOnItemClickListener(mItemClickListener);
        return view;
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
        Log.d(TAG, "mDatabase  = " + mDatabase);
        Log.d(TAG, "mDatabase key = " + mDatabase.getKey());
        Log.d(TAG, "mDatabase.child(mDatabase.getKey()) = " + mDatabase.child(mDatabase.getKey()));

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Fetching Songs...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);

        progressDialog.show();
        mDatabase.addValueEventListener(mValueEventListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }


}



