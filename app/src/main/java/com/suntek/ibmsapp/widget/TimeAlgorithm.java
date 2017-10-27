package com.suntek.ibmsapp.widget;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 时间算法对象
 *
 * @author jimmy
 */
public class TimeAlgorithm
{
    //当前时间字符串(不包含日期)
    private String mTime;

    public TimeAlgorithm(String _mTime)
    {
        mTime = _mTime;
    }

    /**
     * 加上或减去秒数
     *
     * @param _sec
     * @return
     */
    public TimeAlgorithm addOrSub(int _sec)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try
        {
            Date date = sdf.parse("2017-07-30 " + mTime);
            Calendar calendarObj = Calendar.getInstance();
            calendarObj.setTime(date);
            calendarObj.add(Calendar.SECOND, _sec);
            SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");
            TimeAlgorithm mt = new TimeAlgorithm(sdf2.format(calendarObj
                    .getTime()));
            return mt;
        } catch (ParseException e)
        {
            return null;
        }

    }

    /**
     * 当前时间值取余数
     *
     * @param _timeInterval 秒数
     * @return
     */
    public int mod(int _timeInterval)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        try
        {
            date = sdf.parse("2015-07-07 " + mTime);
            Calendar calendarObj = Calendar.getInstance(Locale.CHINA);
            calendarObj.setTime(date);
            //获取分钟数
            int m = calendarObj.get(Calendar.MINUTE);
            //获取秒数
            int s = calendarObj.get(Calendar.SECOND);
            return (60 * m + s) % _timeInterval;
        } catch (ParseException e)
        {
            return -1;
        }

    }

    /**
     * 当前时间值取余数
     *
     * @param _timeInterval 秒数
     * @return
     */
    public int modWithHour(int _timeInterval)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        try
        {
            date = sdf.parse("2015-07-07 " + mTime);
            Calendar calendarObj = Calendar.getInstance(Locale.CHINA);
            calendarObj.setTime(date);
            int h = calendarObj.get(Calendar.HOUR);
            //获取分钟数
            int m = calendarObj.get(Calendar.MINUTE);
            //获取秒数
            int s = calendarObj.get(Calendar.SECOND);
            return (h * 60 * 60 + 60 * m + s) % _timeInterval;
        } catch (ParseException e)
        {
            return -1;
        }

    }

    public long compareTime(TimeAlgorithm _mt)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        try
        {
            date = sdf.parse("2015-07-07 " + mTime);
            Calendar calendarObj = Calendar.getInstance();
            calendarObj.setTime(date);
            Long d = calendarObj.getTimeInMillis();
            date = sdf.parse("2015-07-07 " + _mt.mTime);
            calendarObj.setTime(date);
            Long _d = calendarObj.getTimeInMillis();
            return d - _d;
        } catch (ParseException e)
        {
            return 111111;
        }

    }

    public String getData()
    {
        return mTime;
    }

    public long getSec(String _date)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        try
        {
            date = sdf.parse(_date + " " + mTime);
            Calendar calendarObj = Calendar.getInstance();
            calendarObj.setTime(date);
            Long d = calendarObj.getTimeInMillis() / 1000;
            return d;
        } catch (ParseException e)
        {
            return -1;
        }
    }
}
