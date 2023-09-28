package com.lijun.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lijun.entity.AddressBook;
import com.lijun.mapper.AddressBookMapper;
import org.springframework.stereotype.Service;

@Service
public class AddressBookService extends ServiceImpl<AddressBookMapper, AddressBook> implements com.lijun.service.AddressBookService {
}
