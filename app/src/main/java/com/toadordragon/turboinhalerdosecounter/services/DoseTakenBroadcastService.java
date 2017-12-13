package com.toadordragon.turboinhalerdosecounter.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.toadordragon.turboinhalerdosecounter.R;

public class DoseTakenBroadcastService extends Service {
    private final static String TAG = "DoseBroadcastService";

    // Need start countdown param as service sends out broadcast intents to
    // a) update countdown in dose taken activity
    // b) send notification from main activity
    // So main activity starts the service, but not the countdown
    public final static String COUNTDOWN_ELAPSED_SECONDS_ID = "com.example.thomas.myapplication.ELAPSED_SECONDS_ID";
    public final static String START_COUNTDOWN_ID = "com.example.thomas.myapplication.START_COUNTDOWN_ID";

    public static final String COUNTDOWN_BR = "turboinhalerdosecounter.countdown_br";
    Intent broadcastIntent = new Intent(COUNTDOWN_BR);

    CountDownTimer timer = null;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        if (timer != null) {
            timer.cancel();
        }

        Log.i(TAG, "Timer (dose taken) cancelled");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int ret = super.onStartCommand(intent, flags, startId);

        // Stop timer if already started
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        long countdownIntervalSeconds = 5 * 60;
        boolean startCountdown = intent.getBooleanExtra(START_COUNTDOWN_ID, false);
        long elapsedSeconds = intent.getLongExtra(COUNTDOWN_ELAPSED_SECONDS_ID, 0);
        countdownIntervalSeconds = countdownIntervalSeconds - elapsedSeconds;

        if (startCountdown) {
            if (countdownIntervalSeconds > 0) {
                Log.i(TAG, "Starting timer (dose taken)");
                timer = new CountDownTimer(countdownIntervalSeconds * 1000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        broadcastTimerElapsed(millisUntilFinished / 1000, false);
                    }

                    public void onFinish() {
                        broadcastTimerElapsed(0, true);
                        sendDoseTakenElapsedNotification();
                        stopSelf(); // Job done
                    }
                }.start();
            } else {
                broadcastTimerElapsed(0, true);
                sendDoseTakenElapsedNotification();
                stopSelf(); // Job done
            }
        }

        return ret;
    }

    private void sendDoseTakenElapsedNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
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

    private void broadcastTimerElapsed(long seconds, boolean complete) {
        broadcastIntent.putExtra("countdown", seconds);
        broadcastIntent.putExtra("complete", complete);
        sendBroadcast(broadcastIntent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw null;
    }
}
