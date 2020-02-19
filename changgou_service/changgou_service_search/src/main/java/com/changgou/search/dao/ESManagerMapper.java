package com.changgou.search.dao;

import com.changgou.pojo.SkuInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ESManagerMapper extends ElasticsearchRepository<SkuInfo,Long>  {
}
