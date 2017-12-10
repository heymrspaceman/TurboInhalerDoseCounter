package com.toadordragon.turboinhalerdosecounter.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.toadordragon.turboinhalerdosecounter.database.DoseRecorderDBHelper;
import com.toadordragon.turboinhalerdosecounter.R;
import com.toadordragon.turboinhalerdosecounter.fragments.HistoryFragment;

/**
 * Created by thomas on 07-Mar-17.
 */

public class HistoryActivity extends AppCompatActivity {

    DoseRecorderDBHelper doseRecorderDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Check whether the activity is using the layout version with
        // the fragment_container FrameLayout. If so, we must add the first fragment
        if (findViewById(R.id.fragmentHistory_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create an instance of ExampleFragment
            HistoryFragment firstFragment = new HistoryFragment();

            // In case this activity was started with special instructions from an Intent,
            // pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentHistory_container, firstFragment).commit();
        }
    }

    //TODO if we ever put a onRestartr in here then we call changeCursor

}
