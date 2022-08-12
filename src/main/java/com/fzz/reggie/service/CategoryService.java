package com.fzz.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fzz.reggie.bean.Category;

public interface CategoryService extends IService<Category> {

    void remove(Long id);
}
