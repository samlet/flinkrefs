package com.bluecc.refs.sink;

import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.Table;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.cassandra.CassandraSink;
import org.apache.flink.util.Collector;

/**
 * 在将具有 Java/Scala Tuple 数据类型的结果存储到 Cassandra sink 时，
 * 需要设置 CQL upsert 语句（通过 setQuery('stmt')）将每条记录保存回数据库。
 * 将 upsert 查询缓存为PreparedStatement，每个元组元素都被转换为语句的参数。
 *
 * $ cqlsh
     CREATE KEYSPACE IF NOT EXISTS example
     WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'};

     CREATE TABLE IF NOT EXISTS example.wordcount (
     word text,
     count bigint,
     PRIMARY KEY(word)
     );
 * $ just socket
 * $ just run sink.CassandraSinkProcs
 *
 * cqlsh> select * from example.wordcount;
 *  word     | count
 * ----------+-------
 *     hello |     1
 *  database |     5
 *     world |     1
 *
 */
public class CassandraSinkProcs {
    public static void main(String[] args) throws Exception {
//        storeWithSql();
        storeWithPojo();
    }

    private static void storeWithSql() throws Exception {
        // get the execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // get input data by connecting to the socket
        DataStream<String> text = env.socketTextStream("localhost", 7777, "\n");

        // parse the data, group it, window it, and aggregate the counts
        DataStream<Tuple2<String, Long>> result = text
                .flatMap(new FlatMapFunction<String, Tuple2<String, Long>>() {
                    @Override
                    public void flatMap(String value, Collector<Tuple2<String, Long>> out) {
                        // normalize and split the line
                        String[] words = value.toLowerCase().split("\\s");

                        // emit the pairs
                        for (String word : words) {
                            //Do not accept empty word, since word is defined as primary key in C* table
                            if (!word.isEmpty()) {
                                out.collect(new Tuple2<String, Long>(word, 1L));
                            }
                        }
                    }
                })
                .keyBy(value -> value.f0)
                .window(TumblingProcessingTimeWindows.of(Time.seconds(5)))
                .sum(1);

        CassandraSink.addSink(result)
                .setQuery("INSERT INTO example.wordcount(word, count) values (?, ?);")
                .setHost("127.0.0.1")
                .build();

        env.execute();
    }

    private static void storeWithPojo() throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // get input data by connecting to the socket
        DataStream<String> text = env.socketTextStream("localhost", 7777, "\n");

        // parse the data, group it, window it, and aggregate the counts
        DataStream<WordCount> result = text
                .flatMap(new FlatMapFunction<String, WordCount>() {
                    public void flatMap(String value, Collector<WordCount> out) {
                        // normalize and split the line
                        String[] words = value.toLowerCase().split("\\s");

                        // emit the pairs
                        for (String word : words) {
                            if (!word.isEmpty()) {
                                //Do not accept empty word, since word is defined as primary key in C* table
                                out.collect(new WordCount(word, 1L));
                            }
                        }
                    }
                })
                .keyBy(WordCount::getWord)
                .window(TumblingProcessingTimeWindows.of(Time.seconds(5)))

                .reduce(new ReduceFunction<WordCount>() {
                    @Override
                    public WordCount reduce(WordCount a, WordCount b) {
                        return new WordCount(a.getWord(), a.getCount() + b.getCount());
                    }
                });

        CassandraSink.addSink(result)
                .setHost("127.0.0.1")
                .setMapperOptions(() -> new Mapper.Option[]{Mapper.Option.saveNullFields(true)})
                .build();

        env.execute();

    }


    @Table(keyspace = "example", name = "wordcount")
    public static class WordCount {

        @Column(name = "word")
        private String word = "";

        @Column(name = "count")
        private long count = 0;

        public WordCount() {}

        public WordCount(String word, long count) {
            this.setWord(word);
            this.setCount(count);
        }

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        public long getCount() {
            return count;
        }

        public void setCount(long count) {
            this.count = count;
        }

        @Override
        public String toString() {
            return getWord() + " : " + getCount();
        }
    }

}
