package com.iita.iitagenebank;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Simeon on 10/09/2015.
 */
public class DataAccess
{
    private Context _context;
    public String psUrl = "http://grctomcat.iita.org/IITAToolWebService/rest/PSWebService/";
    public String tsUrl = "http://grctomcat.iita.org/IITAToolWebService/rest/TSWebService/";
    //public String psUrl = "http://192.168.109.1:9191/IITAToolWebService/rest/PSWebService/";
    //public String tsUrl = "http://192.168.109.1:9191/IITAToolWebService/rest/TSWebService/";
    public String pserver = "Tomcat 1 (Production Server)";
    public String tserver = "Tomcat 2 (Testing Server)";
    public String lotFile = "LotFile.txt";
    public String itemFile = "ItemFile.txt";
    public String itemTypeFile = "ItemTypeFile.txt";
    public String containerTypeFile = "ContainerTypeFile.txt";
    public String lotVariableFile = "LotVariableFile.txt";
    public String lotListFile = "LotListFile.txt";
    public String fieldVariableFile = "FieldVariableFile.txt";
    public String childLocationFile = "ChildLocationFile.txt";
    public String parentLocationFile = "ParentLocationFile.txt";
    public String variableFile = "VariableFile.txt";
    public String migrationFile = "MigrationFile.txt";
    public String lotListLotFile = "LotListLotFile.txt";
    public String usersFile = "SubtypeFile.txt";
    public String subtypeFile = "AllUsersFile.txt";
    public String subtypetransactionFile = "SubtypeTransactionFile.txt";
    public String userJSONfilename = "UserFile.txt";
    public String appSettingsfilename = "AppSettings.txt";
    public String deletedFieldVariableFile = "DeletedFieldVariableFile.txt";
    Gson gson = new Gson();
    Type usertype = new TypeToken<Users>() {}.getType();
    public String databaseFolder = "IITADatabase";
    Type itemArrayType = new TypeToken<ArrayList<Item>>() {}.getType();
    Type lotArrayType = new TypeToken<ArrayList<Lot>>() {}.getType();
    Type lotlistArrayType = new TypeToken<ArrayList<LotList>>() {}.getType();
    Type lotlistlotArrayType = new TypeToken<ArrayList<LotListLot>>() {}.getType();
    Type fieldVarArrayType = new TypeToken<ArrayList<FieldVariable>>() {}.getType();
    Type deletedFieldVarArrayType = new TypeToken<ArrayList<DeletedFieldVariable>>() {}.getType();
    Type varArrayType = new TypeToken<ArrayList<Variable>>() {}.getType();
    Type userArrayType = new TypeToken<ArrayList<Users>>() {}.getType();
    Type containerTypeArrayType = new TypeToken<ArrayList<ContainerType>>() {}.getType();
    Type lotVarArrayType = new TypeToken<ArrayList<LotVariable>>() {}.getType();
    Type settype = new TypeToken<SettingDetail>() {}.getType();
    Type locationArrayType = new TypeToken<ArrayList<Location>>() {}.getType();
    Type itemTypeArrayType = new TypeToken<ArrayList<ItemType>>() {}.getType();
    Type subtypeTransArrayType = new TypeToken<ArrayList<SubtypeTransaction>>() {}.getType();
    Type migrationArrayType = new TypeToken<ArrayList<Migration>>() {}.getType();
    Type subtypeArrayType = new TypeToken<ArrayList<Subtype>>() {}.getType();
    ArrayList<LotList> lotListList;
    ArrayList<LotListLot> lotListLotList;
    ArrayList<Item> itemList;
    ArrayList<Lot> lotList;
    ArrayList<Variable> varList;
    ArrayList<FieldVariable> fieldVariables;
    ArrayList<DeletedFieldVariable> deletedFieldVariables;
    ArrayList<LotVariable> lotVariables;
    ArrayList<ItemType> itemTypeList;
    ArrayList<ContainerType> containerTypeList;
    ArrayList<Location> locationList;
    ArrayList<Migration> migrationList;
    ArrayList<SubtypeTransaction> subtypeTransList;
    ArrayList<Subtype> subtypeList;

    public DataAccess(Context context)
    {
        this._context = context;
    }

