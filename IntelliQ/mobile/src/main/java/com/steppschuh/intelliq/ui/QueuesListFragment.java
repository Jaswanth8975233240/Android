package com.steppschuh.intelliq.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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

    View fragment;
    Toolbar toolbar;
    ViewPager queuesTabsViewPager;
    QueuesTabsViewPagerAdapter queuesTabsViewPagerAdapter;
    SlidingTabLayout queuesTabsLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragment = inflater.inflate(R.layout.fragment_queues, container,false);

        app = (IntelliQ) getActivity().getApplication();

        setupToolbar();
        setupUi();

        return fragment;
    }

    private void setupToolbar() {
        toolbar = (Toolbar) fragment.findViewById(R.id.toolbar);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = ((MainActivity) getActivity()).getDrawerLayout();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                ((MainActivity) getActivity()).onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                ((MainActivity) getActivity()).onDrawerSlide(drawerView, slideOffset);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                ((MainActivity) getActivity()).onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
                ((MainActivity) getActivity()).onDrawerStateChanged(newState);
            }
        };
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
    }

    private void setupUi() {
        // setting up the tab view
        queuesTabsViewPagerAdapter = new QueuesTabsViewPagerAdapter(getActivity().getSupportFragmentManager(), getActivity());
        queuesTabsViewPager = (ViewPager) fragment.findViewById(R.id.queuesTabsViewPager);
        queuesTabsViewPager.setAdapter(queuesTabsViewPagerAdapter);
        queuesTabsLayout = (SlidingTabLayout) fragment.findViewById(R.id.tabs);
        queuesTabsLayout.setDistributeEvenly(false);
        queuesTabsLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return ContextCompat.getColor(getContext(), R.color.accent);
            }
        });
        queuesTabsLayout.setViewPager(queuesTabsViewPager);

        // setting up the FAB
        FloatingActionButton fab = (FloatingActionButton) fragment.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "FAB", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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
