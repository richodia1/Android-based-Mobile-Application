package com.iita.iitagenebank;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Simeon on 06/03/2016.
 */
public class AddNewLotItemPopup extends Dialog implements View.OnClickListener
{
    public Activity activ;
    TextView popuptitle;
    EditText quantity, altIdentifier, itemName, prefix, latinName, scale;
    public Button add_button, cancel_button;
    DataAccess da;
    LotList selectedLotList = new LotList();
    ArrayList<Item> itemList = new ArrayList<Item>();
    ArrayList<Lot> lotList = new ArrayList<Lot>();
    ArrayList<LotListLot> lotListLots = new ArrayList<LotListLot>();
    LotlistItemLot newItemLot = new LotlistItemLot();
    Gson gson = new Gson();
    Type type = new TypeToken<LotlistItemLot>() {}.getType();
    Spinner itemTypeName, containerTypeName, parentlocationSpinner, childlocationSpinner;
    List itemTypeList = new ArrayList();
    List containerTypeList = new ArrayList();
    List childlocationList = new ArrayList();
    List parentlocationList = new ArrayList();
    ArrayList<ItemType> allItemType = new ArrayList<ItemType>();
    ArrayList<ContainerType> allContainerType = new ArrayList<ContainerType>();
    ArrayList<Location> allParentLocations = new ArrayList<Location>();
    ArrayList<Location> allChildLocations = new ArrayList<Location>();
    long itemId = 0;
    long lotId = 0;
    long parentlocationId = 0;

