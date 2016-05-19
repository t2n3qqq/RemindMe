package com.qqq.remindme;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.qqq.remindme.Broadcast.NetworkChangeReceiver;
import com.qqq.remindme.DB.DBHandler;
import com.qqq.remindme.DB.DataInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by qqq on 4/1/2016.
 */
public class MainActivity extends AppCompatActivity{

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private final String RX_FILE = "/sys/class/net/wlan0/statistics/rx_bytes";
    private final String TX_FILE = "/sys/class/net/wlan0/statistics/tx_bytes";
    private BroadcastReceiver receiver = new NetworkChangeReceiver();
    private NetworkInfo netInfo;
    private DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    private Activity activity = this;

    @Override
    protected void onPause() {
        super.onPause();
        try{
            unregisterReceiver(receiver);
        }catch (Exception e){
            Log.d("Err", "on Pause Event: " + e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("RESUME", "MAIN RESUME");
        float rx_w_bytes = readFile(RX_FILE);
        float tx_w_bytes = readFile(TX_FILE);
        float wifiData = rx_w_bytes + tx_w_bytes;
        Log.d("NETWORK", "RX: " + Float.toString(rx_w_bytes/1000000) + "; TX: " + tx_w_bytes/1000000
                + "; Total: " + wifiData/1000000);

        float rx_m_bytes = TrafficStats.getMobileRxBytes();
        float tx_m_bytes = TrafficStats.getMobileTxBytes();
        float mobileData = rx_m_bytes + tx_m_bytes;
        Log.d("NETWORK", "RX: " + rx_m_bytes/1000000 + "; TX: " + tx_m_bytes/1000000
                + "; Total: " + mobileData/1000000);

        DBHandler dbHandler = new DBHandler(this);

        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();
        if(netInfo != null && netInfo.isConnected()){
            Log.d("NETWORK", netInfo.getTypeName());
            long currentDate = new Date().getTime();
            if(netInfo.getType() == 1){
                DataInfo last = dbHandler.getLastItem("W");
           //     Calendar calendar = Calendar.getInstance();
         //       calendar.setTimeInMillis(Long.parseLong("1463432400000"));
//                1463346000000
//                1463432400000
//                1463518800000
                long total = getTotal("W");
                if(last == null){
                    dbHandler.addData(new DataInfo(0, new Date().getTime(), "W"));
                    dbHandler.addData(new DataInfo((long) wifiData, new Date().getTime(), "WZ"));
                }
                else {

                    String lastDate = formatter.format(last.getDate());
                    String currentDateStr = formatter.format(currentDate);
                    long zeroBytes = 0;
                    if(dbHandler.getLastItem("WZ") != null)
                        zeroBytes = dbHandler.getLastItem("WZ").getData();

                    if( lastDate.equals(currentDateStr)){
                        dbHandler.updateDataInfo(new DataInfo(last.getId()
                                ,(long) wifiData - zeroBytes - total + last.getData()
                                , currentDate, "W"));
                    }
                    else {
                        dbHandler.addData(new DataInfo((long)wifiData - zeroBytes - total, currentDate, "W"));
                    }
                }
            }
            if(netInfo.getType() == 0){
                DataInfo last = dbHandler.getLastItem("M");
                long total = getTotal("M");
                if(last == null){
                    dbHandler.addData(new DataInfo(0, currentDate, "M"));
                    dbHandler.addData(new DataInfo((long) mobileData, currentDate, "MZ"));
                }
                else {
                    String lastDate = formatter.format(last.getDate());
                    String currentDateStr = formatter.format(currentDate);
                    long zeroBytes = 0;
                    if(dbHandler.getLastItem("MZ") != null)
                        zeroBytes = dbHandler.getLastItem("MZ").getData();
                    if( lastDate.equals(currentDateStr)){
                        dbHandler.updateDataInfo(new DataInfo(last.getId()
                                ,(long) mobileData - zeroBytes - total + last.getData()
                                , currentDate, "M"));
                    }
                    else {
                        dbHandler.addData(new DataInfo((long)mobileData - zeroBytes - total, currentDate, "M"));
                    }
                }
            }
        }

        List<DataInfo> dataInfos = dbHandler.getAllDataInfo();
        if(!dataInfos.isEmpty()){
            for (DataInfo dataInfo : dataInfos) {
                String log = "Id: " + dataInfo.getId() + " ,Data: " +
                        dataInfo.getData() + ",Date: " + dataInfo.getDate() + ",Type: " +
                        dataInfo.getType();
                Log.d("Data Info: : ", log);
            }
        }
        else {
            Log.d("Data Info", "EMPTY LIST");
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("RESUME", "MAIN CREATE");
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

//===================================================
//get wifi data
//===================================================
//        float rx_w_bytes = readFile(RX_FILE);
//        float tx_w_bytes = readFile(TX_FILE);
//        float wifiData = rx_w_bytes + tx_w_bytes;
//        Log.d("NETWORK", "RX: " + Float.toString(rx_w_bytes/1000000) + "; TX: " + tx_w_bytes/1000000
//                + "; Total: " + wifiData/1000000);
////===================================================
////get mobile data
////===================================================
//        float rx_m_bytes = TrafficStats.getMobileRxBytes();//readFile(mobileTxFile_1);//
//        float tx_m_bytes = TrafficStats.getMobileTxBytes();// readFile(mobileTxFile_1);//
//        float mobileData = rx_m_bytes + tx_m_bytes;
//        Log.d("NETWORK", "RX: " + rx_m_bytes/1000000 + "; TX: " + tx_m_bytes/1000000
//                + "; Total: " + mobileData/1000000);
////===================================================
////
////===================================================
//        DBHandler dbHandler = new DBHandler(this);
//
//        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
//        netInfo = cm.getActiveNetworkInfo();
//        //should check null because in air plan mode it will be null
//        if(netInfo != null && netInfo.isConnected()){
//            Log.d("NETWORK", netInfo.getTypeName());
//            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
//            if(netInfo.getType() == 1){
//                DataInfo last = dbHandler.getLastItem("W");
//                Calendar calendar = Calendar.getInstance();
//                calendar.setTimeInMillis(Long.parseLong("1463432400000"));
////                1463346000000
////                1463432400000
////                1463518800000
//                long total = getTotal("W");
//                if(last == null){
//                    dbHandler.addData(new DataInfo(0, new Date().getTime(), "W"));
//                    dbHandler.addData(new DataInfo((long) wifiData, new Date().getTime(), "WZ"));
////                    dbHandler.addData(new DataInfo(100, 1, "W"));
//                }
//                else {
//                    long currentDate = new Date().getTime();
//                    String lastDate = formatter.format(last.getDate());
//                    String currentDateStr = formatter.format(calendar.getTimeInMillis());
//                    long zeroBytes = 0;
//                    if(dbHandler.getLastItem("WZ") != null)
//                        zeroBytes = dbHandler.getLastItem("WZ").getData();
////                    dbHandler.deleteDataInfo(1);
////                    dbHandler.deleteDataInfo(2);
////                    dbHandler.deleteDataInfo(3);
////                    dbHandler.deleteDataInfo(4);
//
////                    if(formatter.format(last.getDate()) == formatter.format(new Date().getTime())){
////                    if(last.getDate() != 1){
//                    if( lastDate.equals(currentDateStr)){
//                        dbHandler.updateDataInfo(new DataInfo(last.getId()
//                                ,(long) wifiData - zeroBytes - total + last.getData()
//                                , calendar.getTimeInMillis(), "W"));
//                    }
//                    else {
//                        dbHandler.addData(new DataInfo((long)wifiData - zeroBytes - total, calendar.getTimeInMillis(), "W"));
//                    }
//                }
//            }
//        }


            //Log.d("NETWORK", "Connected " + netInfo.getTypeName());
//===================================================
//
//===================================================

//        DBHandler dbHandler = new DBHandler(this);
//        Log.d("INSERT: ", "inserting ...");
////        dbHandler.addData(new DataInfo(wifiTotal, 333444, "M"));
////        dbHandler.addData(new DataInfo(123142424, 12412421, "W"));
////        dbHandler.deleteDataInfo(1);
////        dbHandler.deleteDataInfo(2);
//
//        // Reading all shops
//        Log.d("Reading: ", "Reading all shops..");
//        List<DataInfo> dataInfos = dbHandler.getAllDataInfo();
//
//
//
//        for (DataInfo dataInfo : dataInfos) {
//            String log = "Id: " + dataInfo.getId() + " ,Data: " +
//                    dataInfo.getData() + ",Date: " + dataInfo.getDate() + ",Type: " +
//                    dataInfo.getType();
//// Writing shops to log
//            Log.d("Data Info: : ", log);
//        }


        initTollbar();
        initNavigationView();
        initViewpager();
        initTabLayout();



        tabLayout.addTab(tabLayout.newTab().setText("Summary"));
        tabLayout.addTab(tabLayout.newTab().setText("Calls"));
        tabLayout.addTab(tabLayout.newTab().setText("Messages"));
        tabLayout.addTab(tabLayout.newTab().setText("Data"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);




        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        int hasReadCallLogPermission = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CALL_LOG);
        int hasReadSMSPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);// checkSelfPermission(Manifest.permission.READ_SMS);
        int hasNetworkStatePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE);
        if (hasReadSMSPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_SMS},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }
        if (hasReadCallLogPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_CALL_LOG},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }
        if (hasNetworkStatePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_NETWORK_STATE},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }

