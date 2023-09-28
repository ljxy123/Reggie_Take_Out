package com.lijun.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lijun.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
