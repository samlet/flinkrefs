package com.bluecc.refs.ecommerce.beans;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.*;

@Data
public class ProductFeatureType implements Serializable {
    private static final long serialVersionUID = 1L;

    String productFeatureTypeId;
    String parentTypeId;
    String hasTable;
    String description;
    LocalDateTime lastUpdatedStamp;
    LocalDateTime lastUpdatedTxStamp;
    LocalDateTime createdStamp;
    LocalDateTime createdTxStamp;
    
}
