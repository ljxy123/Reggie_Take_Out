package com.lijun.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lijun.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
