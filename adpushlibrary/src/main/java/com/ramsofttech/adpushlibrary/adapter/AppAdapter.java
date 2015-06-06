package com.ramsofttech.adpushlibrary.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.ramsofttech.adpushlibrary.R;
import com.ramsofttech.adpushlibrary.backend.applicationDataApi.model.ApplicationData;
import com.ramsofttech.adpushlibrary.utility.MySingleton;
import com.ramsofttech.adpushlibrary.utility.SelectableRoundedImageView;

import java.util.List;

/**
 * Created by Ravi on 5/12/2015.
 */
public class AppAdapter extends BaseAdapter {
    public static final int DEFAULT_CELL_SIZE = 220; // default value to use for
    // image width and
    // height.
    private Activity context;
    private List<ApplicationData> applicationList;
    private ImageLoader mImageLoader;
    private int mImageOffset = 0; // the index offset into the list of images
    private int mImageCount = -1; // -1 means that we display all images
    private int mNumTopics = 0; // the total number of topics in the topics
    // collection
    private int mCellWidth = DEFAULT_CELL_SIZE;
    private float mCellHeight = DEFAULT_CELL_SIZE;

    public AppAdapter(Activity context, List<ApplicationData> applicationList, int imageOffset,
                      int imageCount, int cellWidth, float cellHeight) {
        this.mCellWidth = cellWidth;
        this.mCellHeight = cellHeight;
        this.mImageOffset = imageOffset;
        this.context = context;
        this.mImageCount = imageCount;
        this.applicationList = applicationList;
        this.mNumTopics = applicationList.size();
        mImageLoader = MySingleton.getInstance(context).getImageLoader();
    }

    @Override
    public int getCount() {
        int count = mImageCount;
        if (mImageOffset + mImageCount >= mNumTopics)
            count = mNumTopics - mImageOffset;
        Log.i("Adapter", "--count" + count);
        return count;
    }

    @Override
    public ApplicationData getItem(int position) {
        return applicationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mImageOffset + position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View rowView;
        int numTopics = applicationList.size();
        LayoutInflater inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rowView = inflater.inflate(R.layout.gridview_item, null);
        rowView.setLayoutParams(new GridView.LayoutParams(mCellWidth, (int)mCellHeight));
        holder.appName = (TextView) rowView.findViewById(R.id.appname);
        holder.appIcon = (SelectableRoundedImageView) rowView.findViewById(R.id.appicon);
        Log.i("Adapter", "--inside appadapter");
        int j = position + mImageOffset;
        if (j < 0)
            j = 0;
        if (j >= numTopics)
            j = numTopics - 1;

        ApplicationData application = getItem(j);
        holder.appName.setText(application.getApplicationName());
        holder.appIcon.setImageUrl("https://ramsofttech-marketing.appspot.com/upload?blob-key=" + application.getAppIcon(), mImageLoader);


        return rowView;
    }

    public class Holder {
        private TextView appName;
        private SelectableRoundedImageView appIcon;
    }
}
