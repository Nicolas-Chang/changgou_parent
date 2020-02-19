package com.changgou.user.pojo;


import javax.persistence.Table;

@Table(name = "tb_point_log")
public class PointLog {

    private String orderId;    //商品唯一表示
    private String userId;      //用户唯一表示
    private Integer point;      //积分标识

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }
}
