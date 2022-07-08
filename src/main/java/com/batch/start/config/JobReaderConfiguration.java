package com.batch.start.config;


import com.batch.start.util.TypeChangeUtil;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * batch reader
 * @author zhang yueqian
 * @date 2022-6-27 15:22
 */
@Configuration
public class JobReaderConfiguration<T>  {

    T t;
    String path;

    @SuppressWarnings("unchecked")
    public  FlatFileItemReader<T> fileReader() {
        Class<T> tClass= (Class<T>) t.getClass();
        Field[] declaredFields = t.getClass().getDeclaredFields();
            Map<String, Class<?>> map=new LinkedHashMap<>();
            for (Field field:declaredFields){
                    String name =field.getName();
                    Class<?> type = field.getType();
                    map.put(name,type);
            }
            FlatFileItemReader<T> reader=new FlatFileItemReader<>();
            reader.setResource(new ClassPathResource(path));
            reader.setLinesToSkip(1);
            DefaultLineMapper<T> userDefaultLineMapper=new DefaultLineMapper<>();
            DelimitedLineTokenizer delimitedLineTokenizer=new DelimitedLineTokenizer();
            String[] names = map.keySet().toArray(new String[0]);
            delimitedLineTokenizer.setNames(names);
            userDefaultLineMapper.setFieldSetMapper(fieldSet->
                   setValues(fieldSet, map, tClass)
            );
            userDefaultLineMapper.setLineTokenizer(delimitedLineTokenizer);
            userDefaultLineMapper.afterPropertiesSet();
            reader.setLineMapper(userDefaultLineMapper);
            return reader;
    }

        private  T setValues(FieldSet fieldSet, Map<String, Class<?>> map, Class<T> tClass)  {
                Object o = null;
                Class<?> aClass = t.getClass();
                Class<? extends FieldSet> fieldSetClass = fieldSet.getClass();
            try {
                o = t.getClass().newInstance();
                for (Map.Entry<String, Class<?>> field : map.entrySet()) {
                    String key=  field.getKey();
                    String s =  key.substring(0, 1).toUpperCase()+key.substring(1);
                    Class<?> c = field.getValue();
                    Method amethod = aClass.getMethod("set" +s, c);
                    Object value = getValue(fieldSetClass, c, key, fieldSet);
                    amethod.invoke(o, TypeChangeUtil.changeValueType(value, c));
                }
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                            e.printStackTrace();
                }
                return tClass.cast(o);
        }



    Object getValue(Class<? extends FieldSet> fieldSetClass, Class<?> c, String key, FieldSet fieldSet) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method filedmethod;
        try {
            filedmethod = fieldSetClass.getMethod("read" + c.getSimpleName(), String.class);
        }catch (NoSuchMethodException e){
            filedmethod = fieldSetClass.getMethod("readString", String.class);
        }
      return  filedmethod.invoke(fieldSet, key);
    }




}
