package com.toadordragon.turboinhalerdosecounter.activities;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.toadordragon.turboinhalerdosecounter.CalendarWrapper;
import com.toadordragon.turboinhalerdosecounter.DoseDateTime;
import com.toadordragon.turboinhalerdosecounter.database.DoseRecorderDBHelper;
import com.toadordragon.turboinhalerdosecounter.R;
import android.support.v4.app.Fragment;
import com.toadordragon.turboinhalerdosecounter.fragments.DoseTakenFragment;
import com.toadordragon.turboinhalerdosecounter.fragments.HistoryFragment;
import com.toadordragon.turboinhalerdosecounter.fragments.ImportExportFragment;
import com.toadordragon.turboinhalerdosecounter.fragments.MainFragment;
import com.toadordragon.turboinhalerdosecounter.fragments.MissedDoseFragment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class MainActivity extends AppCompatActivity implements MainFragment.OnMainFragmentInteractionListener,
        MissedDoseFragment.OnMissedDoseListener, ImportExportFragment.OnImportExportListener {
    private static final String TAG = "MainActivity";
    public final static String ELAPSED_SECONDS_ID = "com.example.thomas.myapplication.ELAPSED_SECONDS_ID";
    public final static int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 98;
    public final static int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 99;
    DoseRecorderDBHelper doseRecorderDb;
    private String[] menuTitles;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private CharSequence drawerTitle;
    private CharSequence mainTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        menuTitles = getResources().getStringArray(R.array.menus_array);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);

        mainTitle = drawerTitle = getTitle();

        drawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, menuTitles));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mainTitle);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View view) {
                super.onDrawerOpened(view);
                getSupportActionBar().setTitle(drawerTitle);
                invalidateOptionsMenu();
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }

        Uri data = getIntent().getData();
        if (data != null) {
            getIntent().setData(null);
            importIntentData(data);
        }

        doseRecorderDb = DoseRecorderDBHelper.getInstance(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
        menu.findItem(R.id.menu_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }


int id = item.getItemId();

        // Handle action buttons
        switch(item.getItemId()) {
            case R.id.menu_settings:
                Log.i(TAG, "Open settings if there are any");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        Fragment newFragment = null;

        // This will be a select
        if (position == 1) {
            newFragment = new HistoryFragment();
        } else if (position == 2) {
            newFragment = new ImportExportFragment();
        } else {
            newFragment = new MainFragment();
        }

        if (newFragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, newFragment).commit();

            drawerList.setItemChecked(position, true);
            setTitle(menuTitles[position]);
            drawerLayout.closeDrawer(drawerList);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mainTitle = title;
        getSupportActionBar().setTitle(mainTitle);
    }

    @Override
    public void onResume() {  // After a pause OR at startup
        super.onResume();
        doseRecorderDb = DoseRecorderDBHelper.getInstance(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    exportDataPermissionGranted();
                }
        }
    }

    @Override
    public void onTakeDose() {
        takeDose();
    }

    @Override
    public void onMissedDose() {
        missedDose();
    }

    @Override
    public void onMissedDoseCancelled() {
        getSupportFragmentManager().popBackStack();
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
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, newFragment).commit();
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

    @Override
    public void onImportPressed()
    {
        importData();
    }

    @Override
    public void onExportPressed()
    {
        exportDataRequest();
    }

    private void importIntentData(Uri data) {
        // Produce summary of the data -
        //132 doses from 4/2/2017 - 3/5/2017 (140 doses) Do you wish to import?
////vhttps://richardleggett.com/blog/2013/01/26/registering_for_file_types_in_android/
        final String scheme = data.getScheme();

        if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            try {
                Context context = getApplicationContext();
                ContentResolver cr = context.getContentResolver();
                InputStream is = cr.openInputStream(data);
                if (is == null) return;

                StringBuffer buf = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String str;
                if (is != null) {
                    while ((str = reader.readLine()) != null) {
                        buf.append(str + "\n");
                    }
                }
                is.close();

                if (buf.length() > 0) {
                    final String[] doses = buf.toString().split(File.separator);

                    if (doses.length == 0) {
                        Toast.makeText(getApplicationContext(), R.string.no_import_data, Toast.LENGTH_SHORT).show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage(String.format(getString(R.string.import_data_message), doses.length));
                        builder.setPositiveButton(R.string.yes_dialog_button, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                doseRecorderDb.importDosesCSV(doses);
                            }
                        });
                        builder.setNegativeButton((R.string.no_dialog_button), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(getApplicationContext(), R.string.import_cancelled, Toast.LENGTH_SHORT).show();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            } catch (FileNotFoundException fx) {
                Log.e(TAG, "Imported file not found: " + fx.getMessage());
            } catch (IOException iox) {
                Log.e(TAG, "Error during importing: " + iox.getMessage());
            }
        }
    }

    private void RefreshDoses() {
        CalendarWrapper calWrapper = new CalendarWrapper();

        final TextView doseMessageInfoTextView = (TextView) findViewById(R.id.dose_message_info);
        int countDay = doseRecorderDb.getDosesForDayCount(calWrapper);
        int count24Hours = doseRecorderDb.getDoses24HoursCount(calWrapper);
        String doseInfoText = String.format(getString(R.string.dose_message_with_24hour, countDay, count24Hours));
        doseMessageInfoTextView.setText(doseInfoText);

        final TextView lastDoseMessageInfoTextView = (TextView) findViewById(R.id.last_dose);
        if (doseRecorderDb.getDosesCount() > 0) {
            String lastDoseText = doseRecorderDb.getLastDoseTimestampFromDay(calWrapper);
            lastDoseMessageInfoTextView.setText(String.format(getString(R.string.last_dose_message), lastDoseText));
        } else {
            String test = getString(R.string.no_previous_doses_message);
            lastDoseMessageInfoTextView.setText(getString(R.string.no_previous_doses_message));
        }

        final TextView doseSummaryTextView = (TextView) findViewById(R.id.dose_summary);
        doseSummaryTextView.setText(doseRecorderDb.getDoseTimesForDay(calWrapper));
    }

    public void takeDose() {
        CalendarWrapper calWrapper = new CalendarWrapper();
        doseRecorderDb.addCount(new DoseDateTime(calWrapper));
        int dosesToday = doseRecorderDb.getDosesForDayCount(new CalendarWrapper());
        DoseTakenFragment newFragment = DoseTakenFragment.newInstance(0, dosesToday);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, newFragment).commit();
    }

    //TODO add info or help pages using https://developer.android.com/training/animation/screen-slide.html

    public void missedDose() {
        MissedDoseFragment newFragment = MissedDoseFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, newFragment).commit();
    }

    public void exportDataRequest() {
        // Check we have permissions
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {
                // No explanation required, request it
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        } else if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            exportDataPermissionGranted();
        }
    }

    private void exportDataPermissionGranted() {
        try {
            String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
            String downloadsDir = baseDir + File.separator + DIRECTORY_DOWNLOADS;
            Calendar calToday = Calendar.getInstance();
            Date dateToday = calToday.getTime();
            // Ditch the timestamp - only one version of the file we store
            //String todayTimestamp = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(dateToday);
            //String fileName = "TurboInhaler CSV " + todayTimestamp + ".txt";
            String fileName = "TurboInhaler CSV2.tdccsv";
            String filePath = downloadsDir + File.separator + fileName;
            File f = new File(filePath);

            if (f.exists()) {
                f.delete();
            }

            if (!f.exists()) {
                f.createNewFile();
            }

            ArrayList<String> allDosesCSV = doseRecorderDb.exportDosesCSV();

            FileWriter mFileWriter;
            if (f.exists()) {
                mFileWriter = new FileWriter(filePath, true);
                for (int i = 0; i < allDosesCSV.size(); ++i) {
                    mFileWriter.write(allDosesCSV.get(i) + File.separator);
                }
                mFileWriter.close();
            }

            try {
                // This makes it appear in downloads
                DownloadManager downloadManager = (DownloadManager) this.getSystemService(this.DOWNLOAD_SERVICE);
                downloadManager.addCompletedDownload(f.getName(), f.getName(), true, "application/tsdcsv", f.getAbsolutePath(), f.length(), true);
            } catch (Exception ex) {
                // Not a big deal if we can't get a download notification to appear
                Log.e(TAG, "Download notification failed: " + ex.getMessage());
            }

            Context context = getApplicationContext();
            Toast.makeText(getApplicationContext(), "Data exported", Toast.LENGTH_SHORT).show();
        }
        catch(IOException ex){
            Log.e(TAG, "Problem exporting: " + ex.getMessage());
        }
    }

    public void importData() {
        // Check we have permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {
                // No explanation required, request it
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        }

        // Dsplay message saygin select data to import (will have .tdccsv extension)
        // Just open downloads folder, we should have a intent set up
        startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
    }
}
