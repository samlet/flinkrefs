package com.bluecc.refs.ecommerce.beans;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.*;

@Data
public class ProductGeo implements Serializable {
    private static final long serialVersionUID = 1L;

    String productId;
    String geoId;
    String productGeoEnumId;
    String description;
    LocalDateTime lastUpdatedStamp;
    LocalDateTime lastUpdatedTxStamp;
    LocalDateTime createdStamp;
    LocalDateTime createdTxStamp;
    
}
