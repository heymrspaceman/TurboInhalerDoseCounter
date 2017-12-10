package com.toadordragon.turboinhalerdosecounter.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.toadordragon.turboinhalerdosecounter.CalendarWrapper;
import com.toadordragon.turboinhalerdosecounter.R;
import com.toadordragon.turboinhalerdosecounter.database.DoseRecorderDBHelper;
import com.toadordragon.turboinhalerdosecounter.fragments.DoseTakenFragment;

public class DoseTakenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dose_taken);

        // Check whether the activity is using the layout version with
        // the fragment_container FrameLayout. If so, we must add the first fragment
        if (findViewById(R.id.fragmentDoseTaken_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            DoseRecorderDBHelper doseRecorderDb = DoseRecorderDBHelper.getInstance(this);

            // Create an instance of ExampleFragment
            int dosesToday = doseRecorderDb.getDosesForDayCount(new CalendarWrapper());
            DoseTakenFragment firstFragment = DoseTakenFragment.newInstance(0, dosesToday);

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentDoseTaken_container, firstFragment).commit();
        }
    }
}
