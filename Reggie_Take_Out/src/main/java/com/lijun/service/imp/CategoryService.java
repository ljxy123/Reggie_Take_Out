package com.lijun.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lijun.common.ConsumerException;
import com.lijun.entity.Category;
import com.lijun.entity.Dish;
import com.lijun.entity.Setmeal;
import com.lijun.mapper.CategoryMapper;
import com.lijun.service.DishService;
import com.lijun.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService extends ServiceImpl<CategoryMapper,Category> implements com.lijun.service.CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /*
    * 根据id删除对应的分类
    * */
    @Override
    public void deleteById(Long id) {
        //判断是否该分类是否有关联的菜品
        LambdaQueryWrapper<Dish> dishWrapper=new LambdaQueryWrapper<>();
        dishWrapper.eq(Dish::getCategoryId,id);
        int dishCount = (int) dishService.count(dishWrapper);
        if (dishCount>0){
            //如果有关联，则抛出异常
            throw new ConsumerException("已经有菜品和此分类进行关联，不能删除!");
        }

        //判断是否该分类是否有关联的套餐
        LambdaQueryWrapper<Setmeal> setmealWrapper=new LambdaQueryWrapper<>();
        setmealWrapper.eq(Setmeal::getCategoryId,id);
        int setmealCount = (int) setmealService.count(setmealWrapper);
        if (setmealCount>0){
            //如果有关联，则抛出异常
            throw new ConsumerException("已经有套餐和此分类进行关联，不能删除!");
        }

        super.removeById(id);
    }
}
