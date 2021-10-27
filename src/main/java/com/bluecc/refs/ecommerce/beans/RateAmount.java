package com.bluecc.refs.ecommerce.beans;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.*;

@Data
public class RateAmount implements Serializable {
    private static final long serialVersionUID = 1L;

    String rateTypeId;
    String rateCurrencyUomId;
    String periodTypeId;
    String workEffortId;
    String partyId;
    String emplPositionTypeId;
    LocalDateTime fromDate;
    LocalDateTime thruDate;
    BigDecimal rateAmount;
    LocalDateTime lastUpdatedStamp;
    LocalDateTime lastUpdatedTxStamp;
    LocalDateTime createdStamp;
    LocalDateTime createdTxStamp;
    
}
