package com.toadordragon.turboinhalerdosecounter;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by thomas on 16-Jul-17.
 */

// Generating a calendar from static method Calendar.getInstance() is a pain to mock or I can't work it out so doing this instead
public class CalendarWrapper {

    Calendar m_cal;

    public CalendarWrapper()
    {
        m_cal = Calendar.getInstance();
    }

    public CalendarWrapper(Calendar cal)
    {
        m_cal = cal;
    }

    // Ideally this would implement Cloneable, but apparently that's more hassle than it's worth
    // Copy constructor is also out because I want to mock it
    public static CalendarWrapper Copy(CalendarWrapper calWrapper)
    {
        Calendar cal = calWrapper.getCalendar();
        return new CalendarWrapper((Calendar)cal.clone());
    }

    public static Calendar CreateCalendar(int year, int month, int day, int hour, int minute, int second, TimeZone timeZone) {
        Calendar newCal = Calendar.getInstance();
        newCal.setTimeZone(timeZone);
        newCal.set(Calendar.YEAR, year);
        newCal.set(Calendar.MONTH, month);
        newCal.set(Calendar.DAY_OF_MONTH, day);
        newCal.set(Calendar.HOUR_OF_DAY, hour);
        newCal.set(Calendar.MINUTE, minute);
        newCal.set(Calendar.SECOND, second);
        newCal.set(Calendar.MILLISECOND, 0);

        return newCal;
    }

    public Calendar getCalendar() {
        return m_cal;
    }

    // Calendar methods
    public void set(int field, int value) {
        m_cal.set(field, value);
    }

    public void add(int field, int amount) {
        m_cal.add(field, amount);
    }

    public TimeZone getTimeZone() {
        return m_cal.getTimeZone();
    }
}
