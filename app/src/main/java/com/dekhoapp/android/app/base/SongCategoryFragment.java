package com.dekhoapp.android.app.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hardik on 8/1/2017.
 */
public class SongCategoryFragment extends Fragment {

    private static final String TAG = SongCategoryFragment.class.getSimpleName();

    private Context mContext;
    private ListView mCategoryListView;
    private SongCategoryAdapter mListAdapter;
    private List<String> mListStrings;
    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long idl) {
            Log.d(TAG, "onItemClick : position = " + position);
            //Toast.makeText(mContext, mListStrings.get(position) + " clicked ", Toast.LENGTH_LONG).show();

            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

            SongListFragment songListFragment = new SongListFragment();

            Bundle bundle = new Bundle();
            bundle.putInt(SongListFragment.SONG_TYPE_KEY,position);

            songListFragment.setArguments(bundle);

            fragmentTransaction.replace(R.id.fragmentContainer, songListFragment);

            fragmentTransaction.addToBackStack(null);

            fragmentTransaction.commit();
        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        mContext = getActivity().getApplicationContext();
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_song_category, container, false);

        mCategoryListView = (ListView) view.findViewById(R.id.category_listView);

        makeListData();
        mListAdapter = new SongCategoryAdapter(mContext, mListStrings);
        mCategoryListView.setAdapter(mListAdapter);
        mCategoryListView.setOnItemClickListener(mItemClickListener);

        return view;
    }

    private void makeListData() {
        Log.d(TAG, "makeListData");
        mListStrings = new ArrayList<>();
        mListStrings.add("Hindi Songs");
        mListStrings.add("English Songs");
        mListStrings.add("Melody Songs");

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

}
