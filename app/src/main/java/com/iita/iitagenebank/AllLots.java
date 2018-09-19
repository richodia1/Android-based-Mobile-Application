package com.iita.iitagenebank;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AllLots extends AppCompatActivity
{
    DataAccess da = new DataAccess(this);
    LotAdapter adapter;
    ArrayList<Lot> lotList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_lots);

        try
        {
            // Set up your ActionBar
            final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            // set actionbar background color
            final int actionBarColor = getResources().getColor(R.color.action_bar);
            actionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));
            actionBar.setTitle("Lots");

            if(da.isDataToUpdateServer())
            {
                if (da.isConnectedToInternet())
                {
                    new GetAllServerLots().execute(da.AppSettingsDetail().getServerUrl() + "lots/" + da.AppSettingsDetail().getUpdateUser());
                }
                else
                {
                    MessagePopup alert = new MessagePopup(AllLots.this, "Error!", "No Internet Connection");
                    alert.show();
                }
            }
            else
            {
                //check if external storage is available and not read only; and save lot detail to device file
                if (!da.isExternalStorageAvailable() || da.isExternalStorageReadOnly())
                {
                    MessagePopup alert = new MessagePopup(AllLots.this, "Error!", "No storage device found");
                    alert.show();
                }
                else
                {
                    new GetAllDeviceLots().execute();
                }
            }
        }
        catch (Exception ex) {}
    }

    private class GetAllServerLots extends AsyncTask<String, Void, ArrayList<Lot>>
    {
        private final ProgressDialog dialog = new ProgressDialog(AllLots.this);
        @Override
        protected void onPostExecute(final ArrayList<Lot> result)
        {
            try {
                if (result != null && result.size() > 0) {
                    //sort alphabetically by title
                    Collections.sort(result, new Comparator<Lot>() {
                        public int compare(Lot a, Lot b) {
                            return a.getItemName().compareTo(b.getItemName());
                        }
                    });

                    final ListView listView = (ListView) findViewById(R.id.all_lots_listview);
                    adapter = new LotAdapter(AllLots.this, R.layout.all_lots_layout, result);

                    listView.setAdapter(adapter);
                    da.setListViewHeightBasedOnChildren(listView);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                            final Lot lot = new Lot(
                                    result.get(position).getLotId(),
                                    result.get(position).getItemId(),
                                    result.get(position).getContainerId(),
                                    result.get(position).getLocationId(),
                                    result.get(position).getBarcode(),
                                    result.get(position).getItemName(),
                                    result.get(position).getLocationDetail(),
                                    result.get(position).getQuantity(),
                                    result.get(position).getStatus(),
                                    result.get(position).getVersion(),
                                    result.get(position).getScale(),
                                    result.get(position).getLastUpdated(),
                                    result.get(position).getLastUpdatedBy(),
                                    result.get(position).getUpdated());
                            //pass clicked lot to LotDetail class to display the detail of the clicked lot
                            Intent i = new Intent(AllLots.this, LotDetail.class);
                            i.putExtra("ClickedLot", lot);
                            startActivity(i);
                        }
                    });
                }
                else
                {
                    MessagePopup alert = new MessagePopup(AllLots.this, "Error!", "There is no lot data found on the server");
                    alert.show();
                }
            }
            catch (Exception ex){}
            dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Please wait...");
            dialog.show();
        }

        @Override
        protected ArrayList<Lot> doInBackground(String... urls) {
            lotList = new ArrayList<Lot>();
            try
            {
                lotList = da.GetAllLot(da.getJSONdata(urls[0]));
            }
            catch(Throwable t)
            {
                t.printStackTrace();
            }
            return lotList;
        }
    }

    private class GetAllDeviceLots extends AsyncTask<String, Void, ArrayList<Lot>>
    {
        private final ProgressDialog dialog = new ProgressDialog(AllLots.this);
        @Override
        protected void onPostExecute(final ArrayList<Lot> result)
        {
            try {
                if (result != null && result.size() > 0) {
                    //sort alphabetically by title
                    Collections.sort(result, new Comparator<Lot>() {
                        public int compare(Lot a, Lot b) {
                            return a.getItemName().compareTo(b.getItemName());
                        }
                    });

                    final ListView listView = (ListView) findViewById(R.id.all_lots_listview);
                    adapter = new LotAdapter(AllLots.this, R.layout.all_lots_layout, result);

                    listView.setAdapter(adapter);
                    da.setListViewHeightBasedOnChildren(listView);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                            final Lot lot = new Lot(
                                    result.get(position).getLotId(),
                                    result.get(position).getItemId(),
                                    result.get(position).getContainerId(),
                                    result.get(position).getLocationId(),
                                    result.get(position).getBarcode(),
                                    result.get(position).getItemName(),
                                    result.get(position).getLocationDetail(),
                                    result.get(position).getQuantity(),
                                    result.get(position).getStatus(),
                                    result.get(position).getVersion(),
                                    result.get(position).getScale(),
                                    result.get(position).getLastUpdated(),
                                    result.get(position).getLastUpdatedBy(),
                                    result.get(position).getUpdated());
                            //pass clicked lot to LotDetail class to display the detail of the clicked lot
                            Intent i = new Intent(AllLots.this, LotDetail.class);
                            i.putExtra("ClickedLot", lot);
                            startActivity(i);
                        }
                    });
                }
                else
                {
                    MessagePopup alert = new MessagePopup(AllLots.this, "Error!", "There is no lot data found on your device");
                    alert.show();
                }
            }
            catch (Exception ex){}
            dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Please wait...");
            dialog.show();
        }

        @Override
        protected ArrayList<Lot> doInBackground(String... params) {
            lotList = new ArrayList<Lot>();
            try
            {
                // get already downloaded lots data from device and display on all lots page/activity
                lotList = da.GetAllLot(da.getJsonDatafromExternalStorage(da.lotFile));
                return lotList;
            }
            catch(Throwable t) {
                t.printStackTrace();
            }
            return null;
        }
    }

    // override the actionbar up button navigating to parent activity but navigate to previous activity
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
