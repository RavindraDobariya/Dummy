package com.ramsofttech.adpushlibrary.view;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.ramsofttech.adpushlibrary.R;
import com.ramsofttech.adpushlibrary.adapter.MyAdapter;
import com.ramsofttech.adpushlibrary.backend.applicationDataApi.ApplicationDataApi;
import com.ramsofttech.adpushlibrary.backend.applicationDataApi.model.ApplicationData;
import com.ramsofttech.adpushlibrary.backend.applicationDataApi.model.CollectionResponseApplicationData;
import com.ramsofttech.adpushlibrary.utility.AppDataHolder;
import com.ramsofttech.adpushlibrary.utility.Constant;
import com.ramsofttech.adpushlibrary.utility.MySingleton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ravi on 5/9/2015.
 */
public class AdView extends LinearLayout {

    private static final String TAG = "AdView";
    private Context context;
    private ViewPager viewPager;

    private FragmentManager mActivity;

    /**
     * @param of type null
     * @return mActivity of type FragmentActivity
     * getter function for mActivity
     * @author rajeshcp
     * @since May 3, 2013
     */
    public FragmentManager getmActivity() {
        return mActivity;
    }

    /**
     * @param mActivity of type FragmentActivity
     * @return of type null
     * setter function for mActivity
     * @author rajeshcp
     * @since May 3, 2013
     */
    public void setmActivity(FragmentManager mActivity) {
        this.mActivity = mActivity;

    }

//    public void setNumRows(int numRows) {
//        AppDataHolder dataHolder = ((AppDataHolder) context.getApplicationContext());
//        dataHolder.setNumRows(numRows);
//    }
//
//    public void setNumCols(int numCols) {
//        AppDataHolder dataHolder = ((AppDataHolder) context.getApplicationContext());
//        dataHolder.setNumCols(numCols);
//    }

    public AdView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        MySingleton singleton = new MySingleton(this.context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.ad_grid_viewpager, this, true);
        viewPager = (ViewPager) view.findViewById(R.id.pager);
        if (attrs == null) {
            return; // quick exit
        }
        AppDataHolder dataHolder = ((AppDataHolder) context.getApplicationContext());
        List<ApplicationData> applicationDataList = new ArrayList<ApplicationData>();
        dataHolder.setApplicationList(applicationDataList);
        TypedArray a = null;
        try {
            a = getContext().obtainStyledAttributes(attrs, R.styleable.AdView);
            dataHolder.setNumCols(a.getInt(R.styleable.AdView_numCols, 4));
            dataHolder.setNumRows(a.getInt(R.styleable.AdView_numRows, 1));
            dataHolder.setCellHeight(a.getFloat(R.styleable.AdView_cellHeight,6));
        } finally {
            if (a != null) {
                a.recycle(); // ensure this is always called
            }
        }

        EndpointsAsyncTask ed = new EndpointsAsyncTask();
        ed.execute();

    }

    class EndpointsAsyncTask extends AsyncTask<Void, Void, List<ApplicationData>> {
        private ApplicationDataApi myApiService = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected List<ApplicationData> doInBackground(Void... params) {
            List<ApplicationData> apps = new ArrayList<ApplicationData>();
            if (myApiService == null) {  // Only do this once
                ApplicationDataApi.Builder builder = new ApplicationDataApi.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        // options for running against local devappserver
                        // - 10.0.2.2 is localhost's IP address in Android emulator
                        // - turn off compression when running against local devappserver
                        .setRootUrl(Constant.BASE_URL)
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        });
                // end options for devappserver

                myApiService = builder.build();
            }

            try {
                ApplicationInfo appInfo = context.getApplicationInfo();
                CollectionResponseApplicationData collectionResponseApplicationData = myApiService.listbypkgname("1", appInfo.packageName).execute();
                apps = collectionResponseApplicationData.getItems();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return apps;
        }

        @Override
        protected void onPostExecute(List<ApplicationData> result) {
            if (result != null) {
                AppDataHolder dataHolder = ((AppDataHolder) context.getApplicationContext());
                dataHolder.setApplicationList(result);
                Resources res = getResources();
                MyAdapter adapter = new MyAdapter(mActivity, context, res, result);
                // AppAdapter appAdapter=new AppAdapter(getContext(),result);
                viewPager.setAdapter(adapter);
            }


        }
    }
}
