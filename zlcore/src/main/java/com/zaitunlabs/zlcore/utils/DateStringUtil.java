package com.zaitunlabs.zlcore.utils;

import android.text.TextUtils;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by ahmad s on 3/8/2016.
 */

public class DateStringUtil {
    public static String convertDateToString(String toFormat, Date date, TimeZone timeZone, Locale locale){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        SimpleDateFormat sdf = new SimpleDateFormat(toFormat, (locale == null?Locale.getDefault():locale));
        sdf.setTimeZone(timeZone==null?TimeZone.getDefault():timeZone);
        return sdf.format(date);
    }

    public static String getDateTimeInString(Date date, TimeZone timeZone, Locale locale){
        if(date == null)return "no record";
        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();

        SimpleDateFormat sdf;
        if(compareToDay(date,today, locale)==0){
            //today
            sdf = new SimpleDateFormat("h:mm a", (locale == null?Locale.getDefault():locale));
            sdf.setTimeZone(timeZone==null?TimeZone.getDefault():timeZone);
            return sdf.format(date);
        }else if(compareToYear(date,today, locale)==0) {
            sdf = new SimpleDateFormat("MMM d h:mm a", (locale == null?Locale.getDefault():locale));
            sdf.setTimeZone(timeZone==null?TimeZone.getDefault():timeZone);
            return sdf.format(date);
        }else{
            //previuos year
            sdf = new SimpleDateFormat("MMM d yyyy", (locale == null?Locale.getDefault():locale));
            sdf.setTimeZone(timeZone==null?TimeZone.getDefault():timeZone);
            return sdf.format(date);
        }
    }

    public static String getDurationInString(long diffInSecond){
        long seconds = diffInSecond;

        long days = seconds / (60*60*24);
        long modulo1 = seconds % (60*60*24);
        long hours = modulo1 / (60*60);
        long modulo2 = modulo1 % (60*60);
        long minutes = modulo2 / 60;
        long modulo3 = modulo2 % 60;
        seconds = modulo3;


        String result = "";
        if(days > 0){
            result += days + " day" + (days==1?"":"s");
        }

        String hoursString = String.valueOf(hours);
        String minutesString = String.valueOf(minutes);
        String secondsString = String.valueOf(seconds);

        if(hoursString.length() == 1)hoursString = "0"+hoursString;
        if(minutesString.length() == 1)minutesString = "0"+minutesString;
        if(secondsString.length() == 1)secondsString = "0"+secondsString;

        result += (days>0?" ":"")+hoursString+":"+minutesString+":"+secondsString;
        return result;
    }

    public static String getDurationInString(Date startDate, Date endDate){
        long diff = endDate.getTime()-startDate.getTime();
        long seconds = diff / 1000;

        long days = seconds / (60*60*24);
        long modulo1 = seconds % (60*60*24);
        long hours = modulo1 / (60*60);
        long modulo2 = modulo1 % (60*60);
        long minutes = modulo2 / 60;
        long modulo3 = modulo2 % 60;
        seconds = modulo3;


        String result = "";
        if(days > 0){
            result += days + " day" + (days==1?"":"s");
        }

        String hoursString = String.valueOf(hours);
        String minutesString = String.valueOf(minutes);
        String secondsString = String.valueOf(seconds);

        if(hoursString.length() == 1)hoursString = "0"+hoursString;
        if(minutesString.length() == 1)minutesString = "0"+minutesString;
        if(secondsString.length() == 1)secondsString = "0"+secondsString;

        result += (days>0?" ":"")+hoursString+":"+minutesString+":"+secondsString;
        return result;
    }

    public static int compareToDay(Date date1, Date date2, Locale locale) {
        if (date1 == null || date2 == null) {
            return 0;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", (locale == null?Locale.getDefault():locale));
        return sdf.format(date1).compareTo(sdf.format(date2));
    }

    public static int compareToYear(Date date1, Date date2, Locale locale) {
        if (date1 == null || date2 == null) {
            return 0;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy", (locale == null?Locale.getDefault():locale));
        return sdf.format(date1).compareTo(sdf.format(date2));
    }

    public static Date getMessageDateWithDefaultNow(String messageDateInString, Locale locale) {
        Date messageDate = Calendar.getInstance().getTime();

        if(TextUtils.isEmpty(messageDateInString)){
            return messageDate;
        }

        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ", (locale == null?Locale.getDefault():locale));
        sf.setLenient(true);
        try {
            messageDate = sf.parse(messageDateInString);
        } catch (ParseException e) {
            //do nothing
            DebugUtil.logD("smart-x", "exception parse date");
        }
        return messageDate;
    }


    public static String getTimeRange(String start, String end) {
        if(TextUtils.isEmpty(start) || TextUtils.isEmpty(end)){
            return "";
        }

        String result = "";
        result += start.split(" ")[1];
        result += " - ";
        result += end.split(" ")[1];

        return result;
    }

    public static Date getDateFromString(String fromFormat, String dateString,  TimeZone timeZone, Locale locale){
        SimpleDateFormat sf = new SimpleDateFormat(fromFormat, (locale == null?Locale.getDefault():locale));
        sf.setLenient(true);
        sf.setTimeZone(timeZone==null?TimeZone.getDefault():timeZone);
        Date date = null;
        try {
            date = sf.parse(dateString);
        } catch (ParseException e) {
            //do nothing
            DebugUtil.logD("zlcore", "exception parse date");
        }
        return date;
    }

    public static String convertDate(String fromFormat, String toFormat, String dateString, TimeZone originTimeZone, TimeZone destinationTimeZone, Locale originLocale, Locale destinationLocale){
        Date date = getDateFromString(fromFormat,dateString, originTimeZone, originLocale);
        return convertDateToString(toFormat, date, destinationTimeZone, destinationLocale);
    }
}
