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
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AllLotLists extends AppCompatActivity {
    DataAccess da = new DataAccess(this);
    LotListAdapter adapter;
    ArrayList<LotList> lotListList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_lot_lists);
    try
    {
        // Set up your ActionBar
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        // set actionbar background color
        final int actionBarColor = getResources().getColor(R.color.action_bar);
        actionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));
        actionBar.setTitle("Lot Lists");
    }
    catch (Exception ex) {}
}

    @Override
    public void onResume()
    {
        super.onResume();  // Always call the superclass method first

        if(da.isDataToUpdateServer())
        {
            if (da.isConnectedToInternet())
            {
                new GetAllServerLotLists().execute(da.AppSettingsDetail().getServerUrl() + "getMyLotlists/" + da.AppSettingsDetail().getUpdateUser());
            }
            else
            {
                MessagePopup alert = new MessagePopup(AllLotLists.this, "Error!", "No Internet Connection");
                alert.show();
            }
        }
        else
        {
            //check if external storage is available and not read only; and save lot list detail to device file
            if (!da.isExternalStorageAvailable() || da.isExternalStorageReadOnly())
            {
                MessagePopup alert = new MessagePopup(AllLotLists.this, "Error!", "No storage device found");
                alert.show();
            }
            else
            {
                new GetAllDeviceLotLists().execute();
            }
        }
    }

private class GetAllServerLotLists extends AsyncTask<String, Void, ArrayList<LotList>>
{
    private final ProgressDialog dialog = new ProgressDialog(AllLotLists.this);
    @Override
    protected void onPostExecute(final ArrayList<LotList> result)
    {
        try {
            if (result != null && result.size() > 0) {

                final ListView listView = (ListView) findViewById(R.id.all_lot_lists_listview);
                adapter = new LotListAdapter(AllLotLists.this, R.layout.all_lot_lists_layout, result);

                listView.setAdapter(adapter);
                da.setListViewHeightBasedOnChildren(listView);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                        final LotList lotList = new LotList(
                                result.get(position).getLotlistId(),
                                result.get(position).getCreatedBy(),
                                result.get(position).getCreatedDate(),
                                result.get(position).getLastUpdated(),
                                result.get(position).getLastUpdatedBy(),
                                result.get(position).getName(),
                                result.get(position).getOwner_id());
                        //pass clicked lot list to LotDetail class to display the lots that belongs to the lists
                        Intent i = new Intent(AllLotLists.this, AllLotListLots.class);
                        i.putExtra("ClickedLotList", lotList);
                        startActivity(i);
                    }
                });
                listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int position, long id) {
                        AllLotListSelectedItemPopup alert = new AllLotListSelectedItemPopup(AllLotLists.this, result, position, adapter);
                        alert.show();
                        return true;
                    }
                });
            }
            else
            {
                MessagePopup alert = new MessagePopup(AllLotLists.this, "Error!", "There is no lot list data found on the server");
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
    protected ArrayList<LotList> doInBackground(String... urls) {
        lotListList = new ArrayList<LotList>();
        try
        {
            lotListList = da.GetAllLotList(da.getJSONdata(urls[0]));
        }
        catch(Throwable t)
        {
            t.printStackTrace();
        }
        return lotListList;
    }
}

private class GetAllDeviceLotLists extends AsyncTask<String, Void, ArrayList<LotList>>
{
    @Override
    protected void onPostExecute(final ArrayList<LotList> result)
    {
        try {
            if (result != null && result.size() > 0) {

                final ListView listView = (ListView) findViewById(R.id.all_lot_lists_listview);
                adapter = new LotListAdapter(AllLotLists.this, R.layout.all_lot_lists_layout, result);

                listView.setAdapter(adapter);
                da.setListViewHeightBasedOnChildren(listView);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                        final LotList lotList = new LotList(
                                result.get(position).getLotlistId(),
                                result.get(position).getCreatedBy(),
                                result.get(position).getCreatedDate(),
                                result.get(position).getLastUpdated(),
                                result.get(position).getLastUpdatedBy(),
                                result.get(position).getName(),
                                result.get(position).getOwner_id());
                        //pass clicked lot list to AllLotListLots class to display the lots that belongs to the lists
                        Intent i = new Intent(AllLotLists.this, AllLotListLots.class);
                        i.putExtra("ClickedLotList", lotList);
                        startActivity(i);
                    }
                });
                listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int position, long id) {
                        AllLotListSelectedItemPopup alert = new AllLotListSelectedItemPopup(AllLotLists.this, result, position, adapter);
                        alert.show();
                        return true;
                    }
                });
            }
            else
            {
                MessagePopup alert = new MessagePopup(AllLotLists.this, "Error!", "There is no lot list data found on the device");
                alert.show();
            }
        }
        catch (Exception ex){}
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ArrayList<LotList> doInBackground(String... params) {
        lotListList = new ArrayList<LotList>();
        try
        {
            // get already downloaded lot lists data from device and display on all lot lists page/activity
            lotListList = da.GetAllLotList(da.getJsonDatafromExternalStorage(da.lotListFile));
            return lotListList;
        }
        catch(Throwable t) {
            t.printStackTrace();
        }
        return null;
    }
}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_all_lot_lists, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home)
        {
            onBackPressed();
            return true;
        }
        else if (id == R.id.action_create_new_lot_list)
        {
            CreateNewLotListPopup alert = new CreateNewLotListPopup(AllLotLists.this);
            alert.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
