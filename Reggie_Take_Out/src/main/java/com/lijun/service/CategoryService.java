package com.lijun.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lijun.entity.Category;

public interface CategoryService extends IService<Category> {
    void deleteById(Long id);
}
