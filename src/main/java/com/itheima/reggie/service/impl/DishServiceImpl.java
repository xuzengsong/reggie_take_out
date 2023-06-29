package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper,Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;
    /**
     * ①. 保存菜品基本信息 ;
     *
     * ②. 获取保存的菜品ID ;
     *
     * ③. 获取菜品口味列表，遍历列表，为菜品口味对象属性dishId赋值;
     *
     * ④. 批量保存菜品口味列表;
     * @param dishDto
     */

    /*
    > 由于在 saveWithFlavor 方法中，进行了两次数据库的保存操作，操作了两张表，那么为了保证数据的一致性，我们需要在方法上加上注解 @Transactional来控制事务。
     */
    @Transactional
    @Override
    public void saveWithFlavor(DishDto dishDto) {

        //①. 保存菜品基本信息 ;dishDto继承自dish
        this.save(dishDto);
        //②. 获取保存的菜品ID ;
        Long dishId = dishDto.getId();//菜品id
        //③. 获取菜品口味列表，遍历列表，为菜品口味对象属性dishId赋值;
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        //④. 批量保存菜品口味列表;
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息，从dish表查询
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        //查询当前菜品对应的口味信息，从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表基本信息
        this.updateById(dishDto);

        //清理当前菜品对应口味数据---dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());

        dishFlavorService.remove(queryWrapper);

        //添加当前提交过来的口味数据---dish_flavor表的insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    @Override
    @Transactional
    public void deleteByidsWithFlavor(Long id) {

        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,id);
        dishFlavorService.remove(queryWrapper);

        this.removeById(id);
    }
}