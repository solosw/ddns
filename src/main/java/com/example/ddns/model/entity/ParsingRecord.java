package com.example.ddns.model.entity;


import com.example.ddns.common.enums.RecordTypeEnum;
import com.example.ddns.common.enums.ServiceProviderEnum;
import com.example.ddns.common.enums.UpdateFrequencyEnum;
import com.example.ddns.common.valid.ValidGroup;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 解析记录表
 * </p>
 *
 * @author sssd
 * @since 2023-03-19
 */

@Data
public class ParsingRecord{
    String cmd,shPath;
    Integer plat;

    @NotNull(groups = {ValidGroup.UpdateGroup.class,ValidGroup.CopyGroup.class}, message = "id不能为空")

    private String id;

    @NotNull(groups = {ValidGroup.SaveGroup.class, ValidGroup.UpdateGroup.class,ValidGroup.CopyGroup.class}, message = "服务提供商不能为空,1 阿里云 2 腾讯云 3 cloudflare")
    private Integer serviceProvider;


    private String serviceProviderName;

    @NotBlank(groups = {ValidGroup.SaveGroup.class, ValidGroup.UpdateGroup.class,ValidGroup.CopyGroup.class}, message = "服务提供商密钥key不能为空,1 阿里云 2 腾讯云 ")
    private String serviceProviderId;

    @NotBlank(groups = {ValidGroup.SaveGroup.class, ValidGroup.UpdateGroup.class,ValidGroup.CopyGroup.class}, message = "服务提供商密钥value不能为空,1 阿里云 2 腾讯云,1 阿里云 2 腾讯云 3 cloudflare")
    private String serviceProviderSecret;

    @NotNull(groups = {ValidGroup.SaveGroup.class, ValidGroup.UpdateGroup.class,ValidGroup.CopyGroup.class}, message = "解析类型不能为空,解析类型:1 AAAA 2 A")
    private Integer recordType;


    private String recordTypeName;

    private String ip;

    @NotNull(groups = {ValidGroup.SaveGroup.class, ValidGroup.UpdateGroup.class,ValidGroup.CopyGroup.class}, message = "获取ip方式不能为空,获取ip方式: 1 interface 2 network 3 cmd")
    private Integer getIpMode;

    private String getIpModeValue;

    @NotBlank(groups = {ValidGroup.SaveGroup.class, ValidGroup.UpdateGroup.class,ValidGroup.CopyGroup.class}, message = "域名不能为空")
    private String domain;

    @NotNull(groups = {ValidGroup.SaveGroup.class, ValidGroup.UpdateGroup.class,ValidGroup.CopyGroup.class}, message = "更新频率不能为空")
    private Integer updateFrequency;

    public String getUpdateFrequencyName() {
        return UpdateFrequencyEnum.getDescByCode(updateFrequency);
    }

    String updateFrequencyName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")

    private LocalDateTime createDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")

    private LocalDateTime updateDate;


    private Long creator;


    private Long updater;


    public String getId() {
        return serviceProviderId+serviceProviderSecret+domain+recordType;
    }

    public String getServiceProviderName() {
        return ServiceProviderEnum.getNameByIndex(serviceProvider);
    }

    public String getRecordTypeName() {
        return RecordTypeEnum.getNameByIndex(recordType);
    }
}
