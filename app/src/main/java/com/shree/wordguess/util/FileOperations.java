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
    private static final String wordDataFile = "words.json";

    private static FileOperations fileOperations;

    public static FileOperations getInstance() {
        if (fileOperations == null) {
            fileOperations = new FileOperations();
        }
        return fileOperations;
    }

    /**
     * Parsing app's resource files (meta data and word data)
     */
    public void parseResourceFiles() {
        parseAppData();
        parseWordFile();
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

    private void parseWordFile() {
        try {
            List<WordData> wordData = null;
            InputStream is =  WordGuessApplication.getInstance().getAssets().open(wordDataFile);
            if(is != null) {
                Reader reader = new InputStreamReader(is, "UTF-8");
                wordData = new Gson().fromJson(
                        reader, new TypeToken<List<WordData>>() {}.getType()
                );
            }

            if (wordData != null) {
                DatabaseUtil.getInstance().insertWordsFromAssets(wordData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
