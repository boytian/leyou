package com.leyou.common.advice;

import com.leyou.common.exception.LyException;
import com.leyou.common.vo.ExceptionResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 作用：拦截controller层的异常，是个通用类
 * @Author: TianTian
 * @Date: 2020/4/19 16:21
 */
@ControllerAdvice
public class BaseExceptionAdvice {
    //第二步:拦截什么?栏RuntimeException
//    @ExceptionHandler(RuntimeException.class)
//    public ResponseEntity<String> handlerRuntimeException(RuntimeException e) {
//
//        return ResponseEntity.status(400).body(e.getMessage());
//
//    }

    //第二步:拦截什么?栏LyException
    @ExceptionHandler(LyException.class)
    public ResponseEntity<ExceptionResult> handlerLyException(LyException e) {

        return ResponseEntity.status(e.getStatus()).body(new ExceptionResult(e));
    }
}
