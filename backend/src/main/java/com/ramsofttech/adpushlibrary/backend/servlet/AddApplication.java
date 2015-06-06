package com.ramsofttech.adpushlibrary.backend.servlet;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.ramsofttech.adpushlibrary.backend.ApplicationData;
import com.ramsofttech.adpushlibrary.backend.ApplicationDataEndpoint;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Ravi on 5/10/2015.
 */
public class AddApplication extends HttpServlet {
    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);
        List<BlobKey> iconBolb = blobs.get("icon");
        List<BlobKey> bannerBlob = blobs.get("banner");
        ApplicationDataEndpoint applicationEndpoint=new ApplicationDataEndpoint();
        ApplicationData application;

        if ((iconBolb == null || iconBolb.isEmpty())&&(bannerBlob == null || bannerBlob.isEmpty())) {
            resp.sendRedirect("/");
        } else {
          //   DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            application=new ApplicationData();
            application.setBannerImage(bannerBlob.get(0).getKeyString());
            application.setAppIcon(iconBolb.get(0).getKeyString());
            application.setApplicationName(req.getParameter("appname"));
            application.setPackageName(req.getParameter("packagename"));
            application.setGridImageCount(Integer.parseInt(req.getParameter("gridcount")));
            application.setIsNew(Integer.parseInt(req.getParameter("new")));
            application.setBannerType(Integer.parseInt(req.getParameter("bannerType")));
            application.setInterstialType(Integer.parseInt(req.getParameter("interstialType")));
            application.setGridType(Integer.parseInt(req.getParameter("gridType")));
            application.setApplicationId(Integer.parseInt(req.getParameter("appid")));
            application.setIsAppActive(1);

            application.setDate(date);
            applicationEndpoint.insert(application);
            resp.sendRedirect("/upload?blob-key=" + bannerBlob.get(0).getKeyString());
        }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BlobKey blobKey = new BlobKey(req.getParameter("blob-key"));
        blobstoreService.serve(blobKey, resp);

    }
}
