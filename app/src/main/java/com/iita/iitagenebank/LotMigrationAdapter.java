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
public class LotMigrationAdapter extends ArrayAdapter<Migration>
{
    private List<Migration> allLotMigrationlist = null;
    LayoutInflater vi;
    int Resource;
    ViewHolder holder;
    DataAccess da;
    Activity activity;

    public LotMigrationAdapter(Activity a, int resource, List<Migration> lotMigrations)
    {
        super(a, resource, lotMigrations);
        vi = (LayoutInflater) a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.Resource = resource;
        this.da = new DataAccess(a);
        this.activity = a;
        this.allLotMigrationlist = lotMigrations;
    }

    @Override
    public View getView(int positn, View convertView, ViewGroup parent)
    {
        final int position = positn;
        View v = convertView;
        if (v == null) {
            holder = new ViewHolder();
            v = vi.inflate(Resource, null);
            holder.migrationDate = (TextView) v.findViewById(R.id.migrationDate);
            holder.newLocation = (TextView) v.findViewById(R.id.newLocation);
            holder.oldLocation = (TextView) v.findViewById(R.id.oldLocation);
            holder.reason = (TextView) v.findViewById(R.id.reason);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        holder.migrationDate.setText("Migration Date: " + da.ConvertDateFormat(allLotMigrationlist.get(position).getMigrationDate()));
        holder.newLocation.setText("New Location: " + allLotMigrationlist.get(position).getNewLocationName());
        holder.oldLocation.setText("Old Location: " + allLotMigrationlist.get(position).getOldLocationName());
        holder.reason.setText(da.ResizeString("Reason: " + allLotMigrationlist.get(position).getReason(), 130));

        return v;
    }

    static class ViewHolder
    {
        public TextView migrationDate;
        public TextView newLocation;
        public TextView oldLocation;
        public TextView reason;
    }
}