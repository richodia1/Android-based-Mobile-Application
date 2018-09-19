package com.iita.iitagenebank;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Settings extends AppCompatActivity
{
    private TextView server, updateData, lastOperation, updateUser;
    DataAccess da = new DataAccess(this);
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        try {
            // Set up your ActionBar
            final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            // set actionbar background color
            final int actionBarColor = getResources().getColor(R.color.action_bar);
            actionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));
            actionBar.setTitle("Settings");

            Button updateButton = (Button) findViewById(R.id.update_button);
            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    EditSettingsPopup alert = new EditSettingsPopup(Settings.this);
                    alert.show();
                }
            });
        }
        catch (Exception ex) {}
    }

    @Override
    public void onResume() {
        super.onResume();
        try
        {
            server = (TextView) findViewById(R.id.server);
            server.setText(da.AppSettingsDetail().getServer());
            updateData = (TextView) findViewById(R.id.updateData);
            updateData.setText(da.AppSettingsDetail().getUpdateData());
            lastOperation = (TextView) findViewById(R.id.lastOperation);
            lastOperation.setText(da.AppSettingsDetail().getLastOperation());
            updateUser = (TextView) findViewById(R.id.updateUser);
            updateUser.setText(da.AppSettingsDetail().getUpdateUser());
        }
        catch (Exception ex) {}
    }
    // override the actionbar up button navigating to parent activity but navigate to previous activity
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
