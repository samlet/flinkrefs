package com.bluecc.refs.ecommerce.beans;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.*;

@Data
public class Party implements Serializable {
    private static final long serialVersionUID = 1L;

    String partyId;
    String partyTypeId;
    String externalId;
    String preferredCurrencyUomId;
    String description;
    String statusId;
    LocalDateTime createdDate;
    String createdByUserLogin;
    LocalDateTime lastModifiedDate;
    String lastModifiedByUserLogin;
    String dataSourceId;
    String isUnread;
    LocalDateTime lastUpdatedStamp;
    LocalDateTime lastUpdatedTxStamp;
    LocalDateTime createdStamp;
    LocalDateTime createdTxStamp;
    
}
