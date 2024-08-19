package com.example.ddns.controller;

import com.example.ddns.common.enums.RecordTypeEnum;
import com.example.ddns.common.enums.ServiceProviderEnum;
import com.example.ddns.config.CaffineConfig;
import com.example.ddns.model.entity.ParsingRecord;
import com.example.ddns.model.entity.ResponseDO;
import com.example.ddns.model.entity.TestEntity;
import com.example.ddns.strategy.DynamicDnsServiceFactory;
import com.example.ddns.strategy.DynamicDnsStrategy;
import com.example.ddns.strategy.TencentDynamicDnsStrategyImpl;
import com.example.ddns.utils.GetIpByCmdUtils;
import com.example.ddns.utils.WanGetIpUtils;
import jakarta.annotation.Resource;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class MainController {

    @Autowired
    CaffineConfig caffine;

    @RequestMapping("/log")
    public ResponseDO getLogs(){
        return  ResponseDO.getSuccess(CaffineConfig.logList);
    }

    @RequestMapping("/getParsingRecords")
    public ResponseDO getParsingRecords(){
        return ResponseDO.getSuccess(CaffineConfig.parsingRecordList);
    }
    @RequestMapping("/deleteParsingRecords")
    public ResponseDO deleteParsingRecords(@RequestBody ParsingRecord parsingRecord)  {
        try {
            DynamicDnsStrategy dynamicDnsStrategy= DynamicDnsServiceFactory.dnsStrategyMap.get(parsingRecord.getServiceProvider());
            dynamicDnsStrategy.remove(parsingRecord,parsingRecord.getIp());
            caffine.del(parsingRecord);
            return ResponseDO.getSuccess(null);
        }catch (Exception e){

            return ResponseDO.getFail("fail",1);
        }

    }

    @RequestMapping("/addRecord")
    public ResponseDO addIp(@RequestBody ParsingRecord parsingRecord){
        DynamicDnsStrategy dynamicDnsStrategy= DynamicDnsServiceFactory.dnsStrategyMap.get(parsingRecord.getServiceProvider());
        if(parsingRecord.getServiceProvider().equals(ServiceProviderEnum.TENCENT.getIndex())){
            try {


                dynamicDnsStrategy.add(parsingRecord, parsingRecord.getIp());
                caffine.add(parsingRecord);
                return  ResponseDO.getSuccess(null);
            } catch (Exception e) {
                return ResponseDO.getFail("fail",1);
            }

        }

        return ResponseDO.getFail("fail",1);
    }


    @RequestMapping("/test")
    public ResponseDO test(@RequestBody TestEntity testEntity) throws IOException {

        if(testEntity.getCmd().isEmpty()) return ResponseDO.getFail("fail",401);

        if(testEntity.getPlat()==0){
            if(testEntity.getShPath().isEmpty()) return ResponseDO.getFail("fail",401);
            return  ResponseDO.getSuccess( GetIpByCmdUtils.runSh(testEntity.getShPath(),testEntity.getCmd()));
        }else
        {

            return  ResponseDO.getSuccess( GetIpByCmdUtils.runCmd(testEntity.getCmd()));
        }

    }

    @RequestMapping("/getIp")
    public ResponseDO getIp(@RequestBody ParsingRecord parsingRecord) throws IOException {
            //命令行
            if(parsingRecord.getGetIpMode().equals(1)){
                if(parsingRecord.getPlat()==1){

                    if(parsingRecord.getRecordType().equals(RecordTypeEnum.IPV6.getIndex())){

                        List<String> ips=GetIpByCmdUtils.findPublicIpv6Addresses(GetIpByCmdUtils.runCmd(parsingRecord.getCmd()));
                        if(!ips.isEmpty()){
                            parsingRecord.setIp(ips.get(0));
                        }else {
                            parsingRecord.setIp("");
                        }

                    }else {

                        List<String> ips=GetIpByCmdUtils.findPublicIPv4(GetIpByCmdUtils.runCmd(parsingRecord.getCmd()));
                        if(!ips.isEmpty()){
                            parsingRecord.setIp(ips.get(0));
                        }else {
                            parsingRecord.setIp("");
                        }
                    }

                }else if(parsingRecord.getPlat().equals(0)){

                    if(parsingRecord.getRecordType().equals(RecordTypeEnum.IPV6.getIndex())){

                        List<String> ips=GetIpByCmdUtils.findPublicIpv6Addresses(GetIpByCmdUtils.runSh(parsingRecord.getShPath(),parsingRecord.getCmd()));
                        if(!ips.isEmpty()){
                            parsingRecord.setIp(ips.get(0));
                        }else {
                            parsingRecord.setIp("");
                        }

                    }else {

                        List<String> ips=GetIpByCmdUtils.findPublicIPv4(GetIpByCmdUtils.runSh(parsingRecord.getShPath(),parsingRecord.getCmd()));
                        if(!ips.isEmpty()){
                            parsingRecord.setIp(ips.get(0));
                        }else {
                            parsingRecord.setIp("");
                        }
                    }

                }

            }else if(parsingRecord.getGetIpMode().equals(2)){

                if(parsingRecord.getRecordType().equals(RecordTypeEnum.IPV4.getIndex())){
                    parsingRecord.setIp(WanGetIpUtils.getPublicIPV4());
                }else {

                    List<String> localIPv6Address = WanGetIpUtils.getLocalIPv6Address();
                    if(!localIPv6Address.isEmpty()){
                        parsingRecord.setIp(localIPv6Address.get(localIPv6Address.size()-1));
                    }else {
                        parsingRecord.setIp("");
                    }
                }

            }
            return ResponseDO.getSuccess(parsingRecord);
    }

}
