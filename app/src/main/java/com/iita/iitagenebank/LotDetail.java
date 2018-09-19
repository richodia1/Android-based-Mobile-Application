package com.iita.iitagenebank;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class LotDetail extends AppCompatActivity
{
    Lot  clickedLot, selectedLot;
    DataAccess da = new DataAccess(this);
    private TextView lot_name , quantity, scale, locationDetail, barcode, version, lastUpdated, lastUpdatedBy;
    ArrayList<Lot> lotList;
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lot_detail);
    }

    @Override
    public void onResume() {
        super.onResume();
        try
        {
            //get selected lot passed as parameter from other Pages
            Intent i = getIntent();
            selectedLot = (Lot) i.getSerializableExtra("ClickedLot");

            if(da.isDataToUpdateServer())
            {
                if (da.isConnectedToInternet())
                {
                    new GetLotDetailAsync().execute(da.AppSettingsDetail().getServerUrl() + "GetLotDetail/" + selectedLot.getLotId());
                }
                else
                {
                    MessagePopup alert = new MessagePopup(LotDetail.this, "Error!", "No Internet Connection");
                    alert.show();
                }
            }
            else
            {
                // get already downloaded lots data from device
                lotList = da.GetAllLot(da.getJsonDatafromExternalStorage(da.lotFile));
                for (Lot lot : lotList)
                {
                    if (selectedLot.getLotId() == lot.getLotId())
                    {
                        clickedLot = lot;
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
        actionBar.setTitle(clickedLot.getItemName());

        lot_name = (TextView) findViewById(R.id.lot_name);
        lot_name.setText(clickedLot.getItemName());
        quantity = (TextView) findViewById(R.id.quantity);
        quantity.setText(String.valueOf((int) clickedLot.getQuantity()));
        scale = (TextView) findViewById(R.id.scale);
        scale.setText(clickedLot.getScale());
        locationDetail = (TextView) findViewById(R.id.locationDetail);
        locationDetail.setText(clickedLot.getLocationDetail());
        barcode = (TextView) findViewById(R.id.barcode);
        barcode.setText(String.valueOf(clickedLot.getBarcode()));
        version = (TextView) findViewById(R.id.version);
        version.setText(String.valueOf(clickedLot.getVersion()));
        lastUpdated = (TextView) findViewById(R.id.lastUpdated);
        lastUpdated.setText(da.ConvertDateFormat(clickedLot.getLastUpdated()));
        lastUpdatedBy = (TextView) findViewById(R.id.lastUpdatedBy);
        lastUpdatedBy.setText(clickedLot.getLastUpdatedBy());
    }

    private class GetLotDetailAsync extends AsyncTask<String, Void, Lot>
    {
        @Override
        protected void onPostExecute(final Lot result)
        {
            try
            {
                clickedLot = result;
                PopulateControl();
            }
            catch (Exception ex){}
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Lot doInBackground(String... urls)
        {
            clickedLot = new Lot();
            try
            {
                clickedLot = gson.fromJson(da.getJSONdata(urls[0]), Lot.class);
            }
            catch(Throwable t)
            {
                t.printStackTrace();
            }
            return clickedLot;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lot_detail, menu);
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
        else if (id == R.id.action_change_lot_quantity)
        {
            EditLotPopup alert = new EditLotPopup(LotDetail.this, clickedLot);
            alert.show();
            return true;
        }
        else if (id == R.id.action_view_field_variables)
        {
            if(da.isDataToUpdateServer())
            {
                if (da.isConnectedToInternet())
                {
                    //pass clicked lot to AllFieldVariables class to display all the field variables of the clicked lot
                    Intent i = new Intent(LotDetail.this, AllFieldVariables.class);
                    i.putExtra("ClickedLot", clickedLot);
                    startActivity(i);
                }
                else
                {
                    MessagePopup alert = new MessagePopup(LotDetail.this, "Error!", "No Internet Connection");
                    alert.show();
                }
            }
            else
            {
                //pass clicked lot to AllFieldVariables class to display all the field variables of the clicked lot
                Intent i = new Intent(LotDetail.this, AllFieldVariables.class);
                i.putExtra("ClickedLot", clickedLot);
                startActivity(i);
            }

            return true;
        }
        else if (id == R.id.action_delete_all_field_variables)
        {
            DeleteALotFieldVariablesPopup alert = new DeleteALotFieldVariablesPopup(LotDetail.this, clickedLot);
            alert.show();
            return true;
        }
        else if (id == R.id.action_view_lot_variables)
        {
            if(da.isDataToUpdateServer())
            {
                if (da.isConnectedToInternet())
                {
                    //pass clicked lot to AllLotVariables class to display all the lot variables of the clicked lot
                    Intent i = new Intent(LotDetail.this, AllLotVariables.class);
                    i.putExtra("ClickedLot", clickedLot);
                    startActivity(i);
                }
                else
                {
                    MessagePopup alert = new MessagePopup(LotDetail.this, "Error!", "No Internet Connection");
                    alert.show();
                }
            }
            else
            {
                //pass clicked lot to AllLotVariables class to display all the lot variables of the clicked lot
                Intent i = new Intent(LotDetail.this, AllLotVariables.class);
                i.putExtra("ClickedLot", clickedLot);
                startActivity(i);
            }

            return true;
        }
        else if (id == R.id.action_migrate_lot)
        {
            MigrateLotPopup alert = new MigrateLotPopup(LotDetail.this, clickedLot);
            alert.show();
            return true;
        }
        else if (id == R.id.action_view_migrations)
        {
            //pass clicked lot to LotMigration class to display all the migrations of the clicked lot
            Intent i = new Intent(LotDetail.this, LotMigrations.class);
            i.putExtra("ClickedLot", clickedLot);
            startActivity(i);
            return true;
        }
        else if (id == R.id.action_view_subtype)
        {
            //pass clicked lot to LotSubtypeTransactions class to display all the subtype transactions of the clicked lot
            Intent i = new Intent(LotDetail.this, LotSubtypeTransactions.class);
            i.putExtra("ClickedLot", clickedLot);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

