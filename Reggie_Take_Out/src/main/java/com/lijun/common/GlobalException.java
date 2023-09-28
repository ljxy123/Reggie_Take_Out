package com.lijun.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

@RestControllerAdvice
@Slf4j
@ResponseBody
public class GlobalException {

    /*
    * 全局异常处理：sql语句中因为唯一约束而导致的错误
    * */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result<String> exceptionDuplicateEntry(SQLIntegrityConstraintViolationException e){
        log.info("错误信息---->{}",e.getMessage());

        if(e.getMessage().contains("Duplicate entry")){
            String[] split = e.getMessage().split(" ");
            String errMessage=split[2]+"已被创建";
            return Result.error(errMessage);
        }

        return Result.error("出现未知错误");
    }

    /*
     * 全局异常处理：获取在删除分类时因有菜品和套餐相关联导致的异常
     * */
    @ExceptionHandler(ConsumerException.class)
    public Result<String> exceptionDuplicateEntry(ConsumerException e){
        log.info("错误信息---->{}",e.getMessage());

        return Result.error(e.getMessage());
    }
}
