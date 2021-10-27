package com.bluecc.refs.ecommerce;

import java.util.List;

public interface IDimJoin<T, D> {
    String getKey(T input);

    /**
     * Join dimension table data
     * @param input input and output object
     * @param rowData data list which query from db
     */
    void join(T input, List<D> rowData);
}

