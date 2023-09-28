package com.lijun.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lijun.dto.DishDto;
import com.lijun.dto.SetmealDto;
import com.lijun.entity.Setmeal;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SetmealService extends IService<Setmeal> {
    void addSetmealWithDishes(SetmealDto setmealDto);

    void deletSetmeals(List<Long> ids);
}
