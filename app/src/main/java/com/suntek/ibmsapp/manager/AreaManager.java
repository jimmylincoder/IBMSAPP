package com.suntek.ibmsapp.manager;

import com.suntek.ibmsapp.component.HttpResponse;
import com.suntek.ibmsapp.component.IbmsHttpEngine;
import com.suntek.ibmsapp.component.core.Autowired;
import com.suntek.ibmsapp.component.core.BaseComponent;
import com.suntek.ibmsapp.component.http.FHttpException;
import com.suntek.ibmsapp.model.Area;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 区域管理类
 *
 * @author jimmy
 */
public class AreaManager extends BaseComponent
{
    @Autowired
    IbmsHttpEngine ibmsHttpEngine;

    /**
     * 获取地域列表
     *
     * @param parentId
     * @return
     */
    public List<Area> getAreaList(String parentId)
    {
        Map<String, Object> params = new HashMap<>();
        params.put("parent_id", parentId);

        HttpResponse response = ibmsHttpEngine.request("area.list", params);
        if (response.getCode() == HttpResponse.STATUS_SUCCESS)
        {
            List<Map<String, Object>> data = (List<Map<String, Object>>) response.getData().get("area_list");
            List<Area> areas = new ArrayList<>();
            for (Map<String, Object> map : data)
            {
                Area area = Area.generateByJson(map);
                areas.add(area);
            }
            return areas;
        }
        else
        {
            throw new FHttpException(FHttpException.CODE_BUSINESS_ERROR, response.getErrorMessage());
        }
    }

    /**
     * 获取根节点
     *
     * @return
     */
    public Area getRootArea()
    {
        Map<String, Object> params = new HashMap<>();

        HttpResponse response = ibmsHttpEngine.request("area.root", params);
        if (response.getCode() == HttpResponse.STATUS_SUCCESS)
        {
            Map<String, Object> data = (Map<String, Object>) response.getData().get("area");
            Area area = Area.generateByJson(data);
            return area;
        }
        else
        {
            throw new FHttpException(FHttpException.CODE_BUSINESS_ERROR, response.getErrorMessage());
        }
    }
}
