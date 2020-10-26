package com.itheima.base64;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class Base64Test {

    @Test
    public void testEncode() throws UnsupportedEncodingException {
        byte[] encode = Base64.getEncoder().encode("abcdefg".getBytes());
        String encodestr = new String(encode, "UTF-8");
        System.out.println(encodestr);
    }
    @Test
    public void testDecode() throws UnsupportedEncodingException {
        byte[] decode = Base64.getDecoder().decode("YWJjZGVmZw==");
        String encodestr = new String(decode, "UTF-8");
        System.out.println(encodestr);
    }
}