    public AddNewLotItemPopup(Activity a, LotList lotList)
    {
        super(a);
        this.activ = a;
        this.selectedLotList = lotList;
        da = new DataAccess(a);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add_new_lot_item);
        try
        {
            popuptitle = (TextView) findViewById(R.id.lotlist_name);
            popuptitle.setText(selectedLotList.getName());

            itemId = da.getNewTableRecordID();
            lotId = da.getNewTableRecordID();

            add_button = (Button) findViewById(R.id.add_button);
            add_button.setOnClickListener(this);

            cancel_button = (Button) findViewById(R.id.cancel_button);
            cancel_button.setOnClickListener(this);

            altIdentifier = (EditText) findViewById(R.id.altIdentifier);
            itemName = (EditText) findViewById(R.id.itemName);
            prefix = (EditText) findViewById(R.id.prefix);
            latinName = (EditText) findViewById(R.id.latinName);
            quantity = (EditText) findViewById(R.id.quantity);
            scale = (EditText) findViewById(R.id.scale);

            new GetItemTypesAsync().execute(da.AppSettingsDetail().getServerUrl() + "itemtypes");
            new GetContainerTypesAsync().execute(da.AppSettingsDetail().getServerUrl() + "containertypes");
            new GetParentLocationsAsync().execute(da.AppSettingsDetail().getServerUrl() + "parentlocations/" + da.AppSettingsDetail().getUpdateUser());
        }
        catch (Exception ex) {}
    }

    private class GetItemTypesAsync extends AsyncTask<String, Void, ArrayList<ItemType>> {
        @Override
        protected void onPostExecute(final ArrayList<ItemType> result) {
            try
            {
                if (result != null && result.size() > 0)
                {
                    allItemType = result;
                    itemTypeList = da.getItemTypeNames(allItemType);
                    itemTypeName = (Spinner) findViewById(R.id.itemTypeName);

                    // set variables as items of field variable drop down / spinner
                    ArrayAdapter dataAdapter = new ArrayAdapter(activ, android.R.layout.simple_spinner_item, itemTypeList);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    itemTypeName.setAdapter(dataAdapter);
                }
            }
            catch (Exception ex) {}
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<ItemType> doInBackground(String... urls) {
            ArrayList<ItemType> ItemTypeList = new ArrayList<ItemType>();
            try {
                // get item types data either from server or device depending on value of the data to update in settings
                if (da.isDataToUpdateServer())
                    ItemTypeList = da.GetAllItemType(da.getJSONdata(urls[0]));
                else
                    ItemTypeList = da.GetAllItemType(da.getJsonDatafromExternalStorage(da.itemTypeFile));

                return ItemTypeList;
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return null;
        }
    }

    private class GetContainerTypesAsync extends AsyncTask<String, Void, ArrayList<ContainerType>> {
        @Override
        protected void onPostExecute(final ArrayList<ContainerType> result) {
            try
            {
                if (result != null && result.size() > 0)
                {
                    allContainerType = result;
                    containerTypeList = da.getContainerTypeNames(allContainerType);
                    containerTypeName = (Spinner) findViewById(R.id.containerTypeName);

                    // set variables as items of field variable drop down / spinner
                    ArrayAdapter dataAdapter = new ArrayAdapter(activ, android.R.layout.simple_spinner_item, containerTypeList);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    containerTypeName.setAdapter(dataAdapter);
                }
            }
            catch (Exception ex) {}
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<ContainerType> doInBackground(String... urls) {
            ArrayList<ContainerType> ContainerTypeList = new ArrayList<ContainerType>();
            try {
                // get container types data either from server or device depending on value of the data to update in settings
                if (da.isDataToUpdateServer())
                    ContainerTypeList = da.GetAllContainerType(da.getJSONdata(urls[0]));
                else
                    ContainerTypeList = da.GetAllContainerType(da.getJsonDatafromExternalStorage(da.containerTypeFile));

                return ContainerTypeList;
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return null;
        }
    }

    private class GetParentLocationsAsync extends AsyncTask<String, Void, ArrayList<Location>> {
        @Override
        protected void onPostExecute(final ArrayList<Location> result) {
            try
            {
                if (result != null && result.size() > 0)
                {
                    allParentLocations = result;
                    parentlocationList = da.getParentLocationNames(allParentLocations);
                    parentlocationSpinner = (Spinner) findViewById(R.id.parentlocationSpinner);

                    // set items of parent location drop down / spinner
                    ArrayAdapter parentlocAdapter = new ArrayAdapter(activ, android.R.layout.simple_spinner_item, parentlocationList);
                    parentlocAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    parentlocationSpinner.setAdapter(parentlocAdapter);
                    parentlocationSpinner.setOnItemSelectedListener(
                            new AdapterView.OnItemSelectedListener()
                            {
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                                {
                                    parentlocationId = result.get(position).getLocationId();
                                    new GetChildLocationsAsync().execute(da.AppSettingsDetail().getServerUrl() + "getAparentChildLocations/" + parentlocationId);
                                }
                                public void onNothingSelected(AdapterView<?> parent) {
                                }
                            });
                }
            }
            catch (Exception ex) {}
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Location> doInBackground(String... urls) {
            ArrayList<Location> LocationList = new ArrayList<Location>();
            try {
                // get location data either from server or device depending on value of the data to update in settings
                if (da.isDataToUpdateServer())
                    LocationList = da.GetAllLocation(da.getJSONdata(urls[0]));
                else
                    LocationList = da.GetAllLocation(da.getJsonDatafromExternalStorage(da.parentLocationFile));

                return LocationList;
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return null;
        }
    }

    private class GetChildLocationsAsync extends AsyncTask<String, Void, ArrayList<Location>> {
        @Override
        protected void onPostExecute(final ArrayList<Location> result) {
            try
            {
                if (result != null && result.size() > 0)
                {
                    allChildLocations = result;
                    childlocationList = da.getLocationNames(allChildLocations);
                    childlocationSpinner = (Spinner) findViewById(R.id.childlocationSpinner);

                    // set items of child location drop down / spinner
                    ArrayAdapter childlocAdapter = new ArrayAdapter(activ, android.R.layout.simple_spinner_item, childlocationList);
                    childlocAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    childlocationSpinner.setAdapter(childlocAdapter);
                }
            }
            catch (Exception ex) {}
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Location> doInBackground(String... urls) {
            ArrayList<Location> LocationList = new ArrayList<Location>();
            try {
                // get location data either from server or device depending on value of the data to update in settings
                if (da.isDataToUpdateServer())
                    LocationList = da.GetAllLocation(da.getJSONdata(urls[0]));
                else
                    LocationList = da.GetAllParentChildLocations(da.getJsonDatafromExternalStorage(da.childLocationFile), parentlocationId);

                return LocationList;
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
            case R.id.add_button:
                try
                {
                    String AltIdentifier = altIdentifier.getText().toString().trim();
                    String ItemName = itemName.getText().toString().trim();
                    String Prefix = prefix.getText().toString().trim();
                    String LatinName = latinName.getText().toString().trim();
                    String Quantity = quantity.getText().toString().trim();
                    String Scale = scale.getText().toString().trim();

                    if (AltIdentifier.length() == 0) {
                        MessagePopup alert = new MessagePopup(activ, "Error!", "Enter the item alternative identifier");
                        alert.show();
                    } else {
                        if (ItemName.length() == 0) {
                            MessagePopup alert = new MessagePopup(activ, "Error!", "Enter the item name");
                            alert.show();
                        } else {
                            if (Prefix.length() == 0) {
                                MessagePopup alert = new MessagePopup(activ, "Error!", "Enter the item prefix");
                                alert.show();
                            } else {
                                if (LatinName.length() == 0) {
                                    MessagePopup alert = new MessagePopup(activ, "Error!", "Enter the item latin name");
                                    alert.show();
                                } else {

                                    if (Quantity.length() == 0) {
                                        MessagePopup alert = new MessagePopup(activ, "Error!", "Enter the lot quantity");
                                        alert.show();
                                    } else {
                                        if (Scale.length() == 0) {
                                            MessagePopup alert = new MessagePopup(activ, "Error!", "Enter the item scale");
                                            alert.show();
                                        }
                                        else
                                        {
                                            long ItemTypeID = allItemType.get(itemTypeName.getSelectedItemPosition()).getItemtypeId();
                                            long ContainerId = allContainerType.get(containerTypeName.getSelectedItemPosition()).getContainertypeId();
                                            long LocationID = allChildLocations.get(childlocationSpinner.getSelectedItemPosition()).getLocationId();
                                            String newLocationName = allChildLocations.get(childlocationSpinner.getSelectedItemPosition()).getName();

                                            //set new field variable values to be recorded
                                            newItemLot.setLotlistId(selectedLotList.getLotlistId());
                                            newItemLot.setAlternativeIdentifier(AltIdentifier);
                                            newItemLot.setItemId(itemId);
                                            newItemLot.setItemName(ItemName);
                                            newItemLot.setPrefix(Prefix);
                                            newItemLot.setItemTypeId(ItemTypeID);
                                            newItemLot.setLatinName(LatinName);
                                            newItemLot.setLotId(lotId);
                                            newItemLot.setContainerId(ContainerId);
                                            newItemLot.setLocationId(LocationID);
                                            newItemLot.setQuantity(Long.valueOf(Quantity).longValue());
                                            newItemLot.setScale(Scale);
                                            newItemLot.setLastUpdated(da.getDate());
                                            newItemLot.setLastUpdatedBy(da.UserDeviceDetail().getUsername());

                                            if (da.isDataToUpdateServer()) {
                                                if (da.isConnectedToInternet()) {
                                                    new AddNewLotlistItemLotAsyncTask().execute(da.AppSettingsDetail().getServerUrl() + "AddNewLotlistItemLot");
                                                } else {
                                                    MessagePopup alert = new MessagePopup(activ, "Error!", "You cannot add new lot; No Internet Connection");
                                                    alert.show();
                                                }
                                            }
                                            else
                                            {
                                                //check if external storage is available and not read only; and save new item lot detail to device file
                                                if (!da.isExternalStorageAvailable() || da.isExternalStorageReadOnly()) {
                                                    MessagePopup alert = new MessagePopup(activ, "Error!", "No storage device found");
                                                    alert.show();
                                                } else {
                                                    //get all items, lots, and lot list lots from device
                                                    if(da.GetAllItem(da.getJsonDatafromExternalStorage(da.itemFile))!=null)
                                                        itemList = da.GetAllItem(da.getJsonDatafromExternalStorage(da.itemFile));
                                                    if(da.GetAllLot(da.getJsonDatafromExternalStorage(da.lotFile))!=null)
                                                        lotList = da.GetAllLot(da.getJsonDatafromExternalStorage(da.lotFile));
                                                    if(da.GetAllLotListLot(da.getJsonDatafromExternalStorage(da.lotListLotFile))!=null)
                                                        lotListLots = da.GetAllLotListLot(da.getJsonDatafromExternalStorage(da.lotListLotFile));

                                                    //set and add an instance of the new added item
                                                    Item item = new Item();
                                                    item.setItemId(itemId);
                                                    item.setAccessionIdentifier(1);
                                                    item.setAlternativeIdentifier(AltIdentifier);
                                                    item.setDateLastModified(da.getDate());
                                                    item.setName(ItemName);
                                                    item.setPrefix(Prefix);
                                                    item.setVersion(0);
                                                    item.setItemTypeId(ItemTypeID);
                                                    item.setLatinName(LatinName);
                                                    itemList.add(item);

                                                    //set and add an instance of the new added lot
                                                    Lot lot = new Lot();
                                                    lot.setLotId(lotId);
                                                    lot.setItemId(itemId);
                                                    lot.setContainerId(ContainerId);
                                                    lot.setLocationId(LocationID);
                                                    lot.setBarcode(0); //it's not needed since it'll be auto generated by the server with the lotId
                                                    lot.setItemName(ItemName);
                                                    lot.setLocationDetail(newLocationName);
                                                    lot.setQuantity(Long.valueOf(Quantity).longValue());
                                                    lot.setStatus(1);
                                                    lot.setVersion(0);
                                                    lot.setScale(Scale);
                                                    lot.setLastUpdated(da.getDate());
                                                    lot.setLastUpdatedBy(da.UserDeviceDetail().getUsername());
                                                    lot.setUpdated(1);
                                                    lotList.add(lot);

                                                    //set and add an instance of the new lot to the lot list lots table
                                                    LotListLot lllot = new LotListLot();
                                                    lllot.setLotlistId(selectedLotList.getLotlistId());
                                                    lllot.setLotId(lotId);
                                                    lotListLots.add(lllot);

                                                    //save items, lots, and lot list lots tables to device
                                                    da.saveToExternalStorage(gson.toJson(itemList), da.itemFile);
                                                    da.saveToExternalStorage(gson.toJson(lotList), da.lotFile);
                                                    da.saveToExternalStorage(gson.toJson(lotListLots), da.lotListLotFile);

                                                    MessagePopup alert = new MessagePopup(activ, "Alert!", "The new item lot was successfully added to the device database");
                                                    alert.show();

                                                    Intent i = new Intent(activ, AllLotListLots.class);
                                                    i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                                    activ.startActivity(i);
                                                    dismiss();
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
                catch (Exception e)
                {
                    MessagePopup alert = new MessagePopup(activ, "Error!", e.getMessage());
                    alert.show();
                }
                break;
            case R.id.cancel_button:
                dismiss();
                break;
            default:
                break;
        }
    }

    private class AddNewLotlistItemLotAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String jsonDataToSend = "", response = "";
            try {
                jsonDataToSend = gson.toJson(newItemLot, type);
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
            try
            {
                if (result != null)
                {
                    wsSQLResult response = gson.fromJson(result, wsSQLResult.class);
                    if (Integer.parseInt(response.getWasSuccessful()) == 1)
                    {
                        Intent i = new Intent(activ, AllLotListLots.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        activ.startActivity(i);
                        dismiss();

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
            catch (Exception ex) {}
        }
    }
}
