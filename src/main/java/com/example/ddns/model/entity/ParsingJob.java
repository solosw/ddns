package com.example.ddns.model.entity;

import com.alibaba.fastjson.JSON;
import com.example.ddns.common.enums.RecordTypeEnum;
import com.example.ddns.common.enums.ServiceProviderEnum;
import com.example.ddns.config.CaffineConfig;
import com.example.ddns.strategy.DynamicDnsServiceFactory;
import com.example.ddns.strategy.DynamicDnsStrategy;
import com.example.ddns.strategy.TencentDynamicDnsStrategyImpl;
import com.example.ddns.utils.GetIpByCmdUtils;
import com.example.ddns.utils.TencentDnsUtils;
import com.example.ddns.utils.WanGetIpUtils;

import org.apache.catalina.core.ApplicationContext;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.List;

public class ParsingJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        System.out.println("开始解析");

        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        // 从JobDataMap中获取参数
        ParsingRecord parsingRecord = JSON.parseObject(jobDataMap.getString("data"),ParsingRecord.class);

        try {
            doTask(parsingRecord);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }


    public void doTask(ParsingRecord parsingRecord) throws Exception {
        DynamicDnsStrategy dynamicDnsStrategy = DynamicDnsServiceFactory.dnsStrategyMap.get(parsingRecord.getServiceProvider());
        String recordId=dynamicDnsStrategy.getRecordId(parsingRecord,parsingRecord.getIp());
        String oldIp=parsingRecord.getIp();
        if(parsingRecord.getGetIpMode().equals(1)){
            if(parsingRecord.getPlat()==1){

                if(parsingRecord.getRecordType().equals(RecordTypeEnum.IPV6.getIndex())){

                    List<String> ips= null;
                    try {
                        ips = GetIpByCmdUtils.findPublicIpv6Addresses(GetIpByCmdUtils.runCmd(parsingRecord.getCmd()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if(!ips.isEmpty()){

                        if(ips.get(0).equals(parsingRecord.getIp())){
                            CaffineConfig.logList.add("Ip未发生改变");
                            return;
                        }
                        parsingRecord.setIp(ips.get(0));
                    }else {
                        parsingRecord.setIp("");
                    }

                }else {

                    List<String> ips= null;
                    try {
                        ips = GetIpByCmdUtils.findPublicIPv4(GetIpByCmdUtils.runCmd(parsingRecord.getCmd()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if(!ips.isEmpty()){
                        if(ips.get(0).equals(parsingRecord.getIp())){
                            CaffineConfig.logList.add("Ip未发生改变");
                            return;
                        }
                        parsingRecord.setIp(ips.get(0));
                    }else {
                        parsingRecord.setIp("");
                    }
                }

            }else if(parsingRecord.getPlat().equals(0)){

                if(parsingRecord.getRecordType().equals(RecordTypeEnum.IPV6.getIndex())){

                    List<String> ips=GetIpByCmdUtils.findPublicIpv6Addresses(GetIpByCmdUtils.runSh(parsingRecord.getShPath(),parsingRecord.getCmd()));
                    if(!ips.isEmpty()){
                        if(ips.get(0).equals(parsingRecord.getIp())){
                            CaffineConfig.logList.add("Ip未发生改变");
                            return;
                        }
                        parsingRecord.setIp(ips.get(0));
                    }else {
                        parsingRecord.setIp("");
                    }

                }else {

                    List<String> ips=GetIpByCmdUtils.findPublicIPv4(GetIpByCmdUtils.runSh(parsingRecord.getShPath(),parsingRecord.getCmd()));
                    if(!ips.isEmpty()){
                        if(ips.get(0).equals(parsingRecord.getIp())){
                            CaffineConfig.logList.add("Ip未发生改变");
                            return;
                        }
                        parsingRecord.setIp(ips.get(0));
                    }else {
                        parsingRecord.setIp("");
                    }
                }

            }

        }else if(parsingRecord.getGetIpMode().equals(2)){

            if(parsingRecord.getRecordType().equals(RecordTypeEnum.IPV4.getIndex())){
                String newIP=WanGetIpUtils.getPublicIPV4();
                if(newIP.equals(parsingRecord.getIp())){
                    CaffineConfig.logList.add("Ip未发生改变");
                    return;
                }
                parsingRecord.setIp(newIP);
            }else {

                List<String> localIPv6Address = WanGetIpUtils.getLocalIPv6Address();
                if(!localIPv6Address.isEmpty()){

                    if(localIPv6Address.get(localIPv6Address.size()-1).equals(parsingRecord.getIp())){
                        CaffineConfig.logList.add("Ip未发生改变");
                        return;
                    }
                    parsingRecord.setIp(localIPv6Address.get(localIPv6Address.size()-1));
                }else {
                    parsingRecord.setIp("");
                }
            }

        }

        dynamicDnsStrategy.update(parsingRecord, parsingRecord.getIp(), recordId);
        CaffineConfig.updateByTask(parsingRecord);
        CaffineConfig.logList.add("ip发送改变:"+oldIp+"-->"+parsingRecord.getIp());
    }


}
