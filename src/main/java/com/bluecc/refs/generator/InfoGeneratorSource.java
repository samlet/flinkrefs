package com.bluecc.refs.generator;

import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;

import java.io.Serializable;

import static org.apache.flink.util.Preconditions.checkArgument;

/** A event stream source that generates the events on the fly. Useful for self-contained demos. */
@SuppressWarnings("serial")
public class InfoGeneratorSource<T> extends RichSourceFunction<T> {

    private final int delayPerRecordMillis;

    private volatile boolean running = true;
    private final IInfoGen<T> generator;
    public InfoGeneratorSource(IInfoGen<T> generator, int delayPerRecordMillis) {
        checkArgument(delayPerRecordMillis >= 0, "delay must be >= 0");

        this.generator=generator;
        this.delayPerRecordMillis = delayPerRecordMillis;
    }

    @Override
    public void run(SourceContext<T> sourceContext) throws Exception {

        final int range = Integer.MAX_VALUE / getRuntimeContext().getNumberOfParallelSubtasks();
        final int min = range * getRuntimeContext().getIndexOfThisSubtask();
        final int max = min + range;

        while (running) {
            sourceContext.collect(generator.next());

            if (delayPerRecordMillis > 0) {
                Thread.sleep(delayPerRecordMillis);
            }
        }
    }

    @Override
    public void cancel() {
        running = false;
    }
}
