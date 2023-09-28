package com.lijun.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lijun.common.Result;
import com.lijun.entity.ShoppingCart;
import com.lijun.service.ShoppingCartService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /*
    * 将菜品添加到购物车当中
    * */
    @PostMapping("/add")
    public Result<String> add(@RequestBody ShoppingCart shoppingCart){
        log.info("添加的菜品信息为:{}",shoppingCart);

        shoppingCartService.add(shoppingCart);

        return Result.success("添加成功");
    }

    /*
    * 查看购物车中添加的菜品和套餐
    * */
    @GetMapping("/list")
    public Result<List<ShoppingCart>> selectAllShoppingCart(Long userId){

        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(userId!=null,ShoppingCart::getUserId,userId);
        wrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(wrapper);

        return Result.success(list);
    }

    /*
    * 清空购物车
    * */
    @DeleteMapping("/clean")
    public Result<String> clean(HttpSession session){
        Long userId = (Long) session.getAttribute("user");

        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,userId);
        shoppingCartService.remove(wrapper);

        return Result.success("清理成功");
    }

    /*
    * 减少商品数量
    * */
    @PostMapping("/sub")
    public Result<String> sub(@RequestBody ShoppingCart shoppingCart){

        shoppingCartService.sub(shoppingCart);

        return Result.success("减少成功");
    }
}
