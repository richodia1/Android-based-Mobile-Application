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
public class LotSubtypeTransactionAdapter extends ArrayAdapter<SubtypeTransaction>
{
    private List<SubtypeTransaction> allLotSubtypeTransactionlist = null;
    private ArrayList<SubtypeTransaction> remainingLotSubtypeTransactionListAfterDelete;
    LayoutInflater vi;
    int Resource;
    ViewHolder holder;
    DataAccess da;
    Activity activity;

    public LotSubtypeTransactionAdapter(Activity a, int resource, List<SubtypeTransaction> lotSubtypeTransactions)
    {
        super(a, resource, lotSubtypeTransactions);
        vi = (LayoutInflater) a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.Resource = resource;
        this.da = new DataAccess(a);
        this.activity = a;

        this.allLotSubtypeTransactionlist = lotSubtypeTransactions;
        this.remainingLotSubtypeTransactionListAfterDelete = new ArrayList<SubtypeTransaction>();
        this.remainingLotSubtypeTransactionListAfterDelete.addAll(allLotSubtypeTransactionlist);
    }

    @Override
    public View getView(int positn, View convertView, ViewGroup parent)
    {
        final int position = positn;
        View v = convertView;
        if (v == null) {
            holder = new ViewHolder();
            v = vi.inflate(Resource, null);
            holder.subtype = (TextView) v.findViewById(R.id.subtype);
            holder.date = (TextView) v.findViewById(R.id.date);
            holder.scale = (TextView) v.findViewById(R.id.scale);
            holder.quantity = (TextView) v.findViewById(R.id.quantity);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }


        holder.subtype.setText(allLotSubtypeTransactionlist.get(position).getSubtype());
        holder.date.setText("Date: " + da.ConvertDateFormat(allLotSubtypeTransactionlist.get(position).getDate()));
        holder.scale.setText("Scale: " + allLotSubtypeTransactionlist.get(position).getScale());
        holder.quantity.setText("Quantity: " + String.valueOf((int) allLotSubtypeTransactionlist.get(position).getQuantity()));

        return v;
    }

    static class ViewHolder
    {
        public TextView subtype;
        public TextView date;
        public TextView scale;
        public TextView quantity;
    }

    @Override
    public int getCount() {
        return allLotSubtypeTransactionlist.size();
    }

    @Override
    public SubtypeTransaction getItem(int position)
    {
        return allLotSubtypeTransactionlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // get remaining sermons after delete operation
    public void filter(String subtypeTransID) {
        allLotSubtypeTransactionlist.clear();
        if (subtypeTransID != null && subtypeTransID != "")
        {
            for (SubtypeTransaction lotSubTT : remainingLotSubtypeTransactionListAfterDelete)
            {
                if (subtypeTransID.matches(String.valueOf(lotSubTT.getSubtypeTransId())) != true)
                {
                    allLotSubtypeTransactionlist.add(lotSubTT);
                }
            }
        }
        else
        {
            allLotSubtypeTransactionlist.addAll(remainingLotSubtypeTransactionListAfterDelete);
        }
        remainingLotSubtypeTransactionListAfterDelete.clear();
        remainingLotSubtypeTransactionListAfterDelete.addAll(allLotSubtypeTransactionlist);
        notifyDataSetChanged();
    }
}
