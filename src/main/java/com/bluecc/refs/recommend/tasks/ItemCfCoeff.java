package com.bluecc.refs.recommend.tasks;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 基于协同过滤的产品相关度计算
 * * 策略1 ：协同过滤
 * *           abs( i ∩ j)
 * *      w = ——————————————
 * *           sqrt(i || j)
 *
 * 		.. 上述公式简单表述就是：
 * 			|𝑁(𝑢)∩𝑁(𝑣)| (两个用户都感兴趣物品数目) /
 * 			|𝑁(𝑢)∪𝑁(𝑣)| (两个用户中只要有一个用户感兴趣的物品数目)
 * 		    分母中加根号.
 *
 * @author XINZE
 */
public class ItemCfCoeff {
    IStore store;

    public ItemCfCoeff(IStore store) {
        this.store = store;
    }

    /**
     * 计算一个产品和其他相关产品的评分,并将计算结果放入Hbase
     *
     * @param id     产品id
     * @param others 其他产品的id
     */
    public void getSingelItemCfCoeff(String id, List<String> others) throws Exception {

        for (String other : others) {
            if (id.equals(other)) continue;
            Double score = twoItemCfCoeff(id, other);
            store.putData("px", id, "p", other, score.toString());
        }
    }

    /**
     * 计算两个产品之间的评分
     *
     * @param id
     * @param other
     * @return
     * @throws IOException
     */
    private Double twoItemCfCoeff(String id, String other) throws IOException {
        List<Map.Entry<String, Object>> p1 = store.getRow("p_history", id);
        List<Map.Entry<String, Object>> p2 = store.getRow("p_history", other);

        int n = p1.size();
        int m = p2.size();
        int sum = 0;
        double total = Math.sqrt(n * m);
        for (Map.Entry<String, Object> entry : p1) {
            String key = entry.getKey();
            for (Map.Entry<String, Object> p : p2) {
                if (key.equals(p.getKey())) {
                    sum++;
                }
            }
        }
        if (total == 0) {
            return 0.0;
        }
        return sum / total;

    }


}
