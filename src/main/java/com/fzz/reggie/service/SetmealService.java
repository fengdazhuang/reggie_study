package com.fzz.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fzz.reggie.bean.Setmeal;
import com.fzz.reggie.dto.SetmealDto;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    List<Setmeal> updateStatus(Integer status,Long[] ids);

    SetmealDto getWithDishes(Long id);

    void updateWithDishes(SetmealDto setmealDto);

    void removeWithDish(Long[] ids);
}
