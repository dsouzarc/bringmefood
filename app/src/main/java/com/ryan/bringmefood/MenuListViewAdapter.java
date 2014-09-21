package com.ryan.bringmefood;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class MenuListViewAdapter extends ArrayAdapter<MenuItem> {
    private final Context theC;
    private final LayoutInflater inflater;
    private final MenuItem[] theMenu;
    private final SparseBooleanArray mSelectedItemsIds;

    public MenuListViewAdapter(Context context, int resource, MenuItem[] objects) {
        super(context, resource, objects);
        this.theC = context;
        this.inflater = LayoutInflater.from(context);
        this.theMenu = objects;
        this.mSelectedItemsIds = new SparseBooleanArray();
    }

    private class ViewHolder {
        TextView itemName;
        TextView itemDescription;
        TextView itemCost;
    }

    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if(view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.lis)
    }
}
