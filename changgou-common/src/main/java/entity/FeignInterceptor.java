package entity;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

public class FeignInterceptor  implements RequestInterceptor {
    /**
     * feign调用之前进行拦截
     * @param requestTemplate
     */
    @Override
    public void apply(RequestTemplate requestTemplate) {
        /**
         * 从数据库加载查询用户信息
         * 1.没用令牌,Feign调用之前,生成令牌(admin)
         * 2.Feign调用之前,令牌存放到Header文件中
         * 3.请求->feign调用->拦截器RequestInterceptor->Feign调用之前执行拦截
         */
        try {
            //使用RequestContextHolder工具获取request相关变量
            //记录当前用户请求的所有数据,包含请求头和请求参数等
            //用户当前请求的时候对应线程的数据,如果开启了熔断,默认是线程池隔离,会开启新的线程,需要将熔断策略换成信号量隔离,此时不会开启新线程
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                //取出request
                HttpServletRequest request = attributes.getRequest();
                //获取所有头文件信息的key
                Enumeration<String> headerNames = request.getHeaderNames();
                if (headerNames != null) {
                    while (headerNames.hasMoreElements()) {
                        //头文件的key
                        String name = headerNames.nextElement();
                        //头文件的value
                        String values = request.getHeader(name);
                        System.out.println(name+":"+values);
                        //将令牌数据添加到头文件中
                        requestTemplate.header(name, values);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
