package com.steppschuh.intelliq.ui;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.steppschuh.intelliq.R;

public class QueuesTabsViewPagerAdapter extends FragmentStatePagerAdapter {

    Context context;

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public QueuesTabsViewPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: {
                return new QueuesTabNearby();
            }
            case 1: {
                return new QueuesTabRecent();
            }
            default: {
                return new QueuesTabNearby();
            }
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0: {
                return context.getString(R.string.tab_queues_nearby);
            }
            case 1: {
                return context.getString(R.string.tab_queues_recent);
            }
            default: {
                return context.getString(R.string.tab_queues_nearby);
            }
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

}