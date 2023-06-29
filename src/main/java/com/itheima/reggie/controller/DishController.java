package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;


    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize,String name){
        //构造分页构造器对象
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null,Dish::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //执行分页查询
        dishService.page(pageInfo,queryWrapper);

        //对象拷贝,除"records"以外
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        List<Dish> records = pageInfo.getRecords();
        //改造"records"
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());

        dishService.saveWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品成功");
    }

//    /**
//     * 删除菜品
//     * @param ids
//     * @return
//     */
//    @DeleteMapping
//    public R<String> delete(Long ids){
//        log.info("删除：{}", ids );
//        dishService.deleteByidsWithFlavor(ids);
//        return R.success("删除菜品成功");
//    }
    /**
     * 批量删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete( Long[] ids){
        log.info("删除：{}", ids );
        for (Long id:ids) {
            dishService.deleteByidsWithFlavor(id);
        }
        return R.success("删除菜品成功");
    }

    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable Integer status, Long[] ids){
        log.info("更新状态：{}", ids );
        List<Dish> dishs = new ArrayList<>();
        for (Long id:ids) {
            Dish dish = dishService.getById(id);
            dish.setStatus(status);
            dishs.add(dish);
        }
        dishService.updateBatchById(dishs);
        return R.success("更新菜品成功");
    }

    /**
     * 根据条件查询对应的菜品数据
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish){
        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getSort).orderByAsc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);
        return R.success(list);
    }


}