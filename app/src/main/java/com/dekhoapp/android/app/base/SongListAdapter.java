package com.dekhoapp.android.app.base;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Hardik on 8/1/2017.
 */
public class SongListAdapter extends BaseAdapter {

    private static final String TAG = SongListAdapter.class.getSimpleName() ;

    private List<SongDataItem> songDataItems;
    private Context mContext;

    public SongListAdapter(Context mContext, List<SongDataItem> songDataItems) {
        this.mContext = mContext;
        this.songDataItems = songDataItems;
    }

    @Override
    public int getCount() {
        return songDataItems.size();
    }

    @Override
    public Object getItem(int i) {
        return songDataItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return songDataItems.indexOf(i);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        Log.d(TAG,"getView: position = " + position);
        MyViewHolder mViewHolder;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_row_item, viewGroup, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        String title = songDataItems.get(position).getSongName();
        Log.d(TAG,"title = " + title);
        mViewHolder.primaryText.setText(title);

        return convertView;
    }

    private class MyViewHolder {
        TextView primaryText;

        public MyViewHolder(View item) {
            primaryText = (TextView) item.findViewById(R.id.primary_text);
        }
    }
}



