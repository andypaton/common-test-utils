package com.alien.utils.core.date;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateHelper {

    public static String DD_MM_YY_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    public String dateAsString(Date date) {
        DateFormat df = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        return df.format(date);
    }
    
    public String dateAsString(Date date, String format) {
        if (date != null){
            DateFormat df = new SimpleDateFormat(format);
            return df.format(date);
        }else{
            return null;
        }
    }
    
    public Date getDatePlusDays(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, days);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }
    
    public String dateTruncatedToNearest10Minutes(Date date) {

        return dateAsString(date, "yyyyMMddHHmm").substring(0, 11);
    }
    
    public String getDayOfMonthSuffix(final int n) {
        if (n >= 11 && n <= 13) {
            return "th";
        }
        switch (n % 10) {
            case 1:  return "st";
            case 2:  return "nd";
            case 3:  return "rd";
            default: return "th";
        }
    }
    
    public String formatDateForOm(Date date){
        
        if (date == null) {return null;}
        String day=dateAsString(date, "dd");
        String month=dateAsString(date, "MMMM");
        
        return day + getDayOfMonthSuffix(Integer.valueOf(day)) + " " + month;
    }
    
    
    public static Date getDateFromString(String date, String format){
        
        Date result = null;
     
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        
        try {
            result  = formatter.parse(date);
        } catch (ParseException e) {
            System.err.println("error parsing date + " + e.getMessage());
        }
        
        return result;
    }

}
