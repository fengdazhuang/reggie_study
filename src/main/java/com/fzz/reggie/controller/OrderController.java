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

        //获取该订单对应的所有的订单明细表
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId,id);
        List<OrderDetail> orderDetailList = orderDetailService.list(queryWrapper);

        //把原来的购物车给清空
        shoppingCartService.clean();

        //获取用户id
        Long userId = BaseContext.getCurrent();
        List<ShoppingCart> shoppingCartList = orderDetailList.stream().map((item) -> {
            //把从order表中和order_details表中获取到的数据赋值给这个购物车对象
            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setUserId(userId);
            shoppingCart.setImage(item.getImage());
            Long dishId = item.getDishId();
            Long setmealId = item.getSetmealId();
            if (dishId != null) {
                //如果是菜品那就添加菜品的查询条件
                shoppingCart.setDishId(dishId);
            } else {
                //添加到购物车的是套餐
                shoppingCart.setSetmealId(setmealId);
            }
            shoppingCart.setName(item.getName());
            shoppingCart.setDishFlavor(item.getDishFlavor());
            shoppingCart.setNumber(item.getNumber());
            shoppingCart.setAmount(item.getAmount());
            shoppingCart.setCreateTime(LocalDateTime.now());
            return shoppingCart;
        }).collect(Collectors.toList());

        //把携带数据的购物车批量插入购物车表  这个批量保存的方法要使用熟练！！！
        shoppingCartService.saveBatch(shoppingCartList);
        return R.success("添加购物车成功");

    }

    @PostMapping("/submit")
    public R<Order> submit(@RequestBody Order order){
        orderService.submit(order);
        return R.success(order);

    }

}
