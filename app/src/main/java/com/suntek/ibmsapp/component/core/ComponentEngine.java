package com.suntek.ibmsapp.component.core;

import android.app.Application;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import dalvik.system.DexFile;

/**
 * Component Engine
 *
 * @author jimmy
 */
public class ComponentEngine implements Serializable
{
    private static Map<String, Object> componentMap;
    public static Properties properties;
    static
    {

    }

    public static void init(Application application)
    {
        componentMap = new HashMap<>();

        try
        {
            InputStream inputStream1=application.getAssets().open("config.properties");
            properties = new Properties();
            properties.load(inputStream1);

        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * 初始化所有组件
     *
     */
    public static void initialLoad(Application application)
    {
        try {
            DexFile df = new DexFile(application.getPackageResourcePath());
            Enumeration<String> n=df.entries();
            while(n.hasMoreElements()){
                String className=n.nextElement().toString();
                if(className.indexOf("com.suntek.ibmsapp.component.httpclient")!=-1)
                {
                    Class clazz = Class.forName(className); //b

                    Object object=ComponentEngine.getInstance(clazz);
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 获取组件单例
     *
     * @param clazz 组件类
     * @return 组件单例
     */
    @SuppressWarnings("unchecked")
    public static Object getInstance(Class<?> clazz)
    {
        String key=clazz.getName();
        if(!clazz.getSuperclass().getName().equals(BaseComponent.class.getName()))
        {
            key=clazz.getSuperclass().getName();
        }

        if (!componentMap.containsKey(key))
        {
            try
            {
                Log.d("F4SDK", "ComponentEngine: create new component: " + clazz.getName());
                Object instance = (Object) clazz.newInstance();
                if(instance instanceof BaseComponent)
                {
                    BaseComponent baseComponent=(BaseComponent)instance;
                    if(baseComponent.isExtends())
                    {
                        key=clazz.getName();
                    }
                    componentMap.put(key, baseComponent);
                }

            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        else if(componentMap.containsKey(clazz.getName()))
        {
            return componentMap.get(clazz.getName());
        }
        return componentMap.get(key);
    }

    /**
     * 清除组件
     */
    public static void clean()
    {
        List<String> willRemoveClasses = new ArrayList<>();

        for (Map.Entry<String, Object> entry : componentMap.entrySet())
        {
            BaseComponent component = (BaseComponent) entry.getValue();
            if (component.cleanable())
            {
                willRemoveClasses.add(entry.getKey());
            }
        }

        for (String clazzStr : willRemoveClasses)
        {
            componentMap.remove(clazzStr);
        }
    }

    /**
     * 绑定服务组件
     *
     * @param target 目标
     */
    @SuppressWarnings("unchecked")
    public static void bind(Object target)
    {
        try
        {
            Field[] fields = target.getClass().getDeclaredFields();
            for (Field field : fields)
            {
                Autowired autowired = field.getAnnotation(Autowired.class);
                if (autowired != null)
                {
                    Class<? extends BaseComponent> key =
                            (Class<? extends BaseComponent>) field.getType();
                    BaseComponent component = (BaseComponent) ComponentEngine.getInstance(key);
                    field.setAccessible(true);
                    field.set(target, component);
                }

                Config config = field.getAnnotation(Config.class);
                if (config != null)
                {
                    String key = config.value();
                    field.setAccessible(true);
                    field.set(target,configDateType(field.getType(),key));
                }
            }
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 注解配置文件，转换格式
     *
     * @param fieldType 要注解的类型格式
     * @param key 赋值的key
     * @return
     */
    private static Object configDateType(Class fieldType, String key)
    {
        if(properties==null || properties.get(key)==null)
        {
            return "";
        }
        if(fieldType==boolean.class)
        {
            return Boolean.parseBoolean(properties.get(key).toString());
        }
        if(fieldType==int.class)
        {
            return Integer.parseInt(properties.get(key).toString());
        }
        if(fieldType==double.class)
        {
            return Double.parseDouble(properties.get(key).toString());
        }
        if(fieldType==float.class)
        {
            return Float.parseFloat(properties.get(key).toString());
        }

        return properties.get(key).toString();

    }

}
