package com.toadordragon.turboinhalerdosecounter;

/**
 * Created by thomas on 04-Apr-17.
 */

import android.content.Context;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RemoteFetchJSON
{

    private static final String HOLIDAYS_API = "https://holidayapi.com/v1/holidays?key=a448ccfe-1241-410b-a9da-63e00ae95bfb&country=us&year=2016";
    private static final String GOOGLE_API = "http://www.google.com";

    public static JSONObject getJSON(Context context)
    {
        try
        {
            URL holidaysUrl = new URL(HOLIDAYS_API);
            HttpURLConnection httpConn = (HttpURLConnection) holidaysUrl.openConnection();

            int status = httpConn.getResponseCode();

            if (status != 401) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));

                StringBuffer json = new StringBuffer(1024);
                String tmp = "";
                while ((tmp = reader.readLine()) != null) {
                    json.append(tmp).append("\n");
                }

                reader.close();

                JSONObject data = new JSONObject(json.toString());

                return data;
            }
        }
        catch (Exception ex)
        {
            return null;
        }

        return null;
    }

}
