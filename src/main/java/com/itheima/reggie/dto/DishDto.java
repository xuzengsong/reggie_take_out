package com.itheima.reggie.dto;

import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {
    private List<DishFlavor> flavors = new ArrayList<>();

    //分类名（川菜，湘菜），跟据分类id查
    private String categoryName;

    private Integer copies;
}