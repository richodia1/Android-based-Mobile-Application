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

public class AllLotVariables extends AppCompatActivity
{
    DataAccess da = new DataAccess(this);
    ArrayList<LotVariable> lotVariables, selectedLotVariables;
    LotVariableAdapter adapter;
    Lot  selectedLot;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_lot_variables);

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

            actionBar.setTitle("Lot Variables " + " (" + selectedLot.getBarcode() + ")");
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
                new GetASpecificLotVariables().execute(da.AppSettingsDetail().getServerUrl() + "getAlotVariables/" + selectedLot.getLotId());
            }
            else
            {
                MessagePopup alert = new MessagePopup(AllLotVariables.this, "Error!", "No Internet Connection");
                alert.show();
            }
        }
        else
        {
            //check if external storage is available and not read only; and save lot detail to device file
            if (!da.isExternalStorageAvailable() || da.isExternalStorageReadOnly())
            {
                MessagePopup alert = new MessagePopup(AllLotVariables.this, "Error!", "No storage device found");
                alert.show();
            }
            else
            {
                new GetASpecificLotVariables().execute();
            }
        }
    }

    private class GetASpecificLotVariables extends AsyncTask<String, Void, ArrayList<LotVariable>>
    {
        @Override
        protected void onPostExecute(final ArrayList<LotVariable> result)
        {
            try {
                if (result != null && result.size() > 0)
                {
                    //sort alphabetically by variable name
                    Collections.sort(result, new Comparator<LotVariable>() {
                        public int compare(LotVariable a, LotVariable b) {
                            return a.getVariableName().compareTo(b.getVariableName());
                        }
                    });

                    final ListView listView = (ListView) findViewById(R.id.all_lot_variables_listview);
                    adapter = new LotVariableAdapter(AllLotVariables.this, R.layout.all_field_variables_layout, result);

                    listView.setAdapter(adapter);
                    da.setListViewHeightBasedOnChildren(listView);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                            final LotVariable lotVar = new LotVariable(
                                    result.get(position).getLotvariableId(),
                                    result.get(position).getCreatedBy(),
                                    result.get(position).getCreatedDate(),
                                    result.get(position).getLastUpdated(),
                                    result.get(position).getLastUpdatedBy(),
                                    result.get(position).getVersion(),
                                    result.get(position).getQuantity(),
                                    result.get(position).getVariabledate(),
                                    result.get(position).getLotId(),
                                    result.get(position).getVariableId(),
                                    result.get(position).getVariableName(),
                                    result.get(position).getUpdated());
                            //pass clicked lot to LotDetail class to display the detail of the clicked lot
                            Intent i = new Intent(AllLotVariables.this, LotVariableDetail.class);
                            i.putExtra("ClickedLotVariable", lotVar);
                            startActivity(i);
                        }
                    });
                }
                else
                {
                    MessagePopup alert = new MessagePopup(AllLotVariables.this, "Error!", "The selected lot have no variables");
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
        protected ArrayList<LotVariable> doInBackground(String... urls) {
            lotVariables = new ArrayList<LotVariable>();
            selectedLotVariables = new ArrayList<LotVariable>();
            try
            {
                // get lot variables data either from server or device depending on value of the "data to update" in settings
                if(da.isDataToUpdateServer())
                    lotVariables = da.GetAllLotVariable(da.getJSONdata(urls[0]));
                else
                    lotVariables = da.GetAllLotVariable(da.getJsonDatafromExternalStorage(da.lotVariableFile));

                //get all field variables of a selected lot and display on all field variables page/activity
                for (LotVariable lotvar : lotVariables)
                {
                    if (selectedLot.getLotId() == lotvar.getLotId())
                    {
                        selectedLotVariables.add(lotvar);
                    }
                }
                return selectedLotVariables;
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
        getMenuInflater().inflate(R.menu.menu_all_lot_variables, menu);
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
        else if (id == R.id.action_record_lot_variables)
        {
            RecordLotVariablePopup alert = new RecordLotVariablePopup(AllLotVariables.this, selectedLot);
            alert.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
