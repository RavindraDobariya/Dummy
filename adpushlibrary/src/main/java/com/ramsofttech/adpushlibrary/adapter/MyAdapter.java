package com.ramsofttech.adpushlibrary.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.ramsofttech.adpushlibrary.R;
import com.ramsofttech.adpushlibrary.backend.applicationDataApi.model.ApplicationData;
import com.ramsofttech.adpushlibrary.fragment.GridFragment;
import com.ramsofttech.adpushlibrary.utility.AppDataHolder;

import java.util.List;


public class MyAdapter extends FragmentStatePagerAdapter {


    static final int DEFAULT_NUM_FRAGMENTS = 1;
    static final int DEFAULT_NUM_ITEMS = 4;

    private List<ApplicationData> applicationList;
    private int mNumItems = 4;
    private int mNumFragments = 1;
    private Context context;


    /**
     * Return a new adapter.
     */

    public MyAdapter(FragmentManager fm,Context context, Resources res, List<ApplicationData> applicationList) {

        super(fm);
        this.context=context;
        this.applicationList = applicationList;
        mNumItems=applicationList.size();
        mNumFragments=applicationList.size()/4;

        setup(applicationList, res);
    }

    /**
     * Get the number of fragments  to be displayed in the ViewPager.
     */

    @Override
    public int getCount() {
        return mNumFragments;
    }

    /**
     * Return a new GridFragment that is used to display n items at the position given.
     *
     * @param position int - the position of the fragement; 0..numFragments-1
     */

    @Override
    public Fragment getItem(int position) {
        //Create a new Fragment and supply the fragment number, image position, and image count as arguments.
        // (This was how arguments were handled in the original pager example.)
        Bundle args = new Bundle();
        args.putInt("num", position + 1);
        args.putInt("firstImage", position * mNumItems);

        // The last page might not have the full number of items.
        int imageCount = mNumItems;
        if (position == (mNumFragments - 1)) {
            int numTopics = applicationList.size();
            int rem = numTopics % mNumItems;
            if (rem > 0) imageCount = rem;
        }
        args.putInt("imageCount", imageCount);


        // Return a new GridFragment object.
        GridFragment f = new GridFragment();
        f.setArguments(args);
        return f;
    }

    /**
     * Set up the adapter using information from a TopicList and resources object.
     * When this method completes, all the instance variables of the adapter are valid;
     *
     * @param tlist TopicList
     * @param res   Resources
     * @return void
     */

    void setup(List<ApplicationData> tlist, Resources res) {
        applicationList = tlist;

        if ((tlist == null) || (res == null)) {
            mNumItems = DEFAULT_NUM_ITEMS;
            mNumFragments = DEFAULT_NUM_FRAGMENTS;
        } else {
            int numTopics = applicationList.size();
            int numRows = res.getInteger(R.integer.grid_num_rows);
            int numCols = res.getInteger(R.integer.grid_num_cols);

            AppDataHolder dataHolder = ((AppDataHolder)context.getApplicationContext());
            if (dataHolder.getNumCols() != 0)
                numCols = dataHolder.getNumCols();
            if (dataHolder.getNumRows() != 0)
                numRows = dataHolder.getNumRows();
            int numTopicsPerPage = numRows * numCols;
            int numFragments = numTopics / numTopicsPerPage;
            if (numTopics % numTopicsPerPage != 0)
                numFragments++; // Add one if there is a partial page

            mNumFragments = numFragments;
            mNumItems = numTopicsPerPage;
        }

        Log.d("GridViewPager", "Num fragments, topics per page: " + mNumFragments + " " + mNumItems);

    } // end setup


}
