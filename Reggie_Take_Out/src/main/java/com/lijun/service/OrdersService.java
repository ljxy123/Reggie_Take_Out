package com.lijun.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lijun.entity.Orders;
import jakarta.servlet.http.HttpSession;

public interface OrdersService extends IService<Orders> {
    void submit(Orders orders);

    Page selectOrderDetail(Integer page, Integer pageSize, HttpSession session);
}
