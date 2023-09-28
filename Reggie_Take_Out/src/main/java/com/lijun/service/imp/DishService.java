package com.lijun.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lijun.dto.DishDto;
import com.lijun.entity.Category;
import com.lijun.entity.Dish;
import com.lijun.entity.DishFlavor;
import com.lijun.mapper.DishMapper;
import com.lijun.service.CategoryService;
import com.lijun.service.DishFlavorService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishService extends ServiceImpl<DishMapper, Dish> implements com.lijun.service.DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    /*
    * 添加菜品
    * */
    @Override
    public void addDish(DishDto dishDto) {
        //将基本菜品信息进行添加
        this.save(dishDto);

        //将各个风味与添加的菜品进行关联
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        //添加菜品风味
        dishFlavorService.saveBatch(flavors);
    }

    /*
    * 根据id查询对应的菜品
    * */
    @Override
    public DishDto selectByIdWithCategory(Long id) {
        //查询到对应的菜品
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        //查询菜品对应的风味
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> list = dishFlavorService.list(wrapper);
        dishDto.setFlavors(list);

        return  dishDto;
    }

    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //更新菜品的信息
        this.updateById(dishDto);

        //清空菜品对应的风味
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(wrapper);

        //重新更新菜品对应的风味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        //添加菜品风味
        dishFlavorService.saveBatch(flavors);

    }
}
