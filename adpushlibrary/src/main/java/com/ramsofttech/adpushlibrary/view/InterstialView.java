package com.ramsofttech.adpushlibrary.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.ramsofttech.adpushlibrary.R;
import com.ramsofttech.adpushlibrary.adapter.InterstialAppAdapter;
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
 * Created by Ravi on 5/20/2015.
 */
public class InterstialView extends LinearLayout {
    private static final String TAG = "InterstialView";
    private Context context;
    private RecyclerView recyclerView;
    private Activity mActivity;
    private String nextPageToken = null;
    private boolean loading = true;
    private List<ApplicationData> applicationDataList;
    private InterstialAppAdapter adapter;
    private Dialog dialog;
    private ImageView close;
    int pastVisiblesItems, visibleItemCount, totalItemCount;


    public InterstialView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        MySingleton singleton = new MySingleton(this.context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.interstial_view, this, true);
        recyclerView = (RecyclerView) view.findViewById(R.id.recList);
        close = (ImageView) view.findViewById(R.id.close);

        recyclerView.setHasFixedSize(true);
        recyclerView.getItemAnimator().setAddDuration(1000);
        recyclerView.getItemAnimator().setRemoveDuration(1000);
        recyclerView.getItemAnimator().setMoveDuration(1000);
        recyclerView.getItemAnimator().setChangeDuration(1000);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        applicationDataList = new ArrayList<ApplicationData>();
        AppDataHolder dataHolder = ((AppDataHolder) context.getApplicationContext());
        dataHolder.setInterstialList(applicationDataList);
        EndpointsAsyncTask ed = new EndpointsAsyncTask();
        ed.execute();
        dialog = new Dialog(context, android.R.style.Theme_Light);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);


        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (nextPageToken != null) {
                    if (nextPageToken.length() > 0) {
                        EndpointsAsyncTask ed = new EndpointsAsyncTask();
                        ed.execute();
                    }
                }
            }
        });

        close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.isShowing())
                    dialog.dismiss();
            }
        });


    }

    public Activity getmActivity() {
        return mActivity;
    }

    /**
     * @param mActivity of type FragmentActivity
     * @return of type null
     * setter function for mActivity
     * @author rajeshcp
     * @since May 3, 2013
     */
    public void setmActivity(Activity mActivity) {
        this.mActivity = mActivity;
        adapter = new InterstialAppAdapter(applicationDataList, mActivity, recyclerView);
        recyclerView.setAdapter(adapter);

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

                CollectionResponseApplicationData collectionResponseApplicationData = myApiService.listbypkgname("3", appInfo.packageName).setCursor(nextPageToken).execute();
                apps = collectionResponseApplicationData.getItems();
                nextPageToken = collectionResponseApplicationData.getNextPageToken();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return apps;
        }

        @Override
        protected void onPostExecute(List<ApplicationData> result) {
            if (result != null) {
                AppDataHolder dataHolder = ((AppDataHolder) context.getApplicationContext());
                applicationDataList.addAll(result);
                dataHolder.setInterstialList(applicationDataList);
                adapter.notifyDataSetChanged();
                if (!dialog.isShowing())
                    dialog.show();


            }


        }
    }

}
