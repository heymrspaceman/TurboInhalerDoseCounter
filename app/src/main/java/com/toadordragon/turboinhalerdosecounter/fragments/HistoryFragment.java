package com.toadordragon.turboinhalerdosecounter.fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.toadordragon.turboinhalerdosecounter.DayHistoryCursorAdapter;
import com.toadordragon.turboinhalerdosecounter.R;
import com.toadordragon.turboinhalerdosecounter.database.DoseRecorderDBHelper;

public class HistoryFragment extends ListFragment {

    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
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
        View thisView = inflater.inflate(R.layout.fragment_history, container, false);

        final ListView historyListView = (ListView) thisView.findViewById(android.R.id.list);

        DoseRecorderDBHelper doseRecorderDb = DoseRecorderDBHelper.getInstance(getActivity());

        Cursor dayHistoryCursor = doseRecorderDb.getCountsByDay();
        if (dayHistoryCursor != null) {
            if (dayHistoryCursor.moveToFirst()) {
                DayHistoryCursorAdapter myAdapter = new DayHistoryCursorAdapter(getActivity(), dayHistoryCursor, 0);

                historyListView.setAdapter(myAdapter);
            }
        }

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
