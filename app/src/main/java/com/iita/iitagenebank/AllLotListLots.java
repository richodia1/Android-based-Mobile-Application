package com.iita.iitagenebank;

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

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AllLotListLots extends AppCompatActivity {
    DataAccess da = new DataAccess(this);
    LotAdapter adapter;
    ArrayList<Lot> lotList;
    ArrayList<Lot> lotListlots;
    LotList selectedLotList = new LotList();
    ArrayList<LotListLot> lotListlotsPair;
    ArrayList<LotListLot> selectedlotListlotsPair;
    Gson gson = new Gson();

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

            //get selected lot passed as parameter from other Pages
            Intent i = getIntent();
            selectedLotList = (LotList) i.getSerializableExtra("ClickedLotList");

            actionBar.setTitle(selectedLotList.getName());
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
                new GetASpecificLotListLots().execute(da.AppSettingsDetail().getServerUrl() + "GetASpecificLotListLots/" + selectedLotList.getLotlistId());
            }
            else
            {
                MessagePopup alert = new MessagePopup(AllLotListLots.this, "Error!", "No Internet Connection");
                alert.show();
            }
        }
        else
        {
            //check if external storage is available and not read only; and save lot detail to device file
            if (!da.isExternalStorageAvailable() || da.isExternalStorageReadOnly())
            {
                MessagePopup alert = new MessagePopup(AllLotListLots.this, "Error!", "No storage device found");
                alert.show();
            }
            else
            {
                new GetASpecificDeviceLotListLots().execute();
            }
        }
    }

    private class GetASpecificLotListLots extends AsyncTask<String, Void, ArrayList<Lot>>
    {
        @Override
        protected void onPostExecute(final ArrayList<Lot> result)
        {
            try
            {
                if (result != null && result.size() > 0)
                {
                    //sort alphabetically by title
                    Collections.sort(result, new Comparator<Lot>() {
                        public int compare(Lot a, Lot b) {
                            return a.getItemName().compareTo(b.getItemName());
                        }
                    });

                    final ListView listView = (ListView) findViewById(R.id.all_lots_listview);
                    adapter = new LotAdapter(AllLotListLots.this, R.layout.all_lots_layout, result);

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
                            Intent i = new Intent(AllLotListLots.this, LotDetail.class);
                            i.putExtra("ClickedLot", lot);
                            startActivity(i);
                        }
                    });
                }
                else
                {
                    MessagePopup alert = new MessagePopup(AllLotListLots.this, "Error!", "There is no lot data found on the server");
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

    private class GetASpecificDeviceLotListLots extends AsyncTask<String, Void, ArrayList<Lot>>
    {
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
                    adapter = new LotAdapter(AllLotListLots.this, R.layout.all_lots_layout, result);

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
                            Intent i = new Intent(AllLotListLots.this, LotDetail.class);
                            i.putExtra("ClickedLot", lot);
                            startActivity(i);
                        }
                    });
                }
                else
                {
                    MessagePopup alert = new MessagePopup(AllLotListLots.this, "Error!", "There is no lot data found on your device");
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
        protected ArrayList<Lot> doInBackground(String... params) {
            lotList = new ArrayList<Lot>();
            lotListlots = new ArrayList<Lot>();
            lotListlotsPair= new ArrayList<LotListLot>();
            selectedlotListlotsPair = new ArrayList<LotListLot>();
            try
            {
                // get all downloaded lots data from device
                lotList = da.GetAllLot(da.getJsonDatafromExternalStorage(da.lotFile));
                // get all downloaded lot list lot data from device (NB: lot list lot table pairs lot list table with lot table
                lotListlotsPair = da.GetAllLotListLot(da.getJsonDatafromExternalStorage(da.lotListLotFile));
                // get all the lot id with the selected lot lists id pair from lot list lot table
                for(LotListLot lll : lotListlotsPair)
                {
                    if(lll.getLotlistId() == selectedLotList.getLotlistId())
                        selectedlotListlotsPair.add(lll);
                }
                // use all the lot id gotten above from the lot list lot table to get all the matched lots from the lot table
                for(LotListLot lll : selectedlotListlotsPair)
                {
                    for(Lot lot : lotList)
                    {
                        if(lll.getLotId() == lot.getLotId())
                            lotListlots.add(lot);
                    }
                }
                return lotListlots; // return and display matched lots
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
        getMenuInflater().inflate(R.menu.menu_lot_list_lots, menu);
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
        else if (id == R.id.action_add_new_lot_item)
        {
            AddNewLotItemPopup alert = new AddNewLotItemPopup(AllLotListLots.this, selectedLotList);
            alert.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
