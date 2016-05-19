package com.qqq.remindme;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.qqq.remindme.Fragments.CallsFragment;
import com.qqq.remindme.Fragments.DataFragment;
import com.qqq.remindme.Fragments.MessagesFragment;
import com.qqq.remindme.Fragments.SummaryFragment;

/**
 * Created by qqq on 4/25/2016.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int numOfTabs){
        super(fm);
        this.mNumOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                SummaryFragment tab1 = new SummaryFragment();
                return tab1;
            case 1:
                CallsFragment tab2 = new CallsFragment();
                return tab2;
            case 2:
                MessagesFragment tab3 = new MessagesFragment();
                return tab3;
            case 3:
                DataFragment tab4 = new DataFragment();
                return tab4;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
