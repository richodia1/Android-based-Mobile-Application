package com.iita.iitagenebank;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Simeon on 22/02/2016.
 */
public class EditLotPopup extends Dialog implements View.OnClickListener
{
    public Activity activ;
    ArrayList<Lot> lotList;
    public TextView popuptitle, current_quantity, observed_quantity;
    public Button update_button, cancel_button;
    DataAccess da;
    Lot selectedLot = new Lot();
    Gson gson = new Gson();
    Lot updatedLot = new Lot();
    Type type = new TypeToken<Lot>() {}.getType();

    public EditLotPopup(Activity a, Lot lot)
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
        setContentView(R.layout.edit_lot_quantity);

        popuptitle = (TextView) findViewById(R.id.lot_name);
        popuptitle.setText(selectedLot.getItemName() + " (" + selectedLot.getBarcode() + ")");

        current_quantity = (TextView) findViewById(R.id.current_quantity);
        current_quantity.setText(String.valueOf((int) selectedLot.getQuantity()));

        observed_quantity = (TextView) findViewById(R.id.observed_quantity);

        update_button = (Button) findViewById(R.id.update_button);
        update_button.setOnClickListener(this);

        cancel_button = (Button) findViewById(R.id.cancel_button);
        cancel_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.update_button:
                String observedQuantity = observed_quantity.getText().toString().trim();
                ArrayList<Lot> updatedLotList = new ArrayList<Lot>();
                if (observedQuantity.length() == 0)
                {
                    MessagePopup alert = new MessagePopup(activ, "Error!", "Enter your observed quantity");
                    alert.show();
                }
                else
                {
                    //update and save new observed quantity to either server or device
                    if(da.isDataToUpdateServer())
                    {
                        if (da.isConnectedToInternet())
                        {
                            updatedLot.setLotId(selectedLot.getLotId());
                            updatedLot.setItemId(selectedLot.getItemId());
                            updatedLot.setContainerId(selectedLot.getContainerId());
                            updatedLot.setLocationId(selectedLot.getLocationId());
                            updatedLot.setBarcode(selectedLot.getBarcode());
                            updatedLot.setItemName(selectedLot.getItemName());
                            updatedLot.setLocationDetail(selectedLot.getLocationDetail());
                            updatedLot.setQuantity(Long.valueOf(observedQuantity).longValue());
                            updatedLot.setStatus(selectedLot.getStatus());
                            updatedLot.setVersion(selectedLot.getVersion());
                            updatedLot.setScale(selectedLot.getScale());
                            updatedLot.setLastUpdated(da.getDate());
                            updatedLot.setLastUpdatedBy(da.UserDeviceDetail().getUsername());
                            updatedLot.setUpdated(1);

                            new UpdateServerLot().execute(da.AppSettingsDetail().getServerUrl() + "UpdateLot");
                        }
                        else
                        {
                            MessagePopup alert = new MessagePopup(activ, "Error!", "No Internet Connection");
                            alert.show();
                        }
                    }
                    else
                    {
                        //get all lots from device (NB: not a specific lot field variables that was passed as argument of the constructor)
                        lotList = da.GetAllLot(da.getJsonDatafromExternalStorage(da.lotFile));

                        for (Lot lot : lotList)
                        {
                            if (selectedLot.getLotId() == lot.getLotId())
                            {
                                updatedLot.setLotId(lot.getLotId());
                                updatedLot.setItemId(lot.getItemId());
                                updatedLot.setContainerId(lot.getContainerId());
                                updatedLot.setLocationId(lot.getLocationId());
                                updatedLot.setBarcode(lot.getBarcode());
                                updatedLot.setItemName(lot.getItemName());
                                updatedLot.setLocationDetail(lot.getLocationDetail());
                                updatedLot.setQuantity(Long.valueOf(observedQuantity).longValue());
                                updatedLot.setStatus(lot.getStatus());
                                updatedLot.setVersion(lot.getVersion());
                                updatedLot.setScale(lot.getScale());
                                updatedLot.setLastUpdated(da.getDate());
                                updatedLot.setLastUpdatedBy(da.UserDeviceDetail().getUsername());
                                updatedLot.setUpdated(1);
                                updatedLotList.add(updatedLot);
                            }
                            else
                            {
                                updatedLotList.add(lot);
                            }
                        }

                        // save updated lists to device
                        da.saveToExternalStorage(gson.toJson(updatedLotList), da.lotFile);
                        MessagePopup alert = new MessagePopup(activ, "Alert!", "Device Lot quantity was successfully updated");
                        alert.show();
                    }

                    Intent i = new Intent(activ, LotDetail.class);
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

    private class UpdateServerLot extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... urls) {
            String jsonDataToSend = "", response = "";

            try
            {
                jsonDataToSend = gson.toJson(updatedLot, type); //convert user details to json
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
            try {
                if(result != null)
                {
                    wsSQLResult response = gson.fromJson(result, wsSQLResult.class);
                    //validate user's login detail by checking if returned user detail email is empty
                    //user email is set empty by the server if user password and email does not match
                    if (Integer.parseInt(response.getWasSuccessful()) == 1)
                    {
                        MessagePopup alert = new MessagePopup(activ, "Alert!", "Server Lot quantity was successfully updated");
                        alert.show();
                    }
                    else
                    {
                        MessagePopup failureAlert = new MessagePopup(activ, "Error!", response.getException());
                        failureAlert.show();
                    }
                }

            } catch (Exception e) {}
        }
    }
}
