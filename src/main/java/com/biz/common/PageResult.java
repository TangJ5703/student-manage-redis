package com.biz.common;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {

    // 数据列表
    private List<T> list;

    // 总记录数
    private long total;

    // 当前页码
    private long pageNum;

    // 每页记录数
    private long pageSize;

    // 消息状态码
    private int code;

    // 消息内容
    private String msg;

    public PageResult(List<T> list, long total, long pageNum, long pageSize) {
        this.list = list;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.code = HttpStatus.SUCCESS;
        this.msg = "响应成功";
    }


    public PageResult() {
        this.code = HttpStatus.SUCCESS;
        this.msg = "响应成功";
    }
}