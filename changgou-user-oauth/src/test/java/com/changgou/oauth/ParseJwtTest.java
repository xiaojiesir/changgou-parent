package com.changgou.oauth;

import org.junit.Test;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;

import java.util.Base64;


public class ParseJwtTest {

    /***
     * 校验令牌
     */
    @Test
    public void testParseToken() {
        //令牌
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IlJPTEVfVklQLFJPTEVfVVNFUiIsIm5hbWUiOiJpdGhlaW1hIiwiaWQiOiIxIn0.RRogSetWeAnyB8he3L18kfnNyq--nU9CkW-gbJjVs79pvuI9Nw3MGSK3SitGEFnayfQa1wffR8nIRjktp2uAmDX1gDY-oIRr5PKM2LHoYnEViMKRH5mSL8f3J9rcJ_pJI59mGCpBn6N77WaDD_aAXPrGjwk9sEcw75pFb5hTyd8qbmy2msTrRobtzdI1zoGJalEllE-IkTdmKcpjzFS30QniTqqY5kb9diPyaDrDefiskumvYMaelHb0T84L5S-6rgkzdpI2o7FI7Xwf3PQZ6L2xNX1Vp9rLx_cZyA5PM7SZtC2W9vxUrKmTCvwo4bAVcNen7G3EXDzJALpjR3kf3w";

        //公钥
        String publickey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmKmOYRhE/XTupA/EC4uSwnSQ5attrgNShlVvp57IHpocqM3UIf9XSQSiEERg67N/OouyuK/j9TLPoHC+CBcFO8vHp+GPiW6z6lu6GGd6IfwtHHdVnynE3o/SEX3ojywQy63NNOiJcipcZvBtMj1xsEuF5+xJJ2OUoogxtvY/KRQMw+IyVPyBcIF0rsBLQ2aehjAI/2crfGNky1lbDG2TS+r5bKScdAJiNmi4OAt5dQT6AjMH6UqpHYHJrqSy+mO7ZdzOwhrjLkVPvc2NeLezbfBoaYhgDO5/kVe+WwF4Z2tcW7o2nrpknbVJ3a3+clREWT/xJAy2BQXYvjwv4kaEBwIDAQAB-----END PUBLIC KEY-----";

        //校验Jwt
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publickey));

        //获取Jwt原始内容 载荷
        String claims = jwt.getClaims();
        System.out.println(claims);
        //jwt令牌
        String encoded = jwt.getEncoded();
        System.out.println(encoded);
    }

    @Test
    public void testDecodeBase64() throws Exception {
        //令牌
        String token = "Y2hhbmdnb3U6Y2hhbmdnb3U=";
        byte[] decode = Base64.getDecoder().decode(token);
        String decodeStr  = new String(decode,"UTF-8");

        //解密结果:   changgou:changgou
        System.out.println(decodeStr);
    }
}
