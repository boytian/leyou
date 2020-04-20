package com.leyou.common.exception;

import com.leyou.common.enums.ExcptionEnum;
import lombok.Getter;

/**
 * @Author: TianTian
 * @Date: 2020/4/19 16:29
 */
@Getter
public class LyException extends RuntimeException{
    private int status;
    public LyException(int status,String message) {
        super(message);
        this.status = status;
    }
    public LyException(ExcptionEnum e) {
        super(e.getMess());
        this.status = e.getStatus();
    }
}
