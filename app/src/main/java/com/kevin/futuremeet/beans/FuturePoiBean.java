package com.kevin.futuremeet.beans;

import com.avos.avoscloud.AVGeoPoint;

import java.util.Date;

/**
 * Created by carver on 2016/4/21.
 */
public class FuturePoiBean {
    private String poiName;
    private String poiAddress;
    private AVGeoPoint avGeoPoint;
    private Date arriveTime;

    public String getPoiName() {
        return poiName;
    }

    public void setPoiName(String poiName) {
        this.poiName = poiName;
    }

    public String getPoiAddress() {
        return poiAddress;
    }

    public void setPoiAddress(String poiAddress) {
        this.poiAddress = poiAddress;
    }

    public AVGeoPoint getAvGeoPoint() {
        return avGeoPoint;
    }

    public void setAvGeoPoint(AVGeoPoint avGeoPoint) {
        this.avGeoPoint = avGeoPoint;
    }

    public Date getArriveTime() {
        return arriveTime;
    }

    public void setArriveTime(Date arriveTime) {
        this.arriveTime = arriveTime;
    }
}
