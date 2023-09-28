package com.lijun.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lijun.common.ConsumerException;
import com.lijun.dto.DishDto;
import com.lijun.dto.SetmealDto;
import com.lijun.entity.Category;
import com.lijun.entity.Setmeal;
import com.lijun.entity.SetmealDish;
import com.lijun.mapper.SetmealMapper;
import com.lijun.service.CategoryService;
import com.lijun.service.SetmealDishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealService extends ServiceImpl<SetmealMapper, Setmeal> implements com.lijun.service.SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    /*
    * 添加套餐
    * 添加套餐中对饮的菜品
    * */
    @Override
    public void addSetmealWithDishes(SetmealDto setmealDto) {
        //添加套餐基本信息
        this.save(setmealDto);

        //将套餐中的菜品和对应的套餐进行绑定
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }

    /*
    * 更具id删除套餐
    * */
    @Transactional
    @Override
    public void deletSetmeals(List<Long> ids) {
        //判断要删除的套餐是否停售
        LambdaQueryWrapper<Setmeal> setmealWrapper = new LambdaQueryWrapper<>();
        setmealWrapper.in(Setmeal::getId,ids);
        setmealWrapper.eq(Setmeal::getStatus,1);

        //如果有停售的套餐则爬出不能删除的异常
        if(this.count(setmealWrapper)>0){
            throw new ConsumerException("有套餐正在销售中，无法删除套餐");
        }

        //如果没有套餐正在销售，则直接删除套餐
        this.removeByIds(ids);

        //删除setmeal_dish表中相应的菜品
        LambdaQueryWrapper<SetmealDish> setmealDishWrapper = new LambdaQueryWrapper<>();
        setmealDishWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(setmealDishWrapper);
    }



}
