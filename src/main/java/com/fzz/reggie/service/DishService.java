package com.fzz.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fzz.reggie.bean.Dish;
import com.fzz.reggie.dto.DishDto;


public interface DishService extends IService<Dish> {
    void saveWithFlavor(DishDto dishDto);

    Dish updateStatus(Long ids);

    DishDto getWithFlavorById(Long id);
}
