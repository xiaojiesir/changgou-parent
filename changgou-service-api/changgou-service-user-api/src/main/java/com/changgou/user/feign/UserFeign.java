package com.changgou.user.feign;

import com.changgou.user.pojo.User;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "user")
@RequestMapping(value = "/user")
public interface UserFeign {

    /**
     * 根据id查询用户信息
     *
     * @param id
     * @return
     */
    @GetMapping({"/load/{id}"})
    public Result<User> findById(@PathVariable(value="id") String id);

    /**
     * 添加积分
     *
     * @param points
     * @param username
     * @return
     */
    @GetMapping(value = "/points/add")
    public Result addPoints(@RequestParam(value = "points") Integer points
            , @RequestParam(value = "username") String username);
}
