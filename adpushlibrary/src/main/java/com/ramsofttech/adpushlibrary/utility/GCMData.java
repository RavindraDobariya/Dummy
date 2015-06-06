package com.ramsofttech.adpushlibrary.utility;

import android.content.Context;
import android.util.Log;


public class GCMData {

    private String EXTRA_MESSAGE = "message";
    private Context context;

    public boolean updateData(Context context) {
        this.context = context;

        //registering with GCM push notificagtion
        GCMRegistrar.checkDevice(context);
        GCMRegistrar.checkManifest(context);
        final String regId = GCMRegistrar.getRegistrationId(context);
        Log.i("--regId", "nature---" + regId);

        if (regId.equals("")) {
            // Automatically registers application on startup.
            Log.i("--SenderID", "nature---" + Constant.SENDER_ID);
            GCMRegistrar.register(context, Constant.SENDER_ID);
        } else {
            // Device is already registered on GCM, check server.

            if (GCMRegistrar.isRegisteredOnServer(context)) {
                // Skips registration.
            } else {
                if (regId.length() > 10) {

                    RegisterPushData registerPushData = new RegisterPushData();
                    registerPushData.registerData(context, regId);
                } else {
                    Log.i("GCMDATA", "registration id not found");
                }
            }
        }

        return true;

    }


}
