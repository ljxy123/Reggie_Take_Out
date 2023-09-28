package com.lijun.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lijun.common.Result;
import com.lijun.entity.Employee;
import com.lijun.service.EmployeeService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    //登录操作
    @PostMapping("/login")
    public Result login(HttpSession session, @RequestBody Employee employee){
        log.info("sessionUserId--->{}",session.getAttribute("userId"));

        //将页面提交的密码进行MD5的加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        log.info("加密后的password------>{}",password);

        //根据页面提交的用户名username来查询用户
        LambdaQueryWrapper<Employee> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getUsername,employee.getUsername());
        Employee one = employeeService.getOne(wrapper);

        //如果没有查询到则返回登录失败的结果
        if(one==null){
            return Result.error("用户名错误!!");
        }

        //进行密码比对，如果密码不一致则返回登录失败的结果
        if(!one.getPassword().equals(password)){
            return Result.error("密码错误!!");
        }

        //查看用户的状态，如果已经禁用，则返回对应的结果
        if(one.getStatus()==0){
            return Result.error("用户已经被禁用");
        }

        //登录成功，将员工的id存入到session并返回登录的结果
        session.setAttribute("userId",one.getId());
        log.info("session--->{}",session.hashCode());

        return Result.success(one);

    }

    /*
    * 退出操作
    * */
    @PostMapping("/logout")
    public Result<String> logout(HttpSession session){
        session.removeAttribute("userId");
        return Result.success("退出登录成功");
    }

    /*
    * 添加员工操作
    * */
    @PostMapping
    public Result<String> addEmp(HttpSession session,@RequestBody Employee employee){
        log.info("添加员工的信息----{}",employee);

        //给新员工添加默认的密码
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //获取当前登录者的id
        Long empId = (Long) session.getAttribute("userId");

        //通过mybatisplus中的公共字段自动填充实现
        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());
        //employee.setCreateUser(empId);
        //employee.setUpdateUser(empId);

        employeeService.save(employee);
        return Result.success("添加成功");
    }

    /*
    * 分页查询员工
    * */
    @GetMapping ("/page")
    public Result<Page> selectByPage(Integer page,Integer pageSize,String name){
        log.info("起始页:{},每页查询数量:{},差选条件:{}",page,pageSize,name);

        Page<Employee> employeePage = new Page<>();

        //设置查询条件
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name!=null,Employee::getName,name);
        wrapper.orderByDesc(Employee::getUpdateTime);

        //分页查询
        Page<Employee> resPage = employeeService.page(employeePage, wrapper);

        return Result.success(resPage);
    }

    /*
    * 更新员工信息
    * */
    @PutMapping
    public Result<String> updateById(HttpSession session,@RequestBody Employee employee){
        log.info("修改的员工信息--->{}",employee);

        //修改者的id和更新的时间-->通过mybatisplus中的公共字段自动填充实现
        //Long userId = (Long) session.getAttribute("userId");
        //employee.setUpdateUser(userId);
        //employee.setUpdateTime(LocalDateTime.now());

        //更新数据
        employeeService.updateById(employee);

        return Result.success("修改成功");
    }

    /*
    * 更具id查询用户
    * */
    @GetMapping("/{id}")
    public Result<Employee> selectById(@PathVariable Long id){
        log.info("要查询的id为--->{}",id);

        Employee employee = employeeService.getById(id);

        return Result.success(employee);
    }
}
