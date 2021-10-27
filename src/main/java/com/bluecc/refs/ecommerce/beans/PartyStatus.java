package com.bluecc.refs.ecommerce.beans;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.*;

@Data
public class PartyStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    String statusId;
    String partyId;
    LocalDateTime statusDate;
    String changeByUserLoginId;
    LocalDateTime lastUpdatedStamp;
    LocalDateTime lastUpdatedTxStamp;
    LocalDateTime createdStamp;
    LocalDateTime createdTxStamp;
    
}
