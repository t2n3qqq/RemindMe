package com.qqq.remindme.Fragments;


import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.net.NetworkInfo;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.qqq.remindme.DB.DBHandler;
import com.qqq.remindme.DB.DataInfo;
import com.qqq.remindme.ListAdapter;
import com.qqq.remindme.MainActivity;
import com.qqq.remindme.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;


/**
 * A simple {@link Fragment} subclass.
 */
public class DataFragment extends ListFragment {

    private final Float DATA_LIMIT = 500f;

    private Handler mHandler = new Handler();
    private long mStartRX = 0;
    private long mStartTX = 0;

    private final String RX_FILE = "/sys/class/net/wlan0/statistics/rx_bytes";
    private final String TX_FILE = "/sys/class/net/wlan0/statistics/tx_bytes";

    private String data[] = new String[] { "one", "two", "three", "four" };

    public DataFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("RESUME", "RESUME FRAGMENT");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("RESUME", "CREATE FRAGMENT");
        DBHandler dbHandler = new DBHandler(getActivity());
        List<DataInfo> dataInfoList = dbHandler.getAllDataInfo("W");
        long totalW = 0;
        for(DataInfo di : dataInfoList){
            totalW += di.getData();
        }
        float rx_w_bytes = readFile(RX_FILE);
        float tx_w_bytes = readFile(TX_FILE);
        float wifiData = rx_w_bytes + tx_w_bytes;
        Log.d("NETWORK", "DATA FRAG RX: " + Float.toString(rx_w_bytes/1000000) + "; TX: " + tx_w_bytes/1000000
                + "; Total: " + wifiData/1000000);

        View view = inflater.inflate(R.layout.fragment_data, container, false);
        TextView tv = (TextView)view.findViewById(R.id.data_total);
        TextView limitView = (TextView) view.findViewById(R.id.data_limit);
        limitView.setText("Limit " + DATA_LIMIT.toString() + " Mb");

        ProgressBar firstBar = (ProgressBar)view.findViewById(R.id.firstBar);
        firstBar.setVisibility(View.VISIBLE);
        firstBar.setMax(100);

//        PieChart pieChart = (PieChart) view.findViewById(R.id.data_pie);
//// creating data values
//        ArrayList<Entry> entries = new ArrayList<>();
//        entries.add(new Entry(4f, 0));
//        entries.add(new Entry(8f, 1));
//        entries.add(new Entry(6f, 2));
//        entries.add(new Entry(12f, 3));
//        entries.add(new Entry(18f, 4));
//        entries.add(new Entry(9f, 5));
//
//        PieDataSet dataset = new PieDataSet(entries, "# of Calls");
//        dataset.setColors(ColorTemplate.COLORFUL_COLORS);
//
//        ArrayList<String> labels = new ArrayList<String>();
//        labels.add("January");
//        labels.add("February");
//        labels.add("March");
//        labels.add("April");
//        labels.add("May");
//        labels.add("June");
//
//        PieData data = new PieData(labels, dataset); // initialize Piedata
//        pieChart.setData(data); //set data into chart
//
//        pieChart.setDescription("Description");  // set the description
        PackageManager pm = getActivity().getPackageManager();
        List<String> stdout = Shell.SH.run("ps");
        List<String> packages = new ArrayList<>();
        for (String line : stdout) {
            // Get the process-name. It is the last column.
            String[] arr = line.split("\\s+");
            String processName = arr[arr.length - 1].split(":")[0];
            packages.add(processName);
        }

// Get a list of all installed apps on the device.
        List<ApplicationInfo> apps = pm.getInstalledApplications(0);

//// Remove apps which are not running.
//        for (Iterator<ApplicationInfo> it = apps.iterator(); it.hasNext(); ) {
//            if (!packages.contains(it.next().packageName)) {
//                it.remove();
//            }
//        }
        for (Iterator<ApplicationInfo> it = apps.iterator(); it.hasNext(); ) {
            if (TrafficStats.getUidRxBytes(it.next().uid) == 0) {
                it.remove();
            }
        }
        Float count = 0f;
        for (ApplicationInfo app : apps) {
            String appName = app.loadLabel(pm).toString();
            int uid = app.uid;
            long ulBytes = TrafficStats.getUidTxBytes(uid);
            long dlBytes = TrafficStats.getUidRxBytes(uid);
            //float tmp = (float) dlBytes;
            count += dlBytes;
    /* do your stuff */
           // Log.v("UIDs : ", Integer.toString(uid) + " -- " + ulBytes + " -- " + dlBytes);
        }
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.FLOOR);
        tv.setText("Used: " + df.format(count/1000000) + " Mb");
        firstBar.setProgress((int)(100*count/1000000/DATA_LIMIT));
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(getActivity().ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = manager.getRunningAppProcesses();
        if (runningProcesses != null && runningProcesses.size() > 0) {
            // Set data to the list adapter
            ListAdapter listAdapter = new ListAdapter(getActivity(), apps, pm);
            listAdapter.sort(new Comparator<ApplicationInfo>() {
                public int compare(ApplicationInfo arg0, ApplicationInfo arg1) {
                    return (int)(TrafficStats.getUidRxBytes(arg1.uid) - TrafficStats.getUidRxBytes(arg0.uid));
                }
            });
            //setListAdapter(listAdapter);
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "No application is running", Toast.LENGTH_LONG).show();
        }

        DonutProgress donutProgress = (DonutProgress) view.findViewById(R.id.donutData);
        donutProgress.setProgress((int)(100*totalW/1000000/DATA_LIMIT));
        donutProgress.setInnerBottomText("Limit: " + DATA_LIMIT);

        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        long send       = 0;
        long recived    = 0;
        // Get UID of the selected process
        int uid = ((ApplicationInfo)getListAdapter().getItem(position)).uid;

        // Get traffic data
        recived = TrafficStats.getUidRxBytes(uid);
        send = TrafficStats.getUidTxBytes(uid);

        // Display data
        Toast.makeText(getActivity().getApplicationContext(), "UID " + uid + " details...\n send: " + send/1000 + "kB" + " \n recived: " + recived/1000 + "kB", Toast.LENGTH_LONG).show();

    }

    //    =======================================
//    Read WiFi data from file
//    =======================================
    private long readFile(String fileName){
        File file = new File(fileName);
        BufferedReader br = null;
        long bytes = 0;
        try{
            br = new BufferedReader(new FileReader(file));
            String line = "";
            line = br.readLine();
            bytes = Long.parseLong(line);
        }  catch (Exception e){
            e.printStackTrace();
            return 0;

        } finally{
            if (br != null)
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        return bytes;
    }
//    =======================================
}
