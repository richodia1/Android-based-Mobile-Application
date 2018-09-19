package com.iita.iitagenebank;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class Login extends AppCompatActivity
{
    EditText Username, Password;
    DataAccess da = new DataAccess(this);
    Gson gson = new Gson();
    Type type = new TypeToken<Users>() {}.getType();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        getSupportActionBar().hide();

        try {
            //redirect user to MyLibraryPage activity if user device login status is 1
            if (da.CheckLoginStatus())
            {
                Intent goToMainPageActivity = new Intent(getApplicationContext(), MainPage.class);
                startActivity(goToMainPageActivity);

            }
            else
            {
                Username = (EditText) findViewById(R.id.username);
                Password = (EditText) findViewById(R.id.password);

                Button loginButton = (Button) findViewById(R.id.login_button);
                loginButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Username.getText().toString().trim().length() == 0) {
                            MessagePopup alert = new MessagePopup(Login.this, "Error!", "Enter your Username");
                            alert.show();
                        } else {
                            if (Password.getText().toString().trim().length() == 0) {
                                MessagePopup alert = new MessagePopup(Login.this, "Error!", "Enter your Password");
                                alert.show();
                            } else {
                                if (da.isConnectedToInternet()) {
                                    // call AsynTask to register user on separate thread
                                    new LogInAsyncTask().execute(da.psUrl + "LoginUser");
                                } else {
                                    MessagePopup alert = new MessagePopup(Login.this, "Error!", "No Internet Connection");
                                    alert.show();
                                }
                            }
                        }
                    }
                });
            }
        }
        catch (Exception e){}
    }


    private class LogInAsyncTask extends AsyncTask<String, Void, String>
    {
        String username = Username.getText().toString().trim();
        String password = Password.getText().toString().trim();

        @Override
        protected String doInBackground(String... urls) {
            String jsonDataToSend = "", response = "";

            try {
                Users user = new Users();
                user.setUsername(username);
                user.setPassword(da.GenerateHash(password));
                user.setLoginStatus(1);
                user.setId(0);
                jsonDataToSend = gson.toJson(user, type); //convert user details to json

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
            try {
                if(result != null)
                {
                    Users ud = gson.fromJson(result, Users.class);
                    //validate user's login detail by checking if returned user detail login status is 1
                    //user login status is set to 0, -1, -2, and -3 by the server if user password and email does not match
                    if (ud.getLoginStatus() == 1)
                    {
                        Users userDetail = new Users();
                        userDetail.setUsername(username);
                        userDetail.setPassword(da.GenerateHash(password));
                        userDetail.setLoginStatus(1);
                        userDetail.setId(ud.getId()); //save server returned user id on the device
                        String jsonUserDetail = gson.toJson(userDetail, type); //convert user details to json
                        //save user detail to device
                        da.saveToExternalStorage(jsonUserDetail, da.userJSONfilename);

                        if(!da.SettingsExist()) //save default app settings if settings does not exist
                        {
                            da.saveAppSettings(da.pserver, da.psUrl, "Server", "", 0, username);
                        }
                        new GetUsersAsync().execute(da.AppSettingsDetail().getServerUrl() + "getAllUsers");
                        Intent goToMainPageActivity = new Intent(getApplicationContext(), MainPage.class);
                        startActivity(goToMainPageActivity);
                    }
                    else
                    {
                        MessagePopup failureAlert = new MessagePopup(Login.this, "Error!", ud.getUsername());
                        failureAlert.show();
                    }
                }
            } catch (Exception e) {}
        }
    }

    private class GetUsersAsync extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(final String result) {
            try
            {
                da.saveToExternalStorage(result, da.usersFile);
            }
            catch (Exception ex) {}
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls)
        {
            String UserList = null;
            try {
                UserList = da.getJSONdata(urls[0]);
                return UserList;
            }
            catch (Throwable t) {t.printStackTrace();}
            return null;
        }
    }

    //override default value and programmatically implement device back button to redirect to MyLibraryPage
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory( Intent.CATEGORY_HOME );
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
