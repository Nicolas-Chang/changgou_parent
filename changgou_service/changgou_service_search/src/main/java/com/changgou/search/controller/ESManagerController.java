package com.changgou.search.controller;


import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.search.dao.ESManagerMapper;
import com.changgou.search.service.SkuSearchService;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.net.idn.Punycode;

@RestController
@RequestMapping("/manager")
public class ESManagerController {
    @Autowired
    private SkuSearchService skuSearchService;


    @GetMapping("/creat")
    public Result creat(){
        skuSearchService.creatIndexAndMapping();
        return new Result(true, StatusCode.OK,"创建成功");
    }

    @GetMapping("/import")
    public Result impor(){
        skuSearchService.importAll();
        return new Result(true, StatusCode.OK,"导入成功");
    }

    @GetMapping("/search/{spuId}")
    public Result select(@PathVariable("spuId") String spuId){
        skuSearchService.importDateToEsById(spuId);
        return new Result(true, StatusCode.OK,"查找");
    }
}
