package com.toadordragon.turboinhalerdosecounter.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.toadordragon.turboinhalerdosecounter.CalendarWrapper;
import com.toadordragon.turboinhalerdosecounter.DoseDateTime;
import com.toadordragon.turboinhalerdosecounter.database.DoseRecorderDBHelper;
import com.toadordragon.turboinhalerdosecounter.R;
import com.toadordragon.turboinhalerdosecounter.fragments.DoseTakenFragment;
import com.toadordragon.turboinhalerdosecounter.fragments.MissedDoseFragment;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by thomas on 07-Mar-17.
 */

public class MissedDoseActivity extends AppCompatActivity implements MissedDoseFragment.OnMissedDoseListener {
    DoseRecorderDBHelper doseRecorderDb;
    MissedDoseFragment firstFragment;

    @Override
    public void onMissedDoseCancelled() {
        finish();
    }

    @Override
    public void onMissedDoseStarted(Calendar calTimePicker) {
        Calendar calToday = Calendar.getInstance();
        Calendar calTodayFiveMinsAgo = Calendar.getInstance();
        calTodayFiveMinsAgo.set(Calendar.MINUTE, -5);

        Date timeNow = calToday.getTime();
        Date timeFiveMinsAgo = calTodayFiveMinsAgo.getTime();
        Date missedDoseTime = calTimePicker.getTime();

        // Check missed dose time is earlier than current time
        if (timeNow.after(missedDoseTime)) {

            // Record the dose
            doseRecorderDb.addCount(new DoseDateTime(new CalendarWrapper(calTimePicker)));

            // If missed dose time is within five minutes we want to display dose taken activity, otherwise we just go back to main activity
            if (timeFiveMinsAgo.before(missedDoseTime)) {

                long elapsedMilliseconds = timeNow.getTime() - missedDoseTime.getTime();
                long elapsedSeconds = elapsedMilliseconds / 1000;
                int dosesToday = doseRecorderDb.getDosesForDayCount(new CalendarWrapper());

                DoseTakenFragment newFragment = DoseTakenFragment.newInstance(elapsedSeconds, dosesToday);

                android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack so the user can navigate back
                // TODO Hitting back during the countdown timer sends us back to the MissedDose fragment - don't know how to get around this
                transaction.replace(R.id.fragmentMissedDose_container, newFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            } else {
                Context context = getApplicationContext();
                Toast.makeText(getApplicationContext(), "Missed dose time recorded", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Context context = getApplicationContext();
            Toast.makeText(getApplicationContext(), "Missed dose time should be earlier than current time", Toast.LENGTH_SHORT).show();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_missed_dose);

        doseRecorderDb = DoseRecorderDBHelper.getInstance(this);

        // Check whether the activity is using the layout version with
        // the fragment_container FrameLayout. If so, we must add the first fragment
        if (findViewById(R.id.fragmentMissedDose_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create an instance of ExampleFragment
            firstFragment = new MissedDoseFragment();

            // In case this activity was started with special instructions from an Intent,
            // pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentMissedDose_container, firstFragment).commit();
        }
    }
}
