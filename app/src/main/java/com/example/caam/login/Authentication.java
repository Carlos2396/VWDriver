package com.example.caam.login;

import android.content.Context;

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
    private Properties data;

    public Authentication(){
        data = new Properties();
        File propertiesFile = new File(FILENAME);
        loadData();
    }

    public boolean isLogged() {
        return data.getProperty("username") != null;
    }

    public String getUsername(){
        return data.getProperty("username");
    }

    private void loadData(){
        try {
            FileInputStream fis = new FileInputStream(FILENAME);
            data.loadFromXML(fis);
            fis.close();
        }
        catch (FileNotFoundException fnfe){
            saveData();
        }
        catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    private void saveData(){
        try {
            FileOutputStream fos = new FileOutputStream(FILENAME);
            data.storeToXML(fos, null);
            fos.close();
        }
        catch (IOException ioe){
            ioe.printStackTrace();
        }
    }
}
