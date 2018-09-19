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
import android.widget.Spinner;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Simeon on 08/03/2016.
 */
public class AddNewLocationPopup extends Dialog implements View.OnClickListener
{
    public Activity activ;
    TextView popuptitle;
    EditText locationName;
    public Button add_button, cancel_button;
    DataAccess da;
    Location newLocation = new Location();
    Gson gson = new Gson();
    Type type = new TypeToken<Location>() {}.getType();
    Spinner parentlocationSpinner;
    List parentlocationList = new ArrayList();
    ArrayList<Location> allParentLocations = new ArrayList<Location>();

    public AddNewLocationPopup(Activity a)
    {
        super(a);
        this.activ = a;
        da = new DataAccess(a);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add_new_location);
        try
        {
            locationName = (EditText) findViewById(R.id.locationName);

            new GetParentLocationsAsync().execute(da.AppSettingsDetail().getServerUrl() + "parentlocations/" + da.AppSettingsDetail().getUpdateUser());

            add_button = (Button) findViewById(R.id.add_button);
            add_button.setOnClickListener(this);

            cancel_button = (Button) findViewById(R.id.cancel_button);
            cancel_button.setOnClickListener(this);
        }
        catch (Exception ex) {}
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

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.add_button:
                try
                {
                    String LocationName = locationName.getText().toString().trim();

                    if (LocationName.length() == 0)
                    {
                        MessagePopup alert = new MessagePopup(activ, "Error!", "You must enter the location name");
                        alert.show();
                    }
                    else
                    {
                        long parentLocationID = allParentLocations.get(parentlocationSpinner.getSelectedItemPosition()).getLocationId();

                        //set new location values to be added
                        newLocation.setLocationId(da.getNewTableRecordID());
                        newLocation.setLocationType("tray");
                        newLocation.setName(LocationName);
                        newLocation.setVersion(0);
                        newLocation.setParentId(parentLocationID);
                        if (da.isConnectedToInternet())
                        {
                            new AddNewLocationAsyncTask().execute(da.AppSettingsDetail().getServerUrl() + "AddNewLocation");
                        }
                        else
                        {
                            MessagePopup alert = new MessagePopup(activ, "Error!", "You cannot add new location; No Internet Connection");
                            alert.show();
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

    private class AddNewLocationAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String jsonDataToSend = "", response = "";
            try {
                jsonDataToSend = gson.toJson(newLocation, type);
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
                        //update downloaded device location lists after adding new location
                        new UpdateDeviceLocationAsyncTask().execute(da.AppSettingsDetail().getServerUrl() + "childlocations/" + da.AppSettingsDetail().getUpdateUser());
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


    private class UpdateDeviceLocationAsyncTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected void onPostExecute(String locations)
        {
            try {
                if (locations != null)
                {
                    da.saveToExternalStorage(locations, da.childLocationFile);

                    Intent i = new Intent(activ, AllLocations.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    activ.startActivity(i);
                    dismiss();

                    MessagePopup alert = new MessagePopup(activ, "Alert!", "The new location was successfully added on the server");
                    alert.show();
                }
            }
            catch (Exception ex){}
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
}
