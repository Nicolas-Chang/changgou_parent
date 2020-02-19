package com.changgou.search.service.Impl;

import com.alibaba.fastjson.JSON;
import com.changgou.pojo.SkuInfo;
import com.changgou.search.service.SearchService;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.util.QueryBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.fetch.subphase.highlight.AbstractHighlighterBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class SearchServiceImpl implements SearchService {


    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public Map search( Map<String, String> searchMap) {

        //创建接收对象
        Map<String,Object> infoMap = new HashMap<>();

        if(searchMap != null && searchMap.size() >= 0){
            //组合条件
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

            if (StringUtils.isNotEmpty(searchMap.get("keywords"))){
                boolQuery.must(QueryBuilders.matchQuery("name",searchMap.get("keywords")).operator(Operator.AND));
            }
            String skuBrand = "skuBrand";

            if(StringUtils.isNotEmpty(searchMap.get("brandName"))){
                boolQuery.filter(QueryBuilders.termQuery("brandName",searchMap.get("brandName")));
            }

            //条件规格

            Set<String> sets = searchMap.keySet();
            for (String set : sets) {
                if(set.startsWith("spec_")){
                    String value = searchMap.get(set).replace("%2B", "+");
                    boolQuery.filter(QueryBuilders.termQuery(("specMap."+set.substring(5) + ".keyword"),value));//域名    封装的结果
                }
            }
            //价格区间查询
            if(StringUtils.isNotEmpty(searchMap.get("price"))){
                String prices = searchMap.get("price");
                String[] price = prices.split("-");
                if(price.length >= 2){
                    //如果有  小于第二个
                    boolQuery.filter(QueryBuilders.rangeQuery("price").lte(price[1]));
                }
                //大于第一个
                boolQuery.filter(QueryBuilders.rangeQuery("price").gte(price[0]));

            }
            //原生搜索实体类
            NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
            nativeSearchQueryBuilder.withQuery(boolQuery);
            //品牌聚合
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(skuBrand).field("brandName"));//第一个参数查询出来的列名 第二个参数 按照那个域查询
            //规格聚合
            String skuSpec = "skuSpec";
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(skuSpec).field("spec.keyword"));
            //排序
            if(StringUtils.isNotEmpty(searchMap.get("sortField"))){//排序的列名
                if("ASC".equals(searchMap.get("sortRule"))){//排序的方式
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort(searchMap.get("sortField")).order(SortOrder.ASC));
                }
                if ("DESC".equals(searchMap.get("sortRule"))){
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort(searchMap.get("sortField")).order(SortOrder.DESC));
                }
            }
            HighlightBuilder.Field field = new HighlightBuilder
                    .Field("name")   //高亮的域
                    .preTags("<span style='color:red'>")  //前置
                    .postTags("</span>");  //后置
            nativeSearchQueryBuilder.withHighlightFields(field);
            //分页
            String pageNum = searchMap.get("pageNum");
            String pageSize = searchMap.get("pageSize");
            if(pageNum == null){
                pageNum = "1";
            }
            if (pageSize == null){
                pageSize = "20";
            }
            nativeSearchQueryBuilder.withPageable(PageRequest.of(Integer.parseInt(pageNum)-1,Integer.parseInt(pageSize)));
            AggregatedPage<SkuInfo> skuInfos = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class, new SearchResultMapper() {

                @Override
                public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                    List<T> list = new ArrayList<>();
                    SearchHits hits = searchResponse.getHits();//得到结果集
                    if (hits != null) {
                        for (SearchHit hit : hits) {
                            SkuInfo skuInfo = JSON.parseObject(hit.getSourceAsString(), SkuInfo.class);
                            Map<String, HighlightField> highlightFieldMap = hit.getHighlightFields();
                            if(highlightFieldMap != null && highlightFieldMap.size() >0){
                                skuInfo.setName(highlightFieldMap.get("name").getFragments()[0].toString());
                            }
                            list.add((T) skuInfo);
                        }
                    }

                    return new AggregatedPageImpl<T>(list,pageable,hits.getTotalHits(),searchResponse.getAggregations());
                }
            });
            // 总条数
            long totalElements = skuInfos.getTotalElements();
            infoMap.put("total",totalElements);
            // 总页数
            int totalPages = skuInfos.getTotalPages();
            //每页条数
            infoMap.put("pageSize",pageSize);
            infoMap.put("totalPages",totalPages);
            // 查询结果
            List<SkuInfo> skuInfos1 = skuInfos.getContent();
            infoMap.put("rows",skuInfos1);
//            StringTerms stringTerms= (StringTerms) skuInfos.getAggregation(skuBrand);
            StringTerms brandTerms = (StringTerms) skuInfos.getAggregation(skuBrand);//获取聚合结果
//            List<String> stringList = stringTerms.getBuckets().stream().map(a -> a.getKeyAsString()).collect(Collectors.toList());
            // 将对象转为LIst集合
            List<String> brandList = brandTerms.getBuckets().stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());
            infoMap.put("brandList",brandList);
            //封装规格聚合
            StringTerms stringTerms = (StringTerms) skuInfos.getAggregation(skuSpec);
            List<String> stringList = stringTerms.getBuckets().stream().map(s -> s.getKeyAsString()).collect(Collectors.toList());
            infoMap.put("skuspec",handForMap(stringList));
            infoMap.put("pageNum",pageNum);
            return infoMap;
        }
        return null;
    }

   public Map<String,Set<String>> handForMap(List<String> stringList){
        Map<String,Set<String>> specMap = new HashMap<>();
        if(stringList !=null && stringList.size() >0){
            for (String entry : stringList) {
                Map<String,String> entryMap = JSON.parseObject(entry, Map.class);
                for (String key : entryMap.keySet()) {
                    Set<String> value = specMap.get(key);
                    if(value == null){
                        value = new HashSet<>();
                    }
                    value.add(entryMap.get(key));
                    specMap.put(key,value);
                }
            }
            }
        return specMap;
        }

/*    public Map<String, Set<String>> formartSpec(List<String> specList){
        Map<String,Set<String>> resultMap = new HashMap<>();
        if (specList!=null && specList.size()>0){
            for (String specJsonString : specList) {  //"{'颜色': '黑色', '尺码': '250度'}"
                //将获取到的json转换为map
                Map<String,String> specMap = JSON.parseObject(specJsonString, Map.class);
                for (String specKey : specMap.keySet()) {
                    Set<String> specSet = resultMap.get(specKey);
                    if (specSet == null){
                        specSet = new HashSet<String>();
                    }
                    //将规格信息存入set中
                    specSet.add(specMap.get(specKey));
                    //将set存入map
                    resultMap.put(specKey,specSet);
                }
            }
        }
        return resultMap;
    }*/
    }