    public ArrayList<DeletedFieldVariable> GetAllDeletedFieldVariable(String jsonDeletedFieldVar)
    {
        deletedFieldVariables = new ArrayList<DeletedFieldVariable>();
        try
        {
            ArrayList<DeletedFieldVariable> allDeletedFieldVariableFromJson = gson.fromJson(jsonDeletedFieldVar, deletedFieldVarArrayType);
            deletedFieldVariables = allDeletedFieldVariableFromJson;
        }
        catch (Exception e)
        {}
        return deletedFieldVariables;
    }

    public ArrayList<Subtype> GetAllSubtype(String jsonSubType)
    {
        subtypeList = new ArrayList<Subtype>();
        try
        {
            ArrayList<Subtype> allSubtypeFromJson = gson.fromJson(jsonSubType, subtypeArrayType);
            subtypeList = allSubtypeFromJson;
        }
        catch (Exception e)
        {}
        return subtypeList;
    }

    public ArrayList<SubtypeTransaction> GetAllLotSubtypeTransaction(String jsonSubTrans)
    {
        subtypeTransList = new ArrayList<SubtypeTransaction>();
        try
        {
            ArrayList<SubtypeTransaction> allSubtypeTransactionFromJson = gson.fromJson(jsonSubTrans, subtypeTransArrayType);
            subtypeTransList = allSubtypeTransactionFromJson;
        }
        catch (Exception e)
        {}
        return subtypeTransList;
    }

    public ArrayList<Item> GetAllItem(String jsonItems)
    {
        itemList = new ArrayList<Item>();
        try
        {
            ArrayList<Item> allItemFromJson = gson.fromJson(jsonItems, itemArrayType);
            itemList = allItemFromJson;
        }
        catch (Exception e)
        {}
        return itemList;
    }

    public ArrayList<Lot> GetAllLot(String jsonLots)
    {
        lotList = new ArrayList<Lot>();
        try
        {
            ArrayList<Lot> allLotFromJson = gson.fromJson(jsonLots, lotArrayType);
            lotList = allLotFromJson;
        }
        catch (Exception e)
        {}
        return lotList;
    }

    public ArrayList<LotListLot> GetAllLotListLot(String jsonLots)
    {
        lotListLotList = new ArrayList<LotListLot>();
        try
        {
            ArrayList<LotListLot> allLotListLotFromJson = gson.fromJson(jsonLots, lotlistlotArrayType);
            lotListLotList = allLotListLotFromJson;
        }
        catch (Exception e)
        {}
        return lotListLotList;
    }

    public ArrayList<LotList> GetAllLotList(String jsonLots)
    {
        lotListList = new ArrayList<LotList>();
        try
        {
            ArrayList<LotList> allLotListFromJson = gson.fromJson(jsonLots, lotlistArrayType);
            lotListList = allLotListFromJson;
        }
        catch (Exception e)
        {}
        return lotListList;
    }

    public ArrayList<FieldVariable> GetAllFieldVariable(String jsonFieldVariables)
    {
        fieldVariables = new ArrayList<FieldVariable>();
        try
        {
            ArrayList<FieldVariable> allFieldVariableFromJson = gson.fromJson(jsonFieldVariables, fieldVarArrayType);
            fieldVariables = allFieldVariableFromJson;
        }
        catch (Exception e)
        {}
        return fieldVariables;
    }

    public ArrayList<LotVariable> GetAllLotVariable(String jsonLotVars)
    {
        lotVariables = new ArrayList<LotVariable>();
        try
        {
            ArrayList<LotVariable> allLotVariableFromJson = gson.fromJson(jsonLotVars, lotVarArrayType);
            lotVariables = allLotVariableFromJson;
        }
        catch (Exception e)
        {}
        return lotVariables;
    }

    public ArrayList<Variable> GetAllVariable(String jsonvars)
    {
       varList = new ArrayList<Variable>();
        try
        {
            ArrayList<Variable> allVariableFromJson = gson.fromJson(jsonvars, varArrayType);
            varList = allVariableFromJson;
        }
        catch (Exception e)
        {}
        return varList;
    }

    public ArrayList<Users> GetAllUsers(String jsonusers)
    {
        ArrayList<Users> userList = new ArrayList<Users>();
        try
        {
            ArrayList<Users> allUsersFromJson = gson.fromJson(jsonusers, userArrayType);
            userList = allUsersFromJson;
        }
        catch (Exception e)
        {}
        return userList;
    }

