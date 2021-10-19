package com.bluecc.refs.sqlflow;

import org.apache.flink.table.functions.ScalarFunction;

public class Scalars {
    public static class HashCode extends ScalarFunction {
        private int factor;
        public HashCode(int factor) {
            this.factor = factor;
        }

        public int eval(String id) {
            return id.hashCode() * factor;
        }
    }

}
