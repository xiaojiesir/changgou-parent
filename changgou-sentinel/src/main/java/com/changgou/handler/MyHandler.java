package com.changgou.handler;

import com.alibaba.csp.sentinel.slots.block.BlockException;

public class MyHandler {
    public static String globalHandlerException(BlockException e) {
        return "----GlobalHandler";
    }

    public static String handlerException(BlockException e) {
        return "----Handler";
    }
}
