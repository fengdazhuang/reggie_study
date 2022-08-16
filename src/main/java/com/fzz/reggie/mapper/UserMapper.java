package com.fzz.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fzz.reggie.bean.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper extends BaseMapper<User> {
}
