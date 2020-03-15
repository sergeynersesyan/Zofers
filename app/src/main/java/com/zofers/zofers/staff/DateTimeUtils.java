

package com.zofers.zofers.staff;

import android.content.Context;
import android.content.res.Resources;

import com.zofers.zofers.R;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.Nullable;

public class DateTimeUtils {
    private static final int SECONDS_IN_MINUTE = 60;
    private static final int SECONDS_IN_HOUR = 3600;
    private static final int SECONDS_IN_DAY = 86400; // 24 * 60 * 60 seconds
    private static final int SECONDS_IN_MONTH = SECONDS_IN_DAY * 30;
    private static final int SECONDS_IN_YEAR = SECONDS_IN_DAY * 365;


    public static String getDateString(Date date, Context context) {
        long dateMillis = date.getTime();
        long currentMillis = System.currentTimeMillis();

        int differenceSeconds = (int) ((currentMillis - dateMillis) / 1000);

        String dateText;
        Resources resources = context.getResources();

        if (differenceSeconds < SECONDS_IN_DAY) {
            return getRelativeDateString(differenceSeconds, context);
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            dateText = String.format(resources.getString(R.string.date_format_day), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.YEAR));
        }
        return dateText;
    }

    public static String getRelativeDateString(Date date, boolean ignoreNegatives, Context context) {
        long dateMillis = date.getTime();
        long currentMillis = System.currentTimeMillis();

        int differenceSeconds = (int) ((currentMillis - dateMillis) / 1000);

        if (ignoreNegatives && differenceSeconds < 0) {
            differenceSeconds = -differenceSeconds;
        }

        return getRelativeDateString(differenceSeconds, context);
    }

    public static String getSmartDate(Date date, boolean isShort) {
        long dateMillis = date.getTime();
        long currentMillis = System.currentTimeMillis();

        int differenceSeconds = (int) ((currentMillis - dateMillis) / 1000);

        DateFormat dateFormat;
        Calendar cal = Calendar.getInstance();
        int todayTimeInSeconds = cal.get(Calendar.HOUR_OF_DAY) * 60 * 60 + cal.get(Calendar.MINUTE) * 60 + cal.get(Calendar.SECOND);
        if (differenceSeconds < todayTimeInSeconds) {
            dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
            return dateFormat.format(date);
        } else {
            if (isShort) {
                dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT);
                return dateFormat.format(date).replace(", " + cal.get(Calendar.YEAR), "");
            } else {
                dateFormat = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT);
                return dateFormat.format(date).replace(", " + cal.get(Calendar.YEAR), " at");
            }
        }
    }

    private static String getRelativeDateString(int differenceSeconds, Context context) {
        String dateText;
        Resources resources = context.getResources();

        if (differenceSeconds < SECONDS_IN_MINUTE) {
            dateText = resources.getString(R.string.date_format_now);
        } else if (differenceSeconds < SECONDS_IN_HOUR) {
            int minutes = differenceSeconds / SECONDS_IN_MINUTE;
            dateText = resources.getQuantityString(R.plurals.date_format_relative_minutes, minutes, minutes);
        } else if (differenceSeconds < SECONDS_IN_DAY) {
            int hours = differenceSeconds / SECONDS_IN_HOUR;
            dateText = resources.getQuantityString(R.plurals.date_format_relative_hours, hours, hours);
        } else if (differenceSeconds < SECONDS_IN_MONTH) {
            int days = differenceSeconds / SECONDS_IN_DAY;
            dateText = resources.getQuantityString(R.plurals.date_format_relative_days, days, days);
        } else if (differenceSeconds < SECONDS_IN_YEAR) {
            int months = differenceSeconds / SECONDS_IN_MONTH;
            dateText = resources.getQuantityString(R.plurals.date_format_relative_mons, months, months);
        } else {
            int years = differenceSeconds / SECONDS_IN_YEAR;
            dateText = resources.getQuantityString(R.plurals.date_format_relative_years, years, years);
        }

        return dateText;
    }

    public static int compareDates(@Nullable Date d1, @Nullable Date d2) {
        if (d1 == null) {
            return d2 == null ? 0 : 1;
        } else if (d2 == null) {
            return -1;
        }
        return d1.before(d2) ? 1 : -1;
    }
}