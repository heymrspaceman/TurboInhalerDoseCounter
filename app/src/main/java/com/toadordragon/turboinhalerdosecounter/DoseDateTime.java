package com.toadordragon.turboinhalerdosecounter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by thomas on 08-Jul-17.
 * Got into a total mess storing UTC times in database then reading them back incorrectly without reference to BST (daylight savings)
 * made this class to resolve this, may be total overkill
 */

public class DoseDateTime {

    public DoseDateTime() {
        InitialiseFormatter();
    }

    public DoseDateTime(Date date, DoseTimeZone zone) {
        InitialiseFormatter();
        InitialiseDate(date, zone);
    }

    public DoseDateTime(String dateText, DoseTimeZone zone) {
        InitialiseFormatter();
        try {
            Date date = m_format.parse(dateText);
            InitialiseDate(date, zone);
        }
        catch (ParseException ex) {
            ex.printStackTrace();
        }
    }

    private void InitialiseFormatter() {
        m_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    private void InitialiseDate(Date date, DoseTimeZone zone) {
        // All dates stored as UTC
        if (zone == DoseTimeZone.Local) {
            m_date = ConvertTimeBetweenZones(date, DoseTimeZone.Local, DoseTimeZone.UTC);
        } else {
            m_date = date;
        }
    }

    public static DoseDateTime Now() {
        Calendar calToday = Calendar.getInstance();
        return new DoseDateTime(calToday.getTime(), DoseTimeZone.UTC);
    }

    private Date ConvertTimeBetweenZones(Date date, DoseTimeZone sourceZone, DoseTimeZone destZone) {
        Date convertedDate = date;
        try
        {
            SimpleDateFormat formatterIn = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (sourceZone == DoseTimeZone.Local) {
                formatterIn.setTimeZone(TimeZone.getTimeZone("UTC"));
            } else {
                formatterIn.setTimeZone(TimeZone.getTimeZone("Europe/London")); // Apparently the same as BST
            }
            String bstAdjustedTimeText = formatterIn.format(date);

            if (destZone == DoseTimeZone.Local) {
                formatterIn.setTimeZone(TimeZone.getTimeZone("UTC"));
            } else {
                formatterIn.setTimeZone(TimeZone.getTimeZone("Europe/London")); // Apparently the same as BST
            }
            Calendar dateCal = Calendar.getInstance();
            dateCal.setTime(formatterIn.parse(bstAdjustedTimeText));
            convertedDate = dateCal.getTime();
        }
        catch (ParseException ex)
        {
            ex.printStackTrace();
        }

        return convertedDate;
    }

    public String GetDateTimeText(DoseTimeZone zone) {
        if (zone == DoseTimeZone.Local) {
            Date localDate = ConvertTimeBetweenZones(m_date, DoseTimeZone.UTC, DoseTimeZone.Local);
            return m_format.format(localDate);
        } else {
            return m_format.format(m_date);
        }
    }

    public String GetHourMinuteText(DoseTimeZone zone) {
        return FormatText(zone, HOUR_MINUTE_FORMAT);
    }

    public String GetHourText(DoseTimeZone zone) {
        return FormatText(zone, HOUR_FORMAT);
    }

    private String FormatText(DoseTimeZone zone, String dateFormat) {
        Date convertedDate = m_date;
        if (zone != DoseTimeZone.UTC) {
            convertedDate = ConvertTimeBetweenZones(m_date, DoseTimeZone.UTC, zone);
        }
        String hourMinuteText = new SimpleDateFormat(dateFormat).format(convertedDate);
        return hourMinuteText.toLowerCase();
    }

    private SimpleDateFormat m_format;
    private Date m_date;

    private static final String HOUR_FORMAT = "ha";
    private static final String HOUR_MINUTE_FORMAT = "h:mma";

    public enum DoseTimeZone {
        Local,
        UTC
    }
}
