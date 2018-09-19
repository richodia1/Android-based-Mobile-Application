package com.iita.iitagenebank;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class Synchronize extends AppCompatActivity
{
    DataAccess da = new DataAccess(this);
    Gson gson = new Gson();
    private RadioGroup radioGroupServerType;
    private RadioButton radioTomcat1, radioTomcat2;
    private String server = "";
    private String serverUrl = "";
    private String updateData = "";
    private String lastOperation = "";
    private int newTableRecordID = 0;
    private String updateUser = "";
    Type type = new TypeToken<Database>() {}.getType();
    ArrayList<Lot> lotList;
    ArrayList<LotList> lotListList;
    ArrayList<LotVariable> lotVariableList;
    ArrayList<FieldVariable> fieldVariableList;
    ArrayList<LotListLot> lotListLot;
    ArrayList<Migration> lotMigration;
    ArrayList<SubtypeTransaction> subtypeTransactions;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.synchronize);
        try
        {
            // Set up your ActionBar
            final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            // set actionbar background color
            final int actionBarColor = getResources().getColor(R.color.action_bar);
            actionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));
            actionBar.setTitle("Synchronize Data");

            radioGroupServerType = (RadioGroup) findViewById(R.id.radioGroupServerType);
            radioTomcat1 = (RadioButton) findViewById(R.id.radioTomcat1);
            radioTomcat2 = (RadioButton) findViewById(R.id.radioTomcat2);
            if (da.AppSettingsDetail().getServerUrl().matches(da.psUrl))
            {
                radioTomcat1.setChecked(true);
            }
            else
            {
                radioTomcat2.setChecked(true);
            }
            //set app settings
            server = da.AppSettingsDetail().getServer();
            serverUrl = da.AppSettingsDetail().getServerUrl();
            updateData = da.AppSettingsDetail().getUpdateData();
            lastOperation = da.AppSettingsDetail().getLastOperation();
            newTableRecordID = da.AppSettingsDetail().getNewTableRecordID();
            updateUser = da.AppSettingsDetail().getUpdateUser();

            Button reinitialize_local_data =(Button)findViewById(R.id.reinitialize_local_data);
            reinitialize_local_data.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    //re-initialize database table by table by setting the file content empty
                    da.saveToExternalStorage("", da.lotFile);
                    da.saveToExternalStorage("", da.lotVariableFile);
                    da.saveToExternalStorage("", da.lotListFile);
                    da.saveToExternalStorage("", da.fieldVariableFile);
                    da.saveToExternalStorage("", da.childLocationFile);
                    da.saveToExternalStorage("", da.parentLocationFile);
                    da.saveToExternalStorage("", da.migrationFile);
                    da.saveToExternalStorage("", da.variableFile);
                    da.saveToExternalStorage("", da.lotListLotFile);
                    da.saveToExternalStorage("", da.containerTypeFile);
                    da.saveToExternalStorage("", da.itemFile);
                    da.saveToExternalStorage("", da.itemTypeFile);
                    da.saveToExternalStorage("", da.subtypeFile);
                    da.saveToExternalStorage("", da.subtypetransactionFile);
                    da.saveToExternalStorage("", da.deletedFieldVariableFile);

                    //set the app Settings last operation to "Data Re-Initialization"
                    lastOperation = "Data Re-Initialization";

                    //update settings last operation
                    da.saveAppSettings(server, serverUrl, updateData, lastOperation, 0, updateUser);

                    MessagePopup alert = new MessagePopup(Synchronize.this, "Alert!", "Data Re-Initialization successfully completed!");
                    alert.show();
                }
            });

            Button download_from_server =(Button)findViewById(R.id.download_data_from_server);
            download_from_server.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (da.isConnectedToInternet())
                    {
                        // check the selected server an update settings accordingly
                        radioGroupServerType = (RadioGroup) findViewById(R.id.radioGroupServerType);
                        radioTomcat1 = (RadioButton) findViewById(R.id.radioTomcat1);
                        int selectedId = radioGroupServerType.getCheckedRadioButtonId();
                        // find which radioButton is checked by id
                        if (selectedId == radioTomcat1.getId())
                        {
                            server = da.pserver;
                            serverUrl = da.psUrl;
                        }
                        else
                        {
                            server = da.tserver;
                            serverUrl = da.tsUrl;
                        }
                        lastOperation = "Data Download";

                        //update settings last operation
                        da.saveAppSettings(server, serverUrl, updateData, lastOperation, 0, updateUser);

                        // call AsyncTask to download database data on separate thread
                        new DownloadDatabaseAsyncTask().execute(serverUrl + "getDatabase/" + updateUser);
                    } else
                    {
                        MessagePopup alert = new MessagePopup(Synchronize.this, "Error!", "No Internet Connection");
                        alert.show();
                    }
                }
            });

            Button upload_to_server =(Button)findViewById(R.id.upload_data_to_server);
            upload_to_server.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (da.isConnectedToInternet())
                    {
                        // check the selected server an update settings accordingly
                        radioGroupServerType = (RadioGroup) findViewById(R.id.radioGroupServerType);
                        radioTomcat1 = (RadioButton) findViewById(R.id.radioTomcat1);
                        int selectedId = radioGroupServerType.getCheckedRadioButtonId();
                        // find which radioButton is checked by id
                        if (selectedId == radioTomcat1.getId())
                        {
                            server = da.pserver;
                            serverUrl = da.psUrl;
                        }
                        else
                        {
                            server = da.tserver;
                            serverUrl = da.tsUrl;
                        }
                        lastOperation = "Data Upload";

                        //update settings last operation
                        da.saveAppSettings(server, serverUrl, updateData, lastOperation, newTableRecordID, updateUser);

                        // call AsyncTask to upload database data on separate thread
                        new UploadDatabaseAsyncTask().execute(serverUrl + "UploadDatabase");
                    }
                    else
                    {
                        MessagePopup alert = new MessagePopup(Synchronize.this, "Error!", "No Internet Connection");
                        alert.show();
                    }
                }
            });
        }
        catch (Exception ex) {}
    }

    private class DownloadDatabaseAsyncTask extends AsyncTask<String, Void, String>
    {
        private final ProgressDialog dialog = new ProgressDialog(Synchronize.this);

        @Override
        protected void onPostExecute(final String databaseFromServer)
        {
            try {
                if (databaseFromServer != null)
                {
                    //convert json database consisting of its tables to be able to save it table by table
                    Database db = gson.fromJson(databaseFromServer, Database.class);

                    //save downloaded database table by table
                    da.saveToExternalStorage(db.getLot(), da.lotFile);
                    da.saveToExternalStorage(db.getLotVariable(), da.lotVariableFile);
                    da.saveToExternalStorage(db.getLotList(), da.lotListFile);
                    da.saveToExternalStorage(db.getFieldVariable(), da.fieldVariableFile);
                    da.saveToExternalStorage(db.getChildLocation(), da.childLocationFile);
                    da.saveToExternalStorage(db.getParentLocation(), da.parentLocationFile);
                    da.saveToExternalStorage(db.getMigration(), da.migrationFile);
                    da.saveToExternalStorage(db.getVariable(), da.variableFile);
                    da.saveToExternalStorage(db.getLotListLot(), da.lotListLotFile);
                    da.saveToExternalStorage(db.getContainerType(), da.containerTypeFile);
                    da.saveToExternalStorage(db.getItem(), "");
                    da.saveToExternalStorage(db.getItemType(), da.itemTypeFile);
                    da.saveToExternalStorage(db.getSubtype(), da.subtypeFile);
                    da.saveToExternalStorage(db.getSubtypeTransaction(), da.subtypetransactionFile);
                    da.saveToExternalStorage(db.getDeletedFieldVariable(), "");
                    da.saveToExternalStorage(db.getAllusers(), da.usersFile);

                    dialog.dismiss();
                    MessagePopup alert = new MessagePopup(Synchronize.this, "Alert!", "Download was successfully completed!");
                    alert.show();
                }
                else
                {
                    dialog.dismiss();
                    MessagePopup alert = new MessagePopup(Synchronize.this, "Error!", "Download Failed");
                    alert.show();
                }
            }
            catch (Exception ex)
            {
                dialog.dismiss();
                MessagePopup alert = new MessagePopup(Synchronize.this, "Error!", ex.getMessage());
                alert.show();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Please wait...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            String data = null;
            try {
                data = da.getJSONdata(urls[0]);
                return data;
            }
            catch(Throwable t) {
                t.printStackTrace();
            }
            return null;
        }
    }

    private class UploadDatabaseAsyncTask extends AsyncTask<String, Void, String>
    {
        private final ProgressDialog dialog = new ProgressDialog(Synchronize.this);

        @Override
        protected String doInBackground(String... urls) {
            String jsonDataToSend = "", response = "";
            try {
                Database db = new Database();
                String lot = "", lotvariable = "", lotlist = "", fieldvariable = "", migration = "", lotlistlot = "", subtype = "";
                String item = da.getJsonDatafromExternalStorage(da.itemFile) != null ? da.getJsonDatafromExternalStorage(da.itemFile) : "";
                String deletedfieldvariable = da.getJsonDatafromExternalStorage(da.deletedFieldVariableFile) != null ? da.getJsonDatafromExternalStorage(da.deletedFieldVariableFile) : "";

                //get and upload only new and updated lot
                if (da.GetAllLot(da.getJsonDatafromExternalStorage(da.lotFile)) != null)
                {
                    ArrayList<Lot> updatedNewLotList = new ArrayList<Lot>();
                    lotList = da.GetAllLot(da.getJsonDatafromExternalStorage(da.lotFile));
                    for(Lot lo : lotList)
                    {
                        if(lo.getLotId() < 0 || lo.getUpdated() == 1)
                            updatedNewLotList.add(lo);
                    }
                    if(updatedNewLotList!= null && updatedNewLotList.size() > 0)
                        lot = gson.toJson(updatedNewLotList, da.lotArrayType);
                }

                //get and upload only new lot list
                if (da.GetAllLotList(da.getJsonDatafromExternalStorage(da.lotListFile)) != null)
                {
                    ArrayList<LotList> newLotList = new ArrayList<LotList>();
                    lotListList = da.GetAllLotList(da.getJsonDatafromExternalStorage(da.lotListFile));
                    for(LotList ll : lotListList)
                    {
                        if(ll.getLotlistId() < 0)
                            newLotList.add(ll);
                    }
                    if(newLotList != null && newLotList.size() > 0)
                        lotlist = gson.toJson(newLotList, da.lotlistArrayType);
                }

                //get and upload only new and updated lot variable
                if (da.GetAllLotVariable(da.getJsonDatafromExternalStorage(da.lotVariableFile)) != null)
                {
                    ArrayList<LotVariable> updatedNewLotVariableList = new ArrayList<LotVariable>();
                    lotVariableList = da.GetAllLotVariable(da.getJsonDatafromExternalStorage(da.lotVariableFile));
                    for(LotVariable lv : lotVariableList)
                    {
                        if(lv.getLotvariableId() < 0 || lv.getUpdated() == 1)
                            updatedNewLotVariableList.add(lv);
                    }
                    if(updatedNewLotVariableList != null && updatedNewLotVariableList.size() > 0)
                        lotvariable = gson.toJson(updatedNewLotVariableList, da.lotVarArrayType);
                }

                //get and upload only new and updated field variable
                if (da.GetAllFieldVariable(da.getJsonDatafromExternalStorage(da.fieldVariableFile)) != null)
                {
                    ArrayList<FieldVariable> updatedNewFieldVariableList = new ArrayList<FieldVariable>();
                    fieldVariableList =da.GetAllFieldVariable(da.getJsonDatafromExternalStorage(da.fieldVariableFile));
                    for(FieldVariable fv : fieldVariableList)
                    {
                        if(fv.getFieldvarId() < 0 || fv.getUpdated() == 1)
                            updatedNewFieldVariableList.add(fv);
                    }
                    if(updatedNewFieldVariableList != null && updatedNewFieldVariableList.size() > 0)
                        fieldvariable = gson.toJson(updatedNewFieldVariableList, da.fieldVarArrayType);
                }

                //get and upload only lot list lots with new lots
                if (da.GetAllLotListLot(da.getJsonDatafromExternalStorage(da.lotListLotFile)) != null)
                {
                    ArrayList<LotListLot> newLotListLot = new ArrayList<LotListLot>();
                    lotListLot = da.GetAllLotListLot(da.getJsonDatafromExternalStorage(da.lotListLotFile));
                    for(LotListLot lll : lotListLot)
                    {
                        if(lll.getLotId() < 0)
                            newLotListLot.add(lll);
                    }
                    if(newLotListLot != null && newLotListLot.size() > 0)
                        lotlistlot = gson.toJson(newLotListLot, da.lotlistlotArrayType);
                }

                //get and upload only new lot migration
                if (da.GetAllMigration(da.getJsonDatafromExternalStorage(da.migrationFile)) != null)
                {
                    ArrayList<Migration> newlotMigration = new ArrayList<Migration>();
                    lotMigration = da.GetAllMigration(da.getJsonDatafromExternalStorage(da.migrationFile));
                    for(Migration mig : lotMigration)
                    {
                        if(mig.getMigId() < 0)
                            newlotMigration.add(mig);
                    }
                    if(newlotMigration != null && newlotMigration.size() > 0)
                        migration = gson.toJson(newlotMigration, da.migrationArrayType);
                }

                //get and upload only new sub type transactions
                if (da.GetAllLotSubtypeTransaction(da.getJsonDatafromExternalStorage(da.subtypetransactionFile)) != null)
                {
                    ArrayList<SubtypeTransaction> newsubtypeTransactions = new ArrayList<SubtypeTransaction>();
                    subtypeTransactions = da.GetAllLotSubtypeTransaction(da.getJsonDatafromExternalStorage(da.subtypetransactionFile));
                    for(SubtypeTransaction stt : subtypeTransactions)
                    {
                        if(stt.getSubtypeTransId() < 0)
                            newsubtypeTransactions.add(stt);
                    }
                    if(newsubtypeTransactions != null && newsubtypeTransactions.size() > 0)
                        subtype = gson.toJson(newsubtypeTransactions, da.subtypeTransArrayType);
                }

                db.setLot(lot);
                db.setLotVariable(lotvariable);
                db.setLotList(lotlist);
                db.setFieldVariable(fieldvariable);
                db.setMigration(migration);
                db.setLotListLot(lotlistlot);
                db.setItem(item);
                db.setSubtypeTransaction(subtype);
                db.setDeletedFieldVariable(deletedfieldvariable);

                jsonDataToSend = gson.toJson(db, type);
                response = da.postJSONdata(urls[0], jsonDataToSend);

            } catch (Exception e) {
                response = e.getMessage();
            }
            return response;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Please wait...");
            dialog.setCancelable(false);
            dialog.show();
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
                        dialog.dismiss();
                        MessagePopup alert = new MessagePopup(Synchronize.this, "Alert!", response.getException());
                        alert.show();
                    }
                    else
                    {
                        dialog.dismiss();
                        MessagePopup alert = new MessagePopup(Synchronize.this, "Error!", response.getException());
                        alert.show();
                    }
                }
                else
                {
                    dialog.dismiss();
                    MessagePopup alert = new MessagePopup(Synchronize.this, "Error!", "Cannot connect to server");
                    alert.show();
                }
            }
            catch (Exception ex)
            {
                dialog.dismiss();
                MessagePopup alert = new MessagePopup(Synchronize.this, "Error!", ex.getMessage());
                alert.show();
            }
        }
    }

    // override the actionbar up button navigating to parent activity but navigate to previous activity
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
