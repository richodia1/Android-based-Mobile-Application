package com.iita.iitagenebank;

        import android.app.Activity;
        import android.app.Dialog;
        import android.content.Intent;
        import android.graphics.drawable.ColorDrawable;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.os.Environment;
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

        import java.io.File;
        import java.lang.reflect.Type;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Calendar;
        import java.util.Date;

/**
 * Created by Simeon on 22/02/2016.
 */
public class EditFieldVariablePopup extends Dialog implements View.OnClickListener
{
    public Activity activ;
    public TextView popuptitle, quantity_value;
    public Button update_button, cancel_button;
    DataAccess da;
    FieldVariable selectedFieldVariable = new FieldVariable();
    ArrayList<FieldVariable> fieldVarList;
    ArrayList<FieldVariable> updatedfieldVarList  = new ArrayList<FieldVariable>();
    FieldVariable updatedfieldVar  = new FieldVariable();
    Gson gson = new Gson();
    Type type = new TypeToken<FieldVariable>() {}.getType();
    Spinner fieldvar_dropdown;
    ImageButton calendarButton;
    private int day = 0;
    private int month = 0;
    private int year = 0;
    private EditText dateView;

    public EditFieldVariablePopup(Activity a, FieldVariable fv)
    {
        super(a);
        this.activ = a;
        this.selectedFieldVariable = fv;
        da = new DataAccess(a);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.edit_field_variable);
        try
        {
            popuptitle = (TextView) findViewById(R.id.fieldvar_name);
            popuptitle.setText(selectedFieldVariable.getVar());

            fieldvar_dropdown = (Spinner) findViewById(R.id.fieldvar_dropdown);
            // set variables as items of field variable drop down / spinner
            ArrayAdapter dataAdapter = new ArrayAdapter(activ, android.R.layout.simple_spinner_item, da.getVariables());
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            fieldvar_dropdown.setAdapter(dataAdapter);

            String currentVar = selectedFieldVariable.getVar(); //the value you want the position for
            ArrayAdapter myAdap = (ArrayAdapter) fieldvar_dropdown.getAdapter(); //cast to an ArrayAdapter
            int spinnerPosition = myAdap.getPosition(currentVar);
            //set the default according to value
            fieldvar_dropdown.setSelection(spinnerPosition);

            quantity_value = (EditText) findViewById(R.id.quantity_value);
            quantity_value.setText(selectedFieldVariable.getQty());

            update_button = (Button) findViewById(R.id.update_button);
            update_button.setOnClickListener(this);

            cancel_button = (Button) findViewById(R.id.cancel_button);
            cancel_button.setOnClickListener(this);

            calendarButton = (ImageButton) findViewById(R.id.calendarButton);
            calendarButton.setOnClickListener(this);

            dateView = (EditText) findViewById(R.id.dateView);
            if(selectedFieldVariable.getDate()!=null && selectedFieldVariable.getDate()!="") {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date parse = sdf.parse(selectedFieldVariable.getDate());
                Calendar c = Calendar.getInstance();
                c.setTime(parse);

                day = c.get(Calendar.DATE);
                month = c.get(Calendar.MONTH);
                year = c.get(Calendar.YEAR);
                dateView.setText(da.PadToTwoDigits(day) + " - " + da.PadToTwoDigits(month + 1) + " - " + year);
            }
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
            case R.id.update_button:
                //set new field variable values to be recorded
                updatedfieldVar.setFieldvarId(selectedFieldVariable.getFieldvarId());
                updatedfieldVar.setCreatedBy(selectedFieldVariable.getCreatedBy());
                updatedfieldVar.setCreatedDate(selectedFieldVariable.getCreatedDate());
                updatedfieldVar.setLastUpdated(da.getDate());
                updatedfieldVar.setLastUpdatedBy(da.UserDeviceDetail().getUsername());
                updatedfieldVar.setVersion(selectedFieldVariable.getVersion());
                updatedfieldVar.setDate(da.YearMonthDayDateFormat(dateView.getText().toString().trim()) + " 00:00:00");
                updatedfieldVar.setQty(quantity_value.getText().toString().trim());
                updatedfieldVar.setVar(String.valueOf(fieldvar_dropdown.getSelectedItem()));
                updatedfieldVar.setLotId(selectedFieldVariable.getLotId());
                updatedfieldVar.setUpdated(1);

                if (quantity_value.getText().toString().trim().length() == 0)
                {
                    MessagePopup alert = new MessagePopup(activ, "Error!", "Enter a valid quantity");
                    alert.show();
                }
                else
                {
                    if (da.isDataToUpdateServer()) {
                        if (da.isConnectedToInternet()) {
                            new UpdateFieldVariableAsyncTask().execute(da.AppSettingsDetail().getServerUrl() + "UpdateFieldVariable");
                        } else {
                            MessagePopup alert = new MessagePopup(activ, "Error!", "You cannot update field variable; No Internet Connection");
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
                            if (da.GetAllFieldVariable(da.getJsonDatafromExternalStorage(da.fieldVariableFile))!=null)
                                fieldVarList = da.GetAllFieldVariable(da.getJsonDatafromExternalStorage(da.fieldVariableFile));

                            for (FieldVariable fv : fieldVarList)
                            {
                                //add updated field variable to device
                                if (selectedFieldVariable.getFieldvarId() == fv.getFieldvarId())
                                {
                                    updatedfieldVarList.add(updatedfieldVar);
                                }
                                else
                                {
                                    updatedfieldVarList.add(fv);
                                }
                            }

                            //save updated field variables table to device
                            da.saveToExternalStorage(gson.toJson(updatedfieldVarList), da.fieldVariableFile);

                            MessagePopup alert = new MessagePopup(activ, "Alert!", "The field variable was successfully updated on the device database");
                            alert.show();
                        }
                    }
                    Intent i = new Intent(activ, FieldVariableDetail.class);
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

    private class UpdateFieldVariableAsyncTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... urls)
        {
            String jsonDataToSend = "", response = "";
            try
            {
                jsonDataToSend = gson.toJson(updatedfieldVar, type);
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
