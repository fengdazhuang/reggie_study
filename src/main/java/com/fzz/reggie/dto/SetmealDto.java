package com.fzz.reggie.dto;

import com.fzz.reggie.bean.Setmeal;
import com.fzz.reggie.bean.SetmealDish;
import lombok.Data;

import java.util.List;

@Data

public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
