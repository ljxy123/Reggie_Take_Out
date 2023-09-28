package com.lijun.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lijun.entity.OrderDetail;
import com.lijun.mapper.OrderDetailMapper;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailService extends ServiceImpl<OrderDetailMapper, OrderDetail> implements com.lijun.service.OrderDetailService {
}
