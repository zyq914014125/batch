package com.batch.start.config;


import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.DuplicateJobException;
import org.springframework.batch.core.configuration.JobFactory;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.configuration.support.ReferenceJobFactory;
import org.springframework.batch.core.job.SimpleJob;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;


/**
 * @author zhang yueqian
 * @date 2022-6-27 14:46
 */
@Configuration
@EnableBatchProcessing
public class JobLuncherConfiguration<T> implements ApplicationContextAware {

    T t;
    private String jobName;
    private String path;
    private JobRegistry jobRegistry;
    private JobBuilderFactory jobBuilderFactory;
    private StepBuilderFactory stepBuilderFactory;
    private JobWriterConfiguration<T> jobWriterConfiguration;
    private JobReaderConfiguration<T> jobReaderConfiguration;
    private ApplicationContext context;


    @Autowired
    public JobLuncherConfiguration(JobRegistry jobRegistry, JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, JobWriterConfiguration<T> jobWriterConfiguration, JobReaderConfiguration<T> jobReaderConfiguration) {
        this.jobRegistry = jobRegistry;
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.jobWriterConfiguration = jobWriterConfiguration;
        this.jobReaderConfiguration = jobReaderConfiguration;

    }

    /**
     * 生成job对象，此job对象为最简Job,后续可自定义Job对象与step对象，仅需重写job()与step()即可
     * @return Job
     */
    public Job job() {
        return jobBuilderFactory.get(jobName)
                .incrementer(new RunIdIncrementer())
                .start(step())
                .build();
    }

    @Bean
    @Scope("prototype")
    @StepScope
    public Step step() {
        jobReaderConfiguration.t = t;
        jobReaderConfiguration.path = path;
        FlatFileItemReader<T> reader = jobReaderConfiguration.fileReader();
        jobWriterConfiguration.t = t;
        JdbcBatchItemWriter<T> itemWriter = jobWriterConfiguration.userJdbcBatchItemWriterTest();
        return stepBuilderFactory.get("step")
                .listener(this)
                .<T, T>chunk(10)
                .reader(reader)
                .writer(itemWriter)
                .build();

    }

    @Bean
    public JobRegistryBeanPostProcessor jobRegistrar() throws Exception {
        JobRegistryBeanPostProcessor postProcessor = new JobRegistryBeanPostProcessor();
        postProcessor.setJobRegistry(jobRegistry);
        postProcessor.setBeanFactory(context.getAutowireCapableBeanFactory());
        postProcessor.afterPropertiesSet();
        return postProcessor;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }


    public Job registerBean(String jobName, String path, T t) throws DuplicateJobException {
        this.t = t;
        this.path = path;
        this.jobName = jobName;
        Job job = new SimpleJob(jobName);
        JobFactory jobFactory = new ReferenceJobFactory(job);
        jobRegistry.register(jobFactory);
        return job();
    }


}
