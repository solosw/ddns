package com.example.ddns.strategy;

import com.example.ddns.common.enums.ServiceProviderEnum;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DynamicDnsServiceFactory implements InitializingBean {

     public static Map<Integer,DynamicDnsStrategy> dnsStrategyMap = new ConcurrentHashMap<>();

     @Resource
    TencentDynamicDnsStrategyImpl tencentDynamicDnsStrategy;
    @Resource
    AliDynamicDnsStrategyImpl aliDynamicDnsStrategy;

    @Resource
    HuaweiDynamicDnsStrategyImpl huaweiDynamicDnsStrategy;


    @Override
    public void afterPropertiesSet() throws Exception {
        dnsStrategyMap.put(ServiceProviderEnum.TENCENT.getIndex(), tencentDynamicDnsStrategy);
        dnsStrategyMap.put(ServiceProviderEnum.ALI_YUN.getIndex(), aliDynamicDnsStrategy);
        dnsStrategyMap.put(ServiceProviderEnum.HUAWEI_YUN.getIndex(), huaweiDynamicDnsStrategy);
    }
}
