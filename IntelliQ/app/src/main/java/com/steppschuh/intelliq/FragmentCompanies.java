package com.steppschuh.intelliq;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Steppschuh on 13/06/15.
 */
public class FragmentCompanies extends Fragment implements CallbackReceiver {

    MobileApp app;

    View contentFragment;

    ListView CompanyList;
    CompanyListAdapter CompanyListAdapter;

    Handler refreshHandler = new Handler();
    Runnable refreshRunable;
    int refreshDelay = 5000;
    boolean shouldRefresh = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        contentFragment = inflater.inflate(R.layout.fragment_companies, container, false);

        app = (MobileApp) getActivity().getApplicationContext();
        getActivity().setTitle(getString(R.string.companies));

        setupUi();
        updateUi();

        return contentFragment;
    }

    private void setupUi() {
        CompanyList = (ListView) contentFragment.findViewById(R.id.companyList);
        ArrayList<Company> companies = new ArrayList<>();
        CompanyListAdapter = new CompanyListAdapter(getActivity(), R.layout.company_list_item, companies);
        CompanyList.setAdapter(CompanyListAdapter);

        refreshHandler = new Handler();
        refreshRunable = new Runnable() {
            public void run() {
                if (shouldRefresh) {
                    app.requestCompanies(FragmentCompanies.this);
                }
                refreshHandler.postDelayed(this, refreshDelay);
            }
        };
    }

    private void updateUi() {
        ArrayList<Company> companies = new ArrayList<>();
        for (Company item : app.getCompanies()) {
            //TODO: filter location
            if (true) {
                companies.add(item);
            }
        }

        CompanyListAdapter.clear();
        for (Company company : companies) {
            CompanyListAdapter.insert(company, CompanyListAdapter.getCount());
        }
        CompanyListAdapter.notifyDataSetChanged();

    }

    @Override
    public void onCallBackReceived(Bundle data) {
        updateUi();
    }

    @Override
    public void onPause() {
        super.onPause();
        shouldRefresh = false;
        refreshHandler.removeCallbacks(refreshRunable);
    }

    @Override
    public void onResume() {
        super.onResume();
        shouldRefresh = true;
        refreshHandler = new Handler();
        refreshHandler.postDelayed(refreshRunable, refreshDelay);
    }
}
