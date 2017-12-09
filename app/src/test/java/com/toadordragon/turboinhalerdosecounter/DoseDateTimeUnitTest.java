package com.toadordragon.turboinhalerdosecounter;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Before;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.mockito.Mock;

/**
 * Created by thomas on 08-Jul-17.
 */

public class DoseDateTimeUnitTest {
    @Mock
    CalendarWrapper mockedCalendarWrapper;

    @Before
    public void setUp() {
        mockedCalendarWrapper = mock(CalendarWrapper.class);
    }

    @Test
    public void DoseDateTime_1stJan() throws Exception {
        long jan1st2017 = 1483228800000L;
        String jan1st2017Text = "2017-01-01 00:00:00";
        Date dtJan1st2017 = new Date(jan1st2017);

        // Constructor 1st Jan 2017 (local)
        DoseDateTime localJan1st2017 = new DoseDateTime(new CalendarWrapper(), dtJan1st2017, TimeZone.getTimeZone("Europe/London"));
        assertEquals("2017-01-01 00:00:00", localJan1st2017.GetDateTimeText(TimeZone.getTimeZone("Europe/London")));
        assertEquals("2017-01-01 00:00:00", localJan1st2017.GetDateTimeText(TimeZone.getTimeZone("UTC")));
        assertEquals(jan1st2017, localJan1st2017.getUTCTime());

        DoseDateTime localJan1st2017_2 = new DoseDateTime(new CalendarWrapper(),jan1st2017Text, TimeZone.getTimeZone("Europe/London"));
        assertEquals("2017-01-01 00:00:00", localJan1st2017_2.GetDateTimeText(TimeZone.getTimeZone("Europe/London")));
        assertEquals("2017-01-01 00:00:00", localJan1st2017_2.GetDateTimeText(TimeZone.getTimeZone("UTC")));
        assertEquals(jan1st2017, localJan1st2017_2.getUTCTime());

        // Constructor 1st Jan 2017 (UTC)
        DoseDateTime utcJan1st2017 = new DoseDateTime(new CalendarWrapper(),dtJan1st2017, TimeZone.getTimeZone("UTC"));
        assertEquals("2017-01-01 00:00:00", utcJan1st2017.GetDateTimeText(TimeZone.getTimeZone("Europe/London")));
        assertEquals("2017-01-01 00:00:00", utcJan1st2017.GetDateTimeText(TimeZone.getTimeZone("UTC")));
        assertEquals(jan1st2017, utcJan1st2017.getUTCTime());

        DoseDateTime utcJan1st2017_2 = new DoseDateTime(new CalendarWrapper(),jan1st2017Text, TimeZone.getTimeZone("UTC"));
        assertEquals("2017-01-01 00:00:00", utcJan1st2017_2.GetDateTimeText(TimeZone.getTimeZone("Europe/London")));
        assertEquals("2017-01-01 00:00:00", utcJan1st2017_2.GetDateTimeText(TimeZone.getTimeZone("UTC")));
        assertEquals(jan1st2017, utcJan1st2017_2.getUTCTime());
    }

    @Test
    public void DoseDateTime_1stMay() throws Exception {
        long may1st2017BST = 1493596800000L;
        String may1st2017Text = "2017-05-01 01:00:00";
        Date dtMay1st2017BST = new Date(may1st2017BST);

        // Constructor 1st May 2017 (local)
        DoseDateTime localMay1st20171amBST = new DoseDateTime(new CalendarWrapper(),dtMay1st2017BST, TimeZone.getTimeZone("Europe/London"));
        assertEquals("2017-05-01 00:00:00", localMay1st20171amBST.GetDateTimeText(TimeZone.getTimeZone("UTC")));
        assertEquals("2017-05-01 01:00:00", localMay1st20171amBST.GetDateTimeText(TimeZone.getTimeZone("Europe/London")));
        assertEquals(may1st2017BST - 3600000, localMay1st20171amBST.getUTCTime());

        DoseDateTime localMay1st20171amBST_2 = new DoseDateTime(new CalendarWrapper(),may1st2017Text, TimeZone.getTimeZone("Europe/London"));
        assertEquals("2017-05-01 00:00:00", localMay1st20171amBST_2.GetDateTimeText(TimeZone.getTimeZone("UTC")));
        assertEquals("2017-05-01 01:00:00", localMay1st20171amBST_2.GetDateTimeText(TimeZone.getTimeZone("Europe/London")));
        assertEquals(may1st2017BST - 3600000, localMay1st20171amBST_2.getUTCTime());

        // Constructor 30 April May 2017 23:00 (UTC)
        DoseDateTime utcMay1st20171amUTC = new DoseDateTime(new CalendarWrapper(),dtMay1st2017BST, TimeZone.getTimeZone("UTC"));
        assertEquals("2017-05-01 01:00:00", utcMay1st20171amUTC.GetDateTimeText(TimeZone.getTimeZone("UTC")));
        assertEquals("2017-05-01 02:00:00", utcMay1st20171amUTC.GetDateTimeText(TimeZone.getTimeZone("Europe/London")));
        assertEquals(may1st2017BST, utcMay1st20171amUTC.getUTCTime());

        DoseDateTime utcMay1st20171amUTC_2 = new DoseDateTime(new CalendarWrapper(),may1st2017Text, TimeZone.getTimeZone("UTC"));
        assertEquals("2017-05-01 01:00:00", utcMay1st20171amUTC_2.GetDateTimeText(TimeZone.getTimeZone("UTC")));
        assertEquals("2017-05-01 02:00:00", utcMay1st20171amUTC_2.GetDateTimeText(TimeZone.getTimeZone("Europe/London")));
        assertEquals(may1st2017BST, utcMay1st20171amUTC_2.getUTCTime());
    }

