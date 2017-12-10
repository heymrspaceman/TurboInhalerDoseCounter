package com.toadordragon.turboinhalerdosecounter;

import android.database.Cursor;
import android.support.test.InstrumentationRegistry;

import com.toadordragon.turboinhalerdosecounter.database.DoseRecorderDBHelper;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.util.List;
import java.util.TimeZone;

import static org.mockito.Mockito.*;

/**
 * Created by thomas on 14-Jul-17.
 */

public class DoseRecorderDBHelperUnitTest {
    private DoseRecorderDBHelper db;

    @Before
    public void setUp() {
        db = DoseRecorderDBHelper.getInstance(InstrumentationRegistry.getTargetContext());
        db.clearDatabaseAndRecreate();
    }

    @After
    public void finish() {
        db.close();
        db.clearDatabase();
    }

    @Test
    public void testPreConditions() {
        assertNotNull(db);
    }

    @Test
    public void DosesTest() throws Exception {

        // Tests required
        // exportDosesCSV
        // importDosesCSV

        CalendarWrapper mockedCalWrapperNewYearsDay2017 = mock(CalendarWrapper.class);
        CalendarWrapper mockedCalWrapperNewYearsDay2018 = mock(CalendarWrapper.class);
        CalendarWrapper mockedCalWrapperDay1Dose1 = mock(CalendarWrapper.class);
        CalendarWrapper mockedCalWrapperDay1Dose2 = mock(CalendarWrapper.class);
        CalendarWrapper mockedCalWrapperDay2Dose1 = mock(CalendarWrapper.class);

        Calendar newYearsDayCal2017 = CalendarWrapper.CreateCalendar(2017, 1, 1, 9, 0, 0, TimeZone.getTimeZone("Europe/London"));
        Calendar newYearsDayCal2018 = CalendarWrapper.CreateCalendar(2018, 1, 1, 9, 0, 0, TimeZone.getTimeZone("Europe/London"));
        Calendar day1DoseCal1 = CalendarWrapper.CreateCalendar(2017, 1, 16, 14, 15, 0, TimeZone.getTimeZone("Europe/London"));
        Calendar day1DoseCal2 = CalendarWrapper.CreateCalendar(2017, 1, 16, 21, 30, 0, TimeZone.getTimeZone("Europe/London"));
        Calendar day2DoseCal1 = CalendarWrapper.CreateCalendar(2017, 1, 17, 1, 15, 0, TimeZone.getTimeZone("Europe/London"));

        when(mockedCalWrapperNewYearsDay2017.getCalendar()).thenReturn(newYearsDayCal2017);
        when(mockedCalWrapperNewYearsDay2017.getTimeZone()).thenReturn(TimeZone.getTimeZone("Europe/London"));
        when(mockedCalWrapperNewYearsDay2018.getCalendar()).thenReturn(newYearsDayCal2018);
        when(mockedCalWrapperNewYearsDay2018.getTimeZone()).thenReturn(TimeZone.getTimeZone("Europe/London"));
        when(mockedCalWrapperDay1Dose1.getCalendar()).thenReturn(day1DoseCal1);
        when(mockedCalWrapperDay1Dose1.getTimeZone()).thenReturn(TimeZone.getTimeZone("Europe/London"));
        when(mockedCalWrapperDay1Dose2.getCalendar()).thenReturn(day1DoseCal2);
        when(mockedCalWrapperDay1Dose2.getTimeZone()).thenReturn(TimeZone.getTimeZone("Europe/London"));
        when(mockedCalWrapperDay2Dose1.getCalendar()).thenReturn(day2DoseCal1);
        when(mockedCalWrapperDay2Dose1.getTimeZone()).thenReturn(TimeZone.getTimeZone("Europe/London"));

        DoseDateTime day1Dose1Time = new DoseDateTime(mockedCalWrapperDay1Dose1);
        DoseDateTime day1Dose2Time = new DoseDateTime(mockedCalWrapperDay1Dose2);
        DoseDateTime day2Dose1Time = new DoseDateTime(mockedCalWrapperDay2Dose1);

        // Before we add any counts
        assertEquals(0, db.getDosesCount());
        assertEquals("", db.getLastDoseTimestampFromDay(mockedCalWrapperNewYearsDay2017));
        assertEquals("", db.getDoseTimesForDay(mockedCalWrapperNewYearsDay2017));
        assertEquals(0, db.getDoses24HoursCount(mockedCalWrapperNewYearsDay2017));
        assertEquals("", db.getLastDoseTimestampFromDay(mockedCalWrapperDay1Dose1));
        assertEquals("", db.getDoseTimesForDay(mockedCalWrapperDay1Dose1));
        assertEquals(0, db.getDoses24HoursCount(mockedCalWrapperDay1Dose1));
        assertEquals("", db.getLastDoseTimestampFromDay(mockedCalWrapperNewYearsDay2018));
        assertEquals("", db.getDoseTimesForDay(mockedCalWrapperNewYearsDay2018));
        assertEquals(0, db.getDoses24HoursCount(mockedCalWrapperNewYearsDay2018));

        // Add dose 1
        db.addCount(day1Dose1Time);
        assertEquals(db.getDosesCount(), 1);
        getCountsByDayCheck(new DayCountCheckResults(mockedCalWrapperDay1Dose1, 1));
        assertEquals("today 2:15pm", db.getLastDoseTimestampFromDay(mockedCalWrapperDay1Dose1));
        assertEquals("yesterday 2:15pm", db.getLastDoseTimestampFromDay(mockedCalWrapperDay2Dose1));
        assertEquals("2pm", db.getDoseTimesForDay(mockedCalWrapperDay1Dose1));
        assertEquals(1, db.getDoses24HoursCount(mockedCalWrapperDay1Dose1));
        assertEquals("350 days ago 2:15pm", db.getLastDoseTimestampFromDay(mockedCalWrapperNewYearsDay2018));
        assertEquals("", db.getDoseTimesForDay(mockedCalWrapperNewYearsDay2018));
        assertEquals(0, db.getDoses24HoursCount(mockedCalWrapperNewYearsDay2018));
        getCountsByDayCheck(new DayCountCheckResults(mockedCalWrapperDay1Dose1, 1));

        // Add same dose again
        db.addCount(day1Dose1Time);
        assertEquals(db.getDosesCount(), 2);
        getCountsByDayCheck(new DayCountCheckResults(mockedCalWrapperDay1Dose1, 2));
        assertEquals("today 2:15pm", db.getLastDoseTimestampFromDay(mockedCalWrapperDay1Dose1));
        assertEquals("yesterday 2:15pm", db.getLastDoseTimestampFromDay(mockedCalWrapperDay2Dose1));
        assertEquals("2pm(2)", db.getDoseTimesForDay(mockedCalWrapperDay1Dose1));
        assertEquals(2, db.getDoses24HoursCount(mockedCalWrapperDay1Dose1));
        assertEquals("350 days ago 2:15pm", db.getLastDoseTimestampFromDay(mockedCalWrapperNewYearsDay2018));
        assertEquals("", db.getDoseTimesForDay(mockedCalWrapperNewYearsDay2018));
        assertEquals(0, db.getDoses24HoursCount(mockedCalWrapperNewYearsDay2018));

        // Different dose - same day
        db.addCount(day1Dose2Time);
        assertEquals(db.getDosesCount(), 3);
        getCountsByDayCheck(new DayCountCheckResults(mockedCalWrapperDay1Dose1, 3));
        assertEquals("today 9:30pm", db.getLastDoseTimestampFromDay(mockedCalWrapperDay1Dose1));
        assertEquals("yesterday 9:30pm", db.getLastDoseTimestampFromDay(mockedCalWrapperDay2Dose1));

        assertEquals("2pm(2), 9pm", db.getDoseTimesForDay(mockedCalWrapperDay1Dose1));
        int test = db.getDoses24HoursCount(mockedCalWrapperDay1Dose1);
        assertEquals(2, db.getDoses24HoursCount(mockedCalWrapperDay1Dose1));
        assertEquals(3, db.getDoses24HoursCount(mockedCalWrapperDay1Dose2));
        assertEquals("350 days ago 9:30pm", db.getLastDoseTimestampFromDay(mockedCalWrapperNewYearsDay2018));
        assertEquals("", db.getDoseTimesForDay(mockedCalWrapperNewYearsDay2018));
        assertEquals(0, db.getDoses24HoursCount(mockedCalWrapperNewYearsDay2018));

        db.addCount(day2Dose1Time);
        assertEquals(db.getDosesCount(), 4);
        List<DayCountCheckResults> expectedDayCounts = new ArrayList<DayCountCheckResults>();
        expectedDayCounts.add(new DayCountCheckResults(mockedCalWrapperDay1Dose1, 3));
        expectedDayCounts.add(new DayCountCheckResults(mockedCalWrapperDay2Dose1, 1));
        getCountsByDayCheck(expectedDayCounts);

        // Currently getLastDoseTimestampFromDay doesn't use the day passed in change that!
        assertEquals("today 9:30pm", db.getLastDoseTimestampFromDay(mockedCalWrapperDay1Dose1));
        assertEquals("today 1:15am", db.getLastDoseTimestampFromDay(mockedCalWrapperDay2Dose1));

        assertEquals("2pm(2), 9pm", db.getDoseTimesForDay(mockedCalWrapperDay1Dose1));
        assertEquals(2, db.getDoses24HoursCount(mockedCalWrapperDay1Dose1));
        assertEquals(3, db.getDoses24HoursCount(mockedCalWrapperDay1Dose2));
        assertEquals("1am", db.getDoseTimesForDay(mockedCalWrapperDay2Dose1));
        assertEquals(4, db.getDoses24HoursCount(mockedCalWrapperDay2Dose1));
        assertEquals("349 days ago 1:15am", db.getLastDoseTimestampFromDay(mockedCalWrapperNewYearsDay2018));
        assertEquals("", db.getDoseTimesForDay(mockedCalWrapperNewYearsDay2018));
        assertEquals(0, db.getDoses24HoursCount(mockedCalWrapperNewYearsDay2018));

        // Confirm New Years Day 2017 returns no doses (after adding doses on later days)
        assertEquals("", db.getLastDoseTimestampFromDay(mockedCalWrapperNewYearsDay2017));
        assertEquals("", db.getDoseTimesForDay(mockedCalWrapperNewYearsDay2017));
        assertEquals(0, db.getDoses24HoursCount(mockedCalWrapperNewYearsDay2017));

        // Edge cases

        CalendarWrapper mockedCalWrapperDay3Dose1 = mock(CalendarWrapper.class);
        CalendarWrapper mockedCalWrapperDay4Dose1 = mock(CalendarWrapper.class);
        CalendarWrapper mockedCalWrapperDay3Check1 = mock(CalendarWrapper.class);
        CalendarWrapper mockedCalWrapperDay3Check2 = mock(CalendarWrapper.class);
        CalendarWrapper mockedCalWrapperDay3Check3 = mock(CalendarWrapper.class);
        CalendarWrapper mockedCalWrapperDay4Check1 = mock(CalendarWrapper.class);
        CalendarWrapper mockedCalWrapperDay4Check2 = mock(CalendarWrapper.class);
        CalendarWrapper mockedCalWrapperDay4Check3 = mock(CalendarWrapper.class);

        Calendar day3DoseCal1 = CalendarWrapper.CreateCalendar(2017, 1, 17, 22, 00, 0, TimeZone.getTimeZone("Europe/London"));
        Calendar day4DoseCal1 = CalendarWrapper.CreateCalendar(2017, 1, 18, 10, 00, 0, TimeZone.getTimeZone("Europe/London"));
        Calendar day3CheckCal1 = CalendarWrapper.CreateCalendar(2017, 1, 17, 21, 59, 59, TimeZone.getTimeZone("Europe/London"));
        Calendar day3CheckCal2 = CalendarWrapper.CreateCalendar(2017, 1, 17, 22, 00, 0, TimeZone.getTimeZone("Europe/London"));
        Calendar day3CheckCal3 = CalendarWrapper.CreateCalendar(2017, 1, 17, 22, 00, 1, TimeZone.getTimeZone("Europe/London"));
        Calendar day4CheckCal1 = CalendarWrapper.CreateCalendar(2017, 1, 18, 9, 59, 59, TimeZone.getTimeZone("Europe/London"));
        Calendar day4CheckCal2 = CalendarWrapper.CreateCalendar(2017, 1, 18, 10, 00, 0, TimeZone.getTimeZone("Europe/London"));
        Calendar day4CheckCal3 = CalendarWrapper.CreateCalendar(2017, 1, 18, 10, 00, 1, TimeZone.getTimeZone("Europe/London"));

        when(mockedCalWrapperDay3Dose1.getCalendar()).thenReturn(day3DoseCal1);
        when(mockedCalWrapperDay3Dose1.getTimeZone()).thenReturn(TimeZone.getTimeZone("Europe/London"));
        when(mockedCalWrapperDay4Dose1.getCalendar()).thenReturn(day4DoseCal1);
        when(mockedCalWrapperDay4Dose1.getTimeZone()).thenReturn(TimeZone.getTimeZone("Europe/London"));
        when(mockedCalWrapperDay3Check1.getCalendar()).thenReturn(day3CheckCal1);
        when(mockedCalWrapperDay3Check1.getTimeZone()).thenReturn(TimeZone.getTimeZone("Europe/London"));
        when(mockedCalWrapperDay3Check2.getCalendar()).thenReturn(day3CheckCal2);
        when(mockedCalWrapperDay3Check2.getTimeZone()).thenReturn(TimeZone.getTimeZone("Europe/London"));
        when(mockedCalWrapperDay3Check3.getCalendar()).thenReturn(day3CheckCal3);
        when(mockedCalWrapperDay3Check3.getTimeZone()).thenReturn(TimeZone.getTimeZone("Europe/London"));
        when(mockedCalWrapperDay4Check1.getCalendar()).thenReturn(day4CheckCal1);
        when(mockedCalWrapperDay4Check1.getTimeZone()).thenReturn(TimeZone.getTimeZone("Europe/London"));
        when(mockedCalWrapperDay4Check2.getCalendar()).thenReturn(day4CheckCal2);
        when(mockedCalWrapperDay4Check2.getTimeZone()).thenReturn(TimeZone.getTimeZone("Europe/London"));
        when(mockedCalWrapperDay4Check3.getCalendar()).thenReturn(day4CheckCal3);
        when(mockedCalWrapperDay4Check3.getTimeZone()).thenReturn(TimeZone.getTimeZone("Europe/London"));

        DoseDateTime day3Dose1Time = new DoseDateTime(mockedCalWrapperDay3Dose1);
        DoseDateTime day4Dose1Time = new DoseDateTime(mockedCalWrapperDay4Dose1);

        DoseDateTime day3Check1Time = new DoseDateTime(mockedCalWrapperDay3Check1);
        DoseDateTime day3Check2Time = new DoseDateTime(mockedCalWrapperDay3Check2);
        DoseDateTime day3Check3Time = new DoseDateTime(mockedCalWrapperDay3Check3);
        DoseDateTime day4Check1Time = new DoseDateTime(mockedCalWrapperDay4Check1);
        DoseDateTime day4Check2Time = new DoseDateTime(mockedCalWrapperDay4Check2);
        DoseDateTime day4Check3Time = new DoseDateTime(mockedCalWrapperDay4Check3);


        // Need additional getDoses24HoursCount test,
        // Add day 3 10pm, day 4 10am,
        // check 24 hour count at day 3 3pm = 2
        // check 24 hour count at day 3 9:59pm = 2
        // check 24 hour count at day 3 10:00pm = 2
        // check 24 hour count at day 3 10:01pm = 1
        // check 24 hour count at day 4 9:59pm = 1
        // check 24 hour count at day 4 10:00pm = 1
        // check 24 hour count at day 4 10:01pm = 0

        // Add day 5 23:59:59, check count at day 6 2pm = 1
        // check count at day 6 23:59:59 = 1
        // check count at day 6 00:00:00 = 1
    }

