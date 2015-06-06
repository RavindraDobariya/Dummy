package com.ramsofttech.adpushlibrary.backend.servlet;

import com.ramsofttech.adpushlibrary.backend.StaticAd;
import com.ramsofttech.adpushlibrary.backend.StaticAdEndpoint;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Ravi on 5/18/2015.
 */
public class SetAdApplication extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


        String appPkgName = req.getParameter("apppkgname");
        String[] adAppPkgNames = req.getParameter("adapppkgnames").split(",");
        StaticAdEndpoint staticAdEndpoint = new StaticAdEndpoint();
        StaticAd staticAd = null;

        for (int i = 0; i < adAppPkgNames.length; i++) {
            staticAd = new StaticAd();
            Date date = new Date();
            staticAd.setId(appPkgName + adAppPkgNames[i]);
            staticAd.setAppPkgName(appPkgName);
            staticAd.setAdAppPkgName(adAppPkgNames[i]);
            staticAd.setDate(date);
            staticAdEndpoint.insert(staticAd);
        }
    }
}
