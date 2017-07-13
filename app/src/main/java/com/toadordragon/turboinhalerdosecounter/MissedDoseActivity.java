package com.toadordragon.turboinhalerdosecounter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by thomas on 07-Mar-17.
 */

public class MissedDoseActivity extends AppCompatActivity {

    DoseRecorderDBHelper doseRecorderDb;
    public final static String ELAPSED_SECONDS_ID = "com.example.thomas.myapplication.ELAPSED_SECONDS_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.missed_dose);

        doseRecorderDb = DoseRecorderDBHelper.getInstance(this);
    }

    public void cancelTime(View view) {
        finish();
    }

    public void setTime(View view) {
        TimePicker timePicker = (TimePicker) findViewById(R.id.missed_dose_time_picker);
        // TODO tidy this deprecated code up - see http://stackoverflow.com/questions/33122147/
        // Making a class wrapper seems best idea

        Calendar calToday = Calendar.getInstance();
        Calendar calTodayFiveMinsAgo = Calendar.getInstance();
        calTodayFiveMinsAgo.set(Calendar.MINUTE, -5);
        Calendar calTimePicker = Calendar.getInstance();

        Date timeNow = calToday.getTime();
        Date timeFiveMinsAgo = calTodayFiveMinsAgo.getTime();

        calTimePicker.set(calToday.get(Calendar.YEAR), calToday.get(Calendar.MONTH), calToday.get(Calendar.DATE), timePicker.getCurrentHour(), timePicker.getCurrentMinute());
        Date missedDoseTime = calTimePicker.getTime();

        // Check missed dose time is earlier than current time
        if (timeNow.after(missedDoseTime)) {

            // Record the dose
            doseRecorderDb.addMissedCount(missedDoseTime, DoseDateTime.DoseTimeZone.Local);

            // If missed dose time is within five minutes we want to display dose taken activity, otherwise we just go back to main activity
            if (timeFiveMinsAgo.before(missedDoseTime)) {

                long elapsedMilliseconds = timeNow.getTime() - missedDoseTime.getTime();
                long elapsedSeconds = elapsedMilliseconds / 1000;

                Intent intent = new Intent(this, DoseTakenActivity.class);
                intent.putExtra(ELAPSED_SECONDS_ID, elapsedSeconds);
                startActivity(intent);
            }

            // Finish the activity - as we never need to go back here, we have either
            // a) recorder an older than 5 minute dose, we go back to the main screen
            // b) started the display dose taken activity, if we press back we don't want to end up here again
            finish();
        } else {
            Context context = getApplicationContext();
            Toast.makeText(getApplicationContext(), "Missed dose time should be earlier than current time", Toast.LENGTH_SHORT).show();
        }
    }
}
