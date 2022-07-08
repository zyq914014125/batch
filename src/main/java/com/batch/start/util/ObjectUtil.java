package com.batch.start.util;

import org.reflections.Reflections;

import java.util.Set;

/**
 * 对象获取工具类
 * @author zhang yueqian
 * @date 2022-7-7 18:05
 */

public class ObjectUtil {
    private ObjectUtil() {
    }

    public static Object getObject(String packagePath, String tableName) throws IllegalAccessException, InstantiationException {
        Reflections reflections=new Reflections(packagePath);
        Set<Class<?>> className=reflections.getTypesAnnotatedWith(BatchTable.class);
        for (Class<?> c :className) {
            BatchTable batchTable = c.getAnnotation(BatchTable.class);
            if(tableName.equals(batchTable.value())){
                return  c.newInstance();
            }
        }
            return null;
    }



}
