package com.changgou.goods.feign;


import com.changgou.entity.Result;
import com.changgou.goods.pojo.Spu;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "goods")
public interface SpuFegin {
    @GetMapping("/spu/{id}")
     Result<Spu> findById(@PathVariable String id);
}
