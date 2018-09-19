package com.iita.iitagenebank;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Simeon on 26/02/2016.
 */
public class EditSettingsPopup extends Dialog implements View.OnClickListener
{
    public Activity activ;
    public RadioGroup radioGroupDataToUpdate, radioGroupServerType;
    public RadioButton radioServer, radioDevice, radioTomcat1, radioTomcat2;
    public Button update_button, cancel_button;
    DataAccess da;
    String updateData = "", serverUrl = "", server = "";
    Spinner updateUser_dropdown;
    List usernameList = new ArrayList();
    ArrayList<Users> allUsers = new ArrayList<Users>();

    public EditSettingsPopup(Activity a)
    {
        super(a);
        this.activ = a;
        da = new DataAccess(a);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.edit_data_to_update);

        new GetUsersAsync().execute(da.AppSettingsDetail().getServerUrl() + "getAllUsers");

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

        radioGroupDataToUpdate = (RadioGroup) findViewById(R.id.radioGroupDataToUpdate);
        radioServer = (RadioButton) findViewById(R.id.radioServer);
        radioDevice = (RadioButton) findViewById(R.id.radioDevice);
        if (da.isDataToUpdateServer())
        {
            radioServer.setChecked(true);
        }
        else
        {
            radioDevice.setChecked(true);
        }

        update_button = (Button) findViewById(R.id.update_button);
        update_button.setOnClickListener(this);

        cancel_button = (Button) findViewById(R.id.cancel_button);
        cancel_button.setOnClickListener(this);
    }

    private class GetUsersAsync extends AsyncTask<String, Void, ArrayList<Users>> {
        @Override
        protected void onPostExecute(final ArrayList<Users> result) {
            try
            {
                if (result != null && result.size() > 0)
                {
                    allUsers = result;
                    usernameList = da.getUsernames(result);

                    updateUser_dropdown = (Spinner) findViewById(R.id.updateUser_dropdown);
                    // set variables as items of field variable drop down / spinner
                    ArrayAdapter dataAdapter = new ArrayAdapter(activ, android.R.layout.simple_spinner_item, usernameList);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    updateUser_dropdown.setAdapter(dataAdapter);

                    if(da.SettingsExist()) //save default app settings if settings does not exist
                    {
                        String currentUser = da.AppSettingsDetail().getUpdateUser(); //the value you want the position for
                        ArrayAdapter myAdap = (ArrayAdapter) updateUser_dropdown.getAdapter(); //cast to an ArrayAdapter
                        int spinnerPosition = myAdap.getPosition(currentUser);
                        //set the default according to value
                        updateUser_dropdown.setSelection(spinnerPosition);
                    }
                }
            }
            catch (Exception ex) {}
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Users> doInBackground(String... urls) {
            ArrayList<Users> UserList = new ArrayList<Users>();
            try {
                    UserList = da.GetAllUsers(da.getJsonDatafromExternalStorage(da.usersFile));
                return UserList;
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.update_button:
            {
                int selectedDataToUpdateId = radioGroupDataToUpdate.getCheckedRadioButtonId();
                // find which radioButton is checked by id
                if (selectedDataToUpdateId == radioServer.getId())
                {
                    updateData = "Server";
                }
                else
                {
                    updateData = "Device";
                }

                int selectedServerId = radioGroupServerType.getCheckedRadioButtonId();
                // find which radioButton is checked by id
                if (selectedServerId == radioTomcat1.getId())
                {
                    serverUrl = da.psUrl;
                    server = da.pserver;
                }
                else
                {
                    serverUrl = da.tsUrl;
                    server = da.tserver;
                }
                String Username = allUsers.get(updateUser_dropdown.getSelectedItemPosition()).getUsername();
                //update settings last operation
                da.saveAppSettings(server,  serverUrl, updateData, da.AppSettingsDetail().getLastOperation(), da.AppSettingsDetail().getNewTableRecordID(), Username);

                Intent i = new Intent(activ, Settings.class);
                i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                activ.startActivity(i);
                dismiss();
            }
            break;
            case R.id.cancel_button:
                dismiss();
                break;
            default:
                break;
        }
    }
}