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
import java.util.List;

/**
 * Created by Simeon on 08/03/2016.
 */
public class RecordSubtypeTransactionPopup extends Dialog implements View.OnClickListener
{
    public Activity activ;
    public TextView popuptitle, quantity, scale;
    public Button record_button, cancel_button;
    DataAccess da;
    Lot selectedLot = new Lot();
    ArrayList<SubtypeTransaction> subtypeTransactionList;
    ArrayList<SubtypeTransaction> newSubtypeTransactionList  = new ArrayList<SubtypeTransaction>();
    SubtypeTransaction newSubtypeTransaction  = new SubtypeTransaction();
    List subtypeList = new ArrayList();
    ArrayList<Subtype> allSubtype = new ArrayList<Subtype>();
    Gson gson = new Gson();
    Type type = new TypeToken<SubtypeTransaction>() {}.getType();
    Spinner subtype_dropdown;
    ImageButton calendarButton;
    private int day = 0;
    private int month = 0;
    private int year = 0;
    private EditText dateView;

    public RecordSubtypeTransactionPopup(Activity a, Lot lot)
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
        setContentView(R.layout.record_subtype_transaction);
        try
        {
            popuptitle = (TextView) findViewById(R.id.lot_name);
            popuptitle.setText("Record Transaction " + " (" + selectedLot.getBarcode() + ")");

            new GetSubtypesAsync().execute(da.AppSettingsDetail().getServerUrl() + "subtypes");

            quantity = (EditText) findViewById(R.id.quantity);
            scale = (EditText) findViewById(R.id.scale);

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

    private class GetSubtypesAsync extends AsyncTask<String, Void, ArrayList<Subtype>> {
        @Override
        protected void onPostExecute(final ArrayList<Subtype> result) {
            try
            {
                if (result != null && result.size() > 0)
                {
                    allSubtype = result;
                    subtypeList = da.getSubtypes(allSubtype);
                    subtype_dropdown = (Spinner) findViewById(R.id.subtype_dropdown);

                    // set variables as items of field variable drop down / spinner
                    ArrayAdapter dataAdapter = new ArrayAdapter(activ, android.R.layout.simple_spinner_item, subtypeList);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    subtype_dropdown.setAdapter(dataAdapter);
                }
            }
            catch (Exception ex) {}
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Subtype> doInBackground(String... urls) {
            ArrayList<Subtype> SubtypeList = new ArrayList<Subtype>();
            try {
                // get Subtype data either from server or device depending on value of the data to update in settings
                if (da.isDataToUpdateServer())
                    SubtypeList = da.GetAllSubtype(da.getJSONdata(urls[0]));
                else
                    SubtypeList = da.GetAllSubtype(da.getJsonDatafromExternalStorage(da.subtypeFile));

                return SubtypeList;
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
                String Quantity = quantity.getText().toString().trim();
                String Scale = scale.getText().toString().trim();

                if (Scale.length() == 0) {
                    MessagePopup alert = new MessagePopup(activ, "Error!", "Enter the scale");
                    alert.show();
                }
                else
                {
                    if (Quantity.length() == 0)
                    {
                        MessagePopup alert = new MessagePopup(activ, "Error!", "Enter a valid quantity");
                        alert.show();
                    }
                    else
                    {
                        //set new Subtype Transaction values to be recorded
                        newSubtypeTransaction.setSubtypeTransId(da.getNewTableRecordID());
                        newSubtypeTransaction.setSubtype(String.valueOf(subtype_dropdown.getSelectedItem()));
                        newSubtypeTransaction.setLotId(selectedLot.getLotId());
                        newSubtypeTransaction.setQuantity(Long.valueOf(Quantity).longValue());
                        newSubtypeTransaction.setDate(da.YearMonthDayDateFormat(dateView.getText().toString().trim()) + " 00:00:00");
                        newSubtypeTransaction.setScale(Scale);
                        newSubtypeTransaction.setCreatedBy(da.UserDeviceDetail().getUsername());
                        newSubtypeTransaction.setCreatedDate(da.getDate());
                        newSubtypeTransaction.setLastUpdatedBy(da.UserDeviceDetail().getUsername());
                        newSubtypeTransaction.setLastUpdated(da.getDate());

                        if (da.isDataToUpdateServer())
                        {
                            if (da.isConnectedToInternet())
                            {
                                new RecordSubtypeTransactionAsyncTask().execute(da.AppSettingsDetail().getServerUrl() + "RecordSubtypeTransaction");
                            }
                            else
                            {
                                MessagePopup alert = new MessagePopup(activ, "Error!", "You cannot record the subtype; No Internet Connection");
                                alert.show();
                            }
                        }
                        else
                        {
                            //check if external storage is available and not read only; and save subtype transaction detail to device file
                            if (!da.isExternalStorageAvailable() || da.isExternalStorageReadOnly()) {
                                MessagePopup alert = new MessagePopup(activ, "Error!", "No storage device found");
                                alert.show();
                            } else {
                                //get all subtype transactions from device (NB: not a specific lot subtype transactions that was passed as argument of the constructor)
                                if (da.GetAllLotSubtypeTransaction(da.getJsonDatafromExternalStorage(da.subtypetransactionFile)) != null)
                                    subtypeTransactionList = da.GetAllLotSubtypeTransaction(da.getJsonDatafromExternalStorage(da.subtypetransactionFile));

                                //add new subtype transaction to device
                                newSubtypeTransactionList = subtypeTransactionList;
                                newSubtypeTransactionList.add(newSubtypeTransaction);

                                //save all subtype transactions to device
                                da.saveToExternalStorage(gson.toJson(newSubtypeTransactionList), da.subtypetransactionFile);

                                MessagePopup alert = new MessagePopup(activ, "Alert!", "The subtype transaction was successfully recorded on the device");
                                alert.show();
                            }
                        }
                        Intent i = new Intent(activ, LotSubtypeTransactions.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        activ.startActivity(i);
                        dismiss();
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

    private class RecordSubtypeTransactionAsyncTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... urls)
        {
            String jsonDataToSend = "", response = "";
            try
            {
                jsonDataToSend = gson.toJson(newSubtypeTransaction, type);
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
