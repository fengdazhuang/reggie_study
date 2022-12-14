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
        return R.success("??????????????????");
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
        return R.success("??????????????????");
    }

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        dishService.saveWithFlavor(dishDto);
        return R.success("??????????????????");
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
        //2.???redis?????????????????????String??????
        String resultSting = (String) redisTemplate.opsForValue().get(key);
        //3.???json?????????List<DishDto>??????
        dishDtoList= JSON.parseArray(resultSting,DishDto.class);
        //4.??????redis????????????????????????????????????
        if(dishDtoList!=null){
            log.info("???redis??????????????????");
            return R.success(dishDtoList);
        }
        //5.??????redis??????????????????????????????
        dishDtoList=dishService.list(dish);
        //6.??????????????????????????????String
        String result = JSON.toJSONString(dishDtoList);
        //7.?????????redis???
        redisTemplate.opsForValue().set(key,result,60, TimeUnit.MINUTES);
        //8.????????????
        return R.success(dishDtoList);

    }



}
