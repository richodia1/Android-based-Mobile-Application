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
 * Created by Simeon on 02/03/2016.
 */
public class LotVariableAdapter extends ArrayAdapter<LotVariable>
{
    private List<LotVariable> allLotVariablelist = null;
    private ArrayList<LotVariable> remainingLotVariableListAfterDelete;
    LayoutInflater vi;
    int Resource;
    ViewHolder holder;
    DataAccess da;
    Activity activity;

    public LotVariableAdapter(Activity a, int resource, List<LotVariable> lotVars)
    {
        super(a, resource, lotVars);
        vi = (LayoutInflater) a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.Resource = resource;
        this.da = new DataAccess(a);
        this.activity = a;

        this.allLotVariablelist = lotVars;
        this.remainingLotVariableListAfterDelete = new ArrayList<LotVariable>();
        this.remainingLotVariableListAfterDelete.addAll(allLotVariablelist);
    }

    @Override
    public View getView(int positn, View convertView, ViewGroup parent)
    {
        final int position = positn;
        View v = convertView;
        if (v == null) {
            holder = new ViewHolder();
            v = vi.inflate(Resource, null);
            holder.variable = (TextView) v.findViewById(R.id.variable);
            holder.updatedBy = (TextView) v.findViewById(R.id.updatedBy);
            holder.quantity = (TextView) v.findViewById(R.id.qty);
            holder.lastUpdated = (TextView) v.findViewById(R.id.lastUpdated);
            holder.version = (TextView) v.findViewById(R.id.version);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }


        holder.variable.setText(allLotVariablelist.get(position).getVariableName());
        holder.updatedBy.setText("Updated By: " + allLotVariablelist.get(position).getLastUpdatedBy());
        holder.quantity.setText("Quantity: " + String.valueOf((int) allLotVariablelist.get(position).getQuantity()));
        holder.lastUpdated.setText("Last Updated: " + da.ConvertDateFormat(allLotVariablelist.get(position).getLastUpdated()));
        holder.version.setText("Version: " + allLotVariablelist.get(position).getVersion());

        return v;
    }

    static class ViewHolder
    {
        public TextView variable;
        public TextView updatedBy;
        public TextView quantity;
        public TextView lastUpdated;
        public TextView version;
    }

    @Override
    public int getCount() {
        return allLotVariablelist.size();
    }

    @Override
    public LotVariable getItem(int position)
    {
        return allLotVariablelist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // get remaining sermons after delete operation
    public void filter(String lotVarID) {
        allLotVariablelist.clear();
        if (lotVarID != null && lotVarID != "")
        {
            for (LotVariable lotVar : remainingLotVariableListAfterDelete)
            {
                if (lotVarID.matches(String.valueOf(lotVar.getLotvariableId())) != true)
                {
                    allLotVariablelist.add(lotVar);
                }
            }
        }
        else
        {
            allLotVariablelist.addAll(remainingLotVariableListAfterDelete);
        }
        remainingLotVariableListAfterDelete.clear();
        remainingLotVariableListAfterDelete.addAll(allLotVariablelist);
        notifyDataSetChanged();
    }
}