//        ConnectivityManager check = (ConnectivityManager)
//                this.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo[] info = check.getAllNetworkInfo();
//        for (int i = 0; i<info.length; i++){
//            if (info[i].getState() == NetworkInfo.State.CONNECTED){
//                Log.d("NETWORK", info[i].getTypeName());
//            }
//        }

    }

    private void initTollbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
//                Toast.makeText(activity, "CALENDAR", Toast.LENGTH_SHORT).show();

                View menuItemView = findViewById(R.id.calendar); // SAME ID AS MENU ID
                PopupMenu popupMenu = new PopupMenu(activity, menuItemView);
                popupMenu.inflate(R.menu.calendar_menu);
                // ...
                popupMenu.show();
                // ...

                return true;
            }
        });
        toolbar.inflateMenu(R.menu.menu);
    }

    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    }

    private void initViewpager() {
        viewPager = (ViewPager)findViewById(R.id.view_pager);
    }

    private void initTabLayout() {
        tabLayout = (TabLayout)findViewById(R.id.tab_layout);
    }

    public static void initGraph(BarChart chart, Map<String, Integer> map){
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<String>();
        int count = 0;
        SortedSet<String> keys = new TreeSet<String>(map.keySet());
        for (String key : keys) {
            Integer value = map.get(key);
            entries.add(new BarEntry(value,count));
            labels.add(key);
            count++;
        }
        BarDataSet dataset = new BarDataSet(entries, "# of Calls");
        dataset.setColors(ColorTemplate.LIBERTY_COLORS);
        BarData data = new BarData(labels, dataset);
        XAxis xAxis = chart.getXAxis();
        xAxis.setSpaceBetweenLabels(1);
        chart.setData(data);
        chart.setDescription("# of times Alice called Bob");
        chart.setScaleEnabled(false);
        chart.notifyDataSetChanged();
        chart.invalidate();
    }

    public static void initGraphTest(LineChart chart, Map<String, Integer> map){
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<String>();
        int count = 0;
        SortedSet<String> keys = new TreeSet<String>(map.keySet());
        for (String key : keys) {
            Integer value = map.get(key);
            entries.add(new BarEntry(value,count));
            labels.add(key);
            count++;
        }
        LineDataSet dataset = new LineDataSet(entries, "# of Calls");
        dataset.setDrawCubic(true);
        dataset.setColors(ColorTemplate.LIBERTY_COLORS);
        dataset.setDrawFilled(true);
        LineData data = new LineData(labels, dataset);

        XAxis xAxis = chart.getXAxis();
        xAxis.setSpaceBetweenLabels(1);
        chart.animateXY(2000, 2000);
        chart.setDrawGridBackground(false);
        chart.setData(data);
        chart.setDescription("# blablabla");
        chart.setScaleEnabled(false);
        chart.notifyDataSetChanged();
        chart.invalidate();
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
    public long getTotal(String type){
        DBHandler dbHandler = new DBHandler(this);
        List<DataInfo> dataInfoList = dbHandler.getAllDataInfo(type);
        long count = 0;
        for(DataInfo di : dataInfoList){
            count += di.getData();
        }
        return count;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        Toast.makeText(this, "CALENDAR", Toast.LENGTH_SHORT).show();
//        return true;
//    }
}
