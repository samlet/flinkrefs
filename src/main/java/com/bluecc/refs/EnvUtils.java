package com.bluecc.refs;

import com.bluecc.refs.taxi.utils.ExerciseBase;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class EnvUtils {
    public static StreamExecutionEnvironment getStreamExecutionEnvironment() {
        // Set up streaming execution environment, including Web UI and REST endpoint.
        // Checkpointing isn't needed for the RidesAndFares exercise; this setup is for
        // using the State Processor API.

        Configuration conf = new Configuration();
        conf.setString("state.backend", "filesystem");
        conf.setString("state.savepoints.dir", "file:///tmp/savepoints");
        conf.setString("state.checkpoints.dir", "file:///tmp/checkpoints");
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf);
        env.setParallelism(ExerciseBase.parallelism);

        env.enableCheckpointing(10000L);
        CheckpointConfig config = env.getCheckpointConfig();
        config.enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        return env;
    }
}
