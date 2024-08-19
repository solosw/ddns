package com.example.ddns.strategy;

import com.example.ddns.common.enums.RecordTypeEnum;
import com.example.ddns.model.entity.ParsingRecord;
import com.example.ddns.utils.HuaweiDnsUtils;
import com.huaweicloud.sdk.dns.v2.DnsClient;
import com.huaweicloud.sdk.dns.v2.model.ListRecordSetsResponse;
import com.huaweicloud.sdk.dns.v2.model.ListRecordSetsWithTags;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


import java.util.List;

/**
 * @author sssd
 * @careate 2023-11-13-21:56
 */
@Service
@Slf4j
public class HuaweiDynamicDnsStrategyImpl implements DynamicDnsStrategy {
    @Override
    public boolean exist(String serviceProviderId, String serviceProviderSecret, String subDomain, String recordType) throws Exception {
        DnsClient client = HuaweiDnsUtils.createClient(serviceProviderId, serviceProviderSecret);
        ListRecordSetsResponse listRecordSetsResponse = HuaweiDnsUtils.listRecordSetsByDomainWithType(client, subDomain, recordType);
        List<ListRecordSetsWithTags> set = listRecordSetsResponse.getRecordsets();
        return !CollectionUtils.isEmpty(set);
    }

    @Override
    public void add(ParsingRecord parsingRecord, String ip) throws Exception {
        DnsClient client = HuaweiDnsUtils.createClient(parsingRecord.getServiceProviderId(), parsingRecord.getServiceProviderSecret());
        String zoneId = HuaweiDnsUtils.getPublicZoneId(client);
        HuaweiDnsUtils.add(client,zoneId,parsingRecord.getDomain(), RecordTypeEnum.getNameByIndex(parsingRecord.getRecordType()),ip);
    }

    @Override
    public void update(ParsingRecord parsingRecord, String ip, String recordId) throws Exception {
        DnsClient client = HuaweiDnsUtils.createClient(parsingRecord.getServiceProviderId(), parsingRecord.getServiceProviderSecret());
        String zoneId = HuaweiDnsUtils.getPublicZoneId(client);
        HuaweiDnsUtils.update(client,recordId,zoneId,parsingRecord.getDomain(),RecordTypeEnum.getNameByIndex(parsingRecord.getRecordType()),ip);
    }

    @Override
    public String getRecordId(ParsingRecord parsingRecord, String ip) throws Exception {
        DnsClient client = HuaweiDnsUtils.createClient(parsingRecord.getServiceProviderId(), parsingRecord.getServiceProviderSecret());
        return HuaweiDnsUtils.getRecordId(client,parsingRecord.getDomain(),RecordTypeEnum.getNameByIndex(parsingRecord.getRecordType()),ip);
    }

    @Override
    public void remove(ParsingRecord parsingRecord, String ip) throws Exception {
        DnsClient client = HuaweiDnsUtils.createClient(parsingRecord.getServiceProviderId(), parsingRecord.getServiceProviderSecret());
        String zoneId = HuaweiDnsUtils.getPublicZoneId(client);
        String recordId = getRecordId(parsingRecord, ip);
        HuaweiDnsUtils.delete(client,recordId,zoneId);
    }

    @Override
    public String getIpBySubDomainWithType(ParsingRecord parsingRecord) throws Exception {
        DnsClient client = HuaweiDnsUtils.createClient(parsingRecord.getServiceProviderId(), parsingRecord.getServiceProviderSecret());
        ListRecordSetsResponse response = HuaweiDnsUtils.listRecordSetsByDomainWithType(client, parsingRecord.getDomain(), RecordTypeEnum.getNameByIndex(parsingRecord.getRecordType()));
        return response.getRecordsets().get(0).getRecords().get(0);
    }
}