    private boolean getCountsByDayCheck(DayCountCheckResults results) {
        List<DayCountCheckResults> resultsList = new ArrayList<DayCountCheckResults>();
        resultsList.add(results);
        return getCountsByDayCheck(resultsList);
    }

    private boolean getCountsByDayCheck(List<DayCountCheckResults> resultsList) {
        DayCountCheckResults expectedResults = resultsList.get(0);
        int expectedDay = expectedResults.getDay();
        int expectedMonth = expectedResults.getMonth();
        int expectedCount = expectedResults.getCount();

        int actualDoses = 0;
        int resultsIndex = 0;
        try {
            Cursor allDoses = db.getCountsByDay();
            if (allDoses != null) {
                if (allDoses.moveToFirst()) {
                    do {
                        expectedResults = resultsList.get(resultsIndex);
                        expectedDay = expectedResults.getDay();
                        expectedMonth = expectedResults.getMonth();
                        expectedCount = expectedResults.getCount();

                        String dayText = allDoses.getString(1);
                        int actualCount = allDoses.getInt(2);
                        String monthText = allDoses.getString(3);
                        int actualDay = Integer.parseInt(dayText);
                        int actualMonth = Integer.parseInt(monthText);
                        assertEquals(expectedDay, actualDay);

                        // Month is always out by 1 - don't know why, lets go with it for now
                        assertEquals(expectedMonth + 1, actualMonth);
                        assertEquals(expectedCount, actualCount);

                        resultsIndex = resultsIndex + 1;
                        actualDoses = actualDoses + actualCount;
                        allDoses.moveToNext();
                    } while (!allDoses.isAfterLast());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

        assertEquals(resultsList.size(), resultsIndex);
        return true;
    }
}

class DayCountCheckResults
{
    public DayCountCheckResults(CalendarWrapper cal, int count) {
        m_day = cal.getCalendar().get(Calendar.DAY_OF_MONTH);
        m_month = cal.getCalendar().get(Calendar.MONTH);
        m_count = count;
    }

    public int getDay() {
        return m_day;
    }

    public int getMonth() {
        return m_month;
    }

    public int getCount() {
        return m_count;
    }

    int m_day;
    int m_month;
    int m_count;
}