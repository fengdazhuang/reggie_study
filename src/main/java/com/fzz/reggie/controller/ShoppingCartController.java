package com.fzz.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fzz.reggie.bean.ShoppingCart;
import com.fzz.reggie.common.BaseContext;
import com.fzz.reggie.common.R;
import com.fzz.reggie.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;


    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrent());
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }

    @PostMapping("/add")
    public R<ShoppingCart> addToCart(@RequestBody ShoppingCart shoppingCart){
        shoppingCart.setUserId(BaseContext.getCurrent());
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrent());
        if(dishId!=null){
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        } else {
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        ShoppingCart cart = shoppingCartService.getOne(queryWrapper);

        if(cart!=null){
            cart.setNumber(cart.getNumber()+1);
            shoppingCartService.updateById(cart);
        } else {
            cart=shoppingCart;
            cart.setCreateTime(LocalDateTime.now());
            cart.setNumber(1);
            shoppingCartService.save(shoppingCart);
        }
        return R.success(cart);
    }

    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        Long id = shoppingCart.getDishId();
        if(id!=null){
            queryWrapper.eq(ShoppingCart::getDishId,id);
        } else {
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        ShoppingCart cart = shoppingCartService.getOne(queryWrapper);
        Integer number = cart.getNumber();
        if(number>1){
            cart.setNumber(number -1);
            shoppingCartService.updateById(cart);
        } else {
            cart=shoppingCart;
            cart.setNumber(0);
            shoppingCartService.removeById(cart.getId());
        }
        return R.success(cart);
    }
}
