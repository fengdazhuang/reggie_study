package com.fzz.reggie.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fzz.reggie.bean.Category;
import com.fzz.reggie.bean.Dish;
import com.fzz.reggie.common.R;
import com.fzz.reggie.dto.DishDto;
import com.fzz.reggie.service.CategoryService;
import com.fzz.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        Page<Dish> pageInfo=new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage=new Page<>();
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name),Dish::getName,name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo,queryWrapper);

        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list=records.stream().map(((item)->{
            DishDto dishDto=new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(category!=null){
                dishDto.setCategoryName(category.getName());
            }
            return dishDto;
        })).collect(Collectors.toList());

        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }

    @DeleteMapping
    public R<String> remove(Long[] ids){
        dishService.removeWithFlavor(ids);
        return R.success("批量删除成功");
    }

    @GetMapping("/{id}")
    public R<DishDto> getDish(@PathVariable("id") Long id){
        DishDto dishDto= dishService.getWithFlavorById(id);
        return R.success(dishDto);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        String key="dish_"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);
        return R.success("修改菜品成功");
    }

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    @PostMapping("/status/{status}")
    public R<List<Dish>> updateStatus(@PathVariable Integer status,Long[] ids){
        List<Dish> list=dishService.updateStatus(status,ids);
        return R.success(list);
    }

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        List<DishDto> dishDtoList=null;
        String key="dish_"+dish.getCategoryId()+"_"+dish.getStatus();
        //2.从redis获取数据，转为String类型
        String resultSting = (String) redisTemplate.opsForValue().get(key);
        //3.把json转换为List<DishDto>类型
        dishDtoList= JSON.parseArray(resultSting,DishDto.class);
        //4.如果redis中数据不为空，则直接返回
        if(dishDtoList!=null){
            log.info("从redis获取数据成功");
            return R.success(dishDtoList);
        }
        //5.如果redis中无数据，查询数据库
        dishDtoList=dishService.list(dish);
        //6.将查询得到的数据转为String
        String result = JSON.toJSONString(dishDtoList);
        //7.保存到redis中
        redisTemplate.opsForValue().set(key,result,60, TimeUnit.MINUTES);
        //8.返回数据
        return R.success(dishDtoList);

    }



}
