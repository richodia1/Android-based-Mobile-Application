package com.iita.iitagenebank;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

public class LotVariableDetail extends AppCompatActivity
{
    LotVariable  clickedLotVariable, selectedLotVariable;
    DataAccess da = new DataAccess(this);
    private TextView var, quantity, version, dateCreated, createdBy, variableDate, lastUpdated, lastUpdatedBy;
    ArrayList<LotVariable> lotvarList;
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lot_variable_detail);
    }

    @Override
    public void onResume() {
        super.onResume();
        try
        {
            //get selected lot passed as parameter from other Pages
            Intent i = getIntent();
            selectedLotVariable = (LotVariable) i.getSerializableExtra("ClickedLotVariable");

            if(da.isDataToUpdateServer())
            {
                if (da.isConnectedToInternet())
                {
                    new GetLotVariableDetailAsync().execute(da.AppSettingsDetail().getServerUrl() + "GetLotVariableDetail/" + selectedLotVariable.getLotvariableId());
                }
                else
                {
                    MessagePopup alert = new MessagePopup(LotVariableDetail.this, "Error!", "No Internet Connection");
                    alert.show();
                }
            }
            else
            {
                // get already downloaded lot variables data from device
                lotvarList = da.GetAllLotVariable(da.getJsonDatafromExternalStorage(da.lotVariableFile));
                for (LotVariable lv : lotvarList)
                {
                    if (selectedLotVariable.getLotvariableId() == lv.getLotvariableId())
                    {
                        clickedLotVariable = lv;
                    }
                }
                PopulateControl();
            }
        }
        catch (Exception ex) {}
    }

    void PopulateControl()
    {
        // Set up your ActionBar
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        // set actionbar background color
        final int actionBarColor = getResources().getColor(R.color.action_bar);
        actionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));
        actionBar.setTitle(clickedLotVariable.getVariableName());

        var = (TextView) findViewById(R.id.var);
        var.setText(clickedLotVariable.getVariableName());
        quantity = (TextView) findViewById(R.id.quantity);
        quantity.setText(String.valueOf((int)clickedLotVariable.getQuantity()));
        version = (TextView) findViewById(R.id.version);
        version.setText(String.valueOf(clickedLotVariable.getVersion()));
        dateCreated = (TextView) findViewById(R.id.dateCreated);
        dateCreated.setText(da.ConvertDateFormat(clickedLotVariable.getCreatedDate()));
        createdBy = (TextView) findViewById(R.id.createdBy);
        createdBy.setText(clickedLotVariable.getCreatedBy());
        variableDate = (TextView) findViewById(R.id.variableDate);
        variableDate.setText(da.ConvertDateFormat(clickedLotVariable.getVariabledate()));
        lastUpdated = (TextView) findViewById(R.id.lastUpdated);
        lastUpdated.setText(da.ConvertDateFormat(clickedLotVariable.getLastUpdated()));
        lastUpdatedBy = (TextView) findViewById(R.id.lastUpdatedBy);
        lastUpdatedBy.setText(clickedLotVariable.getLastUpdatedBy());
    }

    private class GetLotVariableDetailAsync extends AsyncTask<String, Void, LotVariable>
    {
        @Override
        protected void onPostExecute(final LotVariable result)
        {
            try
            {
                clickedLotVariable = result;
                PopulateControl();
            }
            catch (Exception ex){}
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected LotVariable doInBackground(String... urls)
        {
            clickedLotVariable = new LotVariable();
            try
            {
                clickedLotVariable = gson.fromJson(da.getJSONdata(urls[0]), LotVariable.class);
            }
            catch(Throwable t)
            {
                t.printStackTrace();
            }
            return clickedLotVariable;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lot_variable_detail, menu);
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
        else if (id == R.id.action_edit_lot_variable)
        {
            if (da.isDataToUpdateServer())
            {
                if (da.isConnectedToInternet())
                {
                    EditLotVariablePopup alert = new EditLotVariablePopup(LotVariableDetail.this, clickedLotVariable);
                    alert.show();
                }
                else
                {
                    MessagePopup alert = new MessagePopup(LotVariableDetail.this, "Error!", "No Internet Connection");
                    alert.show();
                }
            }
            else
            {
                EditLotVariablePopup alert = new EditLotVariablePopup(LotVariableDetail.this, clickedLotVariable);
                alert.show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
