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
        name = "clickApi",
        version = "v1",
        resource = "click",
        namespace = @ApiNamespace(
                ownerDomain = "backend.adpushlibrary.ramsofttech.com",
                ownerName = "backend.adpushlibrary.ramsofttech.com",
                packagePath = ""
        )
)
public class ClickEndpoint {

    private static final Logger logger = Logger.getLogger(ClickEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(Click.class);
    }

    /**
     * Returns the {@link Click} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code Click} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "click/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Click get(@Named("id") String id) throws NotFoundException {
        logger.info("Getting Click with ID: " + id);
        Click click = ofy().load().type(Click.class).id(id).now();
        if (click == null) {
            throw new NotFoundException("Could not find Click with ID: " + id);
        }
        return click;
    }

    /**
     * Inserts a new {@code Click}.
     */
    @ApiMethod(
            name = "insert",
            path = "click",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Click insert(Click click) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that click.id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        try {
            update(getId(click), click);
        } catch (NotFoundException e) {
            e.printStackTrace();

            Click newClick = new Click();
            newClick = click;
            newClick.setDate(getDate());
            newClick.setId(getId(click));
            newClick.setCount(1);
            ofy().save().entity(click).now();
            logger.info("Created Click with ID: " + click.getId());
        }

        logger.info("Created Click with ID: " + click.getId());

        return ofy().load().entity(click).now();
    }

    /**
     * Updates an existing {@code Click}.
     *
     * @param id    the ID of the entity to be updated
     * @param click the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Click}
     */
    @ApiMethod(
            name = "update",
            path = "click/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public Click update(@Named("id") String id, Click click) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        Click oldClick = checkExists(id);
        Click newClick = new Click();
        newClick = oldClick;
        newClick.setDate(getDate());
        newClick.setId(getId(click));
        newClick.setCount(oldClick.getCount()+1);
        ofy().save().entity(newClick).now();
        logger.info("Updated Click: " + click);
        return ofy().load().entity(click).now();
    }

    /**
     * Deletes the specified {@code Click}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Click}
     */
    @ApiMethod(
            name = "remove",
            path = "click/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") String id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(Click.class).id(id).now();
        logger.info("Deleted Click with ID: " + id);
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
            path = "click",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Click> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<Click> query = ofy().load().type(Click.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Click> queryIterator = query.iterator();
        List<Click> clickList = new ArrayList<Click>(limit);
        while (queryIterator.hasNext()) {
            clickList.add(queryIterator.next());
        }
        return CollectionResponse.<Click>builder().setItems(clickList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private Click checkExists(String id) throws NotFoundException {
        try {
            return ofy().load().type(Click.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find Click with ID: " + id);
        }
    }

    private String getId(Click click) {

        return click.getTo() + "," + click.getFrom() + "," + click.getCountry() + "," + click.getAdtype() + "," + getDate();
    }

    private Date getDate() {

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
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