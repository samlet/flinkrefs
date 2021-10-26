package com.bluecc.refs.source;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Data;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.bluecc.refs.source.Helper.GSON;
import static com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES;

public class OrderInfoSource {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStream<String> inputStream = env.readTextFile("../bluesrv/maintain/dump/order_info.jsonl");
        DataStream<OrderInfo> dataStream = inputStream
                .map(line -> {

                    OrderInfo orderInfo = GSON.fromJson(line, OrderInfo.class);
                    // Long ts = DateTimeUtil.toTs(orderInfo.getCreateTime());
                    DateTime dt = new DateTime(orderInfo.getCreateTime());
                    orderInfo.setTs(dt.getMillis());
                    return orderInfo;
                })
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy.<OrderInfo>forMonotonousTimestamps()
                                .withTimestampAssigner(
                                        (SerializableTimestampAssigner<OrderInfo>) (stats, recordTimestamp) -> stats.getTs()
                                )
                );

        SingleOutputStreamOperator<UserAmount> reduceDS = dataStream.keyBy(stats -> stats.getUserId())
                // .window(TumblingEventTimeWindows.of(Time.seconds(10)))
                .window(TumblingEventTimeWindows.of(Time.minutes(10)))
                .reduce((ReduceFunction<OrderInfo>) (value1, value2) -> {
                    value1.setTotalAmount(value1.totalAmount.add(value2.totalAmount));
                    return value1;
                }, new ProcessWindowFunction<OrderInfo, UserAmount, Long, TimeWindow>() {

                            @Override
                            public void process(Long aLong, ProcessWindowFunction<OrderInfo, UserAmount, Long, TimeWindow>.Context context, Iterable<OrderInfo> elements, Collector<UserAmount> out) {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                for (OrderInfo stat : elements) {
                                    UserAmount result = new UserAmount();
                                    result.setUserId(stat.getUserId());
                                    result.setTotalAmount(stat.getTotalAmount());
                                    result.setStart(simpleDateFormat.format(new Date(context.window().getStart())));
                                    result.setEnd(simpleDateFormat.format(new Date(context.window().getEnd())));
                                    result.setTs(new Date().getTime());
                                    out.collect(result);
                                }
                            }
                        }
                );

        reduceDS.print("amount");
        env.execute();
    }

    @Data
    public static class OrderInfo {
        private static final long serialVersionUID = 1L;

        private Long id;

        /**
         * 收货人
         */
        private String consignee;

        /**
         * 收件人电话
         */
        private String consigneeTel;

        /**
         * 总金额
         */
        private BigDecimal totalAmount;

        /**
         * 订单状态
         */
        private String orderStatus;

        /**
         * 用户id
         */
        private Long userId;


        /**
         * 送货地址
         */
        private String deliveryAddress;

        /**
         * 订单备注
         */
        private String orderComment;

        /**
         * 订单交易编号（第三方支付用)
         */
        private String outTradeNo;

        /**
         * 订单描述(第三方支付用)
         */
        private String tradeBody;

        /**
         * 创建时间
         */
        private String createTime;

        /**
         * 操作时间
         */
        private String operateTime;

        /**
         * 失效时间
         */
        private String expireTime;

        /**
         * 物流单编号
         */
        private String trackingNo;

        /**
         * 父订单编号
         */
        private Long parentOrderId;

        /**
         * 图片路径
         */
        private String imgUrl;

        /**
         * 地区
         */
        private Integer provinceId;


        private BigDecimal originalTotalAmount;
        private BigDecimal feightFee;
        private BigDecimal activityReduceAmount;
        private BigDecimal couponReduceAmount;

        Long ts; //统计时间戳
    }

    @Data
    public static class UserAmount {
        private Long userId;
        private BigDecimal totalAmount;
        private String start;
        private String end;
        private Long ts;
    }

}

