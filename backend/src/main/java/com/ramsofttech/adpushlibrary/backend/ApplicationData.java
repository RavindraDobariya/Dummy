package com.ramsofttech.adpushlibrary.backend;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Unindex;

import java.beans.Transient;
import java.util.Date;

/**
 * Created by Ravi on 5/17/2015.
 */
@Entity
public class ApplicationData {


    @Id
    private String packageName;
    @Unindex
    private String applicationName;
    private String appIcon;
    private String bannerImage;
    @Index
    private Date date;
    @Index
    private int isAppActive;
    @Index
    private int gridType;
    @Index
    private int bannerType;
    @Index
    private int interstialType;
    private int gridImageCount;
    @Index
    private int isNew;
    @Index
    private int applicationId;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(String appIcon) {
        this.appIcon = appIcon;
    }

    public String getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getIsAppActive() {
        return isAppActive;
    }

    public void setIsAppActive(int isAppActive) {
        this.isAppActive = isAppActive;
    }

    public int getGridType() {
        return gridType;
    }

    public void setGridType(int gridType) {
        this.gridType = gridType;
    }

    public int getBannerType() {
        return bannerType;
    }

    public void setBannerType(int bannerType) {
        this.bannerType = bannerType;
    }

    public int getInterstialType() {
        return interstialType;
    }

    public void setInterstialType(int interstialType) {
        this.interstialType = interstialType;
    }

    public int getGridImageCount() {
        return gridImageCount;
    }

    public void setGridImageCount(int gridImageCount) {
        this.gridImageCount = gridImageCount;
    }

    public int getIsNew() {
        return isNew;
    }

    public void setIsNew(int isNew) {
        this.isNew = isNew;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    @Transient
    Key<ApplicationData> getKey() {
        return Key.create(ApplicationData.class, packageName);
    }
}
