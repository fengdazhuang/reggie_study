package com.fzz.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fzz.reggie.bean.ShoppingCart;

public interface ShoppingCartService extends IService<ShoppingCart> {
    void clean();
}
