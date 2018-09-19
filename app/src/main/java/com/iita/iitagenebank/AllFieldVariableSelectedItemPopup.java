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
 * Created by Simeon on 29/02/2016.
 */
public class AllFieldVariableSelectedItemPopup extends Dialog implements View.OnClickListener
{
    public Activity activ;
    ArrayList<FieldVariable> fieldVarList;
    ArrayList<FieldVariable> remainingfieldVarList  = new ArrayList<FieldVariable>();
    public TextView popuptitle;
    public Button deleteButton, cancelButton;
    DataAccess da;
    FieldVariable selectedFieldVariable;
    FieldVariableAdapter adapter;
    Gson gson = new Gson();
    Type type = new TypeToken<FieldVariable>() {}.getType();
    ArrayList<DeletedFieldVariable> deletedFieldVarList;
    ArrayList<DeletedFieldVariable> newDeletedFieldVarList  = new ArrayList<DeletedFieldVariable>();

    public AllFieldVariableSelectedItemPopup(Activity a, ArrayList<FieldVariable> fieldVarList, int selectedFieldVarPosition, FieldVariableAdapter adapter)
    {
        super(a);
        this.activ = a;
        this.fieldVarList = fieldVarList;
        this.selectedFieldVariable = fieldVarList.get(selectedFieldVarPosition);
        da = new DataAccess(a);
        this.adapter = adapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.selected_field_lot_variable_popup);

        popuptitle = (TextView) findViewById(R.id.item_name);
        popuptitle.setText(da.ResizeString(selectedFieldVariable.getVar(), 70));

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
                        new DeleteFieldVariableAsyncTask().execute(da.AppSettingsDetail().getServerUrl() + "DeleteAfieldVariable");
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

                            //delete selected field variable from device
                            for (FieldVariable fv : fieldVarList) {
                                if (fv.getFieldvarId() != selectedFieldVariable.getFieldvarId()) {
                                    remainingfieldVarList.add(fv);
                                }
                            }

                            DeletedFieldVariable newDeletedFieldVar  = new DeletedFieldVariable();
                            newDeletedFieldVar.setFieldvarId(selectedFieldVariable.getFieldvarId());
                            newDeletedFieldVarList.add(newDeletedFieldVar);

                            //save all deleted field variables to device
                            da.saveToExternalStorage(gson.toJson(newDeletedFieldVarList), da.deletedFieldVariableFile);
                            //save remaining field variable to device
                            da.saveToExternalStorage(gson.toJson(remainingfieldVarList), da.fieldVariableFile);
                            adapter.filter(String.valueOf(selectedFieldVariable.getFieldvarId())); // update field variable model adapter
                        }
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


    private class DeleteFieldVariableAsyncTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... urls)
        {
            String jsonDataToSend = "", response = "";
            try
            {
                jsonDataToSend = gson.toJson(selectedFieldVariable, type);
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
                        adapter.filter(String.valueOf(selectedFieldVariable.getFieldvarId()));
                    }
                }
            }
            catch (Exception ex){}
        }
    }
}
