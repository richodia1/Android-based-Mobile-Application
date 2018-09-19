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

public class AllFieldVariables extends AppCompatActivity
{
    DataAccess da = new DataAccess(this);
    ArrayList<FieldVariable> fieldVariables, selectedLotFieldVariables;
    FieldVariableAdapter adapter;
    Lot  selectedLot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_field_variables);

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

            actionBar.setTitle("Field Variables " + " (" + selectedLot.getBarcode() + ")");
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
                new GetASpecificLotFieldVariables().execute(da.AppSettingsDetail().getServerUrl() + "getAlotfieldVariables/" + selectedLot.getLotId());
            }
            else
            {
                MessagePopup alert = new MessagePopup(AllFieldVariables.this, "Error!", "No Internet Connection");
                alert.show();
            }
        }
        else
        {
            //check if external storage is available and not read only; and save lot detail to device file
            if (!da.isExternalStorageAvailable() || da.isExternalStorageReadOnly())
            {
                MessagePopup alert = new MessagePopup(AllFieldVariables.this, "Error!", "No storage device found");
                alert.show();
            }
            else
            {
                new GetASpecificLotFieldVariables().execute();
            }
        }
    }

    private class GetASpecificLotFieldVariables extends AsyncTask<String, Void, ArrayList<FieldVariable>>
    {
        @Override
        protected void onPostExecute(final ArrayList<FieldVariable> result)
        {
            try {
                if (result != null && result.size() > 0)
                {
                    //sort alphabetically by variable name
                    Collections.sort(result, new Comparator<FieldVariable>() {
                        public int compare(FieldVariable a, FieldVariable b) {
                            return a.getVar().compareTo(b.getVar());
                        }
                    });

                    final ListView listView = (ListView) findViewById(R.id.all_field_variables_listview);
                    adapter = new FieldVariableAdapter(AllFieldVariables.this, R.layout.all_field_variables_layout, result);

                    listView.setAdapter(adapter);
                    da.setListViewHeightBasedOnChildren(listView);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                            final FieldVariable fieldVar = new FieldVariable(
                                    result.get(position).getFieldvarId(),
                                    result.get(position).getCreatedBy(),
                                    result.get(position).getCreatedDate(),
                                    result.get(position).getLastUpdated(),
                                    result.get(position).getLastUpdatedBy(),
                                    result.get(position).getVersion(),
                                    result.get(position).getDate(),
                                    result.get(position).getQty(),
                                    result.get(position).getVar(),
                                    result.get(position).getLotId(),
                                    result.get(position).getUpdated());
                            //pass clicked lot to LotDetail class to display the detail of the clicked lot
                            Intent i = new Intent(AllFieldVariables.this, FieldVariableDetail.class);
                            i.putExtra("ClickedFieldVariable", fieldVar);
                            startActivity(i);
                        }
                    });
                    listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
                    {
                        public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int position, long id)
                        {
                            AllFieldVariableSelectedItemPopup alert = new AllFieldVariableSelectedItemPopup(AllFieldVariables.this, result, position, adapter);
                            alert.show();
                            return true;
                        }
                    });
                }
                else
                {
                    MessagePopup alert = new MessagePopup(AllFieldVariables.this, "Error!", "The selected lot have no field variables");
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
        protected ArrayList<FieldVariable> doInBackground(String... urls) {
            fieldVariables = new ArrayList<FieldVariable>();
            selectedLotFieldVariables = new ArrayList<FieldVariable>();
            try
            {
                // get field variables data either from server or device depending on value of the data to update in settings
                if(da.isDataToUpdateServer())
                    fieldVariables = da.GetAllFieldVariable(da.getJSONdata(urls[0]));
                else
                    fieldVariables = da.GetAllFieldVariable(da.getJsonDatafromExternalStorage(da.fieldVariableFile));

                //get all field variables of a selected lot and display on all field variables page/activity
                for (FieldVariable fieldvar : fieldVariables)
                {
                    if (selectedLot.getLotId() == fieldvar.getLotId())
                    {
                        selectedLotFieldVariables.add(fieldvar);
                    }
                }
                return selectedLotFieldVariables;
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
        getMenuInflater().inflate(R.menu.menu_all_field_variables, menu);
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
        else if (id == R.id.action_record_field_variables)
        {
            RecordFieldVariablePopup alert = new RecordFieldVariablePopup(AllFieldVariables.this, selectedLot);
            alert.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
