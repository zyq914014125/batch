package com.batch.start.controller;


import com.batch.start.config.JobLuncherConfiguration;
import com.batch.start.util.ObjectUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


/**
 * @author zhang yueqian
 * @date 2022-6-27 15:03
 */
@Api(tags = {"spring batch导表"})
@RestController
@RequestMapping("/batch")
@Slf4j
public class JobLauncherController {


    JobLauncher jobLauncher;
    JobLuncherConfiguration<Object> jobLuncherConfiguration;


    @Value("${entity.package.path}")
    private String packagePath;

    @Autowired
    public JobLauncherController(JobLauncher jobLauncher, JobLuncherConfiguration<Object> jobLuncherConfiguration) {
        this.jobLauncher = jobLauncher;
        this.jobLuncherConfiguration = jobLuncherConfiguration;
    }

    @ApiOperation(value = "启动导入")
    @GetMapping("/start")
    public Map<String,Object> start(
            @ApiParam(value = "工程名", required = true) @RequestParam(value = "jobName") String jobName,
            @ApiParam(value = "表名", required = true) @RequestParam(value = "tableName") String tableName,
            @ApiParam(value = "文件路径", required = true) @RequestParam(value = "path") String path
    ) {
        Map<String,Object> msg=new HashMap<>();
        try {
            Optional.ofNullable(ObjectUtil.getObject(packagePath, tableName)).ifPresent(object -> {
                Job  job;
                try {
                    job = jobLuncherConfiguration.registerBean(jobName, path, object);
                    jobLauncher.run(job, new JobParameters());
                    msg.put("data","成功导入");
                } catch (Exception e) {
                    msg.put("data","执行工程异常");
                }
            }
        );
    }catch (Exception e) {
            msg.put("data","无法找到该表，或该表不存在");
    }
        return msg;
    }
}