package com.example.caam.login;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created by CAAM on 18/04/2018.
 */

public class Authentication {
    private static final String TAG = "Authentication";
    private static final String FILENAME = "auth.xml";
    private static final String EMAIL = "email";
    private static final String ID = "id";
    private static final String CRAFTER = "crafter";
    public static final String SERVER = "https://fake-backend-mobile-app.herokuapp.com";

    private Properties data;
    private Context context;

    public Authentication(Context context){
        this.context = context;
        this.data = new Properties();
        loadData();
    }

    public boolean isLogged() {
        return data.getProperty("email") != null;
    }

    public boolean setAuthData(String jsonString){
        try{
            JSONArray array = new JSONArray(jsonString);

            if(array.length() == 0){
                return false;
            }

            JSONObject driver = array.getJSONObject(0);
            data.put(EMAIL, driver.getString(EMAIL));
            data.put(ID, driver.getInt(ID) + "");
            return saveData();
        }
        catch(JSONException je){
            je.printStackTrace();
            return false;
        }
    }

    public void removeAuthData(){
        data.remove(EMAIL);
        data.remove(CRAFTER);
        data.remove(ID);
        saveData();
    }

    public int getDriverID() {
        Log.d(TAG, data.toString());
        String driverId = data.getProperty(ID);

        if(driverId == null)
            return 0;

        return Integer.parseInt(driverId);
    }

    public int getCrafter(){
        Log.d(TAG, "Get crafter" + data.toString());
        String crafterId = data.getProperty(CRAFTER);

        if(crafterId == null)
            return 0;

        return Integer.parseInt(crafterId);
    }

    public boolean setCrafter(int crafterId){
        data.put(CRAFTER, crafterId + "");
        return saveData();
    }

    public boolean removeCrafter(){
        data.remove(CRAFTER);
        return saveData();
    }

    private boolean loadData(){
        try {
            FileInputStream fis = context.openFileInput(FILENAME);
            data.loadFromXML(fis);
            fis.close();
            return true;
        }
        catch(FileNotFoundException fnfe){
            return saveData();
        }
        catch (IOException ioe){
            ioe.printStackTrace();
            return false;
        }
    }

    private boolean saveData(){
        try {
            FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            data.storeToXML(fos, null);
            fos.close();
            return true;
        }
        catch (IOException ioe){
            ioe.printStackTrace();
            return false;
        }
    }
}
