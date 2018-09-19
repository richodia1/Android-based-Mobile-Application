package com.iita.iitagenebank;

import android.app.Activity;
import android.app.Dialog;
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
 * Created by Simeon on 04/03/2016.
 */
public class AllLotVariableSelectedItemPopup extends Dialog implements View.OnClickListener
{
    public Activity activ;
    ArrayList<LotVariable> lotVarList;
    ArrayList<LotVariable> remaininglotVarList  = new ArrayList<LotVariable>();
    public TextView popuptitle;
    public Button deleteButton, cancelButton;
    DataAccess da;
    LotVariable selectedLotVariable;
    LotVariableAdapter adapter;
    Gson gson = new Gson();
    Type type = new TypeToken<LotVariable>() {}.getType();

    public AllLotVariableSelectedItemPopup(Activity a, ArrayList<LotVariable> lotVarList, int selectedLotVarPosition, LotVariableAdapter adapter)
    {
        super(a);
        this.activ = a;
        this.lotVarList = lotVarList;
        this.selectedLotVariable = lotVarList.get(selectedLotVarPosition);
        da = new DataAccess(a);
        this.adapter = adapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.selected_field_lot_variable_popup);

        popuptitle = (TextView) findViewById(R.id.item_name);
        popuptitle.setText(da.ResizeString(selectedLotVariable.getVariableName(), 70));

        deleteButton = (Button) findViewById(R.id.btn_delete);
        deleteButton.setText("Delete");
        deleteButton.setOnClickListener(this);

        cancelButton = (Button) findViewById(R.id.btn_cancel);
        cancelButton.setText("Cancel");
        cancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_delete:
                if(da.isDataToUpdateServer())
                {
                    if (da.isConnectedToInternet())
                    {
                        new DeleteLotVariableAsyncTask().execute(da.AppSettingsDetail().getServerUrl() + "DeleteAlotVariable");
                    }
                    else
                    {
                        MessagePopup alert = new MessagePopup(activ, "Error!", "No Internet Connection");
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
                        //get all field variables from device (NB: not a specific lot field variables that was passed as argument of the constructor)
                        lotVarList = da.GetAllLotVariable(da.getJsonDatafromExternalStorage(da.lotVariableFile));

                        //delete selected field variable from device
                        for(LotVariable lv : lotVarList)
                        {
                            if (lv.getLotvariableId() != selectedLotVariable.getLotvariableId())
                            {
                                remaininglotVarList.add(lv);
                            }
                        }

                        //save remaining field variable to device
                        da.saveToExternalStorage(gson.toJson(remaininglotVarList), da.lotVariableFile);
                        adapter.filter(String.valueOf(selectedLotVariable.getLotvariableId())); // update lot variable model adapter
                    }
                }
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }


    private class DeleteLotVariableAsyncTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... urls)
        {
            String jsonDataToSend = "", response = "";
            try
            {
                jsonDataToSend = gson.toJson(selectedLotVariable, type);
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
                        adapter.filter(String.valueOf(selectedLotVariable.getLotvariableId()));
                    }
                }
            }
            catch (Exception ex){}
        }
    }
}
