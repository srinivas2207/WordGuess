package com.shree.wordguess.network;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shree.wordguess.util.ApplicationConstants;
import com.shree.wordguess.util.JsonConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by SrinivasDonapati on 12/26/2017.
 */

public class RestCallResponse {
    private String method;
    private String url;
    private int status;
    private String response;

    /**
     * Returns HTTP status of the response
     * @return
     */
    public int getStatus()
    {
        return status;
    }
    public void setStatus(int status)
    {
        this.status = status;
    }

    /**
     * Returns the reponse of the request
     * @return
     */
    public String getResponse()
    {
        return response;
    }
    public void setResponse(String response)
    {
        this.response = response;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
       String data = null;
       try {
           JSONObject obj = new JSONObject();
           obj.put(JsonConstants.STATUS, status);
           obj.put(JsonConstants.RESPONSE , response);
           obj.put(JsonConstants.METHOD, method);
           obj.put(JsonConstants.URL , url);
           data = obj.toString();
       } catch (JSONException je){
           je.printStackTrace();
       }
       return data;
    }

    public static RestCallResponse convertJson(String data) {
        try {
            Gson gson = new Gson();
            RestCallResponse restCallResponse = gson.fromJson(data, RestCallResponse.class);
            return restCallResponse;
        } catch (Exception e) {
            e.printStackTrace();
        }
       return null;
    }
}
