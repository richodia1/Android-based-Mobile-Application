package com.iita.iitagenebank;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class MainPage extends AppCompatActivity
{
    DataAccess da = new DataAccess(this);
    EditText barcode;
    ArrayList<Lot> lotList = new ArrayList<Lot>();
    Lot alot = new Lot();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        try
        {
            // Inflate your custom layout
            final ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater().inflate(R.layout.custom_actionbar, null);

            // Set up your ActionBar
            final ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setCustomView(actionBarLayout);

            Toolbar parent = (Toolbar) actionBarLayout.getParent();//first get parent toolbar of current action bar
            parent.setContentInsetsAbsolute(0, 0);// set padding programmatically to 0dp

            // You customization
            final int actionBarColor = getResources().getColor(R.color.action_bar);
            actionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));

            final Button actionbarMenu = (Button) findViewById(R.id.actionbar_menu);
            actionbarMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(MainPage.this, v);
                    // Inflate the menu from xml
                    popup.getMenuInflater().inflate(R.menu.menu_main, popup.getMenu());

                    // Force icons to show
                    Object menuHelper;
                    Class[] argTypes;
                    try {
                        Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
                        fMenuHelper.setAccessible(true);
                        menuHelper = fMenuHelper.get(popup);
                        argTypes = new Class[]{boolean.class};
                        menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
                    } catch (Exception e) {
                        popup.show();
                        return;
                    }

                    // Setup menu item selection
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu_lotlists:
                                    Intent goToAllLotListsActivity = new Intent(getApplicationContext(), AllLotLists.class);
                                    startActivity(goToAllLotListsActivity);
                                    return true;
                                case R.id.menu_locations:
                                    Intent goToAllLocationsActivity = new Intent(getApplicationContext(), AllLocations.class);
                                    startActivity(goToAllLocationsActivity);
                                    return true;
                                case R.id.menu_settings:
                                    Intent goToSettingsActivity = new Intent(getApplicationContext(), Settings.class);
                                    startActivity(goToSettingsActivity);
                                    return true;
                                case R.id.menu_about_this_app:
                                    Intent goToAboutAppActivity = new Intent(getApplicationContext(), AboutApp.class);
                                    startActivity(goToAboutAppActivity);
                                    return true;
                                case R.id.menu_log_out:
                                    da.LogOut();
                                    Intent goToLogInActivity = new Intent(getApplicationContext(), Login.class);
                                    startActivity(goToLogInActivity);
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    // Show the menu
                    popup.show();
                }
            });

            final Button actionbarSynchronize = (Button) findViewById(R.id.actionbar_synchronize);
            actionbarSynchronize.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent goToSynchronizeActivity = new Intent(getApplicationContext(), Synchronize.class);
                    startActivity(goToSynchronizeActivity);
                }
            });

            final Button actionbarAllLots = (Button) findViewById(R.id.actionbar_all_lots);
            actionbarAllLots.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent goToAllLotsActivity = new Intent(getApplicationContext(), AllLots.class);
                    startActivity(goToAllLotsActivity);
                }
            });

            barcode = (EditText)findViewById(R.id.barcode);

            Button scanButton =(Button)findViewById(R.id.scan_button);
            scanButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    IntentIntegrator scanIntegrator = new IntentIntegrator(MainPage.this);
                    scanIntegrator.initiateScan();
                }
            });

            Button searchButton =(Button)findViewById(R.id.search_button);
            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (barcode.getText().toString().trim().length() == 0)
                    {
                        MessagePopup alert = new MessagePopup(MainPage.this, "Error!", "Enter a barcode");
                        alert.show();
                    }
                    else
                    {
                        if(da.isDataToUpdateServer())
                        {
                            if (da.isConnectedToInternet())
                            {
                                new GetAllServerLots().execute(da.AppSettingsDetail().getServerUrl() + "lots/" + da.AppSettingsDetail().getUpdateUser());
                            }
                            else
                            {
                                MessagePopup alert = new MessagePopup(MainPage.this, "Error!", "No Internet Connection");
                                alert.show();
                            }
                        }
                        else
                        {
                            lotList = da.GetAllLot(da.getJsonDatafromExternalStorage(da.lotFile));
                            SearchBarcode(lotList);
                        }
                    }
                }
            });
        }
        catch (Exception ex){}
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null)
        {
            String scanContent = scanningResult.getContents();
            barcode.setText(scanContent);
        }
        else
        {
            Toast toast = Toast.makeText(getApplicationContext(), "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private class GetAllServerLots extends AsyncTask<String, Void, ArrayList<Lot>>
    {
        @Override
        protected void onPostExecute(final ArrayList<Lot> result)
        {
            try
            {
                if (result != null && result.size() > 0)
                    SearchBarcode(result);
                else {
                    MessagePopup alert = new MessagePopup(MainPage.this, "Error!", "Canot fetch lot data from the server");
                    alert.show();
                }
            }
            catch (Exception ex){}
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Lot> doInBackground(String... urls)
        {
            ArrayList<Lot> serverlotList = new ArrayList<Lot>();
            try
            {
                serverlotList = da.GetAllLot(da.getJSONdata(urls[0]));
            }
            catch(Throwable t)
            {
                t.printStackTrace();
            }
            return serverlotList;
        }
    }

    void SearchBarcode(ArrayList<Lot> newlotList)
    {
        int k = 0;
        if (newlotList != null)
        {
            for (Lot lot : newlotList)
            {
                if (barcode.getText().toString().trim().matches(String.valueOf(lot.getBarcode()))) {
                    k = 1;
                    alot = lot;
                }
            }

            if (k == 1) {
                //pass clicked lot to LotDetail class to display the detail of the clicked lot
                Intent i = new Intent(MainPage.this, LotDetail.class);
                i.putExtra("ClickedLot", alot);
                startActivity(i);
            }
            else
            {
                MessagePopup alert = new MessagePopup(MainPage.this, "Error!", "Could not find Lot with barcode: " + barcode.getText().toString());
                alert.show();
            }
        }
        else
        {
            MessagePopup alert = new MessagePopup(MainPage.this, "Error!", "You have no data");
            alert.show();
        }
    }
    //override default value and programmatically implement device back button to close the app
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //moveTaskToBack(true);
            Intent setIntent = new Intent(Intent.ACTION_MAIN);
            setIntent.addCategory(Intent.CATEGORY_HOME);
            setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(setIntent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
