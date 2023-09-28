package com.lijun.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lijun.entity.Employee;
import com.lijun.mapper.EmployeeMapper;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService extends ServiceImpl<EmployeeMapper, Employee> implements com.lijun.service.EmployeeService {
}
