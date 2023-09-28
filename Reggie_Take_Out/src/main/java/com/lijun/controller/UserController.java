package com.lijun.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lijun.common.Result;
import com.lijun.entity.User;
import com.lijun.service.UserService;
import com.lijun.utils.SMSUtils;
import com.lijun.utils.ValidateCodeUtils;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /*
    * 手机发送验证码
    * */
    @PostMapping("/sendMsg")
    public Result<String> sendMsg(@RequestBody User user, HttpSession session) throws Exception {
        log.info("用户手机号:{}",user.getPhone());

        //获取用户的电话号码
        String phone = user.getPhone();

        if (phone!=null&&phone.trim().length()!=0) {
            //生成验证码
            String params = ValidateCodeUtils.generateValidateCode(6).toString();
            log.info("生成的验证码为:{}",params);
            //调用api发送发送验证码
            SMSUtils.sendMsg(params, phone);
            //将验证码保存到本地
            session.setAttribute("code",params);
        }

        return Result.success("验证码发送失败");
    }

    /*
    * 判断验证码登录
    * */
    @PostMapping("/login")
    public Result<User> userLogin(@RequestBody Map map,HttpSession session) {
        log.info("登陆着信息:{}",map.toString());

        //获取电话和code
        String phone = (String) map.get("phone");
        String userCode = (String) map.get("code");

        //从session中获取对应的code
        Object code = session.getAttribute("code");

        if(code!=null&&code.equals(userCode)) {

            //更具电话查询对应的用户
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getPhone,phone);
            User user = userService.getOne(wrapper);

            //如果没有用户，则自动注册一个用户
            if (user==null) {
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);

                userService.save(user);
            }

            session.setAttribute("user",user.getId());

            return Result.success(user);

        }

        return Result.error("登录失败");
    }

}
