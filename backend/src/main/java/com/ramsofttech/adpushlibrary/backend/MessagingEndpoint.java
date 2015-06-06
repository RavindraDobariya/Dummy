/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Backend with Google Cloud Messaging" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/GcmEndpoints
*/

package com.ramsofttech.adpushlibrary.backend;

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import javax.inject.Named;

/**
 * An endpoint to send messages to devices registered with the backend
 * <p/>
 * For more information, see
 * https://developers.google.com/appengine/docs/java/endpoints/
 * <p/>
 * NOTE: This endpoint does not use any form of authorization or
 * authentication! If this app is deployed, anyone can access this endpoint! If
 * you'd like to add authentication, take a look at the documentation.
 */
@Api(name = "messaging", version = "v1", namespace = @ApiNamespace(ownerDomain = "backend.adpushlibrary.ramsofttech.com", ownerName = "backend.adpushlibrary.ramsofttech.com", packagePath = ""))
public class MessagingEndpoint {
    private static final Logger log = Logger.getLogger(MessagingEndpoint.class.getName());
    private static final Executor threadPool = Executors.newFixedThreadPool(20);
    private static String cursor = null;
    /**
     * Api Keys can be obtained from the google cloud console
     */
    private static final String API_KEY = System.getProperty("gcm.api.key");

    /**
     * Send to the first 10 devices (You can modify this to send to any number of devices or a specific device)
     *
     * @param message The message to send
     */
    public void sendMessage(@Named("message") String message) throws IOException {
        if (message == null || message.trim().length() == 0) {
            log.warning("Not sending message because it is empty");
            return;
        }
        // crop longer messages
        if (message.length() > 1000) {
            message = message.substring(0, 1000) + "[...]";
        }
        Sender sender = new Sender(API_KEY);
        Message msg = new Message.Builder().addData("message", message).build();
        // sender = new Sender(apiKey);

        RegistrationRecordEndpoint registrationRecordEndpoint = new RegistrationRecordEndpoint();

        CollectionResponse collectionResponse = registrationRecordEndpoint.list(null, 900);
        collectionResponse.getNextPageToken();
        List<RegistrationRecord> registrationRecordList = (List<RegistrationRecord>) collectionResponse.getItems();
        List<String> devices = new ArrayList<String>();
        for (RegistrationRecord registrationRecord : registrationRecordList)
            devices.add(registrationRecord.getRegId());

        MulticastResult multicastResult;
        try {
            //  sender = new Sender(apiKey);
            multicastResult = sender.send(msg, devices, 5);
        } catch (IOException e) {
            //  LOG.error("IOException caught inside method asyncSend");
            return;
        }

        List<Result> results = multicastResult.getResults();

        for (int i = 0; i < results.size(); i++) {


            Result result = results.get(i);
            String messageId = result.getMessageId();


            if (messageId != null) {


                String canonicalRegId = result.getCanonicalRegistrationId();
                if (canonicalRegId != null) {
                    // same device has more than on registration id: update it
                    // LOG.error("canonicalRegId ");
                    registrationRecordEndpoint.updateByRegId(devices.get(i), canonicalRegId);
                }
            } else {
                String error = result.getErrorCodeName();


                if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
                    // application has been removed from device - unregister it
                    registrationRecordEndpoint.inactive(devices.get(i));
                } else {

                }

            }

        }

    }

    // LOG.error("Method PushAndroid.asyncSend executed sucessfully");


    //   List<RegistrationRecord> records = ofy().load().type(RegistrationRecord.class).limit(10).list();
//        for (RegistrationRecord record : records) {
//            Result result = sender.send(msg, record.getRegId(), 5);
//            if (result.getMessageId() != null) {
//                log.info("Message sent to " + record.getRegId());
//                String canonicalRegId = result.getCanonicalRegistrationId();
//                if (canonicalRegId != null) {
//                    // if the regId changed, we have to update the datastore
//                    log.info("Registration Id changed for " + record.getRegId() + " updating to " + canonicalRegId);
//                    record.setRegId(canonicalRegId);
//                    ofy().save().entity(record).now();
//                }
//            } else {
//                String error = result.getErrorCodeName();
//                if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
//                    log.warning("Registration Id " + record.getRegId() + " no longer registered with GCM, removing from datastore");
//                    // if the device is no longer registered with Gcm, remove it from the datastore
//
//                } else {
//                    log.warning("Error when sending message : " + error);
//                }
//            }
//        }

}
