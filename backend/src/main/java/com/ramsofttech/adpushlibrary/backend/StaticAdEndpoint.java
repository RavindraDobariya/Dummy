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

import java.util.ArrayList;
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
        name = "staticAdApi",
        version = "v1",
        resource = "staticAd",
        namespace = @ApiNamespace(
                ownerDomain = "backend.adpushlibrary.ramsofttech.com",
                ownerName = "backend.adpushlibrary.ramsofttech.com",
                packagePath = ""
        )
)
public class StaticAdEndpoint {

    private static final Logger logger = Logger.getLogger(StaticAdEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(StaticAd.class);
    }

    /**
     * Returns the {@link StaticAd} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code StaticAd} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "staticAd/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public StaticAd get(@Named("id") String id) throws NotFoundException {
        logger.info("Getting StaticAd with ID: " + id);
        StaticAd staticAd = ofy().load().type(StaticAd.class).id(id).now();
        if (staticAd == null) {
            throw new NotFoundException("Could not find StaticAd with ID: " + id);
        }
        return staticAd;
    }

    /**
     * Inserts a new {@code StaticAd}.
     */
    @ApiMethod(
            name = "insert",
            path = "staticAd",
            httpMethod = ApiMethod.HttpMethod.POST)
    public StaticAd insert(StaticAd staticAd) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that staticAd.id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(staticAd).now();
        logger.info("Created StaticAd with ID: " + staticAd.getId());

        return ofy().load().entity(staticAd).now();
    }

    /**
     * Updates an existing {@code StaticAd}.
     *
     * @param id       the ID of the entity to be updated
     * @param staticAd the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code StaticAd}
     */
    @ApiMethod(
            name = "update",
            path = "staticAd/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public StaticAd update(@Named("id") String id, StaticAd staticAd) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(id);
        ofy().save().entity(staticAd).now();
        logger.info("Updated StaticAd: " + staticAd);
        return ofy().load().entity(staticAd).now();
    }

    /**
     * Deletes the specified {@code StaticAd}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code StaticAd}
     */
    @ApiMethod(
            name = "remove",
            path = "staticAd/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") String id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(StaticAd.class).id(id).now();
        logger.info("Deleted StaticAd with ID: " + id);
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
            path = "staticAd",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<StaticAd> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<StaticAd> query = ofy().load().type(StaticAd.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<StaticAd> queryIterator = query.iterator();
        List<StaticAd> staticAdList = new ArrayList<StaticAd>(limit);
        while (queryIterator.hasNext()) {
            staticAdList.add(queryIterator.next());
        }
        return CollectionResponse.<StaticAd>builder().setItems(staticAdList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }


    @ApiMethod(
            name = "listbypkgname",
            path = "staticAdByPkgName",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<StaticAd> listBypkgName(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit,@Named("pkgname") String pkgName) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<StaticAd> query = ofy().load().type(StaticAd.class).filter("appPkgName",pkgName).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<StaticAd> queryIterator = query.iterator();
        List<StaticAd> staticAdList = new ArrayList<StaticAd>(limit);
        while (queryIterator.hasNext()) {
            staticAdList.add(queryIterator.next());
        }
        return CollectionResponse.<StaticAd>builder().setItems(staticAdList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(String id) throws NotFoundException {
        try {
            ofy().load().type(StaticAd.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find StaticAd with ID: " + id);
        }
    }
}