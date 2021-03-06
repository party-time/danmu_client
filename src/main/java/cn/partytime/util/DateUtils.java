package cn.partytime.util;

import org.omg.CORBA.PUBLIC_MEMBER;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by lENOVO on 2016/8/24.
 */
public class DateUtils {

    /**
     * 获取当前时间
     * @return 当前时间
     */
    public static Date getCurrentDate(){
        Date date = new Date();
        return  date;
    }

    /**
     * 给日期加上小时
     * @param date
     * @param hours
     * @return
     */
    public static Date addHoursToDate(Date date,int hours){
        Calendar ca=Calendar.getInstance();
        ca.setTime(date);
        ca.add(Calendar.HOUR_OF_DAY, hours);
        return ca.getTime();
    }


    public static long subMinute(Date minDate,Date maxDate){
        long between=(maxDate.getTime()-minDate.getTime())/1000;
        long minute=between/60;
        return minute;
    }

    public static long subHour(Date minDate,Date maxDate){
        long between=(maxDate.getTime()-minDate.getTime())/1000;
        long hour=between/60/60;
        return hour;
    }

    public static long subSecond(Date minDate,Date maxDate){
        long between=(maxDate.getTime()-minDate.getTime())/1000;
        return between;
    }

    /**
     * 在当前日期基础上减去某些天
     * @param beginDate
     * @param day
     * @return
     * @throws ParseException
     */
    public static Date DateMinusSomeDay(Date beginDate, int day) throws ParseException {
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        Calendar date = Calendar.getInstance();
        date.setTime(beginDate);
        date.set(Calendar.DATE, date.get(Calendar.DATE) - 1);
        Date endDate = dft.parse(dft.format(date.getTime()));
        return endDate;
    }



    /**
     * 把毫秒转化成日期
     * @param dateFormat(日期格式，例如：MM/ dd/yyyy HH:mm:ss)
     * @param millSec(毫秒数)
     * @return
     */
    public static String transferLongToDate(String dateFormat,Long millSec){
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date= new Date(millSec);
        return sdf.format(date);
    }


    /**
     * 日期格式化
     * @param time
     * @param formateStr
     * @return
     */
    public static String dateToString(Date time,String formateStr){
        SimpleDateFormat formatter;
        //"yyyy-MM-dd KK:mm:ss a"
        formatter = new SimpleDateFormat (formateStr);
        String ctime = formatter.format(time);
        return ctime;
    }



    public static Date strToDate(String str,String formatStr) {
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date transferLongToDate(Long millis) {

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        Date date = cal.getTime();
        return date;
    }

}
