package com.suntek.ibmsapp.component;

/**
 * 分页
 *
 * @author jimmy
 */
public class Page<T>
{
    private int totalPage;

    private T data;

    public int getTotalPage()
    {
        return totalPage;
    }

    public void setTotalPage(int totalPage)
    {
        this.totalPage = totalPage;
    }

    public T getData()
    {
        return data;
    }

    public void setData(T data)
    {
        this.data = data;
    }
}
