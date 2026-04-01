package com.ch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonDto<T>{
    private String msg;
    private T data;
    private List<T> dataList;
    private String status;
    private int statusCode;
}
