package com.suntek.ibmsapp.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期处理类
 *
 * @author jimmy
 */
public class DateUtil
{
    public static String convertByFormat(Date date, String format)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    public static String convertByFormat(long time, String format)
    {
        Date date = new Date(time);
        return convertByFormat(date, format);
    }

    public static Date convertToDate(String date, String format)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try
        {
            return simpleDateFormat.parse(date);
        } catch (ParseException e)
        {
            return null;
        }
    }

    /**
     * 将Date转成 yyyy-MM-dd格式
     *
     * @param date
     * @return
     */
    public static String convertYYYY_MM_DD(Date date)
    {
        return convertByFormat(date, "yyyy-MM-dd");
    }

    /**
     * 将date转成 yyyy-MM-dd HH:mm:ss
     *
     * @param date
     * @return
     */
    public static String convertYYYY_MM_DD_HH_MM_SS(Date date)
    {
        return convertByFormat(date, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 将date转成 HH:mm:ss
     *
     * @param date
     * @return
     */
    public static String convertHH_MM_SS(Date date)
    {
        return convertByFormat(date, "HH:mm:ss");
    }

    /**
     * 将date转成 HH:mm
     *
     * @param date
     * @return
     */
    public static String convertHH_MM(Date date)
    {
        return convertByFormat(date, "HH:mm");
    }

    /**
     * 将字符串日期转long
     *
     * @param date
     * @return
     */
    public static long convertToLong(String date, String format)
    {
        return convertToDate(date, format).getTime();
    }
}
