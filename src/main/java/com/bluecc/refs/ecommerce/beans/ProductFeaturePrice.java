package com.bluecc.refs.ecommerce.beans;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.*;

@Data
public class ProductFeaturePrice implements Serializable {
    private static final long serialVersionUID = 1L;

    String productFeatureId;
    String productPriceTypeId;
    String currencyUomId;
    LocalDateTime fromDate;
    LocalDateTime thruDate;
    BigDecimal price;
    LocalDateTime createdDate;
    String createdByUserLogin;
    LocalDateTime lastModifiedDate;
    String lastModifiedByUserLogin;
    LocalDateTime lastUpdatedStamp;
    LocalDateTime lastUpdatedTxStamp;
    LocalDateTime createdStamp;
    LocalDateTime createdTxStamp;
    
}
