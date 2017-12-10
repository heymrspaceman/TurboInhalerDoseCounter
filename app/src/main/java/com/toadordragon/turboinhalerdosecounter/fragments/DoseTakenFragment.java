package com.toadordragon.turboinhalerdosecounter.fragments;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v7.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.toadordragon.turboinhalerdosecounter.R;

public class DoseTakenFragment extends Fragment {

    // Parameters
    private long elapsedSeconds;
    private int dosesToday;

    // Non-fragment stuff
    private final static String ELAPSED_SECONDS_ID = "com.example.thomas.myapplication.ELAPSED_SECONDS_ID";
    private final static String DOSES_TODAY_ID = "com.example.thomas.myapplication.DOSES_TODAY_ID";
    CountDownTimer secondsTimer;

    public DoseTakenFragment() {
        // Required empty public constructor
    }

    public static DoseTakenFragment newInstance(long elapsedSeconds, int dosesToday) {
        DoseTakenFragment fragment = new DoseTakenFragment();
        Bundle args = new Bundle();
        args.putLong(ELAPSED_SECONDS_ID, elapsedSeconds);
        args.putInt(DOSES_TODAY_ID, dosesToday);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            elapsedSeconds = getArguments().getLong(ELAPSED_SECONDS_ID);
            dosesToday = getArguments().getInt(DOSES_TODAY_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View thisView = inflater.inflate(R.layout.fragment_dose_taken, container, false);

        final TextView doseMessageTextView = (TextView) thisView.findViewById(R.id.dose_message);
        final TextView timerTextView = (TextView) thisView.findViewById(R.id.timer_message);
        final TextView timerInfoTextView = (TextView) thisView.findViewById(R.id.timer_info_message);

        doseMessageTextView.setText(String.format(getString(R.string.dose_message, dosesToday)));

        long countdownIntervalSeconds = (5 * 60) - elapsedSeconds;

        secondsTimer = new CountDownTimer(countdownIntervalSeconds * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                long totalSeconds = millisUntilFinished / 1000;
                long minutes = totalSeconds / 60;
                long seconds = totalSeconds % 60;

                timerTextView.setText(String.format(getString(R.string.timer_message), minutes, seconds));
            }

            public void onFinish() {
                timerInfoTextView.setText(R.string.safe_dose_message);
                timerTextView.setText(String.format(getString(R.string.timer_message), 0, 0));

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity());
                mBuilder.setSmallIcon(R.mipmap.ic_launcher_transparent);
                mBuilder.setColor(getResources().getColor(R.color.colorDoseNotification));
                mBuilder.setContentTitle(getString(R.string.app_name));

                // Annoyingly this does not get re-centred
                mBuilder.setContentText(getString(R.string.safe_dose_message));

                NotificationManager mNotificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

                // mId allows you to update the notification later on.
                int mId = 0;
                mNotificationManager.notify(mId, mBuilder.build());
            }
        }.start();

        return thisView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        secondsTimer.cancel();
    }
}