    @Test
    public void DoseDateTime_text() throws Exception {
        String time1Text = "2011-07-20 02:30:02";
        DoseDateTime time1 = new DoseDateTime(new CalendarWrapper(),time1Text, TimeZone.getTimeZone("Europe/London"));
        assertEquals("2:30am", time1.GetHourMinuteText(TimeZone.getTimeZone("Europe/London")));
        assertEquals(time1Text, time1.GetDateTimeText(TimeZone.getTimeZone("Europe/London")));
        String time2Text = "1977-01-01 15:43:44";
        DoseDateTime time2 = new DoseDateTime(new CalendarWrapper(),time2Text, TimeZone.getTimeZone("Europe/London"));
        assertEquals("3:43pm", time2.GetHourMinuteText(TimeZone.getTimeZone("Europe/London")));
        assertEquals("3pm", time2.GetHourText(TimeZone.getTimeZone("Europe/London")));
        assertEquals(time2Text, time2.GetDateTimeText(TimeZone.getTimeZone("Europe/London")));
    }

    @Test
    public void DoseDateTime_daysSince() throws  Exception {
        Calendar july16th2017 = Calendar.getInstance();
        july16th2017.set(Calendar.YEAR, 2017);
        july16th2017.set(Calendar.MONTH, 6);
        july16th2017.set(Calendar.DAY_OF_MONTH, 16);
        july16th2017.set(Calendar.HOUR_OF_DAY, 14);
        july16th2017.set(Calendar.MINUTE, 0);
        july16th2017.set(Calendar.SECOND, 0);
        july16th2017.set(Calendar.MILLISECOND, 0);

        when(mockedCalendarWrapper.getCalendar())
                .thenReturn(july16th2017);

        String todayText = "2017-07-16 02:30:02";
        String todayAfterMidnightText = "2017-07-16 00:00:02";
        String yesterdayBeforeMidnightText = "2017-07-15 23:59:58";
        String yesterdayText = "2017-07-15 02:30:02";
        String twoDaysAgoText = "2017-07-14 02:30:02";
        String agesAgoText = "2016-06-05 02:30:02";

        DoseDateTime todayTime = new DoseDateTime(mockedCalendarWrapper, todayText, TimeZone.getTimeZone("Europe/London"));
        assertEquals("today", todayTime.DaysSince(mockedCalendarWrapper));
        DoseDateTime todayAfterMidnightTime = new DoseDateTime(mockedCalendarWrapper, todayAfterMidnightText, TimeZone.getTimeZone("Europe/London"));
        assertEquals("today", todayAfterMidnightTime.DaysSince(mockedCalendarWrapper));
        DoseDateTime yesterdayTime = new DoseDateTime(mockedCalendarWrapper, yesterdayText, TimeZone.getTimeZone("Europe/London"));
        assertEquals("yesterday", yesterdayTime.DaysSince(mockedCalendarWrapper));
        DoseDateTime yesterdayBeforeMidnightTime = new DoseDateTime(mockedCalendarWrapper, yesterdayBeforeMidnightText, TimeZone.getTimeZone("Europe/London"));
        assertEquals("yesterday", yesterdayBeforeMidnightTime.DaysSince(mockedCalendarWrapper));
        DoseDateTime twoDaysAgoTime = new DoseDateTime(mockedCalendarWrapper, twoDaysAgoText, TimeZone.getTimeZone("Europe/London"));
        assertEquals("2 days ago", twoDaysAgoTime.DaysSince(mockedCalendarWrapper));
        DoseDateTime agesAgoTime = new DoseDateTime(mockedCalendarWrapper, agesAgoText, TimeZone.getTimeZone("Europe/London"));
        assertEquals("406 days ago", agesAgoTime.DaysSince(mockedCalendarWrapper));
    }
}
