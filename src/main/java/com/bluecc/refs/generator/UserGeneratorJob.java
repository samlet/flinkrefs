/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bluecc.refs.generator;

import com.bluecc.refs.statemachine.StateMachineExample;
import com.bluecc.refs.statemachine.generator.EventsGeneratorSource;
import com.bluecc.refs.statemachine.kafka.EventDeSerializer;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;

import java.util.Date;

/**
 * Job to generate user-info.
 *
 * $ just consume objects
 * $ just run generator.UserGeneratorJob -sleep 500   # 0.5s
 */
public class UserGeneratorJob {

    public static void main(String[] args) throws Exception {

        final ParameterTool params = ParameterTool.fromArgs(args);

//        double errorRate = params.getDouble("error-rate", 0.0);
        int sleep = params.getInt("sleep", 1000);  // sleep 1 second

        String kafkaTopic = params.get("kafka-topic", "objects");
        String brokers = params.get("brokers", "localhost:9092");

        System.out.printf(
                "Generating events to Kafka with standalone source with sleep delay %s millis\n",
                sleep);
        System.out.println();

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        final UserInfoGen gen=new UserInfoGen("50", new Date(), true);
        env.addSource(new InfoGeneratorSource<UserInfo>(gen, sleep))
                .returns(UserInfo.class)  // http://www.lzhpo.com/article/137
                .addSink(new FlinkKafkaProducer<>(brokers, kafkaTopic,
                        new ObjectDeSerializer<>(UserInfo.class)));

        // trigger program execution
        env.execute("user-info events generator job");
    }
}
