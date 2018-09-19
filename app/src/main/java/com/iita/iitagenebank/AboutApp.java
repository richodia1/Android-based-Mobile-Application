package com.iita.iitagenebank;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Calendar;

public class AboutApp extends AppCompatActivity {
    TextView version, copyright_year;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_app);

        try {
            // Set up your ActionBar
            final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            // set actionbar background color
            final int actionBarColor = getResources().getColor(R.color.action_bar);
            actionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));
            actionBar.setTitle("About IITA Gene Bank App");

            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            int year = Calendar.getInstance().get(Calendar.YEAR);

            version = (TextView) findViewById(R.id.version);
            copyright_year = (TextView) findViewById(R.id.copyright_year);

            version.setText("Version: " + info.versionName);
            copyright_year.setText("© " + year);
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
