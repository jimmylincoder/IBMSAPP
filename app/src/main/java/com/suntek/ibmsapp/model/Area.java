package com.suntek.ibmsapp.model;

import java.io.Serializable;
import java.util.Map;

/**
 *  地域
 *
 *  @author jimmy
 */
public class Area implements Serializable
{
    //区域id
    private String id;

    //区域名称
    private String name;

    //排序顺序
    private String parentId;

    //节点层级
    private String nodeLevel;

    //机构号
    private String ogrCode;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getParentId()
    {
        return parentId;
    }

    public void setParentId(String parentId)
    {
        this.parentId = parentId;
    }

    public String getNodeLevel()
    {
        return nodeLevel;
    }

    public void setNodeLevel(String nodeLevel)
    {
        this.nodeLevel = nodeLevel;
    }

    public String getOgrCode()
    {
        return ogrCode;
    }

    public void setOgrCode(String ogrCode)
    {
        this.ogrCode = ogrCode;
    }

    public static Area generateByJson(Map<String,Object> content)
    {
        Area area = new Area();
        area.setId((String) content.get("id"));
        area.setName((String) content.get("name"));
        area.setParentId((String) content.get("parent_id"));
        area.setNodeLevel((String) content.get("node_level"));
        area.setOgrCode((String) content.get("org_code"));

        return area;
    }
}
