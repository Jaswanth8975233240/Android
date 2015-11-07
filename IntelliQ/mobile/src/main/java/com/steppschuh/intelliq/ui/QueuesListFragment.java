package com.steppschuh.intelliq.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.steppschuh.intelliq.IntelliQ;
import com.steppschuh.intelliq.R;
import com.steppschuh.intelliq.ui.widget.SlidingTabLayout;

public class QueuesListFragment extends Fragment {

    IntelliQ app;

    Toolbar toolbar;
    ViewPager queuesTabsViewPager;
    QueuesTabsViewPagerAdapter queuesTabsViewPagerAdapter;
    SlidingTabLayout queuesTabsLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_queues, container,false);

        app = (IntelliQ) getActivity().getApplication();

        return setupUi(v);
    }

    private View setupUi(View v) {
        // setting up the navigation
        toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = ((MainActivity) getActivity()).getDrawerLayout();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener((MainActivity) getActivity());

        // setting up the tab view
        queuesTabsViewPagerAdapter =  new QueuesTabsViewPagerAdapter(getActivity().getSupportFragmentManager(), getActivity());
        queuesTabsViewPager = (ViewPager) v.findViewById(R.id.queuesTabsViewPager);
        queuesTabsViewPager.setAdapter(queuesTabsViewPagerAdapter);
        queuesTabsLayout = (SlidingTabLayout) v.findViewById(R.id.tabs);
        queuesTabsLayout.setDistributeEvenly(false);
        queuesTabsLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return ContextCompat.getColor(getContext(), R.color.accent);
            }
        });
        queuesTabsLayout.setViewPager(queuesTabsViewPager);

        // setting up the FAB
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "FAB", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

}
