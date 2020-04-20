package com.leyou.common.vo;

import com.leyou.common.exception.LyException;
import lombok.Getter;
import org.joda.time.DateTime;

/**
 * @Author: TianTian
 * @Date: 2020/4/19 17:48
 */
@Getter
public class ExceptionResult {
    private int status;
    private String message;
    private String timestamp;

    public ExceptionResult(LyException e){
        this.status=e.getStatus();
        this.message=e.getMessage();
        this.timestamp= DateTime.now().toString("YYYY年MM月dd号 HH点mm分ss秒");
    }
}
