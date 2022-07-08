package com.batch.start.config;

import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.StatementCreatorUtils;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhang yueqian
 * @date 2022-6-27 15:41
 */
@Configuration
public class  JobWriterConfiguration<T> {


    private DataSource dataSource;
    T t;

    @Autowired
    public JobWriterConfiguration(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public  JdbcBatchItemWriter<T> userJdbcBatchItemWriterTest(){
        Field[] declaredFields = t.getClass().getDeclaredFields();
        String sql="INSERT INTO "+t.getClass().getSimpleName().toLowerCase();
        StringBuilder param=new StringBuilder("(");
        StringBuilder value=new StringBuilder("(");
        List<Object> params=new ArrayList<>();
        for ( Field declaredField:declaredFields) {
            String name = declaredField.getName();
            params.add(name);
            param.append(name).append(",");
            value.append("?").append(",");
        }
        param.deleteCharAt(param.length()-1).append(")");
        value.deleteCharAt(value.length()-1).append(")");
        JdbcBatchItemWriter<T> itemWriter=new JdbcBatchItemWriter<>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql(sql+param.toString()+" values "+value.toString());
        itemWriter.setItemPreparedStatementSetter(new ItemPreparedStatementSetter<T>() {
            @Override
            public void setValues(T t, PreparedStatement preparedStatement) throws SQLException {
                int counter = 1;
                BeanMap map= BeanMap.create(t);
                for (Object key:params){
                    Object value = map.get(key);
                    StatementCreatorUtils.setParameterValue(preparedStatement, counter, -2147483648, (String) key, value);
                    ++counter;
                }

            }
        });
        //设置参数值来源
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        return itemWriter;
    }
}