    public ArrayList<ItemType> GetAllItemType(String jsonIT)
    {
        itemTypeList = new ArrayList<ItemType>();
        try
        {
            ArrayList<ItemType> allItemTypeFromJson = gson.fromJson(jsonIT, itemTypeArrayType);
            itemTypeList = allItemTypeFromJson;
        }
        catch (Exception e)
        {}
        return itemTypeList;
    }

    public ArrayList<Location> GetAllLocation(String jsonlocation)
    {
        locationList = new ArrayList<Location>();
        try
        {
            ArrayList<Location> allLocationFromJson = gson.fromJson(jsonlocation, locationArrayType);
            locationList = allLocationFromJson;
        }
        catch (Exception e)
        {}
        return locationList;
    }

    public ArrayList<Location> GetAllParentChildLocations(String jsonlocation, long locationId)
    {
        ArrayList<Location> locationArray = new ArrayList<Location>();
        try
        {
            ArrayList<Location> allLocationFromJson = gson.fromJson(jsonlocation, locationArrayType);
            for (Location lo : allLocationFromJson)
            {
                if(lo.getParentId() == locationId)
                    locationArray.add(lo);
            }
        }
        catch (Exception e)
        {}
        return locationArray;
    }

    public ArrayList<Migration> GetAllMigration(String jsonMigration)
    {
        migrationList = new ArrayList<Migration>();
        try
        {
            ArrayList<Migration> allMigrationFromJson = gson.fromJson(jsonMigration, migrationArrayType);
            migrationList = allMigrationFromJson;
        }
        catch (Exception e)
        {}
        return migrationList;
    }

    public ArrayList<ContainerType> GetAllContainerType(String jsonCT)
    {
        containerTypeList = new ArrayList<ContainerType>();
        try
        {
            ArrayList<ContainerType> allContainerTypeFromJson = gson.fromJson(jsonCT, containerTypeArrayType);
            containerTypeList = allContainerTypeFromJson;
        }
        catch (Exception e)
        {}
        return containerTypeList;
    }

    public boolean isConnectedToInternet()
    {
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }

    public Location GetParentLocationDetail(ArrayList<Location> allParentLocations, ArrayList<Location> allChildLocations, long childlocationId)
    {
        Location ploc = new Location();
        Location cloc = new Location();

        //get child location detail from id
        for(Location lo : allChildLocations)
        {
            if(childlocationId == lo.getLocationId())
            {
                cloc = lo;
            }
        }

        //get parent location detail
        for(Location lo :allParentLocations)
        {
            if(cloc.getParentId() == lo.getLocationId())
            {
                ploc = lo;
            }
        }

        return ploc;
    }

