package com.leyou.auth.entity;

import lombok.Data;

import java.util.Date;

/**
 * @version 1.0
 * @Author heima
 **/
@Data
public class Payload<T> {
    private String id;
    private T userInfo;
    private Date expiration;
}
