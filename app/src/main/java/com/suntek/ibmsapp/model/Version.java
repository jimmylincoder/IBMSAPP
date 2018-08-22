package com.suntek.ibmsapp.model;

import java.io.Serializable;
import java.util.Map;

/**
 * 版本信息
 *
 * @author jimmy
 */
public class Version implements Serializable
{
    private String isUpdate;

    private String versionNum;

    private String updateContent;

    private String downloadAddress;

    public String getIsUpdate()
    {
        return isUpdate;
    }

    public void setIsUpdate(String isUpdate)
    {
        this.isUpdate = isUpdate;
    }

    public String getVersionNum()
    {
        return versionNum;
    }

    public void setVersionNum(String versionNum)
    {
        this.versionNum = versionNum;
    }

    public String getUpdateContent()
    {
        return updateContent;
    }

    public void setUpdateContent(String updateContent)
    {
        this.updateContent = updateContent;
    }

    public String getDownloadAddress()
    {
        return downloadAddress;
    }

    public void setDownloadAddress(String downloadAddress)
    {
        this.downloadAddress = downloadAddress;
    }

    public static Version generateByJson(Map<String, Object> content)
    {
        Version version = new Version();
        version.setIsUpdate((String) content.get("is_update"));
        version.setDownloadAddress((String) content.get("download_address"));
        version.setVersionNum((String) content.get("version_num"));
        version.setUpdateContent((String) content.get("update_context"));

        return version;
    }
}
