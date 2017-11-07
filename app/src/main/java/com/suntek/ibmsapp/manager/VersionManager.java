package com.suntek.ibmsapp.manager;

import com.suntek.ibmsapp.component.HttpResponse;
import com.suntek.ibmsapp.component.IbmsHttpEngine;
import com.suntek.ibmsapp.component.core.Autowired;
import com.suntek.ibmsapp.component.core.BaseComponent;
import com.suntek.ibmsapp.component.http.FHttpException;
import com.suntek.ibmsapp.model.User;
import com.suntek.ibmsapp.model.Version;

import java.util.HashMap;
import java.util.Map;

/**
 * 版本管理
 *
 * @author jimmy
 */
public class VersionManager extends BaseComponent
{
    @Autowired
    IbmsHttpEngine ibmsHttpEngine;

    public Version checkUpdate(String versionNum)
    {
        Map<String, Object> params = new HashMap<>();
        params.put("version_num", versionNum);

        HttpResponse response = ibmsHttpEngine.request("app.version", params);

        if (response.getCode() == HttpResponse.STATUS_SUCCESS)
        {
            Map<String, Object> content = (Map<String, Object>) response.getData().get("version");
            Version version = Version.generateByJson(content);
            return version;
        }
        else
        {
            throw new FHttpException(FHttpException.CODE_BUSINESS_ERROR, response.getErrorMessage());
        }
    }
}
