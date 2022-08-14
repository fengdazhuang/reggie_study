package com.fzz.reggie.dto;

import com.fzz.reggie.bean.Dish;
import com.fzz.reggie.bean.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

//    private Integer copies;
}
