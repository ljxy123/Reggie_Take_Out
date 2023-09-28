package com.lijun.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lijun.entity.User;
import com.lijun.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class UserService extends ServiceImpl<UserMapper, User> implements com.lijun.service.UserService {
}
