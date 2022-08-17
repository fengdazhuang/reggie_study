package com.fzz.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fzz.reggie.bean.Category;
import com.fzz.reggie.bean.Dish;
import com.fzz.reggie.bean.Setmeal;
import com.fzz.reggie.bean.SetmealDish;
import com.fzz.reggie.common.R;
import com.fzz.reggie.dto.SetmealDto;
import com.fzz.reggie.service.CategoryService;
import com.fzz.reggie.service.DishService;
import com.fzz.reggie.service.SetmealDishService;
import com.fzz.reggie.service.SetmealService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishService dishService;


    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        Page<Setmeal> setmealPage=new Page<>(page,pageSize);
        Page<SetmealDto> setmealDtoPage=new Page<>();
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name),Setmeal::getName,name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(setmealPage,queryWrapper);

        BeanUtils.copyProperties(setmealPage,setmealDtoPage,"records");

        List<Setmeal> records = setmealPage.getRecords();
        List<SetmealDto> list=records.stream().map(((item)->{
            SetmealDto setmealDto=new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);
            Category category = categoryService.getById(setmealDto.getCategoryId());

            if(category!=null){
                setmealDto.setCategoryName(category.getName());
            }
            return setmealDto;
        })).collect(Collectors.toList());
        setmealDtoPage.setRecords(list);
        return R.success(setmealDtoPage);
    }

    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);

        return R.success("新增套餐成功");
    }

    @PostMapping("/status/{status}")
    public R<List<Setmeal>> updateStatus(@PathVariable Integer status, Long[] ids){
        List<Setmeal> list=setmealService.updateStatus(status,ids);
        return R.success(list);
    }

    @GetMapping("/{id}")
    public R<SetmealDto> getWithDishes(@PathVariable Long id){
        SetmealDto setmealDto=setmealService.getWithDishes(id);
        return R.success(setmealDto);
    }

    @PutMapping
    public R<String> updateWithDishes(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithDishes(setmealDto);
        return R.success("修改状态成功");
    }

    @DeleteMapping
    public R<String> removeWithDish(Long[] ids){
        setmealService.removeWithDish(ids);
        return R.success("删除套餐成功");
    }

    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        List<Setmeal> list=setmealService.list(setmeal);

        return R.success(list);
    }

    @GetMapping("/dish/{id}")
    public R<Dish> dish(@PathVariable Long id){
        Dish dish = dishService.getById(id);
        return R.success(dish);
    }


}
