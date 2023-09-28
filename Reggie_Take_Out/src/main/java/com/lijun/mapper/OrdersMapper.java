package com.lijun.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lijun.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
