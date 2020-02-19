package com.changgou.search.service.Impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.feign.SkuFegin;
import com.changgou.goods.pojo.Sku;
import com.changgou.pojo.SkuInfo;
import com.changgou.search.dao.ESManagerMapper;
import com.changgou.search.service.SkuSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

@Service
public class SkuSearchServiceImpl implements SkuSearchService {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private SkuFegin skuFegin;

    @Autowired
    private ESManagerMapper esManagerMapper;

    @Override
    public void creatIndexAndMapping() {
        //创建索引
        elasticsearchTemplate.createIndex(SkuInfo.class);
        //创建映射
        elasticsearchTemplate.putMapping(SkuInfo.class);

    }

    @Override
    public void importAll() {
        List<Sku> skuList = skuFegin.findSkuListBySpuId("all");
        if(skuList == null || skuList.size() <=0){
            throw new RuntimeException("此数据不存在 无法导入");
        };
        String json = JSON.toJSONString(skuList);

        List<SkuInfo> skuInfoList = JSON.parseArray(json, SkuInfo.class);
        for (SkuInfo skuInfo : skuInfoList) {
            Map map = JSON.parseObject(skuInfo.getSpec(), Map.class);
            skuInfo.setSpecMap(map);
        }
        esManagerMapper.saveAll(skuInfoList);

    }

    @Override
    public void importDateToEsById(String spuId) {
        List<Sku> skuList = skuFegin.findSkuListBySpuId(spuId);
        if(skuList == null || skuList.size() <=0){
            throw new RuntimeException("此数据不存在 无法导入");
        };
        String jsonString = JSON.toJSONString(skuList);
        List<SkuInfo> skuInfoList = JSON.parseArray(jsonString, SkuInfo.class);
        for (SkuInfo skuInfo : skuInfoList) {
            Map map = JSON.parseObject(skuInfo.getSpec(), Map.class);
            skuInfo.setSpecMap(map);
        }
        esManagerMapper.saveAll(skuInfoList);

    }

    @Override
    public void deleted(String spuId) {
        List<Sku> skuList = skuFegin.findSkuListBySpuId(spuId);
        if(skuList == null || skuList.size() <=0){
            throw new RuntimeException("此数据不存在 无法导入");
        };
        for (Sku sku : skuList) {
            esManagerMapper.deleteById(Long.parseLong(sku.getId()));
        }
    }
}
