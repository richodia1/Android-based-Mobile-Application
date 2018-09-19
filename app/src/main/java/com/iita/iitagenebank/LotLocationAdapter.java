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
 * Created by Simeon on 08/03/2016.
 */
public class LotLocationAdapter  extends ArrayAdapter<Location>
{
    private ArrayList<Location> allLocationlist = null;
    private ArrayList<Location> remainingLocationListAfterDelete;
    LayoutInflater vi;
    int Resource;
    ViewHolder holder;
    DataAccess da;
    Activity activity;

    public LotLocationAdapter(Activity a, int resource, ArrayList<Location> allLocations)
    {
        super(a, resource, allLocations);
        vi = (LayoutInflater) a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.Resource = resource;
        this.da = new DataAccess(a);
        this.activity = a;
        this.allLocationlist = allLocations;
        this.remainingLocationListAfterDelete = new ArrayList<Location>();
        this.remainingLocationListAfterDelete.addAll(allLocationlist);
    }

    @Override
    public View getView(int positn, View convertView, ViewGroup parent)
    {
        final int position = positn;
        View v = convertView;
        if (v == null)
        {
            holder = new ViewHolder();
            v = vi.inflate(Resource, null);
            holder.locationName = (TextView) v.findViewById(R.id.locationName);
            holder.locationDetail = (TextView) v.findViewById(R.id.locationDetail);
            v.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) v.getTag();
        }

        holder.locationName.setText(allLocationlist.get(position).getName());
        holder.locationDetail.setText("Location Detail: " + allLocationlist.get(position).getLocationType());

        return v;
    }

    static class ViewHolder
    {
        public TextView locationName;
        public TextView locationDetail;
    }

    @Override
    public int getCount() {
        return allLocationlist.size();
    }

    @Override
    public Location getItem(int position)
    {
        return allLocationlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // get remaining sermons after delete operation
    public void filter(String locationID) {
        allLocationlist.clear();
        if (locationID != null && locationID != "")
        {
            for (Location lo : remainingLocationListAfterDelete)
            {
                if (locationID.matches(String.valueOf(lo.getLocationId())) != true)
                {
                    allLocationlist.add(lo);
                }
            }
        }
        else
        {
            allLocationlist.addAll(remainingLocationListAfterDelete);
        }
        remainingLocationListAfterDelete.clear();
        remainingLocationListAfterDelete.addAll(allLocationlist);
        notifyDataSetChanged();
    }
}
