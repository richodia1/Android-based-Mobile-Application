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
 * Created by Simeon on 29/02/2016.
 */
public class FieldVariableAdapter extends ArrayAdapter<FieldVariable>
{
    private List<FieldVariable> allFieldVariablelist = null;
    private ArrayList<FieldVariable> remainingFieldVariableListAfterDelete;
    LayoutInflater vi;
    int Resource;
    ViewHolder holder;
    DataAccess da;
    Activity activity;

    public FieldVariableAdapter(Activity a, int resource, List<FieldVariable> fieldVars)
    {
        super(a, resource, fieldVars);
        vi = (LayoutInflater) a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.Resource = resource;
        this.da = new DataAccess(a);
        this.activity = a;

        this.allFieldVariablelist = fieldVars;
        this.remainingFieldVariableListAfterDelete = new ArrayList<FieldVariable>();
        this.remainingFieldVariableListAfterDelete.addAll(allFieldVariablelist);
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
            holder.qty = (TextView) v.findViewById(R.id.qty);
            holder.lastUpdated = (TextView) v.findViewById(R.id.lastUpdated);
            holder.version = (TextView) v.findViewById(R.id.version);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }


        holder.variable.setText(allFieldVariablelist.get(position).getVar());
        holder.updatedBy.setText("Updated By: " + allFieldVariablelist.get(position).getLastUpdatedBy());
        holder.qty.setText("Quantity/Value: " + allFieldVariablelist.get(position).getQty());
        holder.lastUpdated.setText("Last Updated: " + da.ConvertDateFormat(allFieldVariablelist.get(position).getLastUpdated()));
        holder.version.setText("Version: " + allFieldVariablelist.get(position).getVersion());

        return v;
    }

    static class ViewHolder
    {
        public TextView variable;
        public TextView updatedBy;
        public TextView qty;
        public TextView lastUpdated;
        public TextView version;
    }

    @Override
    public int getCount() {
        return allFieldVariablelist.size();
    }

    @Override
    public FieldVariable getItem(int position)
    {
        return allFieldVariablelist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // get remaining sermons after delete operation
    public void filter(String fieldVarID) {
        allFieldVariablelist.clear();
        if (fieldVarID != null && fieldVarID != "")
        {
            for (FieldVariable fieldVar : remainingFieldVariableListAfterDelete)
            {
                if (fieldVarID.matches(String.valueOf(fieldVar.getFieldvarId())) != true)
                {
                    allFieldVariablelist.add(fieldVar);
                }
            }
        }
        else
        {
            allFieldVariablelist.addAll(remainingFieldVariableListAfterDelete);
        }
        remainingFieldVariableListAfterDelete.clear();
        remainingFieldVariableListAfterDelete.addAll(allFieldVariablelist);
        notifyDataSetChanged();
    }
}
