package com.suntek.ibmsapp.util;

import com.suntek.ibmsapp.model.RecordItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 历史记录数据处理
 *
 * @author jimmy
 */
public class RecordHander
{
    public static Map<String, List<RecordItem>> handleRecordList(List<RecordItem> recordItems)
    {
        Map<String, List<RecordItem>> recordMap = new HashMap<>();
        //当前时间
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (RecordItem recordItem : recordItems)
        {
            String bt = format1.format(recordItem.getStartTime());
            String et = format1.format(recordItem.getEndTime());
            String[] beginStrs = bt.split(" ");
            String[] endStrs = et.split(" ");
            String beginDate = beginStrs[0];
            String endDate1 = endStrs[0];
            List<RecordItem> recordItems1 = (List<RecordItem>) recordMap.get(beginDate);
            List<RecordItem> recordItems2 = (List<RecordItem>) recordMap.get(endDate1);
            if (recordItems1 == null)
                recordItems1 = new ArrayList<RecordItem>();
            if (recordItems2 == null)
                recordItems2 = new ArrayList<RecordItem>();

            if (!beginDate.equals(endDate1))
            {
                RecordItem recordItem1 = new RecordItem();
                RecordItem recordItem2 = new RecordItem();

                recordItem1.setDeviceId(recordItem.getDeviceId());
                recordItem1.setStartTime(recordItem.getStartTime());
                recordItem1.setEndTime(DateUtil.convertToLong(beginDate + " 23:59:59", "yyyy-MM-dd HH:mm:ss"));
                recordItems1.add(recordItem1);

                recordItem2.setDeviceId(recordItem.getDeviceId());
                recordItem2.setStartTime(DateUtil.convertToLong(endDate1 + " 00:00:00", "yyyy-MM-dd HH:mm:ss"));
                recordItem2.setEndTime(recordItem.getEndTime());
                recordItems2.add(recordItem2);

                recordMap.put(beginDate, recordItems1);
                recordMap.put(endDate1, recordItems2);
            }
            else
            {
                recordItems1.add(recordItem);
                recordMap.put(beginDate, recordItems1);
            }
        }
        return recordMap;
    }

    /**
     * 获取录像最早时间录像
     *
     * @param recordItems
     */
    public static RecordItem getEarliestTime(List<RecordItem> recordItems)
    {
        RecordItem recordItemTemp = new RecordItem();
        if (recordItems == null)
            return recordItemTemp;
        recordItemTemp.setStartTime(0);
        for (RecordItem recordItem : recordItems)
        {
            long bt = recordItem.getStartTime();
            if (recordItemTemp.getStartTime() == 0)
                recordItemTemp = recordItem;

            if (bt < recordItemTemp.getStartTime())
            {
                recordItemTemp = recordItem;
            }
        }

        return recordItemTemp;
    }

}



