package com.iita.iitagenebank;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Simeon on 18/02/2016.
 */
public class LotAdapter extends ArrayAdapter<Lot>
{
    private List<Lot> allLotlist = null;
    private ArrayList<Lot> remainingLotListAfterDelete;
    LayoutInflater vi;
    int Resource;
    ViewHolder holder;
    DataAccess da;
    Activity activity;

    public LotAdapter(Activity a, int resource, List<Lot> lots)
    {
        super(a, resource, lots);
        vi = (LayoutInflater) a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.Resource = resource;
        this.da = new DataAccess(a);
        this.activity = a;

        this.allLotlist = lots;
        this.remainingLotListAfterDelete = new ArrayList<Lot>();
        this.remainingLotListAfterDelete.addAll(allLotlist);
    }

    @Override
    public View getView(int positn, View convertView, ViewGroup parent)
    {
        final int position = positn;
        View v = convertView;
        if (v == null) {
            holder = new ViewHolder();
            v = vi.inflate(Resource, null);
            holder.lotName = (TextView) v.findViewById(R.id.lotName);
            holder.barcode = (TextView) v.findViewById(R.id.barcode);
            holder.quantity = (TextView) v.findViewById(R.id.quantity);
            holder.location = (TextView) v.findViewById(R.id.location);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        holder.lotName.setText(allLotlist.get(position).getItemName());
        holder.barcode.setText("Barcode: " + allLotlist.get(position).getBarcode());
        holder.quantity.setText("Quantity: " + String.valueOf((int) allLotlist.get(position).getQuantity()));
        holder.location.setText("Location: " + allLotlist.get(position).getLocationDetail());

        return v;
    }

    static class ViewHolder
    {
        public TextView lotName;
        public TextView barcode;
        public TextView quantity;
        public TextView location;
    }

    @Override
    public int getCount() {
        return allLotlist.size();
    }

    @Override
    public Lot getItem(int position)
    {
        return allLotlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // get remaining sermons after delete operation
    public void filter(String lotID) {
        allLotlist.clear();
        if (lotID != null && lotID != "")
        {
            for (Lot lot : remainingLotListAfterDelete)
            {
                if (lotID.matches(String.valueOf(lot.getLotId())) != true)
                {
                    allLotlist.add(lot);
                }
            }
        }
        else
        {
            allLotlist.addAll(remainingLotListAfterDelete);
        }
        remainingLotListAfterDelete.clear();
        remainingLotListAfterDelete.addAll(allLotlist);
        notifyDataSetChanged();
    }
}
