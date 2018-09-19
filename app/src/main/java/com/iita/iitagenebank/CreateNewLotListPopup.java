package com.iita.iitagenebank;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Simeon on 04/03/2016.
 */
public class CreateNewLotListPopup extends Dialog implements View.OnClickListener
{
    public Activity activ;
    public Button create_button, cancel_button;
    DataAccess da;
    ArrayList<LotList> lotlistList;
    ArrayList<LotList> newLotListList  = new ArrayList<LotList>();
    LotList newLotList  = new LotList();
    Gson gson = new Gson();
    Type type = new TypeToken<LotList>() {}.getType();
    private EditText name;

    public CreateNewLotListPopup(Activity a)
    {
        super(a);
        this.activ = a;
        da = new DataAccess(a);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.create_new_lot_list);
        try
        {
            name = (EditText) findViewById(R.id.name);

            create_button = (Button) findViewById(R.id.create_button);
            create_button.setOnClickListener(this);

            cancel_button = (Button) findViewById(R.id.cancel_button);
            cancel_button.setOnClickListener(this);
        }
        catch (Exception ex) {}
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.create_button:
                //set new lot list values to be created
                newLotList.setLotlistId(da.getNewTableRecordID());
                newLotList.setCreatedBy(da.UserDeviceDetail().getUsername());
                newLotList.setCreatedDate(da.getDate());
                newLotList.setLastUpdated(da.getDate());
                newLotList.setLastUpdatedBy(da.UserDeviceDetail().getUsername());
                newLotList.setName(name.getText().toString().trim());
                newLotList.setOwner_id(da.UserDeviceDetail().getId());

                if (name.getText().toString().trim().length() == 0) {
                    MessagePopup alert = new MessagePopup(activ, "Error!", "Enter a lot list name");
                    alert.show();
                }
                else {
                    if (da.isDataToUpdateServer())
                    {
                        if (da.isConnectedToInternet())
                        {
                            new CreateNewLotListAsyncTask().execute(da.AppSettingsDetail().getServerUrl() + "CreateNewLotList");
                        }
                        else
                        {
                            MessagePopup alert = new MessagePopup(activ, "Error!", "You cannot create new list; No Internet Connection");
                            alert.show();
                        }
                    }
                    else
                    {
                        //check if external storage is available and not read only;
                        if (!da.isExternalStorageAvailable() || da.isExternalStorageReadOnly())
                        {
                            MessagePopup alert = new MessagePopup(activ, "Error!", "No storage device found");
                            alert.show();
                        }
                        else
                        {
                            //get all lot lists from device
                            lotlistList = da.GetAllLotList(da.getJsonDatafromExternalStorage(da.lotListFile));

                            //add new lot list to other to other device lot lists
                            newLotListList = lotlistList;
                            newLotListList.add(newLotList);

                            //save all field variables to device
                            da.saveToExternalStorage(gson.toJson(newLotListList), da.lotListFile);

                            MessagePopup alert = new MessagePopup(activ, "Alert!", "The lot list was successfully created on the device database");
                            alert.show();
                        }
                    }
                    Intent i = new Intent(activ, AllLotLists.class);
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

    private class CreateNewLotListAsyncTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... urls)
        {
            String jsonDataToSend = "", response = "";
            try
            {
                jsonDataToSend = gson.toJson(newLotList, type);
                response = da.postJSONdata(urls[0], jsonDataToSend);

            }
            catch (Exception e)
            {
                response = null;
            }
            return response;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result)
        {
            try {
                if (result != null)
                {
                    wsSQLResult response = gson.fromJson(result, wsSQLResult.class);
                    if (Integer.parseInt(response.getWasSuccessful()) == 1) {
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
            catch (Exception ex){}
        }
    }
}