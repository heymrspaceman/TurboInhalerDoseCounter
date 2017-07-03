package com.toadordragon.turboinhalerdosecounter;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by thomas on 07-Mar-17.
 */

public class HistoryActivity extends ListActivity {

    DoseRecorderDBHelper doseRecorderDb;
    SimpleCursorAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);

        doseRecorderDb = DoseRecorderDBHelper.getInstance(this);

        displayHistory();
    }

    public void displayHistory() {
        Cursor historyCursor = doseRecorderDb.getCountsByDay();
        if (historyCursor != null) {
            if (historyCursor.moveToFirst()) {
                String fromCols[] = new String[]{"day", "month", "dayCount"};
                int toViews[] = new int[]{R.id.history_item_day, R.id.history_item_month, R.id.history_item_count};
                dataAdapter = new SimpleCursorAdapter(this, R.layout.history_list_item, historyCursor, fromCols, toViews, 0);

                ListView historyListView = getListView();
                historyListView.setAdapter(dataAdapter);
            }
        }

        //TODO close this cursor somehow or is it closed as part of SimpleCursorAdapter?
    }
}
