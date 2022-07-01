package com.batch.demo.application;

import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobInstanceAlreadyExistsException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhang yueqian
 * @date 2022-6-27 15:03
 */
@RestController
@RequestMapping("/test")
public class JobOperationController {

     @Autowired
     private JobOperator jobOperator;

     @GetMapping("/jobOperator")
     public void test() throws JobParametersInvalidException, JobInstanceAlreadyExistsException, NoSuchJobException {
         jobOperator.start("userJob","object=");
    }


}
