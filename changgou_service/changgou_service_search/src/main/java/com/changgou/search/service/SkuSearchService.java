package com.changgou.search.service;

public interface SkuSearchService {

    //创建索引库
    void creatIndexAndMapping();
    //将全部数据导入es
    void importAll();
    //根据spuId导入
    void importDateToEsById(String spuId);

    void deleted(String spuId);
}
