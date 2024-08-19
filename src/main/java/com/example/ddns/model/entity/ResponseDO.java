package com.example.ddns.model.entity;

import lombok.Data;


@Data
public class ResponseDO {

    private boolean success;
    private String msg;
    private Object data;
    private Integer errorCode;
    public ResponseDO() {
    }

    public ResponseDO(boolean success, String msg, Object data){
        this.success = success;
        this.msg = msg;
        this.data = data;
    }

    public ResponseDO(boolean success, String msg, Object data, Integer errorCode) {
        this.success = success;
        this.msg = msg;
        this.data = data;
        this.errorCode = errorCode;
    }

    public static ResponseDO getSuccess(Object data){
        return new ResponseDO(true,"success",data,1);
    }

    public static ResponseDO getFail(String msg,Integer errorCode){
        return new ResponseDO(false,msg,null,1);
    }

}

