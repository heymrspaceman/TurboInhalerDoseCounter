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
        addCount(DoseDateTime.Now());
    }

    public void addMissedCount(Date missedDoseTime, TimeZone timeZone) {
        addCount(new DoseDateTime(missedDoseTime, timeZone));
    }

    public void addMissedCount(String missedDoseTimeText, TimeZone timeZone) {
        addCount(new DoseDateTime(missedDoseTimeText, timeZone));
    }

    public void addCount(DoseDateTime doseTime) {
        SQLiteDatabase db = this.getReadableDatabase();

        db.execSQL("INSERT INTO " + DoseRecorderContract.Dose.TABLE_NAME +
                " (" + DoseRecorderContract.Dose.COLUMN_NAME_COUNT + "," + DoseRecorderContract.Dose.COLUMN_NAME_TIMESTAMP + ")" +
                " VALUES (1, '" + doseTime.GetDateTimeText(TimeZone.getTimeZone("UTC")) + "')");
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

    // TODO move this out of database class
    public int getDosesTodayCount() {
        Calendar todayMidnightCal = Calendar.getInstance();
        todayMidnightCal.set(Calendar.HOUR_OF_DAY, 0);
        todayMidnightCal.set(Calendar.MINUTE, 0);
        todayMidnightCal.set(Calendar.SECOND, 0);
        todayMidnightCal.set(Calendar.MILLISECOND, 0);

        Calendar tomorrowMidnightCal = (Calendar)todayMidnightCal.clone();
        tomorrowMidnightCal.add(Calendar.DATE, 1);

        return getDosesInRange(new DoseDateTime(todayMidnightCal), new DoseDateTime(tomorrowMidnightCal), todayMidnightCal.getTimeZone());
    }

    // TODO move this out of database class
    public int getDoses24HoursCount() {
        Calendar nowCal = Calendar.getInstance();

        Calendar dayAgoCal = (Calendar)nowCal.clone();
        dayAgoCal.add(Calendar.DATE, -1);

        return getDosesInRange(new DoseDateTime(dayAgoCal), new DoseDateTime(nowCal), nowCal.getTimeZone());
    }

    private int getDosesInRange(DoseDateTime start, DoseDateTime end, TimeZone zone) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor dosesCount = db.rawQuery("SELECT * FROM " + DoseRecorderContract.Dose.TABLE_NAME +
                " WHERE " + DoseRecorderContract.Dose.COLUMN_NAME_TIMESTAMP + " >= '" + start.GetDateTimeText(zone) +
                "' AND " + DoseRecorderContract.Dose.COLUMN_NAME_TIMESTAMP + " < '" + end.GetDateTimeText(zone) + "'", null);

        if (dosesCount != null) {
            if (dosesCount.moveToFirst()) {
                return dosesCount.getCount();
            }
        }

        return 0;
    }

    public String getTodayDoseTimes(Calendar cal) {
        SQLiteDatabase db = this.getReadableDatabase();

        Calendar calToday = Calendar.getInstance();

        Calendar calTomorrow = (Calendar)calToday.clone();
        calTomorrow.add(Calendar.DATE, 1);

        DoseDateTime startDate = new DoseDateTime(calToday);
        DoseDateTime endDate = new DoseDateTime(calTomorrow);
        String startDateText = startDate.GetDateTimeText(calToday.getTimeZone());
        String endDateText = startDate.GetDateTimeText(calToday.getTimeZone());

        String queryText = "SELECT strftime('%H', " + DoseRecorderContract.Dose.COLUMN_NAME_TIMESTAMP + "), " +
                "COUNT(" + DoseRecorderContract.Dose.COLUMN_NAME_TIMESTAMP + ") FROM " + DoseRecorderContract.Dose.TABLE_NAME + " " +
                "WHERE " + DoseRecorderContract.Dose.COLUMN_NAME_TIMESTAMP + " between '" + startDateText + "' and '" + endDateText + "' " +
                "GROUP BY strftime('%Y-%m-%d %H:00:00', " + DoseRecorderContract.Dose.COLUMN_NAME_TIMESTAMP + ")";

        String todayDoseTimes = "";
        Cursor hourDoses = db.rawQuery(queryText, null);
        if (hourDoses != null) {
            if (hourDoses.moveToFirst()) {

                int doseCount = 0;
                hourDoses.moveToFirst();
                while (!hourDoses.isAfterLast()) {
                    // Put the hour back into todays date
                    String dateTimeText = startDate + " " + hourDoses.getString(0) + ":00:00";
                    DoseDateTime doseDateTime = new DoseDateTime(dateTimeText, TimeZone.getTimeZone("UTC"));
                    int count = hourDoses.getInt(1);

                    todayDoseTimes += String.format(" %s", doseDateTime.GetHourText(cal.getTimeZone()));
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

    public String getLastDoseTimestamp(Calendar cal) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor lastTimestamp = db.rawQuery("SELECT * FROM " + DoseRecorderContract.Dose.TABLE_NAME + " ORDER BY " + DoseRecorderContract.Dose.COLUMN_NAME_TIMESTAMP + " DESC LIMIT 1", null);

        if (lastTimestamp != null) {
            if (lastTimestamp.moveToFirst()) {
                String lastDoseText = lastTimestamp.getString(lastTimestamp.getColumnIndex(DoseRecorderContract.Dose.COLUMN_NAME_TIMESTAMP));
                DoseDateTime lastDoseDateTime = new DoseDateTime(lastDoseText, TimeZone.getTimeZone("UTC"));
                // TODO Do I need to close these Cursors?
                return lastDoseDateTime.GetHourMinuteText(cal.getTimeZone());
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
                int doesValue = Integer.parseInt(doseSplit[0]); // always 1 currently
                String doseTimestamp = doseSplit[1];
                addMissedCount(doseTimestamp, TimeZone.getTimeZone("UTC"));
            }
        }
    }
}
