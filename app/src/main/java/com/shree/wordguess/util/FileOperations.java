package com.shree.wordguess.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shree.wordguess.WordGuessApplication;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SrinivasDonapati on 12/12/2017.
 */

public class FileOperations {

    private static final String appDataFile = "appData.json";

    private static FileOperations fileOperations;

    public static FileOperations getInstance() {
        if (fileOperations == null) {
            fileOperations = new FileOperations();
        }
        return fileOperations;
    }

    public void parseResourceFiles() {
        parseAppData();
        parseWordFiles();
        DatabaseUtil.getInstance().setAppDataParsed(true);
        DatabaseUtil.getInstance().setLastUpdatedTime();
    }

    private void parseAppData() {
        try {
            String metaJson = null;
            InputStream is =  WordGuessApplication.getInstance().getAssets().open(appDataFile);
            if(is != null) {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader bufferedReader = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                metaJson =  sb.toString();
                is.close();
            }

            if (metaJson != null) {
                JSONObject metaData = new JSONObject(metaJson);
                DatabaseUtil.getInstance().updateAppData(metaData);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseWordFiles() {
        try {
            AppData appData = DatabaseUtil.getInstance().getAppData();
            List<AppData.WordFile> files = appData.getFiles();

            for(AppData.WordFile file : files) {
                List<WordData> wordData = null;
                InputStream is =  WordGuessApplication.getInstance().getAssets().open(file.getName());
                if(is != null) {
                    Reader reader = new InputStreamReader(is, "UTF-8");
                    wordData = new Gson().fromJson(
                            reader, new TypeToken<List<WordData>>() {}.getType()
                    );
                }

                if (wordData != null) {
                    DatabaseUtil.getInstance().updateWordData(wordData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
