package com.changgou.goods.feign;


import com.changgou.entity.Result;
import com.changgou.goods.pojo.Sku;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "goods")
public interface SkuFegin {

    @GetMapping("sku/spu/{spuId}")
    List<Sku> findSkuListBySpuId(@PathVariable("spuId") String spuId);

    @GetMapping("sku/{id}")
    Result<Sku> findById(@PathVariable String id);

    @PostMapping("/sku/decr/cont")
    Result updateNum(@RequestParam("username") String username);

    @PutMapping("/sku/resumeStockNum")
    public Result resumeStockNum(@RequestParam("skuId") String skuId,@RequestParam("num") Integer num);
}
