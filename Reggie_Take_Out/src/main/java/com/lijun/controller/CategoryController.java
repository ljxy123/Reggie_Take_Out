package com.lijun.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lijun.common.Result;
import com.lijun.entity.Category;
import com.lijun.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /*
    * 分页查询所有分类
    * */
    @GetMapping("/page")
    public Result<Page> selectByPage(Integer page,Integer pageSize){

        log.info("分页查询菜品的分类,起始页码:{},每页的查询数:{}",page,pageSize);

        Page<Category> categoryPage = new Page<>(page,pageSize);

        LambdaQueryWrapper<Category> wrapper=new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Category::getSort);
        Page<Category> resPage = categoryService.page(categoryPage, wrapper);

        return Result.success(resPage);
    }

    /*
    * 新增分类
    * */

    @PostMapping
    public Result<String> addCategory(@RequestBody Category category){
        log.info("新增分类---->{}",category);

        categoryService.save(category);

        return Result.success("添加分类成功");
    }

    /*
    * 删除分类
    * */
    @DeleteMapping
    public Result<String> deleteCategoryById(@RequestParam(name = "ids") Long id){
        log.info("要删除的分类id为:{}",id);

        categoryService.deleteById(id);

        return Result.success("删除成功");
    }

    /*
    * 修改分类
    * */
    @PutMapping
    public Result<String> updateById(@RequestBody Category category){
        log.info("修改的分类信息----->{}",category);

        categoryService.updateById(category);
        return Result.success("修改成功");
    }

    /*
     * 展示分类下拉列表
     * */
    @GetMapping("/list")
    public Result<List<Category>> selectAllCategory(Category category){
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(category.getType()!=null,Category::getType,category.getType());
        List<Category> list = categoryService.list(wrapper);

        return Result.success(list);
    }
}
