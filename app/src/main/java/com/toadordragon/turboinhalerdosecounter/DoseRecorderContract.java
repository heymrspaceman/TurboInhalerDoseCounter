package com.toadordragon.turboinhalerdosecounter;

import android.provider.BaseColumns;

public final class DoseRecorderContract {
    // Private constructor so class can't be created
    private  DoseRecorderContract() {}

    // Table contents
    public static class Dose implements BaseColumns {
        public static final String TABLE_NAME = "dose";
        // Change count to be inhaler id and time columns later
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
        public static final String COLUMN_NAME_COUNT = "count";
    }

    // Add inhaler table later

    private static final String CREATE_DOSES_TABLE =
            "CREATE TABLE " + Dose.TABLE_NAME + " (" +
                    Dose._ID + " INTEGER PRIMARY KEY," +
                    Dose.COLUMN_NAME_TIMESTAMP + " DATETIME," +
                    Dose.COLUMN_NAME_COUNT + " INTEGER)";

    private static final String DELETE_DOSES_TABLE =
            "DROP TABLE IF EXISTS " + Dose.TABLE_NAME;

    public static final String CREATE_ENTRIES = CREATE_DOSES_TABLE;
    public static final String DELETE_ENTRIES = DELETE_DOSES_TABLE;
}
