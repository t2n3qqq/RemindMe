package com.qqq.remindme.DB;

/**
 * Created by qqq on 5/13/2016.
 */
public class DataInfo {
    private int id;
    //private int uid;
    private long data;
    private long date;
    private String type;

    public DataInfo(long data, long date, String type) {
        //this.id = id;
       // this.uid = uid;
        this.data = data;
        this.type = type;
        this.date = date;
    }
    public DataInfo(int id, long data, long date, String type) {
        this.id = id;
        //this.uid = uid;
        this.data = data;
        this.type = type;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
//    public int getUid() {
//        return uid;
//    }
//
//    public void setUid(int uid) {
//        this.uid = uid;
//    }

    public long getData() {
        return data;
    }

    public void setData(long data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
