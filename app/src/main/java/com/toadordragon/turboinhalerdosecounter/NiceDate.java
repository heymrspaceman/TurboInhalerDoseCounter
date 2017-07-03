package com.toadordragon.turboinhalerdosecounter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by thomas on 06-Mar-17.
 */

public class NiceDate {

    public NiceDate(String dateTimeText)
    {
        m_dateTimeText = dateTimeText;
    }

    public static String ConvertToBSTString(String dateText)
    {
        String convertedDateText = dateText;

        try
        {
            SimpleDateFormat formatterIn = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            formatterIn.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = formatterIn.parse(dateText);
            formatterIn.setTimeZone(TimeZone.getTimeZone("Europe/London")); // Apparently the same as BST
            convertedDateText = formatterIn.format(date);
        }
        catch (ParseException ex)
        {
            // TODO handle this better
        }

        return convertedDateText;
    }

    public static Date ConvertToBST(Date utcDate)
    {
        Date convertedDate = utcDate;
        try
        {
            SimpleDateFormat formatterIn = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            formatterIn.setTimeZone(TimeZone.getTimeZone("Europe/London")); // Apparently the same as BST
            String bstAdjustedTimeText = formatterIn.format(utcDate);

            formatterIn.setTimeZone(TimeZone.getTimeZone("UTC"));
            Calendar dateCal = Calendar.getInstance();
            dateCal.setTime(formatterIn.parse(bstAdjustedTimeText));
            convertedDate = dateCal.getTime();
        }
        catch (ParseException ex)
        {
            // TODO handle this better
        }

        return convertedDate;
    }

    public String NiceFormat()
    {
        String timeText = "";
        String dateText = "";
        try
        {
            String niceDate = "";

            Calendar dateCal = Calendar.getInstance();
            Calendar todayCal = Calendar.getInstance();
            Calendar yesterdayCal = Calendar.getInstance();
            yesterdayCal.add(Calendar.DATE, -1);

            SimpleDateFormat formatterIn = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateCal.setTime(formatterIn.parse(ConvertToBSTString(m_dateTimeText)));
            int hour = dateCal.get(Calendar.HOUR_OF_DAY);

            if ((hour >= 0) && (hour <=12)) {
                timeText = String.format("%d:%02dam", hour, dateCal.get(Calendar.MINUTE));
            }
            else  {
                timeText = String.format("%d:%02dpm", hour - 12, dateCal.get(Calendar.MINUTE));
            }

            if ((dateCal.DAY_OF_YEAR == todayCal.DAY_OF_YEAR) && (dateCal.YEAR == todayCal.YEAR)) {
                dateText = "today";
            }
            else if ((dateCal.DAY_OF_YEAR == yesterdayCal.DAY_OF_YEAR) && (dateCal.YEAR == yesterdayCal.YEAR)) {
                dateText = "yesterday";
            }
            else {
                SimpleDateFormat formatterOut = new SimpleDateFormat("d MMMM");
                dateText = formatterOut.format(dateCal.getTime());
            }
        }
        catch (ParseException ex)
        {
        }

        return dateText + " " + timeText;
    }

    String m_dateTimeText;
}
