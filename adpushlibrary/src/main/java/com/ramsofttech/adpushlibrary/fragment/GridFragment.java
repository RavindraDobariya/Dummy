package com.ramsofttech.adpushlibrary.fragment;

/*
 * Copyright (C) 2012 Wglxy.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.ramsofttech.adpushlibrary.R;
import com.ramsofttech.adpushlibrary.adapter.AppAdapter;
import com.ramsofttech.adpushlibrary.backend.applicationDataApi.model.ApplicationData;
import com.ramsofttech.adpushlibrary.backend.clickApi.ClickApi;
import com.ramsofttech.adpushlibrary.backend.clickApi.model.Click;
import com.ramsofttech.adpushlibrary.utility.AppDataHolder;
import com.ramsofttech.adpushlibrary.utility.Constant;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * A fragment that shows a grid of topics. For each topic, the title and its image are shown.
 */

public class GridFragment extends Fragment {

    int mNum;                   // the id number assigned to this fragment
    int mFirstImage = 0;        // the index number of the first image to show in the fragment
    int mImageCount = -1;        // the number of images to show in the fragment
    // the list of topics used by the fragment

    /**
     * onCreate
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read the arguments and check resource values for number of rows and number of columns
        // so we know how many images to display on this fragment.

        Bundle args = getArguments();
        mNum = ((args != null) ? args.getInt("num") : 0);

        if (args != null) {

            mFirstImage = args.getInt("firstImage");
        }
        // mImageCount = ((args != null) ? args.getInt ("imageCount") : -1);

        // Recalculate image count and then set mFirstImage to the first page that
        // includes the old first image. We recalculate the image count because it might change
        // if we are reorienting from portrait to landscape.
        Resources res = getActivity().getResources();

        int numRows = res.getInteger(R.integer.grid_num_rows);
        int numCols = res.getInteger(R.integer.grid_num_cols);

        AppDataHolder dataHolder = ((AppDataHolder) getActivity().getApplicationContext());
        if (dataHolder.getNumCols() != 0)
            numCols = dataHolder.getNumCols();
        if (dataHolder.getNumRows() != 0)
            numRows = dataHolder.getNumRows();


        int numTopicsPerPage = numRows * numCols;
        mImageCount = numTopicsPerPage;

        mFirstImage = (mFirstImage / numTopicsPerPage) * numTopicsPerPage;   // What happens as you re-orient? Might need to do better here.

    }

    /**
     * onActivityCreated
     * When the activity is created, divide the usable space into columns
     * and put a grid of images in that area.
     */

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Activity a = getActivity();
        Resources res = a.getResources();

        View rootView = getView();
        GridView gridview = (GridView) rootView.findViewById(R.id.grid);

        DisplayMetrics metrics = new DisplayMetrics();
        a.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        // From the resource files, determine how many rows and columns are to be displayed.
         int numRows = res.getInteger(R.integer.grid_num_rows);
         int numCols = res.getInteger(R.integer.grid_num_cols);
        AppDataHolder dataHolder = ((AppDataHolder) getActivity().getApplicationContext());
        if (dataHolder.getNumCols() != 0)
            numCols = dataHolder.getNumCols();
        if (dataHolder.getNumRows() != 0)
            numRows = dataHolder.getNumRows();
        // Figure out how much space is available for the N rows and M columns to be displayed.
        // We start with the root view for the fragment and adjust for the title, padding, etc.
        int titleHeight = res.getDimensionPixelSize(R.dimen.topic_title_height);
        int titlePadding = res.getDimensionPixelSize(R.dimen.topic_title_padding);
        int buttonAreaHeight = res.getDimensionPixelSize(R.dimen.button_area_height);
        int titleBarHeight = res.getDimensionPixelSize(R.dimen.title_bar_height);
        int gridHspacing = res.getDimensionPixelSize(R.dimen.image_grid_hspacing);
        int gridVSpacing = res.getDimensionPixelSize(R.dimen.image_grid_vspacing);
        int otherGridH = res.getDimensionPixelSize(R.dimen.other_grid_h);
        int otherGridW = res.getDimensionPixelSize(R.dimen.other_grid_w);
        int heightUsed = 2 * titleBarHeight + (numRows + 2) * gridVSpacing + (titleHeight + 2 * titlePadding)
                + otherGridH + buttonAreaHeight;

        int widthUsed = 40;           // just a guess for now.
        int availableHeight = metrics.heightPixels - heightUsed;
        int availableWidth = metrics.widthPixels - widthUsed;
        int cellWidth = availableWidth / numCols;
        if (availableWidth > 1000) {
            cellWidth -= 20;
        }

        float cellHeight = numRows * (availableHeight / numRows) / 6;
        if(dataHolder.getCellHeight()!=0)
        {
            cellHeight=numRows * (availableHeight / numRows) / dataHolder.getCellHeight();
        }

