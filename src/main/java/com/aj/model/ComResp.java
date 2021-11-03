package com.aj.model;

import lombok.Data;

@Data
public class ComResp<T> {
    private int code;
    private String msg;
    private T result;

    public void right(T result) {
        this.code = 1;
        this.msg = "接口调用成功！";
        this.result = result;
    }

    public void right(String msg, T result) {
        this.code = 1;
        this.msg = msg;
        this.result = result;
    }

    public void fail(String msg) {
        this.code = 0;
        this.msg = msg;
    }

    public void fail(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
