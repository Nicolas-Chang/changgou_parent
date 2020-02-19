package com.changgou.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fescar.spring.annotation.GlobalTransactional;
import com.changgou.goods.feign.SkuFegin;
import com.changgou.order.config.RabbitMQConfig;
import com.changgou.order.dao.OrderItemMapper;
import com.changgou.order.dao.OrderLogMapper;
import com.changgou.order.dao.OrderMapper;
import com.changgou.order.dao.TaskMapper;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.pojo.OrderLog;
import com.changgou.order.pojo.Task;
import com.changgou.order.service.CartService;
import com.changgou.order.service.OrderService;
import com.changgou.order.pojo.Order;
import com.changgou.order.util.IdWorker;
import com.changgou.pay.feign.payFeign;
import com.changgou.user.feign.UserFeign;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private CartService catService;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SkuFegin skuFegin;

    @Autowired
    private UserFeign userFeign;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private OrderLogMapper orderLogMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private payFeign payFeign;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 查询全部列表
     *
     * @return
     */
    @Override
    public List<Order> findAll() {
        return orderMapper.selectAll();
    }

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    @Override
    public Order findById(String id) {
        return orderMapper.selectByPrimaryKey(id);
    }


    /**
     * 增加
     *
     * @param order
     */
    @Override
    @GlobalTransactional
    public String add(Order order) {
        String username = order.getUsername();
        Map map = catService.list(username);
        List<OrderItem> orderItemList = (List<OrderItem>) map.get("orderItemList");
        IdWorker idWorker = new IdWorker();
        String orderId = idWorker.nextId() + "";
        order.setId(orderId);
        order.setCloseTime(new Date());
        order.setUpdateTime(new Date());
        order.setTotalNum((Integer) map.get("totalNum"));
        order.setTotalMoney((Integer) map.get("totalMoney"));
        order.setPayMoney((Integer) map.get("totalMoney"));
        order.setBuyerRate("0");  //0 未評價
        order.setSourceType("1");  //來源 pc端
        order.setOrderStatus("0");  // 0 未完成 1 已完成 2 退貨
        order.setPayStatus("0");   // 0 未支付 1 已支付  2 支付失敗
        order.setConsignStatus("0");   // 0  未發貨  1 已發貨 3 退貨
        int insert = orderMapper.insert(order);
        // 添加具體訂單
        for (OrderItem orderItem : orderItemList) {
            orderItem.setId(idWorker.nextId() + "");
            orderItem.setIsReturn("0");   //是否退貨
            orderItem.setOrderId(order.getId());   // 設置於主表的關係
            orderItemMapper.insert(orderItem);
            Integer money = orderItem.getMoney();
        }
        Task task = new Task();
        task.setMqExchange(RabbitMQConfig.EX_BUYING_ADDPOINTUSER);
        task.setMqRoutingkey(RabbitMQConfig.CG_BUYING_ADDPOINT_KEY);
        task.setCreateTime(new Date());
        task.setUpdateTime(new Date());
        Map map1 = new HashMap();
        map1.put("username", username);
        map1.put("orderId", order.getId());
        map1.put("point", order.getPayMoney());
        task.setRequestBody(JSON.toJSONString(map1));
        taskMapper.insertSelective(task);
        skuFegin.updateNum(username);
        redisTemplate.delete("cart_" + username);
        rabbitTemplate.convertAndSend("","queue.ordercreate",orderId);
        return orderId;

    }


    /**
     * 修改
     *
     * @param order
     */
    @Override
    public void update(Order order) {
        orderMapper.updateByPrimaryKey(order);
    }

    /**
     * 删除
     *
     * @param id
     */
    @Override
    public void delete(String id) {
        orderMapper.deleteByPrimaryKey(id);
    }


    /**
     * 条件查询
     *
     * @param searchMap
     * @return
     */
    @Override
    public List<Order> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return orderMapper.selectByExample(example);
    }

    /**
     * 分页查询
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public Page<Order> findPage(int page, int size) {
        PageHelper.startPage(page, size);
        return (Page<Order>) orderMapper.selectAll();
    }

    /**
     * 条件+分页查询
     *
     * @param searchMap 查询条件
     * @param page      页码
     * @param size      页大小
     * @return 分页结果
     */
    @Override
    public Page<Order> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page, size);
        Example example = createExample(searchMap);
        return (Page<Order>) orderMapper.selectByExample(example);
    }

    /**
     * 支付成功后的订单
     *
     * @param orderId       订单号
     * @param transactionId 交易号
     */
    @Override
    public void updatePayStatus(String orderId, String transactionId) {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order != null && "0".equals(order.getPayStatus())) {
            order.setPayStatus("1");//已支付
            order.setOrderStatus("1"); //已处理
            order.setTransactionId(transactionId);  //交易号
            order.setPayTime(new Date());
            order.setUpdateTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);

            //保存订单日志
            OrderLog orderLog = new OrderLog();
            orderLog.setId(idWorker.nextId() + "");
            orderLog.setOperater("system");
            orderLog.setOperateTime(new Date()); // 当前日期
            orderLog.setOrderStatus("1");
            orderLog.setPayStatus("1");
            orderLog.setRemarks("交易流水号"+transactionId);
            orderLog.setOrderId(order.getId());
            orderLogMapper.insert(orderLog);

        }
    }

    @Override
    public void closeOrder(String orderId) {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if(order == null){
            throw new RuntimeException("此订单不存在");
        }
        if (!"0".equals(order.getPayStatus())){  //此订单已不是未支付状态
            return;
        }
        //查询支付是否成功
        Map resultMap = payFeign.queryOrderA(orderId).getData();
        System.out.println("查询微信支付结果" + resultMap);
        if("SUCCESS".equals("trade_state")){
            //如果成功 则进行补偿
            updatePayStatus(orderId,(String) resultMap.get("transaction_id"));
        }
        if("NOTPAY".equals(resultMap.get("trade_state"))){
            //未支付状态  则关闭
            System.out.println("开始关闭");
            order.setUpdateTime(new Date());
            order.setCloseTime(new Date());
            order.setOrderStatus("4");
            orderMapper.updateByPrimaryKeySelective(order);

            OrderLog orderLog = new OrderLog();
            orderLog.setOrderId(order.getId());
            orderLog.setId(idWorker.nextId()+"");
            orderLog.setOperater("system");
            orderLog.setOrderStatus("4");
            orderLog.setOperateTime(new Date());
            orderLogMapper.insertSelective(orderLog);
            //恢复库存
            OrderItem _orderItem = new OrderItem();
            _orderItem.setOrderId(orderId);
            List<OrderItem> orderItems = orderItemMapper.select(_orderItem);
            for (OrderItem orderItem : orderItems) {
                String skuId = orderItem.getSkuId();
                Integer num = orderItem.getNum();
                skuFegin.resumeStockNum(skuId,num);
            }

        }
            payFeign.closeOrder(orderId);
    }

    /**
     * 构建查询对象
     *
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap) {
        Example example = new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        if (searchMap != null) {
            // 订单id
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                criteria.andEqualTo("id", searchMap.get("id"));
            }
            // 支付类型，1、在线支付、0 货到付款
            if (searchMap.get("payType") != null && !"".equals(searchMap.get("payType"))) {
                criteria.andEqualTo("payType", searchMap.get("payType"));
            }
            // 物流名称
            if (searchMap.get("shippingName") != null && !"".equals(searchMap.get("shippingName"))) {
                criteria.andLike("shippingName", "%" + searchMap.get("shippingName") + "%");
            }
            // 物流单号
            if (searchMap.get("shippingCode") != null && !"".equals(searchMap.get("shippingCode"))) {
                criteria.andLike("shippingCode", "%" + searchMap.get("shippingCode") + "%");
            }
            // 用户名称
            if (searchMap.get("username") != null && !"".equals(searchMap.get("username"))) {
                criteria.andLike("username", "%" + searchMap.get("username") + "%");
            }
            // 买家留言
            if (searchMap.get("buyerMessage") != null && !"".equals(searchMap.get("buyerMessage"))) {
                criteria.andLike("buyerMessage", "%" + searchMap.get("buyerMessage") + "%");
            }
            // 是否评价
            if (searchMap.get("buyerRate") != null && !"".equals(searchMap.get("buyerRate"))) {
                criteria.andLike("buyerRate", "%" + searchMap.get("buyerRate") + "%");
            }
            // 收货人
            if (searchMap.get("receiverContact") != null && !"".equals(searchMap.get("receiverContact"))) {
                criteria.andLike("receiverContact", "%" + searchMap.get("receiverContact") + "%");
            }
            // 收货人手机
            if (searchMap.get("receiverMobile") != null && !"".equals(searchMap.get("receiverMobile"))) {
                criteria.andLike("receiverMobile", "%" + searchMap.get("receiverMobile") + "%");
            }
            // 收货人地址
            if (searchMap.get("receiverAddress") != null && !"".equals(searchMap.get("receiverAddress"))) {
                criteria.andLike("receiverAddress", "%" + searchMap.get("receiverAddress") + "%");
            }
            // 订单来源：1:web，2：app，3：微信公众号，4：微信小程序  5 H5手机页面
            if (searchMap.get("sourceType") != null && !"".equals(searchMap.get("sourceType"))) {
                criteria.andEqualTo("sourceType", searchMap.get("sourceType"));
            }
            // 交易流水号
            if (searchMap.get("transactionId") != null && !"".equals(searchMap.get("transactionId"))) {
                criteria.andLike("transactionId", "%" + searchMap.get("transactionId") + "%");
            }
            // 订单状态
            if (searchMap.get("orderStatus") != null && !"".equals(searchMap.get("orderStatus"))) {
                criteria.andEqualTo("orderStatus", searchMap.get("orderStatus"));
            }
            // 支付状态
            if (searchMap.get("payStatus") != null && !"".equals(searchMap.get("payStatus"))) {
                criteria.andEqualTo("payStatus", searchMap.get("payStatus"));
            }
            // 发货状态
            if (searchMap.get("consignStatus") != null && !"".equals(searchMap.get("consignStatus"))) {
                criteria.andEqualTo("consignStatus", searchMap.get("consignStatus"));
            }
            // 是否删除
            if (searchMap.get("isDelete") != null && !"".equals(searchMap.get("isDelete"))) {
                criteria.andEqualTo("isDelete", searchMap.get("isDelete"));
            }

            // 数量合计
            if (searchMap.get("totalNum") != null) {
                criteria.andEqualTo("totalNum", searchMap.get("totalNum"));
            }
            // 金额合计
            if (searchMap.get("totalMoney") != null) {
                criteria.andEqualTo("totalMoney", searchMap.get("totalMoney"));
            }
            // 优惠金额
            if (searchMap.get("preMoney") != null) {
                criteria.andEqualTo("preMoney", searchMap.get("preMoney"));
            }
            // 邮费
            if (searchMap.get("postFee") != null) {
                criteria.andEqualTo("postFee", searchMap.get("postFee"));
            }
            // 实付金额
            if (searchMap.get("payMoney") != null) {
                criteria.andEqualTo("payMoney", searchMap.get("payMoney"));
            }

        }
        return example;
    }

}
