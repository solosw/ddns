package com.example.ddns.model.entity;

import com.example.ddns.config.CaffineConfig;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class LogJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        CaffineConfig.logList.clear();
    }
}
