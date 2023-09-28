package com.lijun.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lijun.common.BaseContext;
import com.lijun.entity.ShoppingCart;
import com.lijun.mapper.ShoppingCartMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ShoppingCartService extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements com.lijun.service.ShoppingCartService {

    /*
    * 将菜品添加到购物车当中
    * */
    @Override
    public void add(ShoppingCart shoppingCart) {
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        shoppingCart.setCreateTime(LocalDateTime.now());

        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, userId);

        //判断添加的是菜品还是套餐
        if(shoppingCart.getDishId()!=null){
            //添加菜品
            //判断菜品是否已经添加到了购物车当中
            wrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
            wrapper.eq(ShoppingCart::getDishFlavor,shoppingCart.getDishFlavor());
            ShoppingCart one = this.getOne(wrapper);

            if(one!=null){
                //购物车中已经有了相同的菜品，并且口味也相同,则数量+1
                one.setNumber(one.getNumber()+1);
                this.update(one,wrapper);
            }else {
                //购物车当中没有添加则直接添加到购物车当中
                this.save(shoppingCart);
            }
        }else {
            //添加套餐
            //判断套餐是否已经添加到了购物车当中
            wrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
            ShoppingCart one = this.getOne(wrapper);

            if(one!=null){
                //购物车中已经有了相同的套餐,则数量+1
                one.setNumber(one.getNumber()+1);
                this.update(one,wrapper);
            }else {
                //购物车当中没有添加则直接添加到购物车当中
                this.save(shoppingCart);
            }

        }

    }

    /*
    * 减少商品的数量
    * */
    @Override
    public void sub(ShoppingCart shoppingCart) {
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());

        //判断是菜品还是套餐
        if(shoppingCart.getDishId()!=null){
            //菜品
            wrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
            ShoppingCart one = this.getOne(wrapper);
            Integer number = one.getNumber();
            number=number-1;

            if (number==0){
                //如果数量为0，则直接删除掉菜品
                this.remove(wrapper);
            }else {
                //如果数量不为0，则数量减一
                one.setNumber(number);
                this.update(one,wrapper);
            }
        }else {
            //套餐
            wrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
            ShoppingCart one = this.getOne(wrapper);
            Integer number = one.getNumber();
            number=number-1;

            if (number==0){
                //如果数量为0，则直接删除掉菜品
                this.remove(wrapper);
            }else {
                //如果数量补为0，则数量减一
                one.setNumber(number);
                this.update(one,wrapper);
            }
        }
    }
}
