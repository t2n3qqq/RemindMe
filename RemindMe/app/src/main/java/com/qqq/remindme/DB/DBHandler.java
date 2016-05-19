package com.qqq.remindme.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qqq on 5/13/2016.
 */
public class DBHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "remind_db";
    private static final String TABLE_DATAINFO = "datainfo";
    private static final String KEY_ID = "id";
    private static final String KEY_UID = "uid";
    private static final String KEY_DATA = "data";
    private static final String KEY_DATE = "date";
    private static final String KEY_TYPE = "type";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_DATAINFO + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_DATA + " INTEGER," + KEY_DATE + " INTEGER," + KEY_TYPE + " TEXT" +")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATAINFO);
        onCreate(db);
    }

    public void addData(DataInfo dataInfo){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        //values.put(KEY_UID, dataInfo.getUid());
        values.put(KEY_DATA, dataInfo.getData());
        values.put(KEY_DATE, dataInfo.getDate());
        values.put(KEY_TYPE, dataInfo.getType());

        db.insert(TABLE_DATAINFO, null, values);
    }

    // Getting one shop
    public DataInfo getDataInfo(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_DATAINFO, new String[] { KEY_ID,
                    KEY_DATA, KEY_DATE, KEY_TYPE}, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        DataInfo contact = new DataInfo(Integer.parseInt(cursor.getString(0)),
                Long.parseLong(cursor.getString(1)), Long.parseLong(cursor.getString(2)),
                cursor.getString(3));
        return contact;
    }

    public DataInfo getLastItem(String type){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try{
            cursor = db.query(TABLE_DATAINFO, new String[] { KEY_ID,
                            KEY_DATA, KEY_DATE, KEY_TYPE}, KEY_TYPE + "=?",
                    new String[] { type }, null, null, "DATE DESC");
        }catch (Exception ex){
            Log.d("Err", ex.getMessage());
        }
        DataInfo contact = null;
        if (cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
            contact = new DataInfo(Integer.parseInt(cursor.getString(0)),
                    Long.parseLong(cursor.getString(1)), Long.parseLong(cursor.getString(2)),
                    cursor.getString(3));
        }

        return contact;
    }

    public DataInfo getPrevItem(String type){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try{
            cursor = db.query(TABLE_DATAINFO, new String[] { KEY_ID,
                            KEY_DATA, KEY_DATE, KEY_TYPE}, KEY_TYPE + "=?",
                    new String[] { type }, null, null, "DATE DESC");
        }catch (Exception ex){
            Log.d("Err", ex.getMessage());
        }

        DataInfo contact = null;
        if (cursor != null && cursor.getCount() > 1){
            cursor.moveToFirst();
            cursor.moveToNext();
            contact = new DataInfo(Integer.parseInt(cursor.getString(0)),
                    Long.parseLong(cursor.getString(1)), Long.parseLong(cursor.getString(2)),
                    cursor.getString(3));
        }
        return contact;
    }

    // Getting All Shops
    public List<DataInfo> getAllDataInfo() {
        List<DataInfo> shopList = new ArrayList<DataInfo>();
        String selectQuery = "SELECT * FROM " + TABLE_DATAINFO;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                DataInfo dataInfo = new DataInfo(Integer.parseInt(cursor.getString(0)),
                        Long.parseLong(cursor.getString(1)), Long.parseLong(cursor.getString(2)),
                        cursor.getString(3));
                shopList.add(dataInfo);
            } while (cursor.moveToNext());
        }
        return shopList;
    }
    public List<DataInfo> getAllDataInfo(String type) {
        List<DataInfo> shopList = new ArrayList<DataInfo>();
        String selectQuery = "SELECT * FROM " + TABLE_DATAINFO + " WHERE TYPE = " + "'" +type + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                DataInfo dataInfo = new DataInfo(Integer.parseInt(cursor.getString(0)),
                        Long.parseLong(cursor.getString(1)), Long.parseLong(cursor.getString(2)),
                        cursor.getString(3));
                shopList.add(dataInfo);
            } while (cursor.moveToNext());
        }
        return shopList;
    }

    // Updating a shop
    public int updateDataInfo(DataInfo dataInfo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //values.put(KEY_UID, dataInfo.getUid());
        values.put(KEY_DATA, dataInfo.getData());
        values.put(KEY_DATE, dataInfo.getDate());
        values.put(KEY_TYPE, dataInfo.getType());
// updating row
        return db.update(TABLE_DATAINFO, values, KEY_ID + " = ?",
                new String[]{String.valueOf(dataInfo.getId())});
    }

    // Deleting a shop
    public void deleteDataInfo(DataInfo dataInfo) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DATAINFO, KEY_ID + " = ?",
                new String[] { String.valueOf(dataInfo.getId()) });
        db.close();
    }

    public void deleteDataInfo(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DATAINFO, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
    }

}
