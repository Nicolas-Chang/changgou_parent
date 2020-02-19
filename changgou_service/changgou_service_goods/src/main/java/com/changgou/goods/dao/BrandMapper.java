package com.changgou.goods.dao;

import com.changgou.goods.pojo.Brand;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface BrandMapper extends Mapper<Brand> {

    @Select("SELECT b.`name`,b.`image` FROM tb_brand b WHERE b.`id` IN ( SELECT c.`brand_id`\tFROM tb_category_brand c WHERE c.`category_id` IN (SELECT tc.`id` FROM tb_category tc WHERE tc.`name` = #{categoryName} ))")
    List<Map> findListByCategoryName(@Param("categoryName") String categoryName);
}
