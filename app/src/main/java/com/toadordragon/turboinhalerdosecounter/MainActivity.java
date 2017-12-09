package com.toadordragon.turboinhalerdosecounter;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity {
    public final static String ELAPSED_SECONDS_ID = "com.example.thomas.myapplication.ELAPSED_SECONDS_ID";
    public final static int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 98;
    public final static int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 99;
    DoseRecorderDBHelper doseRecorderDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Uri data = getIntent().getData();
        if (data != null) {
            getIntent().setData(null);
            importIntentData(data);
        }
        RefreshDoses();
    }

    @Override
    public void onResume() {  // After a pause OR at startup
        super.onResume();
        RefreshDoses();
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

            } catch (IOException iox) {

            }
        }
    }

    private void RefreshDoses() {
        doseRecorderDb = DoseRecorderDBHelper.getInstance(this);
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

    public void takeDose(View view) {
        CalendarWrapper calWrapper =new CalendarWrapper();
        doseRecorderDb.addCount(new DoseDateTime(calWrapper));

        Intent intent = new Intent(this, DoseTakenActivity.class);
        intent.putExtra(ELAPSED_SECONDS_ID, 0);
        startActivity(intent);
    }


    //TODO add info or help pages using https://developer.android.com/training/animation/screen-slide.html

    public void missedDose(View view) {
        Intent intent = new Intent(this, MissedDoseActivity.class);
        startActivity(intent);
    }

    public void showHistory(View view) {
        //Intent intent = new Intent(this, HistoryActivity.class);
        Intent intent = new Intent(this, HistoryActivity2.class);
        startActivity(intent);
    }

    public void exportDataRequest(View view) {
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

            // This makes it appear in downloads
            DownloadManager downloadManager = (DownloadManager)this.getSystemService(this.DOWNLOAD_SERVICE);
            downloadManager.addCompletedDownload(f.getName(), f.getName(), true, "application/tsdcsv", f.getAbsolutePath(),f.length(),true);

            Context context = getApplicationContext();
            Toast.makeText(getApplicationContext(), "Data exported", Toast.LENGTH_SHORT).show();
        }
        catch(IOException ex){
        }
    }

    public void importData(View view) {
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
