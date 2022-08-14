package com.fzz.reggie.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzz.reggie.bean.Dish;
import com.fzz.reggie.bean.DishFlavor;
import com.fzz.reggie.dto.DishDto;
import com.fzz.reggie.mapper.DishFlavorMapper;
import com.fzz.reggie.mapper.DishMapper;
import com.fzz.reggie.service.DishFlavorService;
import com.fzz.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        this.save(dishDto);
        for(DishFlavor dishFlavor:dishDto.getFlavors()){
            dishFlavor.setDishId(dishDto.getId());
        }
        dishFlavorService.saveBatch(dishDto.getFlavors());
    }

    @Override
    public Dish updateStatus(Long ids) {
        Dish dish = this.getById(ids);
        dish.setStatus(dish.getStatus()==0?1:0);
        this.updateById(dish);
        return dish;
    }

    @Override
    public DishDto getWithFlavorById(Long id) {
        Dish dish = this.getById(id);
        DishDto dishDto=new DishDto();
        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        BeanUtils.copyProperties(dish,dishDto);
        List<DishFlavor> list=dishFlavorMapper.selectList(queryWrapper);
        dishDto.setFlavors(list);
        return dishDto;
    }


}
