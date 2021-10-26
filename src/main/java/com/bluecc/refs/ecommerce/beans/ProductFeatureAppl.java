package com.bluecc.refs.ecommerce.beans;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.*;

@Data
public class ProductFeatureAppl implements Serializable {
    private static final long serialVersionUID = 1L;

    String productId;
    String productFeatureId;
    String productFeatureApplTypeId;
    LocalDateTime fromDate;
    LocalDateTime thruDate;
    BigDecimal sequenceNum;
    BigDecimal amount;
    BigDecimal recurringAmount;
    LocalDateTime lastUpdatedStamp;
    LocalDateTime lastUpdatedTxStamp;
    LocalDateTime createdStamp;
    LocalDateTime createdTxStamp;
    
}
