package com.bluecc.refs.ecommerce.beans;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.*;

@Data
public class ProductFacility implements Serializable {
    private static final long serialVersionUID = 1L;

    String productId;
    String facilityId;
    BigDecimal minimumStock;
    BigDecimal reorderQuantity;
    BigDecimal daysToShip;
    BigDecimal lastInventoryCount;
    String requirementMethodEnumId;
    LocalDateTime lastUpdatedStamp;
    LocalDateTime lastUpdatedTxStamp;
    LocalDateTime createdStamp;
    LocalDateTime createdTxStamp;
    
}
