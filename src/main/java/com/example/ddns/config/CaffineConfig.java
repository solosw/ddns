package com.example.ddns.config;

import com.alibaba.fastjson.JSON;
import com.example.ddns.common.enums.UpdateFrequencyEnum;
import com.example.ddns.model.entity.LogJob;
import com.example.ddns.model.entity.ParsingJob;
import com.example.ddns.model.entity.ParsingRecord;
import com.example.ddns.utils.FileUtils;
import com.example.ddns.utils.QuartzUtil;
import jakarta.annotation.PostConstruct;
import org.quartz.SchedulerException;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Configuration
public class CaffineConfig {
   static String fileName="./records.json";

    public static void updateByTask(ParsingRecord parsingRecord) throws SchedulerException {

        for(int i=0;i<parsingRecordList.size();i++){
            if(parsingRecordList.get(i).getId().equals(parsingRecord.getId())){
                parsingRecordList.remove(parsingRecordList.get(i));
                break;
            }
        }

        parsingRecordList.add(parsingRecord);

        writeToFile();
        logList.add(parsingRecord.toString()+"更新成功");
    }
  public  static List<ParsingRecord> parsingRecordList=new CopyOnWriteArrayList<>();
    public  static List<String> logList=new CopyOnWriteArrayList<>();
    @PostConstruct
    public void init() throws SchedulerException {

        String content= FileUtils.readJson(fileName);
        if(content==null|| content.trim().isEmpty()) return;
        for(ParsingRecord record: JSON.parseArray(content, ParsingRecord.class)){
            add(record);
        }

        QuartzUtil.addJob("logsId","logsGroup","logTrigger","logTriggerGroup"
                ,LogJob.class,"0 0 0 * * ?",null);

    }



    public  void add(ParsingRecord parsingRecord) throws SchedulerException {
        parsingRecordList.add(parsingRecord);
        QuartzUtil.addJob(parsingRecord.getId(),parsingRecord.getId(),parsingRecord.getId(),
                parsingRecord.getId(), ParsingJob.class,
                UpdateFrequencyEnum.getCronExpressionByCode(parsingRecord.getUpdateFrequency()),parsingRecord);

        writeToFile();
        logList.add(parsingRecord.toString()+"添加成功");
    }
    public  void del(ParsingRecord parsingRecord) throws SchedulerException {

        for(int i=0;i<parsingRecordList.size();i++){
            if(parsingRecordList.get(i).getId().equals(parsingRecord.getId())){
                parsingRecordList.remove(i);
                break;
            }
        }
        QuartzUtil.deleteJob(parsingRecord.getId(),parsingRecord.getId());
        writeToFile();
        logList.add(parsingRecord.toString()+"删除成功");
    }
    public  void update(ParsingRecord parsingRecord) throws SchedulerException {
        for(int i=0;i<parsingRecordList.size();i++){
            if(parsingRecordList.get(i).getId().equals(parsingRecord.getId())){
                parsingRecordList.remove(i);
                break;
            }
        }
        QuartzUtil.deleteJob(parsingRecord.getId(),parsingRecord.getId());
        parsingRecordList.add(parsingRecord);
        QuartzUtil.addJob(parsingRecord.getId(),parsingRecord.getId(),parsingRecord.getId(),
                parsingRecord.getId(), ParsingJob.class,
                UpdateFrequencyEnum.getCronExpressionByCode(parsingRecord.getUpdateFrequency()),parsingRecord);
        writeToFile();
    }


    static void writeToFile(){
        String content=JSON.toJSONString(parsingRecordList);
        FileUtils.writeJson(fileName,content);
    }

}
