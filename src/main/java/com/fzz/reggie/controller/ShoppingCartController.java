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
        queryWrapper.orderByDesc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);

    }

    @PostMapping("/add")
    public R<ShoppingCart> addToCart(@RequestBody ShoppingCart shoppingCart){
        Long currentId = BaseContext.getCurrent();
        shoppingCart.setUserId(currentId);
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);
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
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
            cart=shoppingCart;
        }
        return R.success(cart);
    }

    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrent());
        if(dishId!=null){
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
            ShoppingCart shoppingCart1 = shoppingCartService.getOne(queryWrapper);
            shoppingCart1.setNumber(shoppingCart1.getNumber()-1);
            Integer latestNumber = shoppingCart1.getNumber();
            if(latestNumber>0){
                shoppingCartService.updateById(shoppingCart1);
            }else if(latestNumber==0){
                shoppingCartService.removeById(shoppingCart1.getId());
            }else if(latestNumber<0){
                return R.error("操作异常");
            }
            return R.success(shoppingCart1);
        }

        if(setmealId!=null) {
            queryWrapper.eq(ShoppingCart::getSetmealId, setmealId);
            ShoppingCart shoppingCart2 = shoppingCartService.getOne(queryWrapper);
            shoppingCart2.setNumber(shoppingCart2.getNumber()-1);
            Integer latestNumber = shoppingCart2.getNumber();
            if(latestNumber>0){
                shoppingCartService.updateById(shoppingCart2);
            }else if(latestNumber==0){
                shoppingCartService.removeById(shoppingCart2.getId());
            }else if(latestNumber<0){
                return R.error("操作异常");
            }
            return R.success(shoppingCart2);
        }

        return R.error("操作异常");
    }

    @DeleteMapping("/clean")
    public R<String> clean(){
        shoppingCartService.clean();

        return R.success("清空购物车成功");
    }
}
