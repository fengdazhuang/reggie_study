package com.fzz.reggie.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzz.reggie.bean.Category;
import com.fzz.reggie.bean.Dish;
import com.fzz.reggie.bean.Setmeal;
import com.fzz.reggie.common.CustomException;
import com.fzz.reggie.common.R;
import com.fzz.reggie.mapper.CategoryMapper;
import com.fzz.reggie.service.CategoryService;
import com.fzz.reggie.service.DishService;
import com.fzz.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;


    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper=new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int count1 = dishService.count(dishLambdaQueryWrapper);
        if(count1>0){
            throw new CustomException("已经关联菜品，删除失败");
        }

        LambdaQueryWrapper<Setmeal> SetmealLambdaQueryWrapper=new LambdaQueryWrapper<>();
        SetmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int count2 = setmealService.count(SetmealLambdaQueryWrapper);
        if(count2>0){
            throw new CustomException("已经关联套餐，删除失败");
        }

        super.removeById(id);
    }
}
