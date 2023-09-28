package com.lijun.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lijun.common.BaseContext;
import com.lijun.common.ConsumerException;
import com.lijun.dto.OrderDto;
import com.lijun.entity.*;
import com.lijun.mapper.OrdersMapper;
import com.lijun.service.AddressBookService;
import com.lijun.service.OrderDetailService;
import com.lijun.service.ShoppingCartService;
import com.lijun.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrdersService extends ServiceImpl<OrdersMapper, Orders> implements com.lijun.service.OrdersService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    /*
    * 用户下单
    * */
    @Override
    @Transactional
    public void submit(Orders orders) {
        //获得用户id
        Long userId = BaseContext.getCurrentId();
        orders.setUserId(userId);

        //获取购物车当中的商品
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(wrapper);

        //判断购物车当中是否真的有商品,没有则抛出异常
        if(shoppingCartList == null || shoppingCartList.size()==0) {
            new ConsumerException("下单的时候购物车中不能没有商品");
        }

        //获取下单用户的数据
        User userInfo = userService.getById(userId);

        //获取下单地址的具体信息
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);

        //生成订单id，订单号也用这个id
        long orderId = IdWorker.getId();

        //补全orderdetail对象中的数据
        //计算总金额
        AtomicInteger amount=new AtomicInteger(0);

        List<OrderDetail> orderDetailList = shoppingCartList.stream().map(item->{
            OrderDetail orderDetail = new OrderDetail();

            BeanUtils.copyProperties(item,orderDetail,"id","createTime","userId");
            orderDetail.setOrderId(orderId);

            //累加总金额
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());

            return orderDetail;
        }).collect(Collectors.toList());

        //补全对应order对象中的属性
        orders.setId(orderId);
        orders.setNumber(String.valueOf(orderId));
        orders.setStatus(2);
        orders.setUserId(userInfo.getId());
        orders.setAddressBookId(addressBook.getId());
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setAmount(BigDecimal.valueOf(amount.get()));
        orders.setPhone(userInfo.getPhone());
        orders.setAddress(
                (addressBook.getProvinceName() == null? "" : addressBook.getProvinceName())
                +(addressBook.getCityName() == null? "" : addressBook.getCityName())
                +(addressBook.getDistrictName() == null? "" : addressBook.getDistrictName())
                +(addressBook.getDetail() == null?"" : addressBook.getDetail())
                );
        orders.setConsignee(addressBook.getConsignee());

        //在订单表中添加相应的数据
        this.save(orders);

        //在订单明细中添加对应的数据
        orderDetailService.saveBatch(orderDetailList);

        //下单成功之后删除购物车中的所有商品
        shoppingCartService.remove(wrapper);
    }

    /*
    * 分页查询订单明细
    * */
    @Override
    public Page selectOrderDetail(Integer page, Integer pageSize, HttpSession session) {

        //获取用户id
        Long userId= (Long) session.getAttribute("user");

        log.info("分页查询订单明细，用户id:{}",userId);

        //查询订单
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> ordersWrapper = new LambdaQueryWrapper<>();
        ordersWrapper.eq(userId!=null,Orders::getUserId,userId);
        ordersWrapper.orderByDesc(Orders::getOrderTime);
        Page<Orders> ordersPage1 = this.page(ordersPage, ordersWrapper);

        //将基本订单分页进行拷贝
        Page<OrderDto> orderDtoPage = new Page<>();
        BeanUtils.copyProperties(ordersPage,orderDtoPage,"records");

        //更新page中的数据内容
        orderDtoPage.setRecords(
                ordersPage1.getRecords().stream().map(item->{
                    OrderDto orderDto = new OrderDto();
                    BeanUtils.copyProperties(item,orderDto);

                    //根据订单id查询对应的订单明细
                    Long orderId = item.getId();
                    LambdaQueryWrapper<OrderDetail> orderDetailWrapper = new LambdaQueryWrapper<>();
                    orderDetailWrapper.eq(orderId!=null,OrderDetail::getOrderId,orderId);
                    List<OrderDetail> orderDetailsList = orderDetailService.list(orderDetailWrapper);

                    orderDto.setOrderDetails(orderDetailsList);

                    return orderDto;
                }).collect(Collectors.toList())
        );

        return orderDtoPage;
    }
}
