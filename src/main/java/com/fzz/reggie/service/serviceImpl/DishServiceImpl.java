package com.fzz.reggie.service.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzz.reggie.bean.Dish;
import com.fzz.reggie.mapper.DishMapper;
import com.fzz.reggie.service.DishService;
import org.springframework.stereotype.Service;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
}