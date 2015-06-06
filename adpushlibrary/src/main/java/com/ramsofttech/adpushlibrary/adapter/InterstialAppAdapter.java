package com.ramsofttech.adpushlibrary.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.ramsofttech.adpushlibrary.R;
import com.ramsofttech.adpushlibrary.backend.applicationDataApi.model.ApplicationData;
import com.ramsofttech.adpushlibrary.backend.clickApi.ClickApi;
import com.ramsofttech.adpushlibrary.backend.clickApi.model.Click;
import com.ramsofttech.adpushlibrary.utility.AppDataHolder;
import com.ramsofttech.adpushlibrary.utility.Constant;
import com.ramsofttech.adpushlibrary.utility.MySingleton;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Ravi on 5/22/2015.
 */
public class InterstialAppAdapter extends RecyclerView.Adapter<InterstialAppAdapter.AppViewHolder> {


    private static String TAG = "InterstialAppAdapter";
    private List<ApplicationData> appList;
    private Activity mContext;
    private RecyclerView recList;
    private ImageLoader mImageLoader;

    public InterstialAppAdapter(List<ApplicationData> appList, Activity context, RecyclerView recList) {
        this.appList = appList;
        mContext = context;
        this.recList = recList;
        mImageLoader = MySingleton.getInstance(context).getImageLoader();
    }

    @Override
    public AppViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        final View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_layout, viewGroup, false);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemPosition = recList.getChildAdapterPosition(itemView);
                clickAsyncTask clickAsynctask = new clickAsyncTask(mContext);
                clickAsynctask.execute(itemPosition + "");

                AppDataHolder dataHolder = ((AppDataHolder) mContext.getApplication());
                List<ApplicationData> applicationList = dataHolder.getInterstialList();
                ApplicationData application = applicationList.get(itemPosition);
                mContext.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" + application.getPackageName())));


            }
        });
        return new AppViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AppViewHolder holder, int position) {
        ApplicationData app = appList.get(position);

        holder.appName.setText(app.getApplicationName());
        holder.appIcon.setImageUrl("https://ramsofttech-marketing.appspot.com/upload?blob-key=" + app.getAppIcon(), mImageLoader);


    }

    @Override
    public int getItemCount() {
        return this.appList.size();
    }

    public static class AppViewHolder extends RecyclerView.ViewHolder {
        protected NetworkImageView appIcon;
        protected TextView appName;


        public AppViewHolder(View v) {
            super(v);
            appIcon = (NetworkImageView) v.findViewById(R.id.appIcon);
            appName = (TextView) v.findViewById(R.id.appName);

        }
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
                AppDataHolder dataHolder = ((AppDataHolder) mContext.getApplication());
                List<ApplicationData> applicationList = dataHolder.getInterstialList();
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
                click.setAdtype(3);

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
