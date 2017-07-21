package com.suntek.ibmsapp.component.core;

import java.io.Serializable;

/**
 * Base Component
 *
 * @author jimmy
 */
public abstract class BaseComponent implements Serializable
{
    public BaseComponent()
    {
        ComponentEngine.bind(this);
    }
    //用与判断是否是多继承关系，默认为false
    private boolean isExtends=false;

    /**
     * 是否在内存告急时可清除(默认为true)
     *
     * @return 结果
     */
    public boolean cleanable()
    {
        return true;
    }

    public boolean isExtends()
    {
        return isExtends;
    }

    public void setIsExtends(boolean isExtends)
    {
        this.isExtends = isExtends;
    }
}
