package com.fzz.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fzz.reggie.bean.Order;
import com.fzz.reggie.common.R;
import com.fzz.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String number,LocalDateTime beginTime,LocalDateTime endTime){
        Page<Order> orderPage=new Page<>(page,pageSize);
        LambdaQueryWrapper<Order> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(number),Order::getNumber,number);
        queryWrapper.gt(beginTime!=null,Order::getCheckoutTime,beginTime);
        queryWrapper.lt(endTime!=null,Order::getCheckoutTime,endTime);
        queryWrapper.orderByDesc(Order::getCheckoutTime);
        orderService.page(orderPage,queryWrapper);
        return R.success(orderPage);
    }

    @GetMapping("/userPage")
    public R<Page> page(int page, int pageSize){
        Page<Order> orderPage=new Page<>(page,pageSize);
        LambdaQueryWrapper<Order> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Order::getCheckoutTime);
        orderService.page(orderPage,queryWrapper);
        return R.success(orderPage);
    }

    @PostMapping("/again")
    public R<Order> againOrder(@RequestBody Long id){
        Order order = orderService.getById(id);
        return R.success(order);
    }

}
