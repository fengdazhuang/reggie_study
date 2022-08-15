package com.fzz.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fzz.reggie.bean.Dish;
import com.fzz.reggie.dto.DishDto;

import java.util.List;


public interface DishService extends IService<Dish> {

    void saveWithFlavor(DishDto dishDto);

    List<Dish> updateStatus(Integer status,Long[] ids);

    DishDto getWithFlavorById(Long id);

    void updateWithFlavor(DishDto dishDto);

    void removeWithFlavor(Long[] ids);

    List<Dish> listByCategoryId(Long categoryId);
}
