package com.qqq.remindme;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.support.v4.util.Pair;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by qqq on 5/4/2016.
 */
public class Call{
    Call(){

    }

    private Uri calls_uri = Uri.parse("content://call_log/calls");
    private DateFormat formatter = new SimpleDateFormat("MM/yyyy");
    private Calendar calendar = Calendar.getInstance();
    private Long date;

    public HashMap<String, Integer> getCallsDuration(Activity activity, Integer type){
        Cursor cursor_TEST = activity.getContentResolver().query(calls_uri, null, null, null, "DATE ASC");
        HashMap<String, Pair> datesMap = new HashMap<>();
        String dt;
        while(cursor_TEST.moveToNext()) {
            date = Long.parseLong(cursor_TEST.getString(cursor_TEST.getColumnIndex(CallLog.Calls.DATE)));
            calendar.setTimeInMillis(date);
            Date dat = calendar.getTime();
            dt = formatter.format(dat);
            Pair<Long, Long> pair = getDateRange(calendar);
            datesMap.put(dt, pair);
        }
        HashMap<String, Cursor> inList = new HashMap<>();
        HashMap<String, Cursor> outList = new HashMap<>();
        SortedSet<String> keys = new TreeSet<>(datesMap.keySet());
        for (String key : keys) {
            Pair value = datesMap.get(key);
            cursor_TEST = activity.getContentResolver().query(calls_uri, null
                    ,"DATE > " + value.first.toString() + " AND DATE < "
                            + value.second.toString() + " AND TYPE = " + CallLog.Calls.INCOMING_TYPE, null, "DATE ASC");
            inList.put(key ,cursor_TEST);

            cursor_TEST = activity.getContentResolver().query(calls_uri, null
                    ,"DATE > " + value.first.toString() + " AND DATE < "
                            + value.second.toString() + " AND TYPE = " + CallLog.Calls.OUTGOING_TYPE, null, "DATE ASC");
            outList.put(key ,cursor_TEST);
            Log.v("HASH MAP: ", key + " " + value.first.toString() + " - " + value.second.toString());
        }
        List<String> lst = new ArrayList<>();
        HashMap<String, Integer> durationByMonth = new HashMap<>();
        Float counter;
        Map<String, Cursor> currentMap = null;
        switch (type){
            case 1:
                currentMap = inList;
                break;
            case 2:
                currentMap = outList;
                break;
        }
        keys = new TreeSet<>(currentMap.keySet());
        for (String key : keys) {
            Cursor value = currentMap.get(key);
            counter = 0f;
            while(value.moveToNext()) {
                counter += Integer.parseInt(value.getString(value.getColumnIndex(CallLog.Calls.DURATION)));
            }
            counter = counter / 60;
            durationByMonth.put(key, Math.round(counter));
        }
        keys = new TreeSet<>(durationByMonth.keySet());
        for (String key : keys) {
            Integer value = durationByMonth.get(key);
            lst.add(key + " -- " + value);
        }
        for(String key : lst){
            Log.v("LST ", key);
        }
        return durationByMonth;
    }

    public HashMap<String, Integer> getCallsCount(Activity activity, Integer type){
        Cursor cursor = activity.getContentResolver().query(calls_uri, null, "type=" + type.toString(), null, null);
        HashMap<String, Integer> total_count_map = new HashMap<>();
        List<String> total_count_list = new ArrayList<>();
        while(cursor.moveToNext()) {
            date = Long.parseLong(cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE)));

            long milliSeconds = date;
            calendar.setTimeInMillis(milliSeconds);
            String dt = formatter.format(calendar.getTime());
            total_count_list.add(dt);
        }
        for(String date_entry : total_count_list){
            total_count_map.put(date_entry, Collections.frequency(total_count_list,date_entry));
        }
        return total_count_map;
    }

    public Pair<Long, Long> getDateRange(Calendar calendar) {
        Long begining, end;
        {
            calendar.set(Calendar.DAY_OF_MONTH,
                    calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
            setTimeToBeginningOfDay(calendar);
            begining = calendar.getTimeInMillis();
        }

        {
            calendar.set(Calendar.DAY_OF_MONTH,
                    calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            setTimeToEndofDay(calendar);
            end = calendar.getTimeInMillis();
        }

        return Pair.create(begining, end);
    }

    private static void setTimeToBeginningOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private static void setTimeToEndofDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
    }
}
