package com.steppschuh.intelliq;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Steppschuh on 13/06/15.
 */
public class FragmentLoading extends Fragment {

    MobileApp app;

    View contentFragment;

    TextView statusLabel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        contentFragment = inflater.inflate(R.layout.fragment_loading, container, false);

        app = (MobileApp) getActivity().getApplicationContext();
        getActivity().setTitle(getString(R.string.companies));

        setupUi();

        return contentFragment;
    }

    private void setupUi() {
        statusLabel = (TextView) contentFragment.findViewById(R.id.loading);
        statusLabel.setText(R.string.loading);
    }



}
