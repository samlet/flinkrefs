package com.bluecc.refs.hotitems_analysis.beans;

/**
 * 列名称 说明
 * ----------------
 * 用户ID    整数类型，序列化后的用户ID
 * 商品ID    整数类型，序列化后的商品ID
 * 商品类目ID  整数类型，序列化后的商品所属类目ID
 * 行为类型    字符串，枚举类型，包括('pv', 'buy', 'cart', 'fav')
 * 时间戳 行为发生的时间戳
 *
 * 行为类型    说明
 * ----------------
 * pv  商品详情页pv，等价于点击
 * buy 商品购买
 * cart    将商品加入购物车
 * fav 收藏商品
 */
public class UserBehavior {
    // 定义私有属性
    private Long userId;
    private Long itemId;
    private Integer categoryId;
    private String behavior;
    private Long timestamp;

    public UserBehavior() {
    }

    public UserBehavior(Long userId, Long itemId, Integer categoryId, String behavior, Long timestamp) {
        this.userId = userId;
        this.itemId = itemId;
        this.categoryId = categoryId;
        this.behavior = behavior;
        this.timestamp = timestamp;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getBehavior() {
        return behavior;
    }

    public void setBehavior(String behavior) {
        this.behavior = behavior;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "UserBehavior{" +
                "userId=" + userId +
                ", itemId=" + itemId +
                ", categoryId=" + categoryId +
                ", behavior='" + behavior + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
