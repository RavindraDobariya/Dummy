package com.ramsofttech.adpushlibrary.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * WARNING: This generated code is intended as a sample or starting point for using a
 * Google Cloud Endpoints RESTful API with an Objectify entity. It provides no data access
 * restrictions and no data validation.
 * <p/>
 * DO NOT deploy this code unchanged as part of a real application to real users.
 */
@Api(
        name = "registrationRecordApi",
        version = "v1",
        resource = "registrationRecord",
        namespace = @ApiNamespace(
                ownerDomain = "backend.adpushlibrary.ramsofttech.com",
                ownerName = "backend.adpushlibrary.ramsofttech.com",
                packagePath = ""
        )
)
public class RegistrationRecordEndpoint {

    private static final Logger logger = Logger.getLogger(RegistrationRecordEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(RegistrationRecord.class);
    }

    /**
     * Returns the {@link RegistrationRecord} with the corresponding ID.
     *
     * @param regId the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code RegistrationRecord} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "registrationRecord/{regId}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public RegistrationRecord get(@Named("regId") String regId) throws NotFoundException {
        logger.info("Getting RegistrationRecord with ID: " + regId);
        RegistrationRecord registrationRecord = ofy().load().type(RegistrationRecord.class).id(regId).now();
        if (registrationRecord == null) {
            throw new NotFoundException("Could not find RegistrationRecord with ID: " + regId);
        }
        return registrationRecord;
    }

    /**
     * Inserts a new {@code RegistrationRecord}.
     */
    @ApiMethod(
            name = "insert",
            path = "registrationRecord",
            httpMethod = ApiMethod.HttpMethod.POST)
    public RegistrationRecord insert(RegistrationRecord registrationRecord) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that registrationRecord.regId has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        RegistrationRecord registrationRecord1 = null;
        try {
            remove(registrationRecord.getRegId());

            registrationRecord1 = new RegistrationRecord();
            registrationRecord1 = registrationRecord;
            registrationRecord.setUpdatedDate(getDate());
            ofy().save().entity(registrationRecord1).now();
        } catch (NotFoundException e) {
            e.printStackTrace();

            removebyimei(registrationRecord.getIMEI(), registrationRecord.getPackageName());

            registrationRecord1 = new RegistrationRecord();
            registrationRecord1 = registrationRecord;
            registrationRecord.setUpdatedDate(getDate());
            ofy().save().entity(registrationRecord1).now();

        }

        logger.info("Created RegistrationRecord with ID: " + registrationRecord.getRegId());

        return ofy().load().entity(registrationRecord1).now();
    }

    /**
     * Updates an existing {@code RegistrationRecord}.
     *
     * @param regId              the ID of the entity to be updated
     * @param registrationRecord the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code regId} does not correspond to an existing
     *                           {@code RegistrationRecord}
     */

    public void inactive(@Named("regId") String regId) {
        // TODO: You should validate your ID parameter against your resource's ID here.

        RegistrationRecord registrationRecord1 = null;
        try {
            registrationRecord1 = checkExists(regId);
            registrationRecord1.setIsActive(false);
            registrationRecord1.setUpdatedDate(getDate());
            ofy().save().entity(registrationRecord1).now();
            logger.info("Updated RegistrationRecord: " + registrationRecord1);
        } catch (NotFoundException e) {
            e.printStackTrace();
        }

    }

    public void updateByRegId(@Named("regId") String regId, @Named("canonicalRegId") String canonicalRegId) {
        // TODO: You should validate your ID parameter against your resource's ID here.
        try {
            RegistrationRecord registrationRecord1 = checkExists(regId);
            registrationRecord1.setRegId(canonicalRegId);
            registrationRecord1.setIsActive(true);
            registrationRecord1.setUpdatedDate(getDate());

            ofy().save().entity(registrationRecord1).now();
            logger.info("Updated RegistrationRecord: " + registrationRecord1);
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }

    @ApiMethod(
            name = "update",
            path = "registrationRecord/{regId}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public RegistrationRecord update(@Named("regId") String regId, RegistrationRecord registrationRecord) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        RegistrationRecord registrationRecord1 = checkExists(regId);

        registrationRecord1 = registrationRecord;
        registrationRecord1.setIsActive(false);
        registrationRecord1.setUpdatedDate(getDate());

        ofy().save().entity(registrationRecord).now();
        logger.info("Updated RegistrationRecord: " + registrationRecord);
        return ofy().load().entity(registrationRecord1).now();
    }


    /**
     * Deletes the specified {@code RegistrationRecord}.
     *
     * @param regId the ID of the entity to delete
     * @throws NotFoundException if the {@code regId} does not correspond to an existing
     *                           {@code RegistrationRecord}
     */
    @ApiMethod(
            name = "remove",
            path = "registrationRecord/{regId}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("regId") String regId) throws NotFoundException {
        checkExists(regId);
        ofy().delete().type(RegistrationRecord.class).id(regId).now();
        logger.info("Deleted RegistrationRecord with ID: " + regId);
    }

    /**
     * Deletes the specified {@code RegistrationRecord}.
     * <p/>
     * //* @param regId the ID of the entity to delete
     *
     * @throws NotFoundException if the {@code regId} does not correspond to an existing
     *                           {@code RegistrationRecord}
     */
    @ApiMethod(
            name = "removebyimei",
            path = "removebyimei",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void removebyimei(@Named("imei") String imei, @Named("pkgName") String pkgName) {
        List<RegistrationRecord> registrationRecordList = ofy().load().type(RegistrationRecord.class).filter("IMEI", imei).filter("packageName", pkgName).list();
        ofy().delete().entities(registrationRecordList);

    }

    /**
     * List all entities.
     *
     * @param cursor used for pagination to determine which page to return
     * @param limit  the maximum number of entries to return
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(
            name = "list",
            path = "registrationRecord",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<RegistrationRecord> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<RegistrationRecord> query = ofy().load().type(RegistrationRecord.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<RegistrationRecord> queryIterator = query.iterator();
        List<RegistrationRecord> registrationRecordList = new ArrayList<RegistrationRecord>(limit);
        while (queryIterator.hasNext()) {
            registrationRecordList.add(queryIterator.next());
        }
        return CollectionResponse.<RegistrationRecord>builder().setItems(registrationRecordList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private RegistrationRecord checkExists(String regId) throws NotFoundException {
        try {
            return ofy().load().type(RegistrationRecord.class).id(regId).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find RegistrationRecord with ID: " + regId);
        }
    }


    private Date getDate() {

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String formateddate = dateFormat.format(date);
        try {
            return formatter.parse(formateddate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}