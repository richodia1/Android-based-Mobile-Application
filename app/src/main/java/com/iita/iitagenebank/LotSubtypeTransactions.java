package com.iita.iitagenebank;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class LotSubtypeTransactions extends AppCompatActivity
{
    DataAccess da = new DataAccess(this);
    ArrayList<SubtypeTransaction> lotSubtypeTransactions, selectedLotSubtypeTransactions;
    LotSubtypeTransactionAdapter adapter;
    Lot  selectedLot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lot_subtype_transactions);
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

            actionBar.setTitle("Lot Transactions" + " (" + selectedLot.getBarcode() + ")");
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
                new GetALotSubtypeTransactions().execute(da.AppSettingsDetail().getServerUrl() + "GetALotSubtypeTransactions/" + selectedLot.getLotId());
            }
            else
            {
                MessagePopup alert = new MessagePopup(LotSubtypeTransactions.this, "Error!", "No Internet Connection");
                alert.show();
            }
        }
        else
        {
            //check if external storage is available and not read only; and save lot detail to device file
            if (!da.isExternalStorageAvailable() || da.isExternalStorageReadOnly())
            {
                MessagePopup alert = new MessagePopup(LotSubtypeTransactions.this, "Error!", "No storage device found");
                alert.show();
            }
            else
            {
                new GetALotSubtypeTransactions().execute();
            }
        }
    }

    private class GetALotSubtypeTransactions extends AsyncTask<String, Void, ArrayList<SubtypeTransaction>>
    {
        @Override
        protected void onPostExecute(final ArrayList<SubtypeTransaction> result)
        {
            try {
                if (result != null && result.size() > 0)
                {
                    final ListView listView = (ListView) findViewById(R.id.all_lot_subtype_transaction_listview);
                    adapter = new LotSubtypeTransactionAdapter(LotSubtypeTransactions.this, R.layout.all_subtype_transaction_layout, result);

                    listView.setAdapter(adapter);
                    da.setListViewHeightBasedOnChildren(listView);
                }
                else
                {
                    MessagePopup alert = new MessagePopup(LotSubtypeTransactions.this, "Error!", "The selected lot have no subtype transaction");
                    alert.show();
                }
            }
            catch (Exception ex){}
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<SubtypeTransaction> doInBackground(String... urls) {
            lotSubtypeTransactions = new ArrayList<SubtypeTransaction>();
            selectedLotSubtypeTransactions = new ArrayList<SubtypeTransaction>();
            try
            {
                // get lot subtype transactions data either from server or device depending on value of the "data to update" in settings
                if(da.isDataToUpdateServer())
                    lotSubtypeTransactions = da.GetAllLotSubtypeTransaction(da.getJSONdata(urls[0]));
                else
                    lotSubtypeTransactions = da.GetAllLotSubtypeTransaction(da.getJsonDatafromExternalStorage(da.subtypetransactionFile));

                //get all subtype transactions of a selected lot and display on the page/activity
                for (SubtypeTransaction lotstt : lotSubtypeTransactions)
                {
                    if (selectedLot.getLotId() == lotstt.getLotId())
                    {
                        selectedLotSubtypeTransactions.add(lotstt);
                    }
                }
                return selectedLotSubtypeTransactions;
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
        getMenuInflater().inflate(R.menu.menu_lot_subtype_transactions, menu);
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
        else if (id == R.id.action_record_subtype)
        {
            RecordSubtypeTransactionPopup alert = new RecordSubtypeTransactionPopup(LotSubtypeTransactions.this, selectedLot);
            alert.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
