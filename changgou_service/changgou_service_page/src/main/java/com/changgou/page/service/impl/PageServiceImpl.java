package com.changgou.page.service.impl;

import com.changgou.entity.Result;
import com.changgou.goods.feign.CategoryFegin;
import com.changgou.goods.feign.SkuFegin;
import com.changgou.goods.feign.SpuFegin;
import com.changgou.goods.pojo.Category;
import com.changgou.goods.pojo.Sku;
import com.changgou.goods.pojo.Spu;
import com.changgou.page.service.PageService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class PageServiceImpl implements PageService {



    @Autowired
    private SpuFegin spuFegin;
    @Autowired
    private SkuFegin skuFegin;
    @Autowired
    private CategoryFegin categoryFegin;

    @Autowired
    private TemplateEngine templateEngine;
    @Value("${pagepath}")
    private String pagepath;

    @Override
    public void generatemplatePage(String spuId) {

        Context context = new Context();
        Map<String, Object> item = this.findItem(spuId);
        context.setVariables(item);

        //创建文件
        File dir = new File("D:\\items");
        if(!dir.exists()){
            dir.mkdirs();
        }
        //定义输入流
        File file = new File(dir+"/"+spuId+".html");
        Writer writer = null;
        try {
            writer=new PrintWriter(file);
            templateEngine.process("item",context,writer);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    private Map<String,Object> findItem(String spuId){

        Map<String,Object> resultMap = new HashMap<>();
        Result<Spu> spuResult = spuFegin.findById(spuId);
        Spu spu = spuResult.getData();
        resultMap.put("spu",spu);

        if(spu != null){
            if(StringUtils.isNotEmpty(spu.getImages())){
                resultMap.put("imageList",spu.getImages().split(","));
            }
        }

        Category category1 = (Category) categoryFegin.findById(spu.getCategory1Id()).getData();
        Category category2 = (Category) categoryFegin.findById(spu.getCategory2Id()).getData();
        Category category3 = (Category) categoryFegin.findById(spu.getCategory3Id()).getData();
        resultMap.put("category1",category1);
        resultMap.put("category2",category2);
        resultMap.put("category3",category3);

        List<Sku> bySpuId = skuFegin.findSkuListBySpuId(spuId);

        resultMap.put("skuList",bySpuId);

        return resultMap;


    }
}
