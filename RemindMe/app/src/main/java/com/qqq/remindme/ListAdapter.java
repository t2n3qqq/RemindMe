package com.qqq.remindme;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;

/**
 * Created by qqq on 5/10/2016.
 */
public class ListAdapter extends ArrayAdapter<ApplicationInfo> {

    private final Context context;

    private final List<ApplicationInfo> values;
    private final PackageManager  pm;

    public ListAdapter(Context context, List<ApplicationInfo> values, PackageManager pm) {
        super(context, R.layout.fragment_data, values);
        this.context = context;
        this.values = values;
        this.pm = pm;
    }


    /**
     * Constructing list element view
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.fragment_data, parent, false);

        long ulBytes = TrafficStats.getUidTxBytes(values.get(position).uid);
        long dlBytesLong = TrafficStats.getUidRxBytes(values.get(position).uid);

        float dlBytes = (float)dlBytesLong;
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.FLOOR);

        TextView appNameView = (TextView) rowView.findViewById(R.id.textData);
        TextView appNameViewMb = (TextView) rowView.findViewById(R.id.textDataMb);
        Drawable appIcon = values.get(position).loadIcon(pm);
        String appName = values.get(position).loadLabel(pm).toString();
        appIcon.setBounds(0, 0, 40, 40);
        appNameView.setCompoundDrawables(appIcon, null, null, null);
        appNameView.setCompoundDrawablePadding(15);
        appNameView.setText(appName);
        appNameViewMb.setText(":          " + df.format(dlBytes/1000000) + " Mb");

        return rowView;
    }


}





