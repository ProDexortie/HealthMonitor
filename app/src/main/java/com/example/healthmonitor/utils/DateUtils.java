package com.example.healthmonitor.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    private static final SimpleDateFormat DATE_FORMAT_WITH_DAY = new SimpleDateFormat("EEE, dd MMM", Locale.getDefault());
    private static final SimpleDateFormat DATE_FORMAT_FULL = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());

    // Получение текущей даты в формате "dd.MM.yyyy"
    public static String getCurrentDate() {
        Date date = new Date();
        return DATE_FORMAT.format(date);
    }

    // Получение текущей даты в полном формате "dd MMMM yyyy"
    public static String getCurrentDateFull() {
        Date date = new Date();
        return DATE_FORMAT_FULL.format(date);
    }

    // Получение даты в формате "dd.MM.yyyy" для указанного смещения от текущей даты (в днях)
    public static String getDateWithOffset(String baseDate, int daysOffset) {
        try {
            Date date = DATE_FORMAT.parse(baseDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_YEAR, daysOffset);
            return DATE_FORMAT.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return getDateWithOffset(daysOffset);
        }
    }

    // Форматирование даты с днем недели "EEE, dd MMM"
    public static String formatDateWithDay(String dateString) {
        try {
            Date date = DATE_FORMAT.parse(dateString);
            return DATE_FORMAT_WITH_DAY.format(date);
        } catch (ParseException e) {
            return dateString;
        }
    }

    // Форматирование даты в полный формат "dd MMMM yyyy"
    public static String formatDateFull(String dateString) {
        try {
            Date date = DATE_FORMAT.parse(dateString);
            return DATE_FORMAT_FULL.format(date);
        } catch (ParseException e) {
            return dateString;
        }
    }

    // Получение даты начала текущей недели в формате "dd.MM.yyyy"
    public static String getStartOfWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        return DATE_FORMAT.format(calendar.getTime());
    }

    // Получение даты конца текущей недели в формате "dd.MM.yyyy"
    public static String getEndOfWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        calendar.add(Calendar.DAY_OF_YEAR, 6);
        return DATE_FORMAT.format(calendar.getTime());
    }

    // Получение даты начала текущего месяца в формате "dd.MM.yyyy"
    public static String getStartOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return DATE_FORMAT.format(calendar.getTime());
    }

    // Получение даты конца текущего месяца в формате "dd.MM.yyyy"
    public static String getEndOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return DATE_FORMAT.format(calendar.getTime());
    }

    // Сравнение двух дат
    public static int compareDates(String date1, String date2) {
        try {
            Date d1 = DATE_FORMAT.parse(date1);
            Date d2 = DATE_FORMAT.parse(date2);
            return d1.compareTo(d2);
        } catch (ParseException e) {
            return 0;
        }
    }

    // Получение массива дат для последних n дней
    public static String[] getLastNDays(int n) {
        String[] dates = new String[n];
        for (int i = 0; i < n; i++) {
            dates[i] = getDateWithOffset(-i);
        }
        return dates;
    }
}