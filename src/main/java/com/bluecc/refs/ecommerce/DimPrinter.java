package com.bluecc.refs.ecommerce;

import com.bluecc.refs.ecommerce.beans.Product;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.concurrent.TimeUnit;

public class DimPrinter {
    public static void main(String[] args) throws Exception {
        DimPrinter printer = new DimPrinter();
        printer.printDim();
    }

    public void printDim() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStream<String> ds = env.fromElements("DemoProduct",
                "DemoProduct-1",
                "DemoProduct-2",
                "DemoProduct-3");
        SingleOutputStreamOperator<Product> productDS = AsyncDataStream.unorderedWait(
                ds, new DimInjector.ProductDim(), 60, TimeUnit.SECONDS
        );
        productDS.print();

        env.execute();
    }
}
