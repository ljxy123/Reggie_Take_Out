package com.lijun.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lijun.common.Result;
import com.lijun.dto.DishDto;
import com.lijun.entity.Category;
import com.lijun.entity.Dish;
import com.lijun.entity.DishFlavor;
import com.lijun.service.DishFlavorService;
import com.lijun.service.DishService;
import com.lijun.service.imp.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController{

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    /*
    * 分页查询菜品信息
    * */
    @GetMapping("page")
    public Result<Page> selectByPage(Integer page,Integer pageSize,String name){
        log.info("菜品分页查询,起始页：{}，每页展示数量：{}，查询菜品名称；{}",page,pageSize,name);

        //分页查询菜品
        Page<Dish> dishPage = new Page<>(page,pageSize);
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name!=null&&name.length()!=0,Dish::getName,name);
        wrapper.orderByDesc(Dish::getUpdateTime);
        Page<Dish> resPage = dishService.page(dishPage,wrapper);

        /*
        * 将查询结果中的分类id都转换成分类名称
        * */
        Page<DishDto> dishDtoPage = new Page<>();
        //将dishPage中的属性拷贝到新的分页当中
        BeanUtils.copyProperties(resPage,dishDtoPage,"records");
        List<DishDto> list = resPage.getRecords().stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);

            //查询菜品对应的分类名称
            Category category = categoryService.getById(item.getCategoryId());

            if (category!=null){
                dishDto.setCategoryName(category.getName());
            }

            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return Result.success(dishDtoPage);
    }

    /*
    * 新增菜品
    * */
    @PostMapping
    public Result<String> addDish(@RequestBody DishDto dishDto){
        log.info("新增菜品:{}",dishDto);

        dishService.addDish(dishDto);

        return Result.success("添加成功");
    }

    /*
    * 根据id查询菜品
    * */
    @GetMapping("/{id}")
    public Result<DishDto> selectById(@PathVariable Long id){

        DishDto dishDto = dishService.selectByIdWithCategory(id);

        return Result.success(dishDto);
    }

    /*
    * 更新菜品
    * */

    @PutMapping
    public  Result<String> updateDish(@RequestBody DishDto dishDto){

        dishService.updateWithFlavor(dishDto);

        return Result.success("修改成功");
    }

    /*
     * 根据菜品的分类id查询对应的id
     * */
    @GetMapping("/list")
    public Result<List<DishDto>> selectByCategoryId(DishDto dishDto){
        log.info("查询的分类id:{}",dishDto.getCategoryId());

        //获取分类id
        Long id = dishDto.getCategoryId();

        //根据分类id进行菜品的查询
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(id!=null,Dish::getCategoryId,id);
        wrapper.eq(Dish::getStatus,1);
        wrapper.orderByAsc(Dish::getSort).orderByAsc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(wrapper);

        //查询菜品对应的口味
        List<DishDto> dishDtoList = list.stream().map(item -> {
            DishDto dto = new DishDto();
            BeanUtils.copyProperties(item, dto);

            LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DishFlavor::getDishId, dto.getId());
            List<DishFlavor> dishFlavorList = dishFlavorService.list(queryWrapper);

            dto.setFlavors(dishFlavorList);

            return dto;
        }).collect(Collectors.toList());

        return Result.success(dishDtoList);
    }


}