    public static String getJSONdata(String urlS)
    {
        String pagina = "", devuelve = "";
        URL url;
        try
        {
            url = new URL(urlS);
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)");

            if (conexion.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                String linea = reader.readLine();
                while (linea != null)
                {
                    pagina += linea;
                    linea = reader.readLine();
                }
                reader.close();
                devuelve = pagina;
            }
            else
            {
                conexion.disconnect();
                return null;
            }
            conexion.disconnect();
            return devuelve;
        }
        catch (Exception ex)
        {
            return devuelve;
        }
    }

    public static String  postJSONdata(String targetURL, String urlParameters)
    {
        URL url;
        HttpURLConnection connection = null;
        try {
            //Create connection
            url = new URL(targetURL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");

            connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream ());
            wr.writeBytes(urlParameters);
            wr.flush ();
            wr.close ();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();

        } catch (Exception e) {

            e.printStackTrace();
            return null;

        } finally {

            if(connection != null) {
                connection.disconnect();
            }
        }
    }

    public static String ResizeString(String msg, int MaximumSize)
    {
        String message = msg;
        if (msg.length() <= MaximumSize)
        {
            return message;
        }
        else {
            message = message.substring(0, MaximumSize - 3) + "...";
            return message;
        }
    }

    public String ConvertDateFormat(String time)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");

        Date date = null;
        String str = null;

        try {
            date = sdf.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public String PadToTwoDigits(int value)
    {
        if(String.valueOf(value).length() < 2)
            return "0"+value;
        else
            return String.valueOf(value);
    }

    public String YearMonthDayDateFormat(String date)
    {
        if (date.length() > 0) {
            String day = date.substring(0, 2);
            String month = date.substring(5, 7);
            String year = date.substring(10, date.length());

            return year+"-"+month+"-"+day;
        }
        else
            return "";
    }

    public static void setListViewHeightBasedOnChildren(ListView listView)
    {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight=0;
        View view = null;

        for (int i = 0; i < listAdapter.getCount(); i++)
        {
            view = listAdapter.getView(i, view, listView);

            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.MATCH_PARENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + ((listView.getDividerHeight()) * (listAdapter.getCount()));

        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public void saveAppSettings(String server, String serverUrl, String updateData, String lastOperation, int newTableRecordID, String username)
    {
        //update settings last operation
        SettingDetail sd = new SettingDetail();
        sd.setServer(server);
        sd.setServerUrl(serverUrl);
        sd.setUpdateData(updateData);
        sd.setLastOperation(lastOperation);
        sd.setNewTableRecordID(newTableRecordID);
        sd.setUpdateUser(username);
        String jsonSettingDetail = gson.toJson(sd, settype); //convert setting details to json
        //save updated settings detail to device
        saveToExternalStorage(jsonSettingDetail, appSettingsfilename);
    }

    public List<String> getVariables()
    {
        List list = new ArrayList();
        list.add("Bulbils");
        list.add("Flower");
        list.add("Harvested");
        list.add("Sprouted");
        list.add("Weight");
        return list;
    }

    public List<String> getSubtypes(ArrayList<Subtype> subtypeList)
    {
        List list = new ArrayList();
        try
        {
            for(Subtype st : subtypeList)
                list.add(st.getName());
        }
        catch (Exception e)
        {}
        return list;
    }

    public List<String> getVariableNames(ArrayList<Variable> varList)
    {
        List list = new ArrayList();
        try
        {
            //sort alphabetically by variable name
            Collections.sort(varList, new Comparator<Variable>() {
                public int compare(Variable a, Variable b) {
                    return a.getName().compareTo(b.getName());
                }
            });

            for(Variable v : varList)
            {
                if (!v.getName().matches(""))
                    list.add(v.getName());
            }
        }
        catch (Exception e)
        {}
        return list;
    }

    public List<String> getUsernames(ArrayList<Users> userList)
    {
        List list = new ArrayList();
        try
        {
            //sort alphabetically by username
            Collections.sort(userList, new Comparator<Users>() {
                public int compare(Users a, Users b) {
                    return a.getUsername().compareTo(b.getUsername());
                }
            });

            for(Users u : userList)
            {
                if (!u.getUsername().matches(""))
                    list.add(u.getUsername());
            }
        }
        catch (Exception e)
        {}
        return list;
    }

    public List<String> getItemTypeNames(ArrayList<ItemType> itemTypeList)
    {
        List list = new ArrayList();
        try
        {
            //sort alphabetically by item type name
            Collections.sort(itemTypeList, new Comparator<ItemType>() {
                public int compare(ItemType a, ItemType b) {
                    return a.getName().compareTo(b.getName());
                }
            });

            for(ItemType it : itemTypeList)
            {
                if (!it.getName().matches(""))
                    list.add(it.getName());
            }
        }
        catch (Exception e)
        {}
        return list;
    }

    public List<String> getLocationNames(ArrayList<Location> locationList)
    {
        List list = new ArrayList();
        try
        {
            for(Location l : locationList)
                list.add(l.getName());
        }
        catch (Exception e)
        {}
        return list;
    }

    public List<String> getParentLocationNames(ArrayList<Location> locationList)
    {
        List list = new ArrayList();
        try
        {
            for(Location l : locationList)
                list.add(l.getLocationType());
        }
        catch (Exception e)
        {}
        return list;
    }

    public List<String> getContainerTypeNames(ArrayList<ContainerType> containerTypeList)
    {
        List list = new ArrayList();
        try
        {
            //sort alphabetically by container type name
            Collections.sort(containerTypeList, new Comparator<ContainerType>() {
                public int compare(ContainerType a, ContainerType b) {
                    return a.getName().compareTo(b.getName());
                }
            });

            for(ContainerType ct : containerTypeList)
            {
                if (!ct.getName().matches(""))
                    list.add(ct.getName());
            }
        }
        catch (Exception e)
        {}
        return list;
    }

    public int getNewTableRecordID()
    {
        int newID = AppSettingsDetail().getNewTableRecordID() - 1;
        //update settings last operation
        saveAppSettings(AppSettingsDetail().getServer(), AppSettingsDetail().getServerUrl(), AppSettingsDetail().getUpdateData(), AppSettingsDetail().getLastOperation(), newID, AppSettingsDetail().getUpdateUser());
        return newID;
    }

    public void saveToExternalStorage(String jsonDataToSave, String fileName) {
        try {
            // check if database storage directory exist and create one if not
            File direct = new File(Environment.getExternalStorageDirectory() + "/" + databaseFolder);
            if (!direct.exists()) {
                direct.mkdirs();
            }
            //save json data to device
            File folderFilename = new File(_context.getExternalFilesDir(databaseFolder), fileName);
            FileOutputStream fos = new FileOutputStream(folderFilename);
            fos.write(jsonDataToSave.getBytes());
            fos.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getJsonDatafromExternalStorage(String fileName)
    {
        String myData = "";
        try
        {
            // check if database storage directory exist and create one if not
            File direct = new File(Environment.getExternalStorageDirectory() + "/" + databaseFolder);
            if (!direct.exists()) {
                direct.mkdirs();
            }
            //save json data to device
            File folderFilename = new File(_context.getExternalFilesDir(databaseFolder), fileName);

            FileInputStream fis = new FileInputStream(folderFilename);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br =
                    new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null)
            {
                myData = myData + strLine;
            }
            in.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return myData;
    }

    public Users UserDeviceDetail()
    {
        Users userDetail = new Users();
        try {
            //get user detail from device
            String userDeviceDetail = getJsonDatafromExternalStorage(userJSONfilename);
            userDetail = gson.fromJson(userDeviceDetail, Users.class);
        }
        catch(Exception e) {
        }
        return userDetail;
    }

    public SettingDetail AppSettingsDetail()
    {
        SettingDetail asd = new SettingDetail();
        try {
            //get app setting detail from device
            String asDetail = getJsonDatafromExternalStorage(appSettingsfilename);
            asd = gson.fromJson(asDetail, SettingDetail.class);
        }
        catch(Exception e) {}
        return asd;
    }

    public boolean isDataToUpdateServer()
    {
        if (AppSettingsDetail().getUpdateData().matches("Server"))
            return true;
        else
            return false;
    }

    public boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public boolean SettingsExist()
    {
        //get user detail from device
        File myFileLocation = new File(_context.getExternalFilesDir(databaseFolder), appSettingsfilename);

        if (myFileLocation.exists())
        {
            String appSettingsDetail = getJsonDatafromExternalStorage(appSettingsfilename);
            SettingDetail asd = gson.fromJson(appSettingsDetail, SettingDetail.class);
            if(asd != null)
                return true;
            else
                return false;
        }
        else
            return false;
    }

    public String getDate()
    {
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        return date;
    }

    public void LogOut()
    {
        //get user detail from device
        Users userDetail = new Users();
        userDetail = UserDeviceDetail();
        int loginstatus = 0;

        //update user login status to 0
        Users user = new Users();
        user.setUsername(userDetail.getUsername());
        user.setPassword(userDetail.getPassword());
        user.setId(userDetail.getId());
        user.setLoginStatus(loginstatus);

        //convert updated user detail to json and save it to device
        String updatedUserDetailJSON = gson.toJson(user, usertype);
        saveToExternalStorage(updatedUserDetailJSON, userJSONfilename);
    }

    public boolean CheckLoginStatus()
    {
        //get user detail from device
        File myFileLocation = new File(_context.getExternalFilesDir(databaseFolder), userJSONfilename);

        if (myFileLocation.exists())
        {
            String userDeviceDetail = getJsonDatafromExternalStorage(userJSONfilename);
            Users userDetail = gson.fromJson(userDeviceDetail, Users.class);
            if(userDetail.getLoginStatus() == 1)
                return true;
            else
                return false;
        }
        else
            return false;
    }

    public String GenerateHash(String input) throws NoSuchAlgorithmException
    {
        MessageDigest objSHA = MessageDigest.getInstance("SHA-512");
        byte[] bytSHA = objSHA.digest(input.getBytes());
        BigInteger intNumber = new BigInteger(1, bytSHA);
        String strHashCode = intNumber.toString(16);

        // pad with 0 if the hexa digits are less then 128.
        while (strHashCode.length() < 128) {
            strHashCode = "0" + strHashCode;
        }
        return strHashCode;
    }
}
