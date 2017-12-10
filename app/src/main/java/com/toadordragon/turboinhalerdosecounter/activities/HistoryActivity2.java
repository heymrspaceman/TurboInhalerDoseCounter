package com.toadordragon.turboinhalerdosecounter.activities;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;

import com.toadordragon.turboinhalerdosecounter.DayHistoryCursorAdapter;
import com.toadordragon.turboinhalerdosecounter.database.DoseRecorderDBHelper;
import com.toadordragon.turboinhalerdosecounter.R;
import com.toadordragon.turboinhalerdosecounter.RemoteFetchJSON;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by thomas on 07-Mar-17.
 */

public class HistoryActivity2 extends ListActivity {

    DoseRecorderDBHelper doseRecorderDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history2);

        // Experiments
        // getHolidayDataFromWebServer_Json();
        //getRestfulData();

        doseRecorderDb = DoseRecorderDBHelper.getInstance(this);

        Cursor dayHistoryCursor = doseRecorderDb.getCountsByDay();
        if (dayHistoryCursor != null) {
            if (dayHistoryCursor.moveToFirst()) {
                DayHistoryCursorAdapter myAdapter = new DayHistoryCursorAdapter(this, dayHistoryCursor, 0);

                this.setListAdapter(myAdapter);
            }
        }
    }

    private void getHolidayDataFromWebServer_Json()  {
        new Thread() {
            public void run() {
                JSONObject holidayJson = RemoteFetchJSON.getJSON(null); // pass in null as the activity for now

                if (holidayJson != null) {
                    try {
                        JSONObject holidaysObject = holidayJson.getJSONObject("holidays");
                        Iterator iterator1 = holidaysObject.keys();
                        while(iterator1.hasNext()) {
                            String key = (String) iterator1.next();
                            JSONArray singleHolidayArray = holidaysObject.getJSONArray(key);
                            for (int i =0; i < singleHolidayArray.length(); i++) {
                                JSONObject singleHolidayJson = singleHolidayArray.getJSONObject(i);
                                String holidayName = singleHolidayJson.getString("name");
                                String holidayDate = singleHolidayJson.getString("date");
                            }
                        }
                    } catch (Exception ex) {

                    }
                }
            }
        }.start();
    }

    //TODO if we ever put a onRestartr in here then we call changeCursor

}
