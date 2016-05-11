package com.qqq.remindme;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



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

    }

    private void initTollbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
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

}
