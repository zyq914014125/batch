package com.batch.start.util;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 类型转换工具类
 * @author zhang yueqian
 * @date 2022-7-4 15:16
 */
public  class TypeChangeUtil {

    private TypeChangeUtil() {
    }

    public static  <T> T changeValueType(Object value, Class<T> c)  {
        if(c.isInstance(value)){
            return c.cast(value);
        }else if(c.equals(String.class)){
            return c.cast(value.toString());
        }
        else if(c.equals(BigDecimal.class)){
            return c.cast(new BigDecimal(value.toString()));
        }else if(c.equals(Date.class)){
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Date parse=null;
            try{
                parse = formatter.parse(value.toString());
            }catch (ParseException e){
                parse=new Date();
            }
            return c.cast(parse);
        }
        return null;
    }
}
