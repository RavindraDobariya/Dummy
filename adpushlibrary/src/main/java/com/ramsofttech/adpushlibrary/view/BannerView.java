package com.ramsofttech.adpushlibrary.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.ramsofttech.adpushlibrary.R;
import com.ramsofttech.adpushlibrary.backend.applicationDataApi.ApplicationDataApi;
import com.ramsofttech.adpushlibrary.backend.applicationDataApi.model.ApplicationData;
import com.ramsofttech.adpushlibrary.backend.clickApi.ClickApi;
import com.ramsofttech.adpushlibrary.backend.clickApi.model.Click;
import com.ramsofttech.adpushlibrary.utility.AppDataHolder;
import com.ramsofttech.adpushlibrary.utility.Constant;
import com.ramsofttech.adpushlibrary.utility.MySingleton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Ravi on 5/18/2015.
 */
public class BannerView extends LinearLayout {
    private static final String TAG = "BannerView";
    private Context context;
    private NetworkImageView bannerView;
    private ImageLoader mImageLoader;
    private Activity mActivity;
    private int currentPos;

    /**
     * @return mActivity of type FragmentActivity
     * getter function for mActivity
     * @author rajeshcp
     * @since May 3, 2013
     */
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

    }


    public BannerView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        mImageLoader = MySingleton.getInstance(context).getImageLoader();
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.banner, this, true);
        bannerView = (NetworkImageView) view.findViewById(R.id.banner);
        Toast.makeText(context, "inside bannerview", Toast.LENGTH_LONG).show();
        List<ApplicationData> applicationDataList = new ArrayList<ApplicationData>();
        AppDataHolder dataHolder = ((AppDataHolder) context.getApplicationContext());
        dataHolder.setBannerList(applicationDataList);
        EndpointsAsyncTask ed = new EndpointsAsyncTask();
        ed.execute();

        bannerView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clickAsyncTask clickAsynctask = new clickAsyncTask(mActivity);
                clickAsynctask.execute("" + (currentPos));

                AppDataHolder dataHolder = ((AppDataHolder) mActivity.getApplication());
                List<ApplicationData> applicationList = dataHolder.getBannerList();
                ApplicationData application = applicationList.get(currentPos);
                mActivity.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" + application.getPackageName())));
            }
        });

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
                apps = myApiService.listbypkgname("2", appInfo.packageName).execute().getItems();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return apps;
        }

        @Override
        protected void onPostExecute(final List<ApplicationData> result) {
            if (result != null) {
                if (result.size() > 0) {
                    AppDataHolder dataHolder = ((AppDataHolder) context.getApplicationContext());
                    dataHolder.setBannerList(result);

                    //   bannerView.setImageUrl("https://ramsofttech-marketing.appspot.com/upload?blob-key=" + result.get(0).getBannerImage(), mImageLoader);

                    Timer timer = new Timer();

                    timer.schedule(new TimerTask() {
                        int i = 0;

                        @Override
                        public void run() {
                            // Your logic here...

                            // When you need to modify a UI element, do so on the UI thread.
                            // 'getActivity()' is required as this is being ran from a Fragment.
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.i("image change", "------" + i);
                                    if (i == result.size()) {
                                        i = 0;
                                    }
                                    currentPos = i;
                                    // This code will always run on the UI thread, therefore is safe to modify UI elements.
                                    bannerView.setImageUrl("https://ramsofttech-marketing.appspot.com/upload?blob-key=" + result.get(i).getBannerImage(), mImageLoader);
                                    i++;

                                }
                            });
                        }
                    }, 0, 10000); // End of your timer code.
                }

            }
        }
    }

    private void doFakeWork() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

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
                AppDataHolder dataHolder = ((AppDataHolder) mActivity.getApplication());
                List<ApplicationData> applicationList = dataHolder.getBannerList();
                ApplicationData application = applicationList.get(currentPos);

                TelephonyManager manager = (TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE);
                String country_code = manager.getNetworkCountryIso();
                Log.i("--country code", country_code);
                if (country_code.trim().length() == 0) {
                    country_code = Locale.getDefault().getCountry();

                    if (country_code.trim().length() == 0)
                        country_code = "other";
                }


                ApplicationInfo appInfo = context.getApplicationInfo();

                Log.i("--PackageName", "nature---" + appInfo.packageName);
                String packageName = appInfo.packageName;

                Click click = new Click();
                click.setCount(1);
                click.setFrom(packageName);
                click.setTo(application.getPackageName());
                click.setCountry(country_code);
                click.setAdtype(2);

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

}
