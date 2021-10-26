package com.bluecc.refs.ecommerce.beans;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.*;

@Data
public class ProductFeature implements Serializable {
    private static final long serialVersionUID = 1L;

    String productFeatureId;
    String productFeatureTypeId;
    String productFeatureCategoryId;
    String description;
    String uomId;
    BigDecimal numberSpecified;
    BigDecimal defaultAmount;
    BigDecimal defaultSequenceNum;
    String abbrev;
    String idCode;
    LocalDateTime lastUpdatedStamp;
    LocalDateTime lastUpdatedTxStamp;
    LocalDateTime createdStamp;
    LocalDateTime createdTxStamp;
    
}
