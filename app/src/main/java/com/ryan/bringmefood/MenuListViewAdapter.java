package com.ryan.bringmefood;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

import java.util.LinkedList;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class MenuListViewAdapter extends ArrayAdapter<MenuItem> {
    private final Context theC;
    private final LayoutInflater inflater;
    private final MenuItem[] theMenu;
    private final LinkedList<String> chosenItems;
    private final SparseBooleanArray mSelectedItemsIds;

    public MenuListViewAdapter(Context context, int resource,
                               MenuItem[] objects, LinkedList<String> chosenItems) {
        super(context, resource, objects);
        this.theC = context;
        this.inflater = LayoutInflater.from(context);
        this.theMenu = objects;
        this.chosenItems = chosenItems;
        this.mSelectedItemsIds = new SparseBooleanArray();
    }

    private class ViewHolder {
        TextView itemName;
        TextView itemDescription;
        TextView itemCost;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if(view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.menu_listview_item, null);

            holder.itemName = (TextView) view.findViewById(R.id.itemName);
            holder.itemDescription = (TextView) view.findViewById(com.ryan.bringmefood.R.id.itemDescription);
            holder.itemCost = (TextView) view.findViewById(com.ryan.bringmefood.R.id.itemCost);

            final MenuItem item = theMenu[position];
            holder.itemCost.setText("$: " + item.getCost());
            holder.itemName.setText(item.getName());
            holder.itemDescription.setText(item.getDescription());
            holder.itemDescription.setVisibility(View.INVISIBLE);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(holder.itemDescription.getVisibility() == View.INVISIBLE) {
                        holder.itemDescription.setVisibility(View.VISIBLE);
                    }
                    else {
                        holder.itemDescription.setVisibility(View.INVISIBLE);
                    }
                }
            });
            view.setOnLongClickListener(new android.view.View.OnLongClickListener() {
                @Override
                public boolean onLongClick(android.view.View v) {
                    return false;
                }
            });
        }
        else {
            holder = (ViewHolder) view.getTag();
        }
        return view;
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }
}
