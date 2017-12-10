package com.toadordragon.turboinhalerdosecounter.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.toadordragon.turboinhalerdosecounter.CalendarWrapper;
import com.toadordragon.turboinhalerdosecounter.DoseDateTime;

import java.util.ArrayList;
import java.util.Calendar;

import java.util.List;
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

    public void clearDatabaseAndRecreate() {
        clearDatabase();
    }

    public void clearDatabase() {
        SQLiteDatabase db = this.getReadableDatabase();

        db.execSQL("DELETE FROM " + DoseRecorderContract.Dose.TABLE_NAME);
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
    public int getDosesForDayCount(CalendarWrapper calWrapper) {
        CalendarWrapper todayMidnightCal = CalendarWrapper.Copy(calWrapper);
        todayMidnightCal.set(Calendar.HOUR_OF_DAY, 0);
        todayMidnightCal.set(Calendar.MINUTE, 0);
        todayMidnightCal.set(Calendar.SECOND, 0);
        todayMidnightCal.set(Calendar.MILLISECOND, 0);

        CalendarWrapper tomorrowMidnightCal = CalendarWrapper.Copy(todayMidnightCal);
        tomorrowMidnightCal.add(Calendar.DATE, 1);

        return getDosesInRange(new DoseDateTime(todayMidnightCal), new DoseDateTime(tomorrowMidnightCal));
    }

    // TODO move this out of database class
    public int getDoses24HoursCount(CalendarWrapper calWrapper) {
        CalendarWrapper nowCal = CalendarWrapper.Copy(calWrapper);

        CalendarWrapper dayAgoCal = CalendarWrapper.Copy(nowCal);
        dayAgoCal.add(Calendar.DATE, -1);

        return getDosesInRange(new DoseDateTime(dayAgoCal), new DoseDateTime(nowCal));
    }

    private int getDosesInRange(DoseDateTime start, DoseDateTime end) {
        SQLiteDatabase db = this.getReadableDatabase();
        TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");

        String query1 = "SELECT * FROM " + DoseRecorderContract.Dose.TABLE_NAME;
        Cursor dosesCount1 = db.rawQuery(query1, null);
        int result1 = 0;
        if (dosesCount1 != null) {
            result1 = dosesCount1.getCount();
        }

        String query = "SELECT * FROM " + DoseRecorderContract.Dose.TABLE_NAME +
                " WHERE " + DoseRecorderContract.Dose.COLUMN_NAME_TIMESTAMP + " >= '" + start.GetDateTimeText(utcTimeZone) +
                "' AND " + DoseRecorderContract.Dose.COLUMN_NAME_TIMESTAMP + " <= '" + end.GetDateTimeText(utcTimeZone) + "'";
        Cursor dosesCount = db.rawQuery(query, null);

        int result = 0;
        if (dosesCount != null) {
            result = dosesCount.getCount();
        }

        return result;
    }

    // Possibly CalendarWrapper should be added to the constructor
    public String getDoseTimesForDay(CalendarWrapper calWrapper) {
        SQLiteDatabase db = this.getReadableDatabase();

        CalendarWrapper dayMidnightCal = CalendarWrapper.Copy(calWrapper);
        dayMidnightCal.set(Calendar.HOUR_OF_DAY, 0);
        dayMidnightCal.set(Calendar.MINUTE, 0);
        dayMidnightCal.set(Calendar.SECOND, 0);
        dayMidnightCal.set(Calendar.MILLISECOND, 0);
        DoseDateTime todayMidnight = new DoseDateTime(dayMidnightCal);

        CalendarWrapper nextDayCal = CalendarWrapper.Copy(dayMidnightCal);
        nextDayCal.add(Calendar.DATE, 1);

        DoseDateTime startDate = new DoseDateTime(dayMidnightCal);
        DoseDateTime endDate = new DoseDateTime(nextDayCal);
        TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
        String startDateText = startDate.GetDateTimeText(utcTimeZone);
        String endDateText = endDate.GetDateTimeText(utcTimeZone);

        String queryText = "SELECT strftime('%H', " + DoseRecorderContract.Dose.COLUMN_NAME_TIMESTAMP + "), " +
                "COUNT(" + DoseRecorderContract.Dose.COLUMN_NAME_TIMESTAMP + ") FROM " + DoseRecorderContract.Dose.TABLE_NAME + " " +
                "WHERE " + DoseRecorderContract.Dose.COLUMN_NAME_TIMESTAMP + " between '" + startDateText + "' and '" + endDateText + "' " +
                "GROUP BY strftime('%Y-%m-%d %H:00:00', " + DoseRecorderContract.Dose.COLUMN_NAME_TIMESTAMP + ")";

        List<String> dayDoseTimes = new ArrayList<String>();

        Cursor hourDoses = db.rawQuery(queryText, null);
        if (hourDoses != null) {
            if (hourDoses.moveToFirst()) {
                CalendarWrapper calHourDose = null;
                DoseDateTime hourDoseTime = null;
                int doseCount = 0;
                int hour = 0;
                boolean firstDose = false;
                hourDoses.moveToFirst();
                while (!hourDoses.isAfterLast()) {
                    hour = Integer.parseInt(hourDoses.getString(0));
                    calHourDose = CalendarWrapper.Copy(dayMidnightCal);
                    calHourDose.add(Calendar.HOUR, hour);
                    hourDoseTime = new DoseDateTime(calHourDose);
                    doseCount = hourDoses.getInt(1);

                    String dose = String.format("%s", hourDoseTime.GetHourText(calWrapper.getTimeZone()));
                    if (doseCount > 1) {
                        dose += String.format("(%d)", doseCount);
                    }
                    dayDoseTimes.add(dose);
                    hourDoses.moveToNext();
                }
            }
        }

        return TextUtils.join(", ", dayDoseTimes);
    }

    public String getLastDoseTimestampFromDay(CalendarWrapper calWrapper) {
        SQLiteDatabase db = this.getReadableDatabase();

        CalendarWrapper dayAlmostMidnightCal = CalendarWrapper.Copy(calWrapper);
        dayAlmostMidnightCal.set(Calendar.HOUR_OF_DAY, 23);
        dayAlmostMidnightCal.set(Calendar.MINUTE, 59);
        dayAlmostMidnightCal.set(Calendar.SECOND, 59);
        dayAlmostMidnightCal.set(Calendar.MILLISECOND, 999);
        DoseDateTime dayAlmostMidnight = new DoseDateTime(dayAlmostMidnightCal);

        String query = "SELECT * FROM " + DoseRecorderContract.Dose.TABLE_NAME +
                " WHERE " + DoseRecorderContract.Dose.COLUMN_NAME_TIMESTAMP + " <= '" + dayAlmostMidnight.GetDateTimeText(calWrapper.getTimeZone()) + "'" +
                " ORDER BY " + DoseRecorderContract.Dose.COLUMN_NAME_TIMESTAMP + " DESC LIMIT 1";
        Cursor lastTimestamp = db.rawQuery(query, null);

        if (lastTimestamp != null) {
            if (lastTimestamp.moveToFirst()) {
                String lastDoseText = lastTimestamp.getString(lastTimestamp.getColumnIndex(DoseRecorderContract.Dose.COLUMN_NAME_TIMESTAMP));
                DoseDateTime lastDoseDateTime = new DoseDateTime(calWrapper, lastDoseText, TimeZone.getTimeZone("UTC"));
                String prefix = lastDoseDateTime.DaysSince(calWrapper);

                // TODO Do I need to close these Cursors?
                return prefix + " " + lastDoseDateTime.GetHourMinuteText(calWrapper.getTimeZone());
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
                addCount(new DoseDateTime(new CalendarWrapper(), doseTimestamp, TimeZone.getTimeZone("UTC")));
            }
        }
    }
}
