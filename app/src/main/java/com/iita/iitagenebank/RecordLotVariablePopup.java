package com.iita.iitagenebank;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Simeon on 06/04/2016.
 */
public class RecordLotVariablePopup extends Dialog implements View.OnClickListener
{
    public Activity activ;
    public TextView popuptitle, quantity_value;
    public Button record_button, cancel_button;
    DataAccess da;
    Lot selectedLot = new Lot();
    ArrayList<LotVariable> lotVarList;
    ArrayList<LotVariable> newlotVarList  = new ArrayList<LotVariable>();
    LotVariable newlotVar  = new LotVariable();
    Gson gson = new Gson();
    Type type = new TypeToken<LotVariable>() {}.getType();
    Spinner lotvar_dropdown;
    List varNameList = new ArrayList();
    ArrayList<Variable> allVariable = new ArrayList<Variable>();
    ImageButton calendarButton;
    private int day = 0;
    private int month = 0;
    private int year = 0;
    private EditText dateView;

    public RecordLotVariablePopup(Activity a, Lot lot)
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
        setContentView(R.layout.record_lot_variable);
        try
        {
            popuptitle = (TextView) findViewById(R.id.lot_name);
            popuptitle.setText("Record Lot Variable " + " (" + selectedLot.getBarcode() + ")");

            new GetVariablesAsync().execute(da.AppSettingsDetail().getServerUrl() + "variables");

            quantity_value = (EditText) findViewById(R.id.quantity_value);

            record_button = (Button) findViewById(R.id.record_button);
            record_button.setOnClickListener(this);

            cancel_button = (Button) findViewById(R.id.cancel_button);
            cancel_button.setOnClickListener(this);

            calendarButton = (ImageButton) findViewById(R.id.calendarButton);
            calendarButton.setOnClickListener(this);

            dateView = (EditText) findViewById(R.id.dateView);
            Calendar c = Calendar.getInstance();
            day = c.get(Calendar.DATE);
            month = c.get(Calendar.MONTH);
            year = c.get(Calendar.YEAR);
            dateView.setText(da.PadToTwoDigits(day) + " - " + da.PadToTwoDigits(month + 1) + " - " + year);
        }
        catch (Exception ex) {}
    }

    private class GetVariablesAsync extends AsyncTask<String, Void, ArrayList<Variable>> {
        @Override
        protected void onPostExecute(final ArrayList<Variable> result) {
            try
            {
                if (result != null && result.size() > 0)
                {
                    allVariable = result;
                    varNameList = da.getVariableNames(result);

                    lotvar_dropdown = (Spinner) findViewById(R.id.lotvar_dropdown);
                    // set variables as items of field variable drop down / spinner
                    ArrayAdapter dataAdapter = new ArrayAdapter(activ, android.R.layout.simple_spinner_item, varNameList);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    lotvar_dropdown.setAdapter(dataAdapter);
                }
            }
            catch (Exception ex) {}
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Variable> doInBackground(String... urls) {
            ArrayList<Variable> VariableList = new ArrayList<Variable>();
            ArrayList<Variable> sortedVariableList = new ArrayList<Variable>();
            try {
                // get variables data either from server or device depending on value of the data to update in settings
                if (da.isDataToUpdateServer())
                    VariableList = da.GetAllVariable(da.getJSONdata(urls[0]));
                else
                    VariableList = da.GetAllVariable(da.getJsonDatafromExternalStorage(da.variableFile));

                //sort the variable list according to variable name
                Collections.sort(VariableList, new Comparator<Variable>() {
                    public int compare(Variable a, Variable b) {
                        return a.getName().compareTo(b.getName());
                    }
                });

                //select only the variables with names
                for(Variable v : VariableList)
                {
                    if (!v.getName().matches(""))
                        sortedVariableList.add(v);
                }

                return sortedVariableList;
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
            case R.id.calendarButton:
                final View dialogView = View.inflate(activ, R.layout.date_picker, null);
                final AlertDialog alertDialog = new AlertDialog.Builder(activ).create();

                dialogView.findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatePicker datePicker2 = (DatePicker) alertDialog.findViewById(R.id.datepicker2);
                        dateView.setText(da.PadToTwoDigits(datePicker2.getDayOfMonth()) + " - " + da.PadToTwoDigits(datePicker2.getMonth() + 1) + " - " + datePicker2.getYear());
                        alertDialog.dismiss();
                    }});
                dialogView.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.setView(dialogView);
                alertDialog.show();
                break;
            case R.id.record_button:
                //set new field variable values to be recorded
                newlotVar.setLotvariableId(da.getNewTableRecordID());
                newlotVar.setCreatedBy(da.UserDeviceDetail().getUsername());
                newlotVar.setCreatedDate(da.getDate());
                newlotVar.setLastUpdated(da.getDate());
                newlotVar.setLastUpdatedBy(da.UserDeviceDetail().getUsername());
                newlotVar.setVersion(0);
                newlotVar.setVariabledate(da.YearMonthDayDateFormat(dateView.getText().toString().trim()) + " 00:00:00");
                newlotVar.setQuantity(Long.valueOf(quantity_value.getText().toString().trim()).longValue());
                newlotVar.setVariableName(String.valueOf(lotvar_dropdown.getSelectedItem()));
                newlotVar.setVariableId(allVariable.get(lotvar_dropdown.getSelectedItemPosition()).getVarId());
                newlotVar.setLotId(selectedLot.getLotId());
                newlotVar.setUpdated(1);

                if (quantity_value.getText().toString().trim().length() == 0) {
                    MessagePopup alert = new MessagePopup(activ, "Error!", "Enter a valid quantity");
                    alert.show();
                }
                else {
                    if (da.isDataToUpdateServer()) {
                        if (da.isConnectedToInternet()) {
                            new RecordLotVariableAsyncTask().execute(da.AppSettingsDetail().getServerUrl() + "RecordLotVariable");
                        } else {
                            MessagePopup alert = new MessagePopup(activ, "Error!", "You cannot record lot variable; No Internet Connection");
                            alert.show();
                        }
                    } else {
                        //check if external storage is available and not read only; and save lot detail to device file
                        if (!da.isExternalStorageAvailable() || da.isExternalStorageReadOnly()) {
                            MessagePopup alert = new MessagePopup(activ, "Error!", "No storage device found");
                            alert.show();
                        } else {
                            //get all lot variables from device (NB: not a specific lot variables that was passed as argument of the constructor)
                            if(da.GetAllLotVariable(da.getJsonDatafromExternalStorage(da.lotVariableFile))!=null)
                                lotVarList = da.GetAllLotVariable(da.getJsonDatafromExternalStorage(da.lotVariableFile));

                            //add new lot variable to device
                            newlotVarList = lotVarList;
                            newlotVarList.add(newlotVar);

                            //save all lot variables to device
                            da.saveToExternalStorage(gson.toJson(newlotVarList), da.lotVariableFile);

                            MessagePopup alert = new MessagePopup(activ, "Alert!", "The lot variable was successfully recorded on the device");
                            alert.show();
                        }
                    }
                    Intent i = new Intent(activ, AllLotVariables.class);
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

    private class RecordLotVariableAsyncTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... urls)
        {
            String jsonDataToSend = "", response = "";
            try
            {
                jsonDataToSend = gson.toJson(newlotVar, type);
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
