package com.lijun.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lijun.dto.DishDto;
import com.lijun.entity.Dish;

public interface DishService extends IService<Dish> {
    void addDish(DishDto dishDto);

    DishDto selectByIdWithCategory(Long id);

    void updateWithFlavor(DishDto dishDto);
}
