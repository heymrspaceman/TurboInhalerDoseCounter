package com.toadordragon.turboinhalerdosecounter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by thomas on 15-Mar-17.
 */

public class DayHistoryCursorAdapter extends CursorAdapter {

    private LayoutInflater cursorInflator;
    int itemCount;

    public DayHistoryCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        cursorInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void bindView(View view, Context context, Cursor cursor) {
        itemCount++;
        TextView dayText = (TextView) view.findViewById(R.id.history_item_day);
        TextView monthText = (TextView) view.findViewById(R.id.history_item_month);
        TextView countText = (TextView) view.findViewById(R.id.history_item_count);
        String day = cursor.getString(1);
        int dayCount = cursor.getInt(2);
        String month = cursor.getString(3);

        String dayCountText = String.format("%d", dayCount);
        dayText.setText(day);
        monthText.setText(month);
        countText.setText(dayCountText);

        //TODO add some fake jan - feb data so we can add good month diviers
        // Even counts are pink
        if ((dayCount % 2) == 0) {
            ImageView dividerImageView = (ImageView) view.findViewById(R.id.list_item_entry_drawable);
            dividerImageView.setBackgroundColor(Color.parseColor("#ff00AA"));
        }
        else {
            ImageView dividerImageView = (ImageView) view.findViewById(R.id.list_item_entry_drawable);
            dividerImageView.setBackgroundColor(Color.parseColor("#ffffff"));
        }
    }

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return cursorInflator.inflate(R.layout.history_list_item, parent, false);
    }
}
