package com.lijun.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lijun.common.Result;
import com.lijun.dto.DishDto;
import com.lijun.dto.SetmealDto;
import com.lijun.entity.Category;
import com.lijun.entity.Dish;
import com.lijun.entity.Setmeal;
import com.lijun.service.CategoryService;
import com.lijun.service.DishService;
import com.lijun.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    /*
    * 添加套餐
    * */
    @PostMapping
    public Result<String> addSetmeal(@RequestBody SetmealDto setmealDto){
        log.info("添加的套餐信息:{}",setmealDto);

        setmealService.addSetmealWithDishes(setmealDto);

        return Result.success("添加成功");
    }

    /*
    * 分页查询所有的套餐
    * */
    @GetMapping("/page")
    public Result<Page<SetmealDto>> selectAllSetmeal(Integer page, Integer pageSize, String name){

        //分页查询套餐
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name!=null&&name.length()!=0,Setmeal::getName,name);
        wrapper.orderByDesc(Setmeal::getUpdateTime);
        Page<Setmeal> setmealPage1 = setmealService.page(setmealPage, wrapper);

        Page<SetmealDto> setmealDtoPage = new Page<>();
        BeanUtils.copyProperties(setmealPage,setmealDtoPage,"records");

        //将查询的套餐添加上套餐分类
        List<SetmealDto> setmealDtoList = setmealPage1.getRecords().stream().map(item -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);

            Category category = categoryService.getById(item.getCategoryId());
            setmealDto.setCategoryName(category.getName());

            return setmealDto;
        }).collect(Collectors.toList());

        setmealDtoPage.setRecords(setmealDtoList);

        return Result.success(setmealDtoPage);
    }

    /*
    * 删除套餐
    * */
    @DeleteMapping
    public Result<String> delteSetmeals(@RequestParam List<Long> ids){
        log.info("删除的套餐id:{}",ids);

        setmealService.deletSetmeals(ids);

        return Result.success("删除成功");
    }

    /*
     * 修改套餐的销售状态
     * */
    @PostMapping("/status/{type}")
    public Result<String> setStatus(@PathVariable Integer type,@RequestParam List<Long> ids) {
        log.info("要停售的套餐id为:{}",ids);
        //获得所有对应的套餐
        List<Setmeal> setmeals = setmealService.listByIds(ids);

        //修改销售状态
        setmeals = setmeals.stream().map(item -> {
            item.setStatus(item.getStatus() == 1 ? 0 : 1);
            return item;
        }).collect(Collectors.toList());

        setmealService.updateBatchById(setmeals);

        return Result.success("停售成功");
    }

    /*
    * 根据分类查询对应的菜品
    * */
    @GetMapping("/list")
    public Result<List<Setmeal>> selectByCategoryId(Setmeal setmeal) {
        log.info("查询的套餐信息:{}",setmeal);

        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        wrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        List<Setmeal> list = setmealService.list(wrapper);

        return Result.success(list);
    }
}
