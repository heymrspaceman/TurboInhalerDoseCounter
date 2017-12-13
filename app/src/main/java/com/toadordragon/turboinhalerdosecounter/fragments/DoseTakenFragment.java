package com.toadordragon.turboinhalerdosecounter.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.toadordragon.turboinhalerdosecounter.R;
import com.toadordragon.turboinhalerdosecounter.services.DoseTakenBroadcastService;

public class DoseTakenFragment extends Fragment {
    private final static String TAG = "DoseTakenFragment";

    // Parameters
    private long elapsedSeconds;
    private int dosesToday;

    // Non-fragment stuff
    private final static String ELAPSED_SECONDS_ID = "com.example.thomas.myapplication.ELAPSED_SECONDS_ID";
    private final static String DOSES_TODAY_ID = "com.example.thomas.myapplication.DOSES_TODAY_ID";

    TextView timerTextView;
    TextView timerInfoTextView;

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

        startCountdownService();
    }

    private void startCountdownService() {
        Intent startServiceIntent = new Intent(getActivity(), DoseTakenBroadcastService.class);
        startServiceIntent.putExtra(DoseTakenBroadcastService.START_COUNTDOWN_ID, true);
        startServiceIntent.putExtra(DoseTakenBroadcastService.COUNTDOWN_ELAPSED_SECONDS_ID, elapsedSeconds);
        getActivity().startService(startServiceIntent);
        Log.i(TAG, "Sent start service intent to countdown service (should already be started) but this time with countdown start");
    }

    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null) {
                long secondsUntilFinished = intent.getLongExtra("countdown", 0);
                boolean complete = intent.getBooleanExtra("complete", false);
                updateTime(secondsUntilFinished, complete);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(br, new IntentFilter((DoseTakenBroadcastService.COUNTDOWN_BR)));
        Log.i(TAG, "Registered broadcast receiver");
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(br);
        Log.i(TAG, "Unregistered broadcast receiver");
    }

    @Override
    public void onStop() {
        try {
            getActivity().unregisterReceiver(br);
        } catch (Exception e) {
            // Receiver probably stopped in onPause
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Stopped service");
        super.onDestroy();
    }

    private void updateTime(long secondsUntilFinished, boolean complete) {

        timerTextView.setText(String.format(getString(R.string.timer_message), secondsUntilFinished / 60, secondsUntilFinished % 60));
        if (complete) {
            timerInfoTextView.setText(R.string.safe_dose_message);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View thisView = inflater.inflate(R.layout.fragment_dose_taken, container, false);

        TextView doseMessageTextView = (TextView) thisView.findViewById(R.id.dose_message);
        timerTextView = (TextView) thisView.findViewById(R.id.timer_message);
        timerInfoTextView = (TextView) thisView.findViewById(R.id.timer_info_message);

        doseMessageTextView.setText(String.format(getString(R.string.dose_message, dosesToday)));

        long countdownIntervalSeconds = (5 * 60) - elapsedSeconds;

        return thisView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
