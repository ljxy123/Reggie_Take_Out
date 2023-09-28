package com.lijun.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lijun.common.Result;
import com.lijun.entity.Orders;
import com.lijun.service.OrdersService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    /*
    * 用户下单
    * */
    @PostMapping("/submit")
    public Result<String> submit(@RequestBody Orders orders){
        log.info("用户的下单信息:{}",orders);

        ordersService.submit(orders);

        return Result.success("下单成功");
    }

    /*
    * 移动端分页查看订单明细
    * */
    @GetMapping("/userPage")
    public Result<Page> selecOrderDetail(Integer page, Integer pageSize, HttpSession session){

        Page detailPage =ordersService.selectOrderDetail(page,pageSize,session);

        return Result.success(detailPage);
    }

    /*
    * 管理端订单的分页查询
    * */
    @GetMapping("/page")
    public Result<Page> selectOrder(Integer page, Integer pageSize, String number, @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime beginTime, @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime){

        Page<Orders> ordersPage = new Page<>(page, pageSize);

        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(number!=null,Orders::getNumber,number);
        wrapper.between(beginTime != null && endTime != null,Orders::getOrderTime,beginTime,endTime);
        Page<Orders> resPage = ordersService.page(ordersPage,wrapper);

        return Result.success(resPage);
    }
}
