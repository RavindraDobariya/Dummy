package com.ramsofttech.adpushlibrary.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.Date;

/**
 * Created by Ravi on 5/18/2015.
 */
@Entity
public class StaticAd {


    @Id
    private String id;
    @Index
    private String appPkgName;
    @Index
    private String adAppPkgName;
    @Index
    private Date date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAppPkgName() {
        return appPkgName;
    }

    public void setAppPkgName(String appPkgName) {
        this.appPkgName = appPkgName;
    }

    public String getAdAppPkgName() {
        return adAppPkgName;
    }

    public void setAdAppPkgName(String adAppPkgName) {
        this.adAppPkgName = adAppPkgName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
