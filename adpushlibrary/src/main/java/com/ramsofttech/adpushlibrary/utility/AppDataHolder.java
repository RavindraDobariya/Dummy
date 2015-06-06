package com.ramsofttech.adpushlibrary.utility;


import android.app.Application;

import com.ramsofttech.adpushlibrary.backend.applicationDataApi.model.ApplicationData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ravi on 5/12/2015.
 */
public class AppDataHolder extends Application {

    private List<ApplicationData> applicationList = new ArrayList<ApplicationData>();
    private List<ApplicationData> bannerList = new ArrayList<ApplicationData>();
    private List<ApplicationData> interstialList = new ArrayList<ApplicationData>();
    private int numCols=2;
    private int numRows=2;
    private float cellHeight=6;

    public List<ApplicationData> getApplicationList() {
        return applicationList;
    }

    public void setApplicationList(List<ApplicationData> applicationList) {
        this.applicationList = applicationList;
    }

    public List<ApplicationData> getInterstialList() {
        return interstialList;
    }

    public void setInterstialList(List<ApplicationData> interstialList) {
        this.interstialList = interstialList;
    }

    public int getNumCols() {
        return numCols;
    }

    public float getCellHeight() {
        return cellHeight;
    }

    public void setCellHeight(float cellHeight) {
        this.cellHeight = cellHeight;
    }

    public void setNumCols(int numCols) {
        this.numCols = numCols;
    }

    public int getNumRows() {
        return numRows;
    }

    public void setNumRows(int numRows) {
        this.numRows = numRows;
    }

    public List<ApplicationData> getBannerList() {
        return bannerList;
    }

    public void setBannerList(List<ApplicationData> bannerList) {
        this.bannerList = bannerList;
    }
}
