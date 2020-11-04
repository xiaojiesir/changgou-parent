package com.changgou.pay.service;

import java.util.Map;


public interface WeixinPayService {
    /**
     * 生成二维码
     *
     * @param out_trade_no
     * @param total_fee
     * @return
     */
    Map<String, String> createNative(Map<String, String> paramMap);

    /**
     * @param out_trade_no
     * @return
     */
    Map<String, String> queryStatus(String out_trade_no);
}
