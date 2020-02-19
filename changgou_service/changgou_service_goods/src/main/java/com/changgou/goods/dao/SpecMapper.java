package com.changgou.goods.dao;

import com.changgou.goods.pojo.Spec;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface SpecMapper extends Mapper<Spec> {


    @Select("SELECT ts.`name`,ts.`options` FROM tb_spec AS ts WHERE ts.`template_id` IN(SELECT tc.`template_id` FROM tb_category tc WHERE tc.`name`=#{categoryName})")
    List<Map> findListByCategoryName(@Param("categoryName") String categoryName);
}
