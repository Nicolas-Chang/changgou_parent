package com.changgou.user.feign;

import com.changgou.entity.Result;
import com.changgou.user.pojo.Address;
import com.changgou.user.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@FeignClient(name = "user")
public interface UserFeign {
    @GetMapping("/user/load/{username}")
    public User findByUsername(@PathVariable String username);


    @GetMapping(value = "/address/list")
    public Result<List<Address>> findAddress();

    @PostMapping("/user/decr/point")
    public Result updatePoints(@RequestParam Integer money, @RequestParam String username);

}
