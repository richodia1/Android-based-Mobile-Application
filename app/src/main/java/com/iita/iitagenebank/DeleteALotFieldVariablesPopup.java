package com.iita.iitagenebank;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Simeon on 29/02/2016.
 */
public class DeleteALotFieldVariablesPopup extends Dialog implements View.OnClickListener
{
    public Activity activ;
    Lot selectedLot;
    public TextView popuptitle, popup_content;
    public Button delete_button, cancel_button;
    DataAccess da;
    ArrayList<FieldVariable> fieldVarList;
    ArrayList<FieldVariable> remainingfieldVarList  = new ArrayList<FieldVariable>();
    Gson gson = new Gson();
    Type type = new TypeToken<Lot>() {}.getType();
    ArrayList<DeletedFieldVariable> deletedFieldVarList;
    ArrayList<DeletedFieldVariable> newDeletedFieldVarList  = new ArrayList<DeletedFieldVariable>();


    public DeleteALotFieldVariablesPopup(Activity a, Lot selectedLot)
    {
        super(a);
        this.activ = a;
        this.selectedLot = selectedLot;
        da = new DataAccess(a);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.delete_confirmation);

        popuptitle = (TextView) findViewById(R.id.popup_title);
        popuptitle.setText(selectedLot.getItemName() + " (" + selectedLot.getBarcode() + ")");

        popup_content = (TextView) findViewById(R.id.popup_content);
        popup_content.setText("Confirm delete of all field variables?");

        delete_button = (Button) findViewById(R.id.delete_button);
        delete_button.setText("Delete");
        delete_button.setOnClickListener(this);

        cancel_button = (Button) findViewById(R.id.cancel_button);
        cancel_button.setText("Cancel");
        cancel_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.delete_button:
                if(da.isDataToUpdateServer())
                {
                    if (da.isConnectedToInternet())
                    {
                        new DeleteALotFieldVariablesAsyncTask().execute(da.AppSettingsDetail().getServerUrl() + "DeleteALotFieldVariables");
                    }
                    else
                    {
                        MessagePopup alert = new MessagePopup(activ, "Error!", "You cannot delete field variables; No Internet Connection");
                        alert.show();
                    }
                }
                else
                {
                    //check if external storage is available and not read only; and save lot detail to device file
                    if (!da.isExternalStorageAvailable() || da.isExternalStorageReadOnly())
                    {
                        MessagePopup alert = new MessagePopup(activ, "Error!", "No storage device found");
                        alert.show();
                    }
                    else
                    {
                        //get all deleted field variables from device
                        if(da.GetAllDeletedFieldVariable(da.getJsonDatafromExternalStorage(da.deletedFieldVariableFile))!=null)
                        {
                            deletedFieldVarList = da.GetAllDeletedFieldVariable(da.getJsonDatafromExternalStorage(da.deletedFieldVariableFile));
                            newDeletedFieldVarList = deletedFieldVarList;
                        }

                        //get all field variables from device (NB: not a specific lot field variables that was passed as argument of the constructor)
                        if (da.GetAllFieldVariable(da.getJsonDatafromExternalStorage(da.fieldVariableFile)) != null)
                        {
                            fieldVarList = da.GetAllFieldVariable(da.getJsonDatafromExternalStorage(da.fieldVariableFile));

                            //delete selected lot field variables from device
                            for (FieldVariable fv : fieldVarList)
                            {
                                if (fv.getLotId() != selectedLot.getLotId())
                                    remainingfieldVarList.add(fv);
                                else
                                {
                                    DeletedFieldVariable newDeletedFieldVar  = new DeletedFieldVariable();
                                    newDeletedFieldVar.setFieldvarId(fv.getFieldvarId());
                                    newDeletedFieldVarList.add(newDeletedFieldVar);
                                }
                            }
                            //save all deleted field variables to device
                            da.saveToExternalStorage(gson.toJson(newDeletedFieldVarList), da.deletedFieldVariableFile);
                            //save remaining field variables to device
                            da.saveToExternalStorage(gson.toJson(remainingfieldVarList), da.fieldVariableFile);

                            MessagePopup alert = new MessagePopup(activ, "Alert!", "The selected lot field variables were successfully deleted from device");
                            alert.show();
                        }
                    }
                }
                dismiss();
                break;
            case R.id.cancel_button:
                dismiss();
                break;
            default:
                break;
        }
    }

    private class DeleteALotFieldVariablesAsyncTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... urls)
        {
            String jsonDataToSend = "", response = "";
            try
            {
                jsonDataToSend = gson.toJson(selectedLot, type);
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
                if (result != null) {
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
            }
            catch (Exception ex){}
        }
    }
}
