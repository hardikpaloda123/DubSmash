package com.dekhoapp.android.app.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Hardik on 8/1/2017.
 */
public class SongCategoryAdapter extends BaseAdapter {

    private List<String> mStrings;
    private Context mContext;

    public SongCategoryAdapter(Context mContext, List<String> stringList) {
        this.mStrings = stringList;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mStrings.size();
    }

    @Override
    public Object getItem(int i) {
        return mStrings.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mStrings.indexOf(i);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        MyViewHolder mViewHolder;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_row_item, viewGroup, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        String title = mStrings.get(position);

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