        // Put this back in to check the calculations for cell height and width.
        Log.d("Debug", "--- metrics h: " + metrics.heightPixels + "  w: " + metrics.widthPixels);
        Log.d("Debug", "    available h: " + availableHeight + "  w: " + availableWidth);
        Log.d("Debug", "    already used h: " + heightUsed + "  w: " + widthUsed);
        Log.d("Debug", "    cell h: " + cellHeight + "  w: " + cellWidth);
        Log.d("Debug", "--- num rows: " + numRows + "  cols: " + numCols);
        Log.d("Debug", "--- firstImage: " + mFirstImage + " image count: " + mImageCount);


        if (gridview == null) Log.d("DEBUG", "Unable to locate the gridview.");
        else {
            Log.i("Adapter", "--inside call appadapter");
          //  AppDataHolder dataHolder = ((AppDataHolder) getActivity().getApplication());
            gridview.setNumColumns(numCols);
            // Connect the gridview with an adapter that fills up the space.
            //     gridview.setAdapter (new GridImageAdapter (a, mTopicList, mFirstImage, mImageCount, cellWidth, cellHeight));
            gridview.setAdapter(new AppAdapter(a, dataHolder.getApplicationList(), mFirstImage, mImageCount, cellWidth, cellHeight));

            // Arrange it so a long click on an item in the grid shows the topic associated with the image.
            gridview.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                        long arg3) {
                    // TODO Auto-generated method stub
                    //showTopic(mFirstImage + position);
                    Toast.makeText(getActivity(), "" + (mFirstImage + position), Toast.LENGTH_LONG).show();

                    clickAsyncTask clickAsynctask = new clickAsyncTask(getActivity());
                    clickAsynctask.execute("" + (mFirstImage + position));

                    AppDataHolder dataHolder = ((AppDataHolder) getActivity().getApplication());
                    List<ApplicationData> applicationList = dataHolder.getApplicationList();
                    ApplicationData application = applicationList.get(mFirstImage + position);
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + application.getPackageName())));
                }
            });

        }
    }

    /**
     * onCreateView
     * Build the view that shows the grid.
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Build the view that shows the grid.
        View view = inflater.inflate(R.layout.gridview, container, false);

        GridView gridview = (GridView) view.findViewById(R.id.grid);
//        gridview.setTag(new Integer(mNum));
//
//        // Set label text for the view
//        View tv = view.findViewById(R.id.text);
//        if (tv != null) {
//            ((TextView) tv).setText("Topics " + mNum);
//        }
//
//        // Hide the "no items" content until it is needed.
//        View nc = view.findViewById(R.id.no_topics_text);
//        if (nc != null) {
//            nc.setVisibility(View.INVISIBLE);
//        }

        return view;
    }

    /**
     * showTopic - start a GridImageActivity object to display the nth topic.
     *
     * @param index int - the index number of the topic to display
     */
    class clickAsyncTask extends AsyncTask<String, Void, String> {
        private ClickApi regService = null;

        private Context context;

        // TODO: change to your own sender ID to Google Developers Console project number, as per instructions above


        public clickAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            Integer index = Integer.parseInt(params[0]);
            if (regService == null) {
                ClickApi.Builder builder = new ClickApi.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        // Need setRootUrl and setGoogleClientRequestInitializer only for local testing,
                        // otherwise they can be skipped
                        //.setRootUrl("https://ramsofttect-cpn.appspot.com/_ah/api/")
                        .setRootUrl(Constant.BASE_URL)
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest)
                                    throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        });
                // end of optional local run code

                regService = builder.build();
            }


            // You should send the registration ID to your server over HTTP,
            // so it can use GCM/HTTP or CCS to send messages to your app.
            // The request to your server should be authenticated if your app
            // is using accounts.

            try {
                AppDataHolder dataHolder = ((AppDataHolder) getActivity().getApplication());
                List<ApplicationData> applicationList = dataHolder.getApplicationList();
                ApplicationData application = applicationList.get(index);

                TelephonyManager manager = (TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE);
                String country_code = manager.getNetworkCountryIso();

                if (country_code.trim().length() == 0) {
                    country_code = Locale.getDefault().getCountry();

                    if (country_code.trim().length() == 0)
                        country_code = "other";
                }

                Log.i("--country code", country_code);


                ApplicationInfo appInfo = context.getApplicationInfo();

                Log.i("--PackageName", "nature---" + appInfo.packageName);
                String packageName = appInfo.packageName;

                Click click = new Click();
                click.setCount(1);
                click.setFrom(packageName);
                click.setTo(application.getPackageName());
                click.setCountry(country_code);
                click.setAdtype(1);

                regService.insert(click).execute();

            } catch (IOException ex) {
                ex.printStackTrace();

            }
            return "success";
        }

        @Override
        protected void onPostExecute(String msg) {

            Logger.getLogger("REGISTRATION").log(Level.INFO, msg);
        }
    }

} // end class GridFragment
