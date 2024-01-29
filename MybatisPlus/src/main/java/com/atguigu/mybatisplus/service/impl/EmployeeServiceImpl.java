package com.atguigu.mybatisplus.service.impl;

import com.atguigu.mybatisplus.bean.Employee;
import com.atguigu.mybatisplus.mapper.EmployeeMapper;
import com.atguigu.mybatisplus.service.EmployeeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2024-01-28
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

}
