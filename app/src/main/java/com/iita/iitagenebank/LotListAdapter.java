package com.iita.iitagenebank;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Simeon on 04/03/2016.
 */
public class LotListAdapter extends ArrayAdapter<LotList>
{
    private List<LotList> allLotlist = null;
    private ArrayList<LotList> remainingLotListAfterDelete;
    LayoutInflater vi;
    int Resource;
    ViewHolder holder;
    DataAccess da;
    Activity activity;

    public LotListAdapter(Activity a, int resource, List<LotList> lotList)
    {
        super(a, resource, lotList);
        vi = (LayoutInflater) a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.Resource = resource;
        this.da = new DataAccess(a);
        this.activity = a;

        this.allLotlist = lotList;
        this.remainingLotListAfterDelete = new ArrayList<LotList>();
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
            holder.name = (TextView) v.findViewById(R.id.name);
            holder.updatedBy = (TextView) v.findViewById(R.id.updatedBy);
            holder.lastUpdated = (TextView) v.findViewById(R.id.lastUpdated);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }


        holder.name.setText(allLotlist.get(position).getName());
        holder.updatedBy.setText("Updated By: " + allLotlist.get(position).getLastUpdatedBy());
        holder.lastUpdated.setText("Last Updated: " + da.ConvertDateFormat(allLotlist.get(position).getLastUpdated()));

        return v;
    }

    static class ViewHolder
    {
        public TextView name;
        public TextView updatedBy;
        public TextView lastUpdated;
    }

    @Override
    public int getCount() {
        return allLotlist.size();
    }

    @Override
    public LotList getItem(int position)
    {
        return allLotlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // get remaining List after delete operation
    public void filter(String lotListID) {
        allLotlist.clear();
        if (lotListID != null && lotListID != "")
        {
            for (LotList lotList : remainingLotListAfterDelete)
            {
                if (lotListID.matches(String.valueOf(lotList.getLotlistId())) != true)
                {
                    allLotlist.add(lotList);
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