package com.lijun.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lijun.entity.ShoppingCart;

public interface ShoppingCartService extends IService<ShoppingCart> {
    void add(ShoppingCart shoppingCart);

    void sub(ShoppingCart shoppingCart);
}
