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

public class FieldVariableDetail extends AppCompatActivity
{
    FieldVariable  clickedFieldVariable, selectedFieldVariable;
    DataAccess da = new DataAccess(this);
    private TextView var, quantity_value, version, dateCreated, createdBy, variableDate, lastUpdated, lastUpdatedBy;
    ArrayList<FieldVariable> fieldvarList;
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.field_variable_detail);
    }

    @Override
    public void onResume() {
        super.onResume();
        try
        {
            //get selected lot passed as parameter from other Pages
            Intent i = getIntent();
            selectedFieldVariable = (FieldVariable) i.getSerializableExtra("ClickedFieldVariable");

            if(da.isDataToUpdateServer())
            {
                if (da.isConnectedToInternet())
                {
                    new GetFieldVariableDetailAsync().execute(da.AppSettingsDetail().getServerUrl() + "GetFieldVariableDetail/" + selectedFieldVariable.getFieldvarId());
                }
                else
                {
                    MessagePopup alert = new MessagePopup(FieldVariableDetail.this, "Error!", "No Internet Connection");
                    alert.show();
                }
            }
            else
            {
                // get already downloaded field variables data from device
                fieldvarList = da.GetAllFieldVariable(da.getJsonDatafromExternalStorage(da.fieldVariableFile));
                for (FieldVariable fv : fieldvarList)
                {
                    if (selectedFieldVariable.getFieldvarId() == fv.getFieldvarId())
                    {
                        clickedFieldVariable = fv;
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
        actionBar.setTitle(clickedFieldVariable.getVar());

        var = (TextView) findViewById(R.id.var);
        var.setText(clickedFieldVariable.getVar());
        quantity_value = (TextView) findViewById(R.id.quantity_value);
        quantity_value.setText(clickedFieldVariable.getQty());
        version = (TextView) findViewById(R.id.version);
        version.setText(String.valueOf(clickedFieldVariable.getVersion()));
        dateCreated = (TextView) findViewById(R.id.dateCreated);
        dateCreated.setText(da.ConvertDateFormat(clickedFieldVariable.getCreatedDate()));
        createdBy = (TextView) findViewById(R.id.createdBy);
        createdBy.setText(clickedFieldVariable.getCreatedBy());
        variableDate = (TextView) findViewById(R.id.variableDate);
        variableDate.setText(da.ConvertDateFormat(clickedFieldVariable.getDate()));
        lastUpdated = (TextView) findViewById(R.id.lastUpdated);
        lastUpdated.setText(da.ConvertDateFormat(clickedFieldVariable.getLastUpdated()));
        lastUpdatedBy = (TextView) findViewById(R.id.lastUpdatedBy);
        lastUpdatedBy.setText(clickedFieldVariable.getLastUpdatedBy());
    }

    private class GetFieldVariableDetailAsync extends AsyncTask<String, Void, FieldVariable>
    {
        @Override
        protected void onPostExecute(final FieldVariable result)
        {
            try
            {
                clickedFieldVariable = result;
                PopulateControl();
            }
            catch (Exception ex){}
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected FieldVariable doInBackground(String... urls)
        {
            clickedFieldVariable = new FieldVariable();
            try
            {
                clickedFieldVariable = gson.fromJson(da.getJSONdata(urls[0]), FieldVariable.class);
            }
            catch(Throwable t)
            {
                t.printStackTrace();
            }
            return clickedFieldVariable;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_field_variable_detail, menu);
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
        else if (id == R.id.action_edit_field_variable)
        {
            EditFieldVariablePopup alert = new EditFieldVariablePopup(FieldVariableDetail.this, clickedFieldVariable);
            alert.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

