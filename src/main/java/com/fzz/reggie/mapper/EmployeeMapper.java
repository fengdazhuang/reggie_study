package com.fzz.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fzz.reggie.bean.Employee;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeMapper extends BaseMapper<Employee> {


}
