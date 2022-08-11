package com.fzz.reggie.bean;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data

public class Employee implements Serializable {
    private static final Long serialVersionUID=1L;

    private Long id;
    private String name;
    private String username;
    private String password;
    private String sex;
    private String phone;
    private String idNumber;
    private Integer status;
    /**创建时间*/
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**更新时间*/
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill= FieldFill.INSERT)
    private Long createUser;
    @TableField(fill= FieldFill.INSERT_UPDATE)
    private Long updateUser;

}
