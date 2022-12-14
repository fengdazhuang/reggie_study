package com.fzz.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fzz.reggie.bean.Order;
import com.fzz.reggie.bean.OrderDetail;
import com.fzz.reggie.bean.ShoppingCart;
import com.fzz.reggie.common.BaseContext;
import com.fzz.reggie.common.R;
import com.fzz.reggie.service.OrderDetailService;
import com.fzz.reggie.service.OrderService;
import com.fzz.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private ShoppingCartService shoppingCartService;

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
    public R<String> againOrder(@RequestBody Map<String,String> map){
        String ids = map.get("id");
        Long id = Long.parseLong(ids);

        //????????????????????????????????????????????????
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId,id);
        List<OrderDetail> orderDetailList = orderDetailService.list(queryWrapper);

        //??????????????????????????????
        shoppingCartService.clean();

        //????????????id
        Long userId = BaseContext.getCurrent();
        List<ShoppingCart> shoppingCartList = orderDetailList.stream().map((item) -> {
            //??????order?????????order_details??????????????????????????????????????????????????????
            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setUserId(userId);
            shoppingCart.setImage(item.getImage());
            Long dishId = item.getDishId();
            Long setmealId = item.getSetmealId();
            if (dishId != null) {
                //????????????????????????????????????????????????
                shoppingCart.setDishId(dishId);
            } else {
                //??????????????????????????????
                shoppingCart.setSetmealId(setmealId);
            }
            shoppingCart.setName(item.getName());
            shoppingCart.setDishFlavor(item.getDishFlavor());
            shoppingCart.setNumber(item.getNumber());
            shoppingCart.setAmount(item.getAmount());
            shoppingCart.setCreateTime(LocalDateTime.now());
            return shoppingCart;
        }).collect(Collectors.toList());

        //???????????????????????????????????????????????????  ???????????????????????????????????????????????????
        shoppingCartService.saveBatch(shoppingCartList);
        return R.success("?????????????????????");

    }

    @PostMapping("/submit")
    public R<Order> submit(@RequestBody Order order){
        orderService.submit(order);
        return R.success(order);

    }

}
