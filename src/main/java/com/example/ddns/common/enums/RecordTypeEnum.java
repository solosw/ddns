package com.example.ddns.common.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sssd
 * @created 2023-03-20-15:27
 */
@Getter
public enum RecordTypeEnum {
    IPV6(1,"AAAA"),
    IPV4(2,"A");

    private static final Map<Integer,String> map = new HashMap<>();

    static {
        for (RecordTypeEnum el : RecordTypeEnum.values()) {
            map.put(el.getIndex(), el.getName());
        }
    }

    public static String getNameByIndex(Integer index){
        return map.get(index);
    }


    private Integer index;
    private String name;

    RecordTypeEnum() {
    }

    RecordTypeEnum(Integer index, String name) {
        this.index = index;
        this.name = name;
    }

}
