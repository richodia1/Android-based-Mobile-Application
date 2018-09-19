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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AllLocations extends AppCompatActivity
{
    DataAccess da = new DataAccess(this);
    LotLocationAdapter adapter;
    ArrayList<Location> allChildLocations = new ArrayList<Location>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_locations);

        try
        {
            // Set up your ActionBar
            final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            // set actionbar background color
            final int actionBarColor = getResources().getColor(R.color.action_bar);
            actionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));
            actionBar.setTitle("All Lot Locations");
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
                new  GetLocationsAsync().execute(da.AppSettingsDetail().getServerUrl() + "childlocations/" + da.AppSettingsDetail().getUpdateUser());
            }
            else
            {
                MessagePopup alert = new MessagePopup(AllLocations.this, "Error!", "No Internet Connection");
                alert.show();
            }
        }
        else
        {
            //check if external storage is available and not read only; and save lot detail to device file
            if (!da.isExternalStorageAvailable() || da.isExternalStorageReadOnly())
            {
                MessagePopup alert = new MessagePopup(AllLocations.this, "Error!", "No storage device found");
                alert.show();
            }
            else
            {
                new  GetLocationsAsync().execute();
            }
        }
    }

    private class GetLocationsAsync extends AsyncTask<String, Void, ArrayList<Location>> {
        private final ProgressDialog dialog = new ProgressDialog(AllLocations.this);
        @Override
        protected void onPostExecute(final ArrayList<Location> result) {
            try {
                if (result != null && result.size() > 0)
                {
                    //allLocations = result;
                    allChildLocations = result;

                    final ListView listView = (ListView) findViewById(R.id.all_lot_locations_listview);
                    adapter = new LotLocationAdapter(AllLocations.this, R.layout.all_lot_locations_layout, allChildLocations);

                    listView.setAdapter(adapter);
                    da.setListViewHeightBasedOnChildren(listView);
                }
                else
                {
                    MessagePopup alert = new MessagePopup(AllLocations.this, "Error!", "Could not fetch location lists");
                    alert.show();
                }
            }
            catch (Exception ex){}
            dialog.dismiss();
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            dialog.setMessage("Please wait...");
            dialog.show();
        }

        @Override
        protected ArrayList<Location> doInBackground(String... urls) {
            ArrayList<Location> LocationList = new ArrayList<Location>();
            try
            {
                // get location data either from server or device depending on value of the data to update in settings
                if (da.isDataToUpdateServer())
                    LocationList = da.GetAllLocation(da.getJSONdata(urls[0]));
                else
                    LocationList = da.GetAllLocation(da.getJsonDatafromExternalStorage(da.childLocationFile));

                return LocationList;
            }
            catch (Throwable t)
            {
                t.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_all_locations, menu);
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
        else if (id == R.id.action_add_new_location)
        {
            AddNewLocationPopup alert = new AddNewLocationPopup(AllLocations.this);
            alert.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
