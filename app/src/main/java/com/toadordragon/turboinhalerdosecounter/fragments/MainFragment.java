package com.toadordragon.turboinhalerdosecounter.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.toadordragon.turboinhalerdosecounter.CalendarWrapper;
import com.toadordragon.turboinhalerdosecounter.R;
import com.toadordragon.turboinhalerdosecounter.database.DoseRecorderDBHelper;

public class MainFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "MainFragment";

    // Fragment stuff
    private OnMainFragmentInteractionListener mListener;

    // Non-fragment stuff
    DoseRecorderDBHelper doseRecorderDb;

    public final static int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 98;
    public final static int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 99;

    TextView doseMessageInfoTextView;
    TextView lastDoseMessageInfoTextView;
    TextView doseSummaryTextView;
    View fragmentContainerView;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
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
        View thisView = inflater.inflate(R.layout.fragment_main, container, false);
        doseMessageInfoTextView = (TextView) thisView.findViewById(R.id.dose_message_info);
        lastDoseMessageInfoTextView = (TextView) thisView.findViewById(R.id.last_dose);
        doseSummaryTextView = (TextView) thisView.findViewById(R.id.dose_summary);
        fragmentContainerView = thisView.findViewById(R.id.fragmentMain_container);

        Button btnTakeDose = (Button) thisView.findViewById(R.id.mainTakeDoseBtn);
        Button btnMissedDose = (Button) thisView.findViewById(R.id.mainMissedDoseBtn);
        Button btnMainHistory = (Button) thisView.findViewById(R.id.mainHistoryBtn);
        Button btnImport= (Button) thisView.findViewById(R.id.mainImportBtn);
        Button btnExport = (Button) thisView.findViewById(R.id.mainExportBtn);
        btnTakeDose.setOnClickListener(this);
        btnMissedDose.setOnClickListener(this);
        btnMainHistory.setOnClickListener(this);
        btnImport.setOnClickListener(this);
        btnExport.setOnClickListener(this);

        RefreshDoses();

        return thisView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Put this back in if we do callback/listener
        if (context instanceof OnMainFragmentInteractionListener) {
            mListener = (OnMainFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMainFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnMainFragmentInteractionListener {
        void onTakeDose();
        void onMissedDose();
        void onHistory();
        void onImport();
        void onExport();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mainTakeDoseBtn:
                mListener.onTakeDose();
                break;
            case R.id.mainMissedDoseBtn:
                mListener.onMissedDose();
                break;
            case R.id.mainHistoryBtn:
                mListener.onHistory();
                break;
            case R.id.mainImportBtn:
                mListener.onImport();
                break;
            case R.id.mainExportBtn:
                mListener.onExport();
                break;
        }
    }

    private void RefreshDoses() {
        doseRecorderDb = DoseRecorderDBHelper.getInstance(getActivity());
        CalendarWrapper calWrapper = new CalendarWrapper();

        int countDay = doseRecorderDb.getDosesForDayCount(calWrapper);
        int count24Hours = doseRecorderDb.getDoses24HoursCount(calWrapper);
        String doseInfoText = String.format(getString(R.string.dose_message_with_24hour, countDay, count24Hours));
        doseMessageInfoTextView.setText(doseInfoText);

        if (doseRecorderDb.getDosesCount() > 0) {
            String lastDoseText = doseRecorderDb.getLastDoseTimestampFromDay(calWrapper);
            lastDoseMessageInfoTextView.setText(String.format(getString(R.string.last_dose_message), lastDoseText));
        } else {
            String test = getString(R.string.no_previous_doses_message);
            lastDoseMessageInfoTextView.setText(getString(R.string.no_previous_doses_message));
        }

        doseSummaryTextView.setText(doseRecorderDb.getDoseTimesForDay(calWrapper));
    }
}
