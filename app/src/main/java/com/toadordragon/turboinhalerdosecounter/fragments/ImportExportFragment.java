package com.toadordragon.turboinhalerdosecounter.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.toadordragon.turboinhalerdosecounter.R;


public class ImportExportFragment extends Fragment implements View.OnClickListener {
    // Fragment stuff
    private OnImportExportListener mListener;

    // Non-fragment stuff
    public static ImportExportFragment newInstance(String param1, String param2) {
        ImportExportFragment fragment = new ImportExportFragment();
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
        View thisView = inflater.inflate(R.layout.fragment_import_export, container, false);

        Button btnImport = (Button)thisView.findViewById(R.id.mainImportBtn);
        Button btnExport = (Button)thisView.findViewById(R.id.mainExportBtn);
        btnImport.setOnClickListener(this);
        btnExport.setOnClickListener(this);

        return thisView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mainImportBtn:
                mListener.onImportPressed();
                break;
            case R.id.mainExportBtn:
                mListener.onExportPressed();
                break;
        }
    }

    public interface OnImportExportListener
    {
        void onImportPressed();
        void onExportPressed();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnImportExportListener) {
            mListener = (OnImportExportListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
