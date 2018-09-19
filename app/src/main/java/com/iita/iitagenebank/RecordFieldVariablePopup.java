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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Simeon on 02/03/2016.
 */
public class RecordFieldVariablePopup extends Dialog implements View.OnClickListener
{
    public Activity activ;
    public TextView popuptitle, quantity_value;
    public Button record_button, cancel_button;
    DataAccess da;
    Lot selectedLot = new Lot();
    ArrayList<FieldVariable> fieldVarList;
    ArrayList<FieldVariable> newfieldVarList  = new ArrayList<FieldVariable>();
    FieldVariable newfieldVar  = new FieldVariable();
    Gson gson = new Gson();
    Type type = new TypeToken<FieldVariable>() {}.getType();
    Spinner fieldvar_dropdown;
    ImageButton calendarButton;
    private int day = 0;
    private int month = 0;
    private int year = 0;
    private EditText dateView;

    public RecordFieldVariablePopup(Activity a, Lot lot)
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
        setContentView(R.layout.record_field_variable);
        try
        {
            popuptitle = (TextView) findViewById(R.id.lot_name);
            popuptitle.setText("Record Field Variable " + " (" + selectedLot.getBarcode() + ")");

            fieldvar_dropdown = (Spinner) findViewById(R.id.fieldvar_dropdown);
            // set variables as items of field variable drop down / spinner
            ArrayAdapter dataAdapter = new ArrayAdapter(activ, android.R.layout.simple_spinner_item, da.getVariables());
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            fieldvar_dropdown.setAdapter(dataAdapter);

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
                newfieldVar.setFieldvarId(da.getNewTableRecordID());
                newfieldVar.setCreatedBy(da.UserDeviceDetail().getUsername());
                newfieldVar.setCreatedDate(da.getDate());
                newfieldVar.setLastUpdated(da.getDate());
                newfieldVar.setLastUpdatedBy(da.UserDeviceDetail().getUsername());
                newfieldVar.setVersion(0);
                newfieldVar.setDate(da.YearMonthDayDateFormat(dateView.getText().toString().trim()) + " 00:00:00");
                newfieldVar.setQty(quantity_value.getText().toString().trim());
                newfieldVar.setVar(String.valueOf(fieldvar_dropdown.getSelectedItem()));
                newfieldVar.setLotId(selectedLot.getLotId());
                newfieldVar.setUpdated(1);

                if (quantity_value.getText().toString().trim().length() == 0) {
                    MessagePopup alert = new MessagePopup(activ, "Error!", "Enter a valid quantity");
                    alert.show();
                }
                else {
                    if (da.isDataToUpdateServer()) {
                        if (da.isConnectedToInternet()) {
                            new RecordFieldVariableAsyncTask().execute(da.AppSettingsDetail().getServerUrl() + "RecordFieldVariable");
                        } else {
                            MessagePopup alert = new MessagePopup(activ, "Error!", "You cannot record field variable; No Internet Connection");
                            alert.show();
                        }
                    } else {
                        //check if external storage is available and not read only; and save lot detail to device file
                        if (!da.isExternalStorageAvailable() || da.isExternalStorageReadOnly()) {
                            MessagePopup alert = new MessagePopup(activ, "Error!", "No storage device found");
                            alert.show();
                        } else {
                            //get all field variables from device (NB: not a specific lot field variables that was passed as argument of the constructor)
                            if(da.GetAllFieldVariable(da.getJsonDatafromExternalStorage(da.fieldVariableFile))!=null) {
                                fieldVarList = da.GetAllFieldVariable(da.getJsonDatafromExternalStorage(da.fieldVariableFile));
                                newfieldVarList = fieldVarList;
                            }
                            //add new field variable to device
                            newfieldVarList.add(newfieldVar);

                            //save all field variables to device
                            da.saveToExternalStorage(gson.toJson(newfieldVarList), da.fieldVariableFile);

                            MessagePopup alert = new MessagePopup(activ, "Alert!", "The field variable was successfully recorded on the device");
                            alert.show();
                        }
                    }
                    Intent i = new Intent(activ, AllFieldVariables.class);
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

    private class RecordFieldVariableAsyncTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... urls)
        {
            String jsonDataToSend = "", response = "";
            try
            {
                jsonDataToSend = gson.toJson(newfieldVar, type);
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
