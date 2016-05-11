package com.qqq.remindme;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.CallLog;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.lang.reflect.Array;
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
 * A simple {@link Fragment} subclass.
 */
public class CallsFragment extends Fragment {


    public CallsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calls, container, false);

        BarChart barChart = (BarChart) view.findViewById(R.id.chart_calls);

        TextView textAll =(TextView) view.findViewById(R.id.callsTotal);
        TextView textIn =(TextView) view.findViewById(R.id.callsIncoming);
        TextView textOut =(TextView) view.findViewById(R.id.callsOutgoing);
        TextView textMissed =(TextView) view.findViewById(R.id.callsMissed);

        String outgoing_type = Integer.toString(CallLog.Calls.OUTGOING_TYPE);
        String incoming_type = Integer.toString(CallLog.Calls.INCOMING_TYPE);
        String missed_type = Integer.toString(CallLog.Calls.MISSED_TYPE);

        Uri calls_uri = Uri.parse("content://call_log/calls");
        Cursor c_all = getActivity().getContentResolver().query(calls_uri, null, null, null, null);
        Cursor c_out = getActivity().getContentResolver().query(calls_uri, null, "type=" + outgoing_type, null, null);
        Cursor c_in = getActivity().getContentResolver().query(calls_uri, null, "type=" + incoming_type, null, null);
        Cursor c_missed = getActivity().getContentResolver().query(calls_uri, null, "type=" + missed_type, null, null);
        textOut.setText("Outgoing calls count: " + c_out.getCount());
        textIn.setText("Incoming calls count: " + c_in.getCount());
        textAll.setText("Total calls count: " + c_all.getCount());
        textMissed.setText("Missed calls count: " + c_missed.getCount());

        Call call = new Call();
        HashMap<String, Integer> callsDuration = call.getCallsCount(getActivity(), CallLog.Calls.INCOMING_TYPE);
        MainActivity.initGraph(barChart, callsDuration);

        return view;
    }

}
