package com.fzz.reggie.service.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzz.reggie.bean.Employee;
import com.fzz.reggie.mapper.EmployeeMapper;
import com.fzz.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {



}
