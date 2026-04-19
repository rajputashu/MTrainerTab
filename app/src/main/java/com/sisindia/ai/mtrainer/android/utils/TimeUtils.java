package com.sisindia.ai.mtrainer.android.utils;

import android.text.TextUtils;

import org.threeten.bp.Clock;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeUtils {

    public static String DASHBOARD_DD_MMM = "'DashBoard' dd MMM";
    //    public static String DD_MM_YYYY = "dd/mm/yyyy";
    public static String CREATE_TASK_DATE = "dd MMM'' yyyy";
    public static String CREATE_TASK_TIME = "hh:mm a";

    public static String YEAR = "YYYY";
    public static String MONTH = "MMMM";
    public static String MONTHNUM = "MM";

    public static int getYear() {
        return Integer.parseInt(ZonedDateTime.now().format(DateTimeFormatter.ofPattern(YEAR)));
    }

    public static String getMonth() {
        return ZonedDateTime.now().format(DateTimeFormatter.ofPattern(MONTH));
    }

    public static int getMonthNum() {
        return Integer.parseInt(ZonedDateTime.now().format(DateTimeFormatter.ofPattern(MONTHNUM)));
    }


    public static String getCurrentDateForRotaScreen() {
        return ZonedDateTime.now().format(DateTimeFormatter.ofPattern(DASHBOARD_DD_MMM));
    }

    public static long getCurrentDateTimeInMilli() {
        return ZonedDateTime.now().toLocalDateTime().toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    public static LocalDateTime toLocal(LocalDateTime utcDateTime) {
        return ZonedDateTime.ofInstant(utcDateTime.atZone(ZoneOffset.UTC).toInstant(), ZoneId.systemDefault()).toLocalDateTime();
    }


    /**
     * @param type If true: local to utc/If false: utc to local
     */
    private static String doTimeConversion(String time, boolean type) {
        ZoneId localZone = ZoneId.systemDefault();
        LocalTime lt = LocalTime.parse(time);
        LocalDateTime ldt = LocalDate.now(localZone).atTime(lt);
        ZonedDateTime resultTime;
        if (type) {
            resultTime = ldt.atZone(localZone).withZoneSameInstant(ZoneOffset.UTC);
        } else {
            resultTime = ldt.atOffset(ZoneOffset.UTC).atZoneSameInstant(localZone);
        }
        LocalTime newTime = resultTime.toLocalTime();
        return newTime.toString();
    }

    public static Boolean compareDateWithCurrentDate(String lastLaunchDate) {
        if (TextUtils.isEmpty(lastLaunchDate))
            return false;

        LocalDateTime localDate = LocalDateTime.parse(lastLaunchDate, DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime currentDate = LocalDateTime.now();

        return localDate.getMonth().name().equals(currentDate.getMonth().name())
                && localDate.getDayOfMonth() == currentDate.getDayOfMonth()
                && localDate.getYear() == currentDate.getYear();
    }


    public static long getCurrentEpochUTC() {
        return Instant.now(Clock.systemUTC()).toEpochMilli();
    }

    public static String deviceDateTimeString(long epochMilliUtc) {
        Instant instant = Instant.ofEpochMilli(epochMilliUtc);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy, hh:mm:ss a", Locale.US).withZone(ZoneId.systemDefault());
        return formatter.format(instant);
    }

    public static String uTCDateTimeString(long epochMilliUtc) {
        Instant instant = Instant.ofEpochMilli(epochMilliUtc);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy, hh:mm:ss a", Locale.US).withZone(ZoneId.of("UTC"));
        return formatter.format(instant);

    }

    public static long convertDateStringToLongUTC(String stringUTCDate, DateTimeFormatter inputDateFormat) {
        LocalDateTime localDate = LocalDateTime.parse(stringUTCDate, inputDateFormat);
        long timeInMilliseconds = localDate.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli();
        return timeInMilliseconds;
    }


    public static String convertIntSecondsToHHMMSS(int sec) {
        Date d = new Date(sec * 1000L);
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss"); // HH for 0-23
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(d);
    }

    public static String convertIntSecondsToMM(int sec) {
        // Date d = new Date(sec * 1000L);
        Date d = new Date();
        // SimpleDateFormat df = new SimpleDateFormat("mm"); // HH for 0-23
        SimpleDateFormat df = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
        // df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(d);
    }

    public static LocalDateTime getLocalDateTimeFromUtcStr(String utcStrDate) {
        LocalDateTime dateTime = LocalDateTime.parse(utcStrDate, DateTimeFormatter.ISO_DATE_TIME);
        return toLocal(dateTime);
    }

    public static LocalDateTime toUtc(final LocalDateTime time, final ZoneId fromZone) {
        return toZone(time, fromZone, ZoneOffset.UTC);
    }

    public static LocalDateTime toUtc(final LocalDateTime time) {
        return toUtc(time, ZoneId.systemDefault());
    }

    public static LocalDateTime toZone(final LocalDateTime time, final ZoneId fromZone, final ZoneId toZone) {
        final ZonedDateTime zonedtime = time.atZone(fromZone);
        final ZonedDateTime converted = zonedtime.withZoneSameInstant(toZone);
        return converted.toLocalDateTime();
    }


    public static String TASK_DATE_FORMAT(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern(CREATE_TASK_DATE));
    }

    public static String TASK_TIME_FORMAT(LocalTime time) {
        return time.format(DateTimeFormatter.ofPattern(CREATE_TASK_TIME));
    }


    public static boolean isNewTaskInBetweenTwoLocalDateTime(LocalDateTime newStart, LocalDateTime newEnd,
                                                             LocalDateTime oldStart, LocalDateTime oldEnd) {

        if (newStart.toLocalDate().getMonth().equals(oldStart.toLocalDate().getMonth()) &&
                newStart.toLocalDate().getDayOfMonth() == (oldStart.toLocalDate().getDayOfMonth())) {

            if (newStart.toLocalTime().equals(oldStart.toLocalTime())) { // same hour and same minutes
                return true;
            }
            if ((newStart.isAfter(oldStart) && newStart.isBefore(oldEnd)) || (oldStart.isAfter(newStart) && oldStart.isBefore(newEnd))) {
                return true;
            }
            return (newEnd.isAfter(oldStart) && newEnd.isBefore(oldEnd)) || (oldEnd.isAfter(newStart) && oldEnd.isBefore(newEnd));

        }
        return false;
    }

    public static String formatServerDate(String serverDate) {
        if (serverDate == null || serverDate.isEmpty()) return "";
        try {
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault());
            SimpleDateFormat output = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
            Date date = input.parse(serverDate);
            return date != null ? output.format(date) : "";
        } catch (ParseException e) {
            return serverDate;
        }
    }

}
