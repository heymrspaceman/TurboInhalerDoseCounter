package com.toadordragon.turboinhalerdosecounter;

import org.junit.Test;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by thomas on 08-Jul-17.
 */

public class DoseDateTimeUnitTest {
    @Test
    public void DoseDateTime_1stJan() throws Exception {
        long jan1st2017 = 1483228800000L;
        String jan1st2017Text = "2017-01-01 00:00:00";
        Date dtJan1st2017 = new Date(jan1st2017);

        // Constructor 1st Jan 2017 (local)
        DoseDateTime localJan1st2017 = new DoseDateTime(dtJan1st2017, DoseDateTime.DoseTimeZone.Local);
        assertEquals("2017-01-01 00:00:00", localJan1st2017.GetDateTimeText(DoseDateTime.DoseTimeZone.Local));
        assertEquals("2017-01-01 00:00:00", localJan1st2017.GetDateTimeText(DoseDateTime.DoseTimeZone.UTC));

        DoseDateTime localJan1st2017_2 = new DoseDateTime(jan1st2017Text, DoseDateTime.DoseTimeZone.Local);
        assertEquals("2017-01-01 00:00:00", localJan1st2017_2.GetDateTimeText(DoseDateTime.DoseTimeZone.Local));
        assertEquals("2017-01-01 00:00:00", localJan1st2017_2.GetDateTimeText(DoseDateTime.DoseTimeZone.UTC));

        // Constructor 1st Jan 2017 (UTC)
        DoseDateTime utcJan1st2017 = new DoseDateTime(dtJan1st2017, DoseDateTime.DoseTimeZone.UTC);
        assertEquals("2017-01-01 00:00:00", utcJan1st2017.GetDateTimeText(DoseDateTime.DoseTimeZone.Local));
        assertEquals("2017-01-01 00:00:00", utcJan1st2017.GetDateTimeText(DoseDateTime.DoseTimeZone.UTC));

        DoseDateTime utcJan1st2017_2 = new DoseDateTime(jan1st2017Text, DoseDateTime.DoseTimeZone.UTC);
        assertEquals("2017-01-01 00:00:00", utcJan1st2017_2.GetDateTimeText(DoseDateTime.DoseTimeZone.Local));
        assertEquals("2017-01-01 00:00:00", utcJan1st2017_2.GetDateTimeText(DoseDateTime.DoseTimeZone.UTC));
    }

    @Test
    public void DoseDateTime_1stMay() throws Exception {
        long may1st2017BST = 1493596800000L;
        String may1st2017Text = "2017-05-01 01:00:00";
        Date dtMay1st2017BST = new Date(may1st2017BST);

        // Constructor 1st May 2017 (local)
        DoseDateTime localMay1st20171amBST = new DoseDateTime(dtMay1st2017BST, DoseDateTime.DoseTimeZone.Local);
        assertEquals("2017-05-01 00:00:00", localMay1st20171amBST.GetDateTimeText(DoseDateTime.DoseTimeZone.UTC));
        assertEquals("2017-05-01 01:00:00", localMay1st20171amBST.GetDateTimeText(DoseDateTime.DoseTimeZone.Local));

        DoseDateTime localMay1st20171amBST_2 = new DoseDateTime(may1st2017Text, DoseDateTime.DoseTimeZone.Local);
        assertEquals("2017-05-01 00:00:00", localMay1st20171amBST_2.GetDateTimeText(DoseDateTime.DoseTimeZone.UTC));
        assertEquals("2017-05-01 01:00:00", localMay1st20171amBST_2.GetDateTimeText(DoseDateTime.DoseTimeZone.Local));

        // Constructor 30 April May 2017 23:00 (UTC)
        DoseDateTime utcMay1st20171amUTC = new DoseDateTime(dtMay1st2017BST, DoseDateTime.DoseTimeZone.UTC);
        assertEquals("2017-05-01 01:00:00", utcMay1st20171amUTC.GetDateTimeText(DoseDateTime.DoseTimeZone.UTC));
        assertEquals("2017-05-01 02:00:00", utcMay1st20171amUTC.GetDateTimeText(DoseDateTime.DoseTimeZone.Local));

        DoseDateTime utcMay1st20171amUTC_2 = new DoseDateTime(may1st2017Text, DoseDateTime.DoseTimeZone.UTC);
        assertEquals("2017-05-01 01:00:00", utcMay1st20171amUTC_2.GetDateTimeText(DoseDateTime.DoseTimeZone.UTC));
        assertEquals("2017-05-01 02:00:00", utcMay1st20171amUTC_2.GetDateTimeText(DoseDateTime.DoseTimeZone.Local));
    }

    @Test
    public void DoseDateTime_text() throws Exception {
        String time1Text = "2011-07-20 02:30:02";
        DoseDateTime time1 = new DoseDateTime(time1Text, DoseDateTime.DoseTimeZone.Local);
        assertEquals("2:30am", time1.GetHourMinuteText(DoseDateTime.DoseTimeZone.Local));
        assertEquals(time1Text, time1.GetDateTimeText(DoseDateTime.DoseTimeZone.Local));
        String time2Text = "1977-01-01 15:43:44";
        DoseDateTime time2 = new DoseDateTime(time2Text, DoseDateTime.DoseTimeZone.Local);
        assertEquals("3:43pm", time2.GetHourMinuteText(DoseDateTime.DoseTimeZone.Local));
        assertEquals("3pm", time2.GetHourText(DoseDateTime.DoseTimeZone.Local));
        assertEquals(time2Text, time2.GetDateTimeText(DoseDateTime.DoseTimeZone.Local));
    }
}