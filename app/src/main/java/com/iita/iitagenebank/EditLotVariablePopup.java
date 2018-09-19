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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by Simeon on 02/03/2016.
 */
public class EditLotVariablePopup extends Dialog implements View.OnClickListener {
    public Activity activ;
    public TextView popuptitle, quantity;
    ImageButton calendarButton;
    public Button update_button, cancel_button;
    DataAccess da;
    LotVariable selectedLotVariable = new LotVariable();
    ArrayList<LotVariable> lotVarList;
    ArrayList<LotVariable> updatedlotVarList = new ArrayList<LotVariable>();
    LotVariable updatedlotVar = new LotVariable();
    Gson gson = new Gson();
    Type type = new TypeToken<LotVariable>() {}.getType();
    Spinner lotvar_dropdown;
    List varNameList = new ArrayList();
    ArrayList<Variable> allVariable = new ArrayList<Variable>();
    private int day = 0;
    private int month = 0;
    private int year = 0;
    private EditText dateView;

    public EditLotVariablePopup(Activity a, LotVariable lv) {
        super(a);
        this.activ = a;
        this.selectedLotVariable = lv;
        da = new DataAccess(a);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.edit_lot_variable);
        try {
            popuptitle = (TextView) findViewById(R.id.lotvar_name);
            popuptitle.setText(selectedLotVariable.getVariableName());

            new GetVariablesAsync().execute(da.AppSettingsDetail().getServerUrl() + "variables");

            quantity = (EditText) findViewById(R.id.quantity);
            quantity.setText(String.valueOf((int) selectedLotVariable.getQuantity()));

            update_button = (Button) findViewById(R.id.update_button);
            update_button.setOnClickListener(this);

            cancel_button = (Button) findViewById(R.id.cancel_button);
            cancel_button.setOnClickListener(this);

            calendarButton = (ImageButton) findViewById(R.id.calendarButton);
            calendarButton.setOnClickListener(this);

            dateView = (EditText) findViewById(R.id.dateView);
            if(selectedLotVariable.getVariabledate()!=null && selectedLotVariable.getVariabledate()!="") {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date parse = sdf.parse(selectedLotVariable.getVariabledate());
                Calendar c = Calendar.getInstance();
                c.setTime(parse);

                day = c.get(Calendar.DATE);
                month = c.get(Calendar.MONTH);
                year = c.get(Calendar.YEAR);
                dateView.setText(da.PadToTwoDigits(day) + " - " + da.PadToTwoDigits(month + 1) + " - " + year);
            }
        }
        catch (Exception ex) {
        }
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

                    String currentVar = selectedLotVariable.getVariableName(); //the value you want the position for
                    ArrayAdapter myAdap = (ArrayAdapter) lotvar_dropdown.getAdapter(); //cast to an ArrayAdapter
                    int spinnerPosition = myAdap.getPosition(currentVar);
                    //set the default according to value
                    lotvar_dropdown.setSelection(spinnerPosition);
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
    public void onClick(View v) {
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
            case R.id.update_button:
                //set new field variable values to be recorded
                updatedlotVar.setLotvariableId(selectedLotVariable.getLotvariableId());
                updatedlotVar.setCreatedBy(selectedLotVariable.getCreatedBy());
                updatedlotVar.setCreatedDate(selectedLotVariable.getCreatedDate());
                updatedlotVar.setLastUpdated(da.getDate());
                updatedlotVar.setLastUpdatedBy(da.UserDeviceDetail().getUsername());
                updatedlotVar.setVersion(selectedLotVariable.getVersion());
                updatedlotVar.setVariabledate(da.YearMonthDayDateFormat(dateView.getText().toString().trim()) + " 00:00:00");
                updatedlotVar.setQuantity(Long.valueOf(quantity.getText().toString().trim()).longValue());
                updatedlotVar.setVariableName(String.valueOf(lotvar_dropdown.getSelectedItem()));
                updatedlotVar.setVariableId(allVariable.get(lotvar_dropdown.getSelectedItemPosition()).getVarId());
                updatedlotVar.setLotId(selectedLotVariable.getLotId());
                updatedlotVar.setUpdated(1);

                if (quantity.getText().toString().trim().length() == 0)
                {
                    MessagePopup alert = new MessagePopup(activ, "Error!", "Enter a valid quantity");
                    alert.show();
                }
                else
                {
                    if (da.isDataToUpdateServer())
                    {
                        if (da.isConnectedToInternet())
                        {
                            new UpdateLotVariableAsyncTask().execute(da.AppSettingsDetail().getServerUrl() + "UpdateLotVariable");
                        }
                        else
                        {
                            MessagePopup alert = new MessagePopup(activ, "Error!", "You cannot update lot variable; No Internet Connection");
                            alert.show();
                        }
                    }
                    else {
                        //check if external storage is available and not read only; and save lot detail to device file
                        if (!da.isExternalStorageAvailable() || da.isExternalStorageReadOnly()) {
                            MessagePopup alert = new MessagePopup(activ, "Error!", "No storage device found");
                            alert.show();
                        } else {
                            //get all field variables from device (NB: not a specific lot field variables that was passed as argument of the constructor)
                            lotVarList = da.GetAllLotVariable(da.getJsonDatafromExternalStorage(da.lotVariableFile));

                            for (LotVariable lv : lotVarList) {
                                //add updated field variable to device
                                if (selectedLotVariable.getLotvariableId() == lv.getLotvariableId()) {
                                    updatedlotVarList.add(updatedlotVar);
                                } else {
                                    updatedlotVarList.add(lv);
                                }
                            }

                            //save updated field variables table to device
                            da.saveToExternalStorage(gson.toJson(updatedlotVarList), da.lotVariableFile);

                            MessagePopup alert = new MessagePopup(activ, "Alert!", "The lot variable was successfully updated on the device");
                            alert.show();

                            Intent i = new Intent(activ, LotVariableDetail.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            activ.startActivity(i);
                            dismiss();
                        }
                    }
                }
                break;
            case R.id.cancel_button:
                dismiss();
                break;
            default:
                break;
        }
    }

    private class UpdateLotVariableAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String jsonDataToSend = "", response = "";
            try {
                jsonDataToSend = gson.toJson(updatedlotVar, type);
                response = da.postJSONdata(urls[0], jsonDataToSend);

            } catch (Exception e) {
                response = null;
            }
            return response;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            try {
                if (result != null) {
                    wsSQLResult response = gson.fromJson(result, wsSQLResult.class);
                    if (Integer.parseInt(response.getWasSuccessful()) == 1) {
                        MessagePopup alert = new MessagePopup(activ, "Alert!", response.getException());
                        alert.show();

                        Intent i = new Intent(activ, LotVariableDetail.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        activ.startActivity(i);
                        dismiss();
                    } else {
                        MessagePopup alert = new MessagePopup(activ, "Error!", response.getException());
                        alert.show();
                    }
                } else {
                    MessagePopup alert = new MessagePopup(activ, "Error!", "Cannot connect to server");
                    alert.show();
                }
            } catch (Exception ex) {
            }
        }
    }
}