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
 * Created by Simeon on 23/03/2016.
 */
public class AllLotListSelectedItemPopup extends Dialog implements View.OnClickListener
{
    public Activity activ;
    ArrayList<LotList> lotList;
    ArrayList<LotList> remaininglotList  = new ArrayList<LotList>();
    ArrayList<LotListLot> lotListLot;
    ArrayList<LotListLot> remaininglotListLot  = new ArrayList<LotListLot>();
    public TextView popuptitle;
    public Button deleteButton, cancelButton;
    DataAccess da;
    LotList selectedLotList;
    LotListAdapter adapter;
    Gson gson = new Gson();
    Type type = new TypeToken<LotList>() {}.getType();

    public AllLotListSelectedItemPopup(Activity a, ArrayList<LotList> lotList, int selectedLotListPosition, LotListAdapter adapter)
    {
        super(a);
        this.activ = a;
        this.lotList = lotList;
        this.selectedLotList = lotList.get(selectedLotListPosition);
        da = new DataAccess(a);
        this.adapter = adapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.selected_field_lot_variable_popup);

        popuptitle = (TextView) findViewById(R.id.item_name);
        popuptitle.setText(da.ResizeString(selectedLotList.getName(), 70));

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
                        new DeleteLotListAsyncTask().execute(da.AppSettingsDetail().getServerUrl() + "DeleteALotList");
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
                        //get all lot list lot and Lot List from device
                        lotListLot = da.GetAllLotListLot(da.getJsonDatafromExternalStorage(da.lotListLotFile));
                        lotList = da.GetAllLotList(da.getJsonDatafromExternalStorage(da.lotListFile));

                        //delete all lot list lots of selected Lot List from device
                        for(LotListLot lll : lotListLot)
                        {
                            if (lll.getLotlistId() != selectedLotList.getLotlistId())
                            {
                                remaininglotListLot.add(lll);
                            }
                        }

                        //delete selected Lot List from device
                        for(LotList ll : lotList)
                        {
                            if (ll.getLotlistId() != selectedLotList.getLotlistId())
                            {
                                remaininglotList.add(ll);
                            }
                        }

                        //save remaining lot list lot and Lot List to device
                        da.saveToExternalStorage(gson.toJson(remaininglotListLot), da.lotListLotFile);
                        da.saveToExternalStorage(gson.toJson(remaininglotList), da.lotListFile);
                        adapter.filter(String.valueOf(selectedLotList.getLotlistId())); // update Lot List model adapter
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


    private class DeleteLotListAsyncTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... urls)
        {
            String jsonDataToSend = "", response = "";
            try
            {
                jsonDataToSend = gson.toJson(selectedLotList, type);
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
                        adapter.filter(String.valueOf(selectedLotList.getLotlistId()));
                    }
                }
            }
            catch (Exception ex){}
        }
    }
}
