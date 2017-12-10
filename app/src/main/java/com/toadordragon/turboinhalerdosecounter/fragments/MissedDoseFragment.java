package com.toadordragon.turboinhalerdosecounter.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

import com.toadordragon.turboinhalerdosecounter.R;

import java.util.Calendar;

public class MissedDoseFragment extends Fragment implements View.OnClickListener {
    // Fragment stuff
    private OnMissedDoseListener mListener;

    // Non-fragment stuff
    private TimePicker timePicker;

    public MissedDoseFragment() {
        // Required empty public constructor
    }

    // Use this factory method to create a new instance of this fragment using the provided parameters.
    public static MissedDoseFragment newInstance() {
        MissedDoseFragment fragment = new MissedDoseFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View thisView = inflater.inflate(R.layout.fragment_missed_dose, container, false);

        Button btnSetTime = (Button) thisView.findViewById(R.id.setTimeBtn);
        Button btnCancelTime = (Button) thisView.findViewById(R.id.cancelTimeBtn);
        btnSetTime.setOnClickListener(this);
        btnCancelTime.setOnClickListener(this);

        timePicker = (TimePicker)thisView.findViewById(R.id.missed_dose_time_picker);
        return thisView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMissedDoseListener) {
            mListener = (OnMissedDoseListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMissedDoseListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnMissedDoseListener {
        void onMissedDoseStarted(Calendar calTimePicker);
        void onMissedDoseCancelled();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setTimeBtn:
                setTime(v);
                break;
            case R.id.cancelTimeBtn:
                cancelTime(v);
                break;
        }
    }

    public void cancelTime(View view) {
        mListener.onMissedDoseCancelled();
    }

    public void setTime(View view) {
        Calendar calToday = Calendar.getInstance();
        Calendar calTimePicker = Calendar.getInstance();

        // This may used deprecated methods - http://stackoverflow.com/questions/33122147
        calTimePicker.set(calToday.get(Calendar.YEAR), calToday.get(Calendar.MONTH), calToday.get(Calendar.DATE), timePicker.getCurrentHour(), timePicker.getCurrentMinute());

        mListener.onMissedDoseStarted(calTimePicker);
    }
}
