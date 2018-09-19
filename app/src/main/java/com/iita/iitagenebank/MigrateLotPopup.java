package com.iita.iitagenebank;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Simeon on 07/03/2016.
 */
public class MigrateLotPopup extends Dialog implements View.OnClickListener
{
    public Activity activ;
    TextView popuptitle, old_location;
    EditText reason;
    public Button update_button, cancel_button;
    DataAccess da;
    Lot selectedLot = new Lot();
    ArrayList<Lot> lotList = new ArrayList<Lot>();
    ArrayList<Lot> newLotList = new ArrayList<Lot>();
    ArrayList<Migration> migrationList = new ArrayList<Migration>();
    Migration newLotMigration = new Migration();
    Gson gson = new Gson();
    Type type = new TypeToken<Migration>() {}.getType();
    Spinner parentlocationSpinner, childlocationSpinner;
    List childlocationList = new ArrayList();
    List parentlocationList = new ArrayList();
    ArrayList<Location> allParentLocations = new ArrayList<Location>();
    ArrayList<Location> allChildLocations = new ArrayList<Location>();
    ArrayList<Location> allParentChildLocations = new ArrayList<Location>();
    long parentlocationId = 0;

    public MigrateLotPopup(Activity a, Lot lot)
    {
        super(a);
        this.activ = a;
        this.selectedLot = lot;
        da = new DataAccess(a);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.migrate_lot);
        try
        {
            popuptitle = (TextView) findViewById(R.id.lot_name);
            popuptitle.setText("Migrate Accession (" + selectedLot.getItemName() + ")");

            old_location = (TextView) findViewById(R.id.old_location);
            old_location.setText(selectedLot.getLocationDetail());
            reason = (EditText) findViewById(R.id.reason);

            update_button = (Button) findViewById(R.id.update_button);
            update_button.setOnClickListener(this);

            cancel_button = (Button) findViewById(R.id.cancel_button);
            cancel_button.setOnClickListener(this);

            new  GetLocationsAsync().execute(da.AppSettingsDetail().getServerUrl() + "childlocations/" + da.AppSettingsDetail().getUpdateUser());
            new GetParentLocationsAsync().execute(da.AppSettingsDetail().getServerUrl() + "parentlocations/" + da.AppSettingsDetail().getUpdateUser());
        }
        catch (Exception ex) {}
    }

    private class GetLocationsAsync extends AsyncTask<String, Void, ArrayList<Location>> {
        @Override
        protected void onPostExecute(final ArrayList<Location> result) {
            try {
                if (result != null && result.size() > 0)
                {
                    allChildLocations = result;
                }
            }
            catch (Exception ex){}
        }

        @Override
        protected void onPreExecute()
        {super.onPreExecute();}

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

    private class GetParentLocationsAsync extends AsyncTask<String, Void, ArrayList<Location>> {
        @Override
        protected void onPostExecute(final ArrayList<Location> result) {
            try
            {
                if (result != null && result.size() > 0)
                {
                    allParentLocations = result;
                    parentlocationList = da.getParentLocationNames(allParentLocations);
                    parentlocationSpinner = (Spinner) findViewById(R.id.parentlocationSpinner);

                    // set items of parent location drop down / spinner
                    ArrayAdapter parentlocAdapter = new ArrayAdapter(activ, android.R.layout.simple_spinner_item, parentlocationList);
                    parentlocAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    parentlocationSpinner.setAdapter(parentlocAdapter);

                    //set the current child location detail as the default new location spinner value
                    String currentVar = da.GetParentLocationDetail(allParentLocations, allChildLocations, selectedLot.getLocationId()).getName();
                    if(currentVar!=null && currentVar!="") {
                        ArrayAdapter myAdap = (ArrayAdapter) parentlocationSpinner.getAdapter(); //cast to an ArrayAdapter
                        int spinnerPosition = myAdap.getPosition(currentVar);
                        //set the default according to value
                        parentlocationSpinner.setSelection(spinnerPosition);
                    }
                    parentlocationSpinner.setOnItemSelectedListener(
                            new AdapterView.OnItemSelectedListener() {
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    parentlocationId = result.get(position).getLocationId();
                                    new GetChildLocationsAsync().execute(da.AppSettingsDetail().getServerUrl() + "getAparentChildLocations/" + parentlocationId);
                                }

                                public void onNothingSelected(AdapterView<?> parent) {
                                }
                            });
                }
            }
            catch (Exception ex) {}
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Location> doInBackground(String... urls) {
            ArrayList<Location> LocationList = new ArrayList<Location>();
            try {
                // get location data either from server or device depending on value of the data to update in settings
                if (da.isDataToUpdateServer())
                    LocationList = da.GetAllLocation(da.getJSONdata(urls[0]));
                else
                    LocationList = da.GetAllLocation(da.getJsonDatafromExternalStorage(da.parentLocationFile));

                return LocationList;
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return null;
        }
    }

    private class GetChildLocationsAsync extends AsyncTask<String, Void, ArrayList<Location>> {
        @Override
        protected void onPostExecute(final ArrayList<Location> result) {
            try
            {
                if (result != null && result.size() > 0)
                {
                    allParentChildLocations = result;
                    childlocationList = da.getLocationNames(allParentChildLocations);
                    childlocationSpinner = (Spinner) findViewById(R.id.childlocationSpinner);

                    // set items of child location drop down / spinner
                    ArrayAdapter childlocAdapter = new ArrayAdapter(activ, android.R.layout.simple_spinner_item, childlocationList);
                    childlocAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    childlocationSpinner.setAdapter(childlocAdapter);
                }
            }
            catch (Exception ex) {}
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Location> doInBackground(String... urls) {
            ArrayList<Location> LocationList = new ArrayList<Location>();
            try {
                // get location data either from server or device depending on value of the data to update in settings
                if (da.isDataToUpdateServer())
                    LocationList = da.GetAllLocation(da.getJSONdata(urls[0]));
                else
                    LocationList = da.GetAllParentChildLocations(da.getJsonDatafromExternalStorage(da.childLocationFile), parentlocationId);

                return LocationList;
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.update_button:
                try
                {
                    String Reason = reason.getText().toString().trim();

                    if (Reason.length() == 0)
                    {
                        MessagePopup alert = new MessagePopup(activ, "Error!", "You must enter the reason for migration");
                        alert.show();
                    }
                    else
                    {
                        long LocationID = allParentChildLocations.get(childlocationSpinner.getSelectedItemPosition()).getLocationId();

                        String newLocationName = allParentChildLocations.get(childlocationSpinner.getSelectedItemPosition()).getName();
                        String oldLocationName = selectedLot.getLocationDetail();

                        //set new field variable values to be recorded
                        newLotMigration.setMigId(da.getNewTableRecordID());
                        newLotMigration.setMigrationDate(da.getDate());
                        newLotMigration.setNewLocationId(LocationID);
                        newLotMigration.setNewLocationName(newLocationName);
                        newLotMigration.setOldLocationId(selectedLot.getLocationId());
                        newLotMigration.setOldLocationName(oldLocationName);
                        newLotMigration.setLotId(selectedLot.getLotId());
                        newLotMigration.setReason(Reason);
                        newLotMigration.setCreatedBy(da.UserDeviceDetail().getUsername());
                        newLotMigration.setCreatedDate(da.getDate());
                        newLotMigration.setLastUpdated(da.getDate());
                        newLotMigration.setLastUpdatedBy(da.UserDeviceDetail().getUsername());

                        if (da.isDataToUpdateServer())
                        {
                            if (da.isConnectedToInternet())
                            {
                                new MigrateLotAsyncTask().execute(da.AppSettingsDetail().getServerUrl() + "MigrateLot");
                            }
                            else
                            {
                                MessagePopup alert = new MessagePopup(activ, "Error!", "You cannot add new lot; No Internet Connection");
                                alert.show();
                            }
                        }
                        else {
                            //check if external storage is available and not read only
                            if (!da.isExternalStorageAvailable() || da.isExternalStorageReadOnly()) {
                                MessagePopup alert = new MessagePopup(activ, "Error!", "No storage device found");
                                alert.show();
                            } else {
                                //get all lots from device and update the location id and location detail of the selected lot
                                lotList = da.GetAllLot(da.getJsonDatafromExternalStorage(da.lotFile));
                                Lot lot = new Lot();
                                lot.setLotId(selectedLot.getLotId());
                                lot.setItemId(selectedLot.getItemId());
                                lot.setContainerId(selectedLot.getContainerId());
                                lot.setLocationId(LocationID);
                                lot.setBarcode(selectedLot.getBarcode());
                                lot.setItemName(selectedLot.getItemName());
                                lot.setLocationDetail(newLocationName);
                                lot.setQuantity(selectedLot.getQuantity());
                                lot.setStatus(selectedLot.getStatus());
                                lot.setVersion(selectedLot.getVersion());
                                lot.setScale(selectedLot.getScale());
                                lot.setLastUpdated(da.getDate());
                                lot.setLastUpdatedBy(da.UserDeviceDetail().getUsername());
                                lot.setUpdated(1);

                                for (Lot lo : lotList) {
                                    if (selectedLot.getLotId() == lo.getLotId())
                                        newLotList.add(lot);
                                    else
                                        newLotList.add(lo);
                                }

                                //get all lots from device and update the location id and location detail of the selected lot
                                if (da.GetAllMigration(da.getJsonDatafromExternalStorage(da.migrationFile)) != null)
                                    migrationList = da.GetAllMigration(da.getJsonDatafromExternalStorage(da.migrationFile));
                                migrationList.add(newLotMigration);

                                //save updated lots and migration tables to device
                                da.saveToExternalStorage(gson.toJson(newLotList), da.lotFile);
                                da.saveToExternalStorage(gson.toJson(migrationList), da.migrationFile);

                                MessagePopup alert = new MessagePopup(activ, "Alert!", "The selected lot was successfully migrated on the device");
                                alert.show();

                                Intent i = new Intent(activ, LotDetail.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                activ.startActivity(i);
                                dismiss();
                            }
                        }
                    }
                }
                catch (Exception e)
                {
                    MessagePopup alert = new MessagePopup(activ, "Error!", e.getMessage());
                    alert.show();
                }
                break;
            case R.id.cancel_button:
                dismiss();
                break;
            default:
                break;
        }
    }

    private class MigrateLotAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String jsonDataToSend = "", response = "";
            try {
                jsonDataToSend = gson.toJson(newLotMigration, type);
                response = da.postJSONdata(urls[0], jsonDataToSend);

            } catch (Exception e) {
                response = null;
            }
            return response;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result)
        {
            try
            {
                if (result != null)
                {
                    wsSQLResult response = gson.fromJson(result, wsSQLResult.class);
                    if (Integer.parseInt(response.getWasSuccessful()) == 1)
                    {
                        Intent i = new Intent(activ, LotDetail.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        activ.startActivity(i);
                        dismiss();

                        MessagePopup alert = new MessagePopup(activ, "Alert!", response.getException());
                        alert.show();
                    }
                    else
                    {
                        MessagePopup alert = new MessagePopup(activ, "Error!", response.getException());
                        alert.show();
                    }
                }
                else
                {
                    MessagePopup alert = new MessagePopup(activ, "Error!", "Cannot connect to server");
                    alert.show();
                }
            }
            catch (Exception ex) {}
        }
    }
}
