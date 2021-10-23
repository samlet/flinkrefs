package com.bluecc.refs.recommend;

import com.bluecc.refs.functor.Aggs;
import com.bluecc.refs.functor.domain.LogEntity;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.redis.RedisSink;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommand;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommandDescription;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisMapper;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;

/**
 * 热门商品 -> redis
 *
 * @author XINZE
 */
public class TopProduct {
    private static final Logger logger = LoggerFactory.getLogger(TopProduct.class);

    private static final int topSize = 5;

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        // 开启EventTime
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        FlinkJedisPoolConfig conf = new FlinkJedisPoolConfig.Builder()
                .setHost(Property.getStrValue("redis.host"))
//				.setPort(Property.getIntValue("redis.port"))
//				.setDatabase(Property.getIntValue("redis.db"))
                .build();

        Properties properties = Property.getKafkaProperties("topProuct");
        DataStreamSource<String> dataStream = env.addSource(new FlinkKafkaConsumer<String>("con", new SimpleStringSchema(), properties));

        DataStream<TopProductEntity> topProduct = dataStream.map(new TopProductMapFunction()).
                // 抽取时间戳做watermark 以 秒 为单位
                        assignTimestampsAndWatermarks(new AscendingTimestampExtractor<LogEntity>() {
                    @Override
                    public long extractAscendingTimestamp(LogEntity logEntity) {
                        return logEntity.getTime() * 1000;
                    }
                })
                // 按照productId 按滑动窗口
                .keyBy("productId").timeWindow(Time.seconds(60), Time.seconds(5))
                .aggregate(new Aggs.CountAgg(), new WindowResultFunction())
                .keyBy("windowEnd")
                .process(new TopNHotItems(topSize)).flatMap(new FlatMapFunction<List<String>, TopProductEntity>() {
                    @Override
                    public void flatMap(List<String> strings, Collector<TopProductEntity> collector) throws Exception {
                        System.out.println("-------------Top N Product------------");
                        for (int i = 0; i < strings.size(); i++) {
                            TopProductEntity top = new TopProductEntity();
                            top.setRankName(String.valueOf(i));
                            top.setProductId(Integer.parseInt(strings.get(i)));
                            // 输出排名结果
                            System.out.println(top);
                            collector.collect(top);
                        }

                    }
                });
        topProduct.addSink(new RedisSink<>(conf, new TopNRedisSink()));

        env.execute("Top N ");
    }

}

class TopProductMapFunction implements MapFunction<String, LogEntity> {
    @Override
    public LogEntity map(String s) throws Exception {
        LogEntity log = LogToEntity.getLog(s);
        return log;
    }
}

class TopProductEntity {

    private int productId;
    private int actionTimes;
    private long windowEnd;
    private String rankName;

    public static TopProductEntity of(Integer itemId, long end, Long count) {
        TopProductEntity res = new TopProductEntity();
        res.setActionTimes(count.intValue());
        res.setProductId(itemId);
        res.setWindowEnd(end);
        res.setRankName(String.valueOf(end));
        return res;
    }

    public String getRankName() {
        return rankName;
    }

    public long getWindowEnd() {
        return windowEnd;
    }

    public void setWindowEnd(long windowEnd) {
        this.windowEnd = windowEnd;
    }

    public void setRankName(String rankName) {
        this.rankName = rankName;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getActionTimes() {
        return actionTimes;
    }

    public void setActionTimes(int actionTimes) {
        this.actionTimes = actionTimes;
    }

    @Override
    public String toString() {
        return "TopProductEntity{" +
                "productId=" + productId +
                ", actionTimes=" + actionTimes +
                ", windowEnd=" + windowEnd +
                ", rankName='" + rankName + '\'' +
                '}';
    }
}

class TopNRedisSink implements RedisMapper<TopProductEntity> {
    @Override
    public RedisCommandDescription getCommandDescription() {
        return new RedisCommandDescription(RedisCommand.SET, null);
    }

    @Override
    public String getKeyFromData(TopProductEntity s) {
        return String.valueOf(s.getRankName());
    }

    @Override
    public String getValueFromData(TopProductEntity s) {
        return String.valueOf(s.getProductId());
    }
}


class LogToEntity {
    private static final Logger logger = LoggerFactory.getLogger(LogToEntity.class);
    public static LogEntity getLog(String s) {
        logger.info(s);
        String[] values = s.split(",");
        if (values.length < 2) {
            logger.error("Message is not correct: {}", s);
            return null;
        }
        LogEntity log = new LogEntity();
        log.setUserId(Integer.parseInt(values[0]));
        log.setProductId(Integer.parseInt(values[1]));
        log.setTime(Long.parseLong(values[2]));
        log.setAction(values[3]);

        return log;
    }
}

class WindowResultFunction
        implements WindowFunction<Long, TopProductEntity, Tuple, TimeWindow> {
    @Override
    public void apply(Tuple key, TimeWindow window, Iterable<Long> aggregateResult, Collector<TopProductEntity> collector) throws Exception {
        int itemId = key.getField(0);
        Long count = aggregateResult.iterator().next();
        collector.collect(TopProductEntity.of(itemId, window.getEnd(), count));
    }
}
