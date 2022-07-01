package com.batch.demo.config;

import com.batch.demo.entity.User;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.converter.DefaultJobParametersConverter;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @author zhang yueqian
 * @date 2022-6-27 14:46
 */
@Configuration
public class JobOperatorTest implements StepExecutionListener, ApplicationContextAware {


    private JobLauncher jobLauncher;

    private JobRepository jobRepository;

    private JobExplorer jobExplorer;

    private JobRegistry jobRegistry;

    private Map<String, JobParameter> parameters;


    private JobBuilderFactory jobBuilderFactory;


    private StepBuilderFactory stepBuilderFactory;


    private JobWriterConfiguration jobWriterConfiguration;


    private JobReaderConfiguration jobReaderConfiguration;

    private ApplicationContext context;

    @Autowired
    public JobOperatorTest(JobLauncher jobLauncher, JobRepository jobRepository, JobExplorer jobExplorer, JobRegistry jobRegistry, JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, JobWriterConfiguration jobWriterConfiguration, JobReaderConfiguration jobReaderConfiguration) {
        this.jobLauncher = jobLauncher;
        this.jobRepository = jobRepository;
        this.jobExplorer = jobExplorer;
        this.jobRegistry = jobRegistry;
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.jobWriterConfiguration = jobWriterConfiguration;
        this.jobReaderConfiguration = jobReaderConfiguration;
    }

    @Bean
    public JobOperator jobOperator(){
        SimpleJobOperator operator=new SimpleJobOperator();
        //设置启动器
        operator.setJobLauncher(jobLauncher);
        //设置参数转换
        operator.setJobParametersConverter(new DefaultJobParametersConverter());
        //设置repository
        operator.setJobRepository(jobRepository);
        //创建任务相关信息
        operator.setJobExplorer(jobExplorer);
        //创建注册器
        operator.setJobRegistry(jobRegistry);
        return  operator;
    }

    /**
     * 创建 user 表Job
     * @return
     */
    @Bean(value = "userJob")
    public Job userJob() {
        return jobBuilderFactory.get("userJob")
                .start(userStep())
                .build();
    }

    /**
     *创建 user 表step
     * @return
     */
    @Bean
    public  Step userStep()  {

        User user=new User();
        FlatFileItemReader<User> reader = jobReaderConfiguration.fileReader("user.csv");
        jobWriterConfiguration.t=user;
        JdbcBatchItemWriter<User> itemWriter = jobWriterConfiguration.userJdbcBatchItemWriterTest();
        return stepBuilderFactory.get("jobOperatorDemoStep")
            .listener(this)
                .<User,User>chunk(10)
                .reader(reader)
                .writer(itemWriter)
                .build();
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        parameters = stepExecution.getJobParameters().getParameters();
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return null;
    }


    @Bean
    public JobRegistryBeanPostProcessor jobRegistrar() throws Exception {
        JobRegistryBeanPostProcessor postProcessor = new JobRegistryBeanPostProcessor();
        //将所有job关联到factory中
        postProcessor.setJobRegistry(jobRegistry);
        postProcessor.setBeanFactory(context.getAutowireCapableBeanFactory());
        postProcessor.afterPropertiesSet();
        return postProcessor;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
