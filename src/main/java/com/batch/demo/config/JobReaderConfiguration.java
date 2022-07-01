package com.batch.demo.config;

import com.batch.demo.entity.User;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

/**
 * batch reader
 * @author zhang yueqian
 * @date 2022-6-27 15:22
 */
@Configuration
public class JobReaderConfiguration  {


    public  FlatFileItemReader<User> fileReader(String path){
            FlatFileItemReader<User> reader=new FlatFileItemReader<>();
            //读文件
            reader.setResource(new ClassPathResource(path));
            //跳过第一行
            reader.setLinesToSkip(1);
            //创建映射
            DefaultLineMapper<User> userDefaultLineMapper=new DefaultLineMapper<>();
            //创建名称
            DelimitedLineTokenizer delimitedLineTokenizer=new DelimitedLineTokenizer();
            delimitedLineTokenizer.setNames("name","id","password");
            //映射
            userDefaultLineMapper.setLineTokenizer(delimitedLineTokenizer);
            userDefaultLineMapper.setFieldSetMapper(fieldSet ->
                 new User(fieldSet.readString("name"),fieldSet.readBigDecimal("id"),fieldSet.readString("password"))
            );
            userDefaultLineMapper.afterPropertiesSet();
            reader.setLineMapper(userDefaultLineMapper);
            return reader;
    }

}
