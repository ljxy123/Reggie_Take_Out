package com.lijun.dto;

import com.lijun.entity.OrderDetail;
import com.lijun.entity.Orders;
import lombok.Data;

import java.util.List;

@Data
public class OrderDto extends Orders {
    private List<OrderDetail> OrderDetails;


}
