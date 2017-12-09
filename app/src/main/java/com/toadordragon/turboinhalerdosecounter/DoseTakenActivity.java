package com.toadordragon.turboinhalerdosecounter;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;

public class DoseTakenActivity extends AppCompatActivity {
    public final static String ELAPSED_SECONDS_ID = "com.example.thomas.myapplication.ELAPSED_SECONDS_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);
        CalendarWrapper calWrapper = new CalendarWrapper();

        long elapsedSeconds = 0;
        Intent thisIntent = getIntent();
        elapsedSeconds = thisIntent.getLongExtra(ELAPSED_SECONDS_ID, -1);

        DoseRecorderDBHelper db = DoseRecorderDBHelper.getInstance(this);

        final TextView doseMessageTextView = (TextView) findViewById(R.id.dose_message);
        final TextView timerTextView = (TextView) findViewById(R.id.timer_message);
        final TextView timerInfoTextView = (TextView) findViewById(R.id.timer_info_message);

        doseMessageTextView.setText(String.format(getString(R.string.dose_message, db.getDosesForDayCount(calWrapper))));

        db.getCountsByDay();
        String dosesToday = db.getDoseTimesForDay(calWrapper);
        long countdownIntervalSeconds = (5 * 60) - elapsedSeconds;

        new CountDownTimer(countdownIntervalSeconds * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                long totalSeconds = millisUntilFinished / 1000;
                long minutes = totalSeconds / 60;
                long seconds = totalSeconds % 60;

                timerTextView.setText(String.format(getString(R.string.timer_message), minutes, seconds));
            }

            public void onFinish() {
                timerInfoTextView.setText(R.string.safe_dose_message);
                timerTextView.setText(String.format(getString(R.string.timer_message), 0, 0));

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
                mBuilder.setSmallIcon(R.mipmap.ic_launcher_transparent);
                mBuilder.setColor(getResources().getColor(R.color.colorDoseNotification));
                mBuilder.setContentTitle(getString(R.string.app_name));

                // Annoyingly this does not get re-centred
                mBuilder.setContentText(getString(R.string.safe_dose_message));

                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                // mId allows you to update the notification later on.
                int mId = 0;
                mNotificationManager.notify(mId, mBuilder.build());
            }
        }.start();

    }
}
