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

    private CalendarWrapper m_calWrapper;
    private SimpleDateFormat m_format;
    private Date m_date;
    private TimeZone m_utcTimeZone;

    public DoseDateTime(CalendarWrapper calWrapper, Date date, TimeZone zone) {
        m_calWrapper = calWrapper;
        InitialiseFormatter();
        InitialiseDate(date, zone);
    }

    public DoseDateTime(CalendarWrapper calWrapper, String dateText, TimeZone zone) {
        m_calWrapper = calWrapper;
        InitialiseFormatter();
        try {
            Date date = m_format.parse(dateText);
            InitialiseDate(date, zone);
        }
        catch (ParseException ex) {
            ex.printStackTrace();
        }
    }

    public DoseDateTime(CalendarWrapper calWrapper) {
        m_calWrapper = calWrapper;
        InitialiseFormatter();
        Calendar cal = calWrapper.getCalendar();
        InitialiseDate(cal.getTime(), cal.getTimeZone());
    }

    private void InitialiseFormatter() {
        m_format = new SimpleDateFormat(FULL_DATE_FORMAT);
    }

    private void InitialiseDate(Date date, TimeZone zone) {
        m_utcTimeZone = TimeZone.getTimeZone("UTC");
        // All dates stored as UTC
        if (m_utcTimeZone != zone) {
            m_date = ConvertTimeBetweenZones(date, zone, m_utcTimeZone);
        } else {
            m_date = date;
        }
    }

    // mock the Calendar.getInstance so we can set the current date/time later in the unit tests
    public static DoseDateTime Now() {
        return new DoseDateTime(new CalendarWrapper());
    }

    private Date ConvertTimeBetweenZones(Date date, TimeZone sourceZone, TimeZone destZone) {
        Date convertedDate = date;
        try
        {
            SimpleDateFormat formatterIn = new SimpleDateFormat(FULL_DATE_FORMAT);
            formatterIn.setTimeZone(destZone);
            String bstAdjustedTimeText = formatterIn.format(date);
            formatterIn.setTimeZone(sourceZone);
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

    public long getUTCTime() {
        return m_date.getTime();
    }

    public String GetDateTimeText(TimeZone zone) {
        return FormatText(zone, FULL_DATE_FORMAT);
    }

    public String GetHourMinuteText(TimeZone zone) {
        return FormatText(zone, HOUR_MINUTE_FORMAT);
    }

    public String GetHourText(TimeZone zone) {
        return FormatText(zone, HOUR_FORMAT);
    }

    private String FormatText(TimeZone zone, String dateFormat) {
        Date convertedDate = m_date;
        if (m_utcTimeZone != zone) {
            convertedDate = ConvertTimeBetweenZones(m_date, m_utcTimeZone, zone);
        }
        String hourMinuteText = new SimpleDateFormat(dateFormat).format(convertedDate);
        return hourMinuteText.toLowerCase();
    }

    // Pass in todayMidnight so we can test, but we remove it later and mock the Calendar.getInstance so we can set the current date/time later in the unit tests
    public String DaysSince(CalendarWrapper calWrapper) {
        Calendar cal = calWrapper.getCalendar();
        Calendar midnightCal = (Calendar)cal.clone() ;
        midnightCal.set(Calendar.HOUR_OF_DAY, 0);
        midnightCal.set(Calendar.MINUTE, 0);
        midnightCal.set(Calendar.SECOND, 0);
        midnightCal.set(Calendar.MILLISECOND, 0);
        DoseDateTime midnightToday = new DoseDateTime(new CalendarWrapper(midnightCal));

        if (this.getUTCTime() > midnightToday.getUTCTime()) {
            return "today";
        }

        long midnightTodaySeconds = midnightToday.getUTCTime();
        long thisSeconds = this.getUTCTime();
        long secondsSince = midnightTodaySeconds - thisSeconds;

        long oneDay = 24 * 60 * 60 * 1000;
        long daysSeconds = secondsSince % oneDay;
        long days = (secondsSince - daysSeconds) / oneDay;

        if (days == 0) {
            return "yesterday";
        } else {
            return (days + 1) + " days ago";
        }
    }

    private final String FULL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private final String HOUR_FORMAT = "ha";
    private final String HOUR_MINUTE_FORMAT = "h:mma";
}
