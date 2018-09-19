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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class LotMigrations extends AppCompatActivity
{
    DataAccess da = new DataAccess(this);
    ArrayList<Migration> lotMigrations, selectedLotMigrations;
    LotMigrationAdapter adapter;
    Lot selectedLot;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lot_migrations);

        try
        {
            // Set up your ActionBar
            final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            // set actionbar background color
            final int actionBarColor = getResources().getColor(R.color.action_bar);
            actionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));

            //get selected lot passed as parameter from other Pages
            Intent i = getIntent();
            selectedLot = (Lot) i.getSerializableExtra("ClickedLot");

            actionBar.setTitle("Lot Migrations " + " (" + selectedLot.getItemName() + " : " + selectedLot.getBarcode() + ")");
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
                new GetASpecificLotMigrations().execute(da.AppSettingsDetail().getServerUrl() + "GetAlotMigrations/" + selectedLot.getLotId());
            }
            else
            {
                MessagePopup alert = new MessagePopup(LotMigrations.this, "Error!", "No Internet Connection");
                alert.show();
            }
        }
        else
        {
            //check if external storage is available and not read only; and save lot detail to device file
            if (!da.isExternalStorageAvailable() || da.isExternalStorageReadOnly())
            {
                MessagePopup alert = new MessagePopup(LotMigrations.this, "Error!", "No storage device found");
                alert.show();
            }
            else
            {
                new GetASpecificLotMigrations().execute();
            }
        }
    }

    private class GetASpecificLotMigrations extends AsyncTask<String, Void, ArrayList<Migration>>
    {
        @Override
        protected void onPostExecute(final ArrayList<Migration> result)
        {
            try {
                if (result != null && result.size() > 0)
                {
                    final ListView listView = (ListView) findViewById(R.id.all_lot_migrations_listview);
                    adapter = new LotMigrationAdapter(LotMigrations.this, R.layout.all_lot_migrations_layout, result);

                    listView.setAdapter(adapter);
                    da.setListViewHeightBasedOnChildren(listView);
                }
                else
                {
                    MessagePopup alert = new MessagePopup(LotMigrations.this, "Error!", "The selected lot have not been migrated before");
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
        protected ArrayList<Migration> doInBackground(String... urls) {
            lotMigrations = new ArrayList<Migration>();
            selectedLotMigrations = new ArrayList<Migration>();
            try
            {
                // get lot migrations data either from server or device depending on value of the "data to update" in settings
                if(da.isDataToUpdateServer())
                    lotMigrations = da.GetAllMigration(da.getJSONdata(urls[0]));
                else
                    lotMigrations = da.GetAllMigration(da.getJsonDatafromExternalStorage(da.migrationFile));

                //get all migrations of a selected lot and display on the lot migration page/activity
                for (Migration lotmig : lotMigrations)
                {
                    if (selectedLot.getLotId() == lotmig.getLotId())
                    {
                        selectedLotMigrations.add(lotmig);
                    }
                }
                return selectedLotMigrations;
            }
            catch(Throwable t) {
                t.printStackTrace();
            }
            return null;
        }
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
        return super.onOptionsItemSelected(item);
    }
}
