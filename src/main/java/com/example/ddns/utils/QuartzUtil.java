package com.example.ddns.utils;

import com.alibaba.fastjson.JSON;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class QuartzUtil {

    private static Scheduler scheduler;

    static {
        try {
            scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public static void addJob(String jobId, String jobGroup, String triggerId, String triggerGroup, Class<? extends Job> jobClass, String cronExpression,Object data) throws SchedulerException {
        JobDataMap jobDataMap = new JobDataMap();

        // 将数据添加到JobDataMap中，这里使用字符串序列化，实际使用时可能需要根据具体情况序列化
        jobDataMap.put("data", JSON.toJSONString(data));


        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobId, jobGroup).setJobData(jobDataMap).build();

        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerId, triggerGroup).withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).build();
        scheduler.scheduleJob(jobDetail, trigger);
    }

    public static void deleteJob(String jobId, String jobGroup) throws SchedulerException {
        JobKey jobKey = new JobKey(jobId, jobGroup);
        scheduler.deleteJob(jobKey);
    }

    public static void pauseJob(String jobId, String jobGroup) throws SchedulerException {
        JobKey jobKey = new JobKey(jobId, jobGroup);
        scheduler.pauseJob(jobKey);
    }

    public static void resumeJob(String jobId, String jobGroup) throws SchedulerException {
        JobKey jobKey = new JobKey(jobId, jobGroup);
        scheduler.resumeJob(jobKey);
    }

    public static void shutdown() throws SchedulerException {
        scheduler.shutdown();
    }

}

