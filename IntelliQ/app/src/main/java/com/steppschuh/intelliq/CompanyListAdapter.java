package com.steppschuh.intelliq;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import java.util.ArrayList;


public class CompanyListAdapter extends ArrayAdapter<Company> {

    ArrayList<Company> items = new ArrayList<>();
    Activity context;

    public CompanyListAdapter(Activity context, int resource, ArrayList<Company> items) {
        super(context, resource, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public Company getItem(int position) {
        return items.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        Company currentItem = getItem(position);

        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.company_list_item, null);
            rowView.setTag(currentItem.getId());
        }

        // fill data
        ((TextView) rowView.findViewById(R.id.itemNameLabel)).setText(currentItem.getName());
        ((TextView) rowView.findViewById(R.id.itemQueueValue)).setText(String.valueOf(currentItem.getPeopleInQueue()));
        ((TextView) rowView.findViewById(R.id.itemWaitingTime)).setText(String.valueOf(currentItem.getWaitingTime()) + " min");
        ((TextView) rowView.findViewById(R.id.itemQueueValue)).setText(String.valueOf(currentItem.getPeopleInQueue()));

        // load image
        ImageView itemImage = (ImageView) rowView.findViewById(R.id.itemImage);
        Ion.with(itemImage)
                .placeholder(R.drawable.sample)
                .error(R.drawable.error)
                .load(currentItem.getLogoUrl());

        rowView.setTag(currentItem.getId());
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MobileApp) context.getApplication()).requestQueueEntry((String) v.getTag(), null);
                ((MainActivity) context).showLoading();
            }
        });

        return rowView;
    }

    @Override
    public int getCount() {
        return items.size();
    }
}
