package com.qqq.remindme.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.qqq.remindme.DB.DBHandler;
import com.qqq.remindme.DB.DataInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by qqq on 5/15/2016.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    public static int isWiFi = -1;
    public static int isMobile = -1;

    private final String RX_FILE = "/sys/class/net/wlan0/statistics/rx_bytes";
    private final String TX_FILE = "/sys/class/net/wlan0/statistics/tx_bytes";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in air plan mode it will be null
        if(netInfo != null && netInfo.isConnected()){
            //Log.d("NETWORK", "Connected " + netInfo.getTypeName());
            if( (netInfo.getType() == 1)&&(isWiFi != 1) ){
                isWiFi = 1;
                isMobile = 0;
                Log.d("NETWORK", "WIFI: " + isWiFi + "; MOBILE: "  + isMobile);

                float rx_w_bytes = readFile(RX_FILE);
                float tx_w_bytes = readFile(TX_FILE);
                float wifiData = rx_w_bytes + tx_w_bytes;
                Log.d("NETWORK", "RX: " + Float.toString(rx_w_bytes/1000000) + "; TX: " + tx_w_bytes/1000000
                        + "; Total: " + wifiData/1000000);

//
//                DBHandler dbHandler = new DBHandler(context);
//                Log.d("INSERT: ", "inserting ...");
//                dbHandler.addData(new DataInfo((long)wifiData, new Date().getTime(), "W"));
//                //dbHandler.addData(new DataInfo(123142424, 12412421, "M"));
////        dbHandler.deleteDataInfo(1);
////        dbHandler.deleteDataInfo(2);
//
//                // Reading all shops
//                Log.d("Reading: ", "Reading all shops..");
//                List<DataInfo> dataInfos = dbHandler.getAllDataInfo();
//                DataInfo lastItem = dbHandler.getLastItem("W");
//                Log.d("Data Info: ", Long.toString(lastItem.getDate()));
//
//
//
//                for (DataInfo dataInfo : dataInfos) {
//                    String log = "Id: " + dataInfo.getId() + " ,Data: " +
//                            dataInfo.getData() + ",Date: " + dataInfo.getDate() + ",Type: " +
//                            dataInfo.getType();
//// Writing shops to log
//                    Log.d("Data Info: : ", log);
//                }


            }
            if( (netInfo.getType() == 0)&&(isMobile != 1)){
                isMobile = 1;
                isWiFi = 0;
                Log.d("NETWORK", "WIFI: " + isWiFi + "; MOBILE: "  + isMobile);
            }
        }
        else {
            isWiFi = -1;
            isMobile = -1;
            Log.d("NETWORK", "WIFI: " + isWiFi + "; MOBILE: "  + isMobile);
        }
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
