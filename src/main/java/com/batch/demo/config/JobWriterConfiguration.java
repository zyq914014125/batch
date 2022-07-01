package com.batch.demo.config;

import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.lang.reflect.Field;

/**
 * @author zhang yueqian
 * @date 2022-6-27 15:41
 */
@Configuration
public class JobWriterConfiguration<T> {

    @Autowired
    public DataSource dataSource;

    public T t;


//    @Bean
//    public JdbcBatchItemWriter<User> userJdbcBatchItemWriter(){
//        JdbcBatchItemWriter<User> itemWriter=new JdbcBatchItemWriter<>();
//        itemWriter.setDataSource(dataSource);
//        itemWriter.setSql("INSERT INTO user (NAME,ID,PASSWORD) VALUES (:name,:id,:password)");
//        //设置参数值来源
//        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
//        return itemWriter;
//    }

    @Bean
    public  JdbcBatchItemWriter<T> userJdbcBatchItemWriterTest(){
        Field[] declaredFields = t.getClass().getDeclaredFields();
        String sql="INSERT INTO "+t.getClass().getSimpleName().toLowerCase();
        StringBuilder param=new StringBuilder("(");
        StringBuilder value=new StringBuilder("(");
        for ( Field declaredField:declaredFields) {
            String name = declaredField.getName();
            param.append(name).append(",");
            value.append(":").append(name).append(",");
        }
        param.deleteCharAt(param.length()-1).append(")");
        value.deleteCharAt(value.length()-1).append(")");
        JdbcBatchItemWriter<T> itemWriter=new JdbcBatchItemWriter<>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql(sql+param.toString()+" values "+value.toString());
        //设置参数值来源
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        return itemWriter;
    }
}
