package com.itheima.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 员工实体
 */
@Data
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    private String idNumber;//身份证号码

    private Integer status;

    //公共字段填充创建时
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    //公共字段填充创建_更新时
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    //公共字段填充创建
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    //公共字段填充创建_更新时
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

}
