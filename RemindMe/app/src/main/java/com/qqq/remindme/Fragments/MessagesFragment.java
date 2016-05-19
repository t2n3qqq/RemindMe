package com.qqq.remindme.Fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.qqq.remindme.MainActivity;
import com.qqq.remindme.R;

import java.io.Console;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;


/**
 * A simple {@link Fragment} subclass.
 */
public class MessagesFragment extends Fragment {


    public MessagesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        BarChart chart = (BarChart) view.findViewById(R.id.chart_sms);

        TextView textAll = (TextView)view.findViewById(R.id.textAll);
        TextView textSent = (TextView)view.findViewById(R.id.textSent);
        TextView textReceive = (TextView)view.findViewById(R.id.textReceive);

        Map<String, Integer> total_map = new HashMap<String, Integer>();
        List<String> arrayList = new ArrayList<String>();
        DateFormat formatter = new SimpleDateFormat("MM/yyyy");
        Calendar calendar = Calendar.getInstance();
        Uri sms_inbox = Uri.parse("content://sms/inbox");
        Uri sms_sent = Uri.parse("content://sms/sent");
        Uri sms_total = Uri.parse("content://sms");
        Cursor c_in = getActivity().getContentResolver().query(sms_inbox, null, null, null, null);
        Cursor c_out = getActivity().getContentResolver().query(sms_sent, null, null, null, null);
        Cursor c_total = getActivity().getContentResolver().query(sms_total,
                new String[] { "_id", "thread_id", "address", "person", "date","body", "type" },
                null, null, null);
        String[] columns = new String[] { "address", "person", "date", "body","type" };
        textSent.setText("Messages Sent: " + c_out.getCount());
        textReceive.setText("Message Receive: " + c_in.getCount());
        textAll.setText("Messages Total: " + (c_in.getCount() + c_out.getCount()));
        if (c_total.getCount() > 0) {
            while (c_total.moveToNext()){
                long milliSeconds= Long.parseLong(c_total.getString(c_total.getColumnIndex(columns[2])));
                calendar.setTimeInMillis(milliSeconds);
                String dt = formatter.format(calendar.getTime());
                arrayList.add(dt);
            }
        }

        for(String date : arrayList){
            total_map.put(date, Collections.frequency(arrayList,date));
        }
//            Set<String> mySet = new HashSet<String>(arrayList);
//            for(String s: mySet){
//
//                Log.v("COUNT: ", s + " " + Collections.frequency(arrayList,s));
//                total_map.put(s, Integer.toString(Collections.frequency(arrayList,s)) );
//
//            }
//--------------------------------------------------------------------------------------------------
//            String address = "";
//            String name = "";
//            String date = "";
//            String msg = "";
//            String type = "";
//
//            Uri mSmsinboxQueryUri = Uri.parse("content://sms/inbox");
//            Cursor cursor1 = getActivity().getContentResolver().query(mSmsinboxQueryUri,new String[] { "_id", "thread_id", "address", "person", "date","body", "type" }, null, null, null);
//            getActivity().startManagingCursor(cursor1);
//            String[] columns = new String[] { "address", "person", "date", "body","type" };
//            if (cursor1.getCount() > 0) {
//                String count = Integer.toString(cursor1.getCount());
//                while (cursor1.moveToNext()){
//                     address = cursor1.getString(cursor1.getColumnIndex(columns[0]));
//                     name = cursor1.getString(cursor1.getColumnIndex(columns[1]));
//                     date = cursor1.getString(cursor1.getColumnIndex(columns[2]));
//                     msg = cursor1.getString(cursor1.getColumnIndex(columns[3]));
//                     type = cursor1.getString(cursor1.getColumnIndex(columns[4]));
//                }
//            }
//
//            Log.v("count", "s " + cursor1.getCount());
//
//            String x = date;
//
//            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
//
//            long milliSeconds= Long.parseLong(x);
//            //System.out.println(milliSeconds);
//
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTimeInMillis(milliSeconds);
//            //System.out.println(formatter.format(calendar.getTime()));
//
//            String dt = formatter.format(calendar.getTime());
//
//
//            if(address != null)Log.v("address", address);
//            if(name != null) Log.v("name", name);
//            if(date != null)Log.v("date", dt);
//            if(msg != null)Log.v("msg", msg);
//            if(type != null)Log.v("type", type);
//--------------------------------------------------------------------------------------------------
//            Uri SMS_INBOX = Uri.parse("content://sms/conversations/");
//
//            Cursor c = getActivity().getContentResolver().query(SMS_INBOX, null, null, null, null);
//
//            getActivity().startManagingCursor(c);
//            String[] count = new String[c.getCount()];
//            String[] snippet = new String[c.getCount()];
//            String[] thread_id = new String[c.getCount()];
//
//            c.moveToFirst();
//
//            int cnt = 0;
//            int thrd = 0;
//            String strCount = "";
//            String strThread = "";
//            for (int i = 0; i < c.getCount(); i++) {
//                count[i] = c.getString(c.getColumnIndexOrThrow("msg_count"))
//                        .toString();
//                strCount += count[i] + "  ";
//                thread_id[i] = c.getString(c.getColumnIndexOrThrow("thread_id"))
//                        .toString();
//                strThread += thread_id[i] + "  ";
//                snippet[i] = c.getString(c.getColumnIndexOrThrow("snippet"))
//                        .toString();
//                Log.v("count", count[i]);
//                Log.v("thread", thread_id[i]);
//                Log.v("snippet", snippet[i]);
//                c.moveToNext();
//            }
//            Log.v("SUMMARY COUNT", strCount);
//            Log.v("SUMMARY THREAD", strThread);

//--------------------------------------------------------------------------------------------------

//
//            Uri sms_content = Uri.parse("content://sms/");
//            Cursor c = getActivity().getContentResolver().query(sms_content, null,null, null, null);
//            c.moveToFirst();
//            textView.setText("SMS COUNT" + "" +c.getCount()); //do some other operation


        MainActivity.initGraph(chart,total_map);
        return view;
    }



}
