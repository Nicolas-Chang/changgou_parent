package com.changgou.goods.service;

import com.changgou.goods.pojo.Goods;
import com.changgou.goods.pojo.Spu;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface SpuService {

    /***
     * 查询所有
     * @return
     */
    List<Spu> findAll();

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    Spu findById(String id);

    /***
     * 新增
     * @param goods
     */
    void add(Goods goods);

    /***
     * 修改
     * @param spu
     */
    void update(Goods goods);

    /***
     * 删除
     * @param id
     */
    void delete(String id);

    /***
     * 多条件搜索
     * @param searchMap
     * @return
     */
    List<Spu> findList(Map<String, Object> searchMap);

    /***
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    Page<Spu> findPage(int page, int size);

    /***
     * 多条件分页查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    Page<Spu> findPage(Map<String, Object> searchMap, int page, int size);


    /**
     * 根据id查询spu和对应的sku
     * @param id
     * @return
     */
    Goods fingGoodsById(String id);

    /**
     * 审核
     * @param id
     */
    public void audit(String id);

    /**
     *下架
     * @param id
     */
    void pull(String id);

    /**
     * 逻辑删除
     * @param id
     */
    void deleyed(String id);

    /**
     * 根据商品名查询
     * @param serachMap
     * @return
     */
    List<Spu> selected(Map<String,Object> serachMap);
}
