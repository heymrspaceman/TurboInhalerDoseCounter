package com.toadordragon.turboinhalerdosecounter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.Date;
import java.util.TimeZone;

public class DoseRecorderDBHelper extends SQLiteOpenHelper {
    private static DoseRecorderDBHelper sInstance;

    public static final String DATABASE_NAME = "DoseRecorder.db";

    // This ensures only one DBHelper is created for all activities
    // just make sure to new it once in the main activity
    // then use getInstance all other times
    public static synchronized DoseRecorderDBHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DoseRecorderDBHelper(context.getApplicationContext());
        }

        return sInstance;
    }

    // Must be private see getInstance method
    private DoseRecorderDBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DoseRecorderContract.CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DoseRecorderContract.DELETE_ENTRIES);
    }

    public void addCount() {
        SQLiteDatabase db = this.getReadableDatabase();

        Calendar calToday = Calendar.getInstance();
        Date doseTime = calToday.getTime();

        String doseDatePart = new SimpleDateFormat("yyyy-MM-dd").format(doseTime);
        String doseTimePart = new SimpleDateFormat("HH:mm:ss").format(doseTime);

        db.execSQL("INSERT INTO " + DoseRecorderContract.Dose.TABLE_NAME +
                " (" + DoseRecorderContract.Dose.COLUMN_NAME_COUNT + "," + DoseRecorderContract.Dose.COLUMN_NAME_TIMESTAMP + ")" +
                " VALUES (1, '" + doseDatePart + " " + doseTimePart + "')");
    }

    public void addMissedCount(Date missedTime) {
        SQLiteDatabase db = this.getReadableDatabase();

        String startDate = new SimpleDateFormat("yyyy-MM-dd").format(missedTime);
        String doseTime = new SimpleDateFormat("HH:mm:ss").format(missedTime);

        db.execSQL("INSERT INTO " + DoseRecorderContract.Dose.TABLE_NAME +
                " (" + DoseRecorderContract.Dose.COLUMN_NAME_COUNT + "," + DoseRecorderContract.Dose.COLUMN_NAME_TIMESTAMP + ")" +
                " VALUES (1, '" + startDate + " " + doseTime + "')");
    }

    public void addMissedCount(String timestamp) {
        SQLiteDatabase db = this.getReadableDatabase();

        db.execSQL("INSERT INTO " + DoseRecorderContract.Dose.TABLE_NAME +
                " (" + DoseRecorderContract.Dose.COLUMN_NAME_COUNT + "," + DoseRecorderContract.Dose.COLUMN_NAME_TIMESTAMP + ")" +
                " VALUES (1, '" + timestamp + "')");
    }

    public int getDosesCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor dosesCount = db.rawQuery("SELECT * FROM " + DoseRecorderContract.Dose.TABLE_NAME, null);

        if (dosesCount != null) {
            if (dosesCount.moveToFirst()) {
                return dosesCount.getCount();
            }
        }

        return 0;
    }

    public int getDosesTodayCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Calendar calToday = Calendar.getInstance();
        Calendar calTomorrow = Calendar.getInstance();
        calTomorrow.add(Calendar.DATE, 1);

        String startDate = new SimpleDateFormat("yyyy-MM-dd").format(calToday.getTime());
        String endDate = new SimpleDateFormat("yyyy-MM-dd").format(calTomorrow.getTime());
        Cursor dosesCount = db.rawQuery("SELECT * FROM " + DoseRecorderContract.Dose.TABLE_NAME +
                " WHERE " + DoseRecorderContract.Dose.COLUMN_NAME_TIMESTAMP + " >= '" + startDate +
                "' AND " + DoseRecorderContract.Dose.COLUMN_NAME_TIMESTAMP + " < '" + endDate + "'", null);

        if (dosesCount != null) {
            if (dosesCount.moveToFirst()) {
                return dosesCount.getCount();
            }
        }

        return 0;
    }

    public int getDoses24HoursCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Calendar calYesterday = Calendar.getInstance();
        Calendar calTomorrow = Calendar.getInstance();
        calYesterday.add(Calendar.DATE, -1);

        String startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calYesterday.getTime());
        String endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calTomorrow.getTime());
        Cursor dosesCount = db.rawQuery("SELECT * FROM " + DoseRecorderContract.Dose.TABLE_NAME +
                " WHERE " + DoseRecorderContract.Dose.COLUMN_NAME_TIMESTAMP + " >= '" + startDate +
                "' AND " + DoseRecorderContract.Dose.COLUMN_NAME_TIMESTAMP + " < '" + endDate + "'", null);

        if (dosesCount != null) {
            if (dosesCount.moveToFirst()) {
                return dosesCount.getCount();
            }
        }

        return 0;
    }

    public String getTodayDoseTimes() {
        SQLiteDatabase db = this.getReadableDatabase();
        Calendar calToday = Calendar.getInstance();
        Calendar calTomorrow = Calendar.getInstance();
        calTomorrow.add(Calendar.DATE, 1);
        String startDate = new SimpleDateFormat("yyyy-MM-dd").format(calToday.getTime());
        String endDate = new SimpleDateFormat("yyyy-MM-dd").format(calTomorrow.getTime());
        String queryText = "SELECT strftime('%H', " + DoseRecorderContract.Dose.COLUMN_NAME_TIMESTAMP + "), " +
                "COUNT(" + DoseRecorderContract.Dose.COLUMN_NAME_TIMESTAMP + ") FROM " + DoseRecorderContract.Dose.TABLE_NAME + " " +
                "WHERE " + DoseRecorderContract.Dose.COLUMN_NAME_TIMESTAMP + " between '" + startDate + "' and '" + endDate + "' " +
                "GROUP BY strftime('%Y-%m-%d %H:00:00', " + DoseRecorderContract.Dose.COLUMN_NAME_TIMESTAMP + ")";

        String todayDoseTimes = "";
        Cursor hourDoses = db.rawQuery(queryText, null);
        if (hourDoses != null) {
            if (hourDoses.moveToFirst()) {

                int doseCount = 0;
                hourDoses.moveToFirst();
                while (!hourDoses.isAfterLast()) {

                    String hourText = hourDoses.getString(0);
                    int count = hourDoses.getInt(1);
                    int hour = Integer.parseInt(hourText);
                    // TODO BST conversion here

                    String formattedHour = "";
                    if ((hour >= 0) && (hour <= 12)) {
                        formattedHour = String.format("%dam", hour);
                    } else {
                        formattedHour = String.format("%dpm", hour - 12);
                    }

                    todayDoseTimes += String.format(" %s", formattedHour);
                    if (count > 1) {
                        todayDoseTimes += String.format("(%d)", count);
                    }
                    hourDoses.moveToNext();
                    doseCount++;
                    if (doseCount != hourDoses.getCount()) {
                        todayDoseTimes += ", ";
                    }
                }
            }
        }

        return todayDoseTimes;
    }

    public String getLastDoseTimestamp() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor lastTimestamp = db.rawQuery("SELECT * FROM " + DoseRecorderContract.Dose.TABLE_NAME + " ORDER BY " + DoseRecorderContract.Dose.COLUMN_NAME_TIMESTAMP + " DESC LIMIT 1", null);

        if (lastTimestamp != null) {
            if (lastTimestamp.moveToFirst()) {
                String lastDoseText = lastTimestamp.getString(lastTimestamp.getColumnIndex(DoseRecorderContract.Dose.COLUMN_NAME_TIMESTAMP));
                NiceDate lastDose = new NiceDate(lastDoseText);

                // TODO Do I need to close these Cursors?
                return lastDose.NiceFormat();
            }
        }

        return "";
    }

    public Cursor getCountsByDay() {
        SQLiteDatabase db = this.getReadableDatabase();
        String groupCountQuery = "SELECT _id, " +
                "strftime('%d', " + DoseRecorderContract.Dose.COLUMN_NAME_TIMESTAMP + ") AS day, " +
                "COUNT(" + DoseRecorderContract.Dose.COLUMN_NAME_TIMESTAMP + ") AS dayCount" +
                ", strftime('%m', " + DoseRecorderContract.Dose.COLUMN_NAME_TIMESTAMP + ") AS month" +
                " FROM " + DoseRecorderContract.Dose.TABLE_NAME + " " +
                "GROUP BY strftime('%d', " + DoseRecorderContract.Dose.COLUMN_NAME_TIMESTAMP + "), strftime('%m', " + DoseRecorderContract.Dose.COLUMN_NAME_TIMESTAMP + ")" +
                "ORDER BY month, day";

        Cursor countsResults = db.rawQuery(groupCountQuery, null);
        return countsResults;
    }

    public ArrayList<String> exportDosesCSV() {
        ArrayList<String> allDosesList = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        String groupCountQuery = "SELECT " + DoseRecorderContract.Dose.COLUMN_NAME_COUNT + ", " + DoseRecorderContract.Dose.COLUMN_NAME_TIMESTAMP +
                " FROM " + DoseRecorderContract.Dose.TABLE_NAME + " " +
                " ORDER BY " + DoseRecorderContract.Dose.COLUMN_NAME_TIMESTAMP;
        Cursor allDoses = db.rawQuery(groupCountQuery, null);

        if (allDoses != null) {
            if (allDoses.moveToFirst()) {
                while (!allDoses.isAfterLast()) {
                    int count = allDoses.getInt(0);
                    String timestamp = allDoses.getString(1);
                    allDosesList.add(String.format("%d,%s", count, timestamp));
                    allDoses.moveToNext();
                }
            }
        }

        return allDosesList;
    }

    public void importDosesCSV(String[] doses) {
        for (int i = 0; i < doses.length; ++i) {
            String[] doseSplit = doses[i].split(",");
            if (doseSplit.length == 2) {
                int doesValue = Integer.parseInt(doseSplit[0]);
                String doseTimestamp = doseSplit[1];
                addMissedCount(doseTimestamp);
            }
        }
    }
}
