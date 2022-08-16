package com.fzz.reggie.controller;

import com.fzz.reggie.bean.ShoppingCart;
import com.fzz.reggie.common.R;
import com.fzz.reggie.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        List<ShoppingCart> list = shoppingCartService.list();

        return R.success(list);
    }

    @PostMapping("/add")
    public R<String> addToCart(@RequestBody ShoppingCart shoppingCart){
        shoppingCartService.save(shoppingCart);

        return R.success("添加至购物车成功");
    }
}
