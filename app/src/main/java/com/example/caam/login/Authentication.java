package com.example.caam.login;

import android.content.Context;

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
    private static final String FILENAME = "auth.xml";
    private static final String USERNAME = "name";
    private static final String EMAIL = "email";
    private static final String CRAFTER = "crafter";
    public static final String SERVER = "http://192.168.100.17:3000";

    private Properties data;
    private Context context;

    public Authentication(Context context){
        this.context = context;
        this.data = new Properties();
        loadData();
    }

    public boolean isLogged() {
        return data.getProperty("username") != null;
    }

    public boolean setAuthData(String jsonString){
        try{
            JSONArray array = new JSONArray(jsonString);

            if(array.length() == 0){
                return false;
            }

            JSONObject driver = array.getJSONObject(0);
            data.put(USERNAME, driver.getString(USERNAME));
            data.put(EMAIL, driver.getString(EMAIL));
            return saveData();
        }
        catch(JSONException je){
            je.printStackTrace();
            return false;
        }
    }

    public String getUsername(){
        return data.getProperty(USERNAME);
    }

    public int getCrafter(){
        return Integer.parseInt(data.getProperty(CRAFTER));
    }

    public void setCrafter(String plates){
        data.put(CRAFTER, plates);
        saveData();
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
