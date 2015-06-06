package com.ramsofttech.adpushlibrary.utility;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Patterns;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.ramsofttech.adpushlibrary.backend.registrationRecordApi.RegistrationRecordApi;
import com.ramsofttech.adpushlibrary.backend.registrationRecordApi.model.RegistrationRecord;

import java.io.IOException;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by Ravi on 4/11/2015.
 */
public class RegisterPushData {
    private String gmail = "";
    private Context context;
    private RegistrationRecord gcmData;

    public void registerData(Context context, String regId) {
        //getting gamilid of device
        this.context = context;
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        Account[] accounts = AccountManager.get(context).getAccountsByType("com.google");
        String myEmailid = accounts[0].toString();
        Log.d("My email id that i want", myEmailid);
        for (Account account : accounts) {
            gmail = account.name;
            Log.i("Possible email id of ", gmail);

        }

        //getting country code number of sim

        TelephonyManager manager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String country_code = manager.getNetworkCountryIso();
        Log.i("--country code", country_code);
        if (country_code.trim().length() == 0) {
            country_code = Locale.getDefault().getCountry();

            if (country_code.trim().length() == 0)
                country_code = "other";
        }

        //getting imei number of devices
        String imei = manager.getDeviceId();
        Log.i("--imei", "nature---" + imei);

        //gettig package name of application
        ApplicationInfo appInfo = context.getApplicationInfo();
        Log.i("--PackageName", "nature---" + appInfo.packageName);

        //getting application name
        String title = (String) (appInfo != null ? context.getPackageManager()
                .getApplicationLabel(appInfo) : "Unknown");

        Log.i("--appName", "nature---" + title);
        gcmData = new RegistrationRecord();
        gcmData.setAppName(title);
        gcmData.setPackageName(appInfo.packageName);
        gcmData.setCountry(country_code);
        gcmData.setGmailId(gmail);
        gcmData.setImei(imei);
        gcmData.setRegId(regId);
        gcmData.setActive(true);

        GcmRegistrationAsyncTask gcmRegistrationAsyncTask = new GcmRegistrationAsyncTask(context);
        gcmRegistrationAsyncTask.execute();

    }


    class GcmRegistrationAsyncTask extends AsyncTask<Void, Void, RegistrationRecord> {
        private RegistrationRecordApi regService = null;

        private Context context;

        // TODO: change to your own sender ID to Google Developers Console project number, as per instructions above


        public GcmRegistrationAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected RegistrationRecord doInBackground(Void... params) {
            if (regService == null) {
                RegistrationRecordApi.Builder builder = new RegistrationRecordApi.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        // Need setRootUrl and setGoogleClientRequestInitializer only for local testing,
                        // otherwise they can be skipped
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
                RegistrationRecord registrationRecord = regService.insert(gcmData).execute();
                return registrationRecord;
            } catch (IOException ex) {
                ex.printStackTrace();

            }

            return null;
        }

        @Override
        protected void onPostExecute(RegistrationRecord msg) {
            //    Toast.makeText(context,  msg, Toast.LENGTH_LONG).show();
            if (msg != null) {
                GCMRegistrar.setRegisteredOnServer(context, true);
            } else {
                GCMRegistrar.setRegisteredOnServer(context, true);
            }

        }
    }
}
