package com.fzz.reggie.dto;


import lombok.Data;

import java.util.List;

@Data

public class OrdersDto /*extends Orders*/ {

    // 用户名
    private String userName;

    // 手机号
    private String phone;

    // 地址
    private String address;

    private String consignee;

    /*private List<OrderDetail> orderDetails;*/
	
}
