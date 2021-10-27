package com.bluecc.refs.ecommerce.beans;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.*;

@Data
public class EmplPosition implements Serializable {
    private static final long serialVersionUID = 1L;

    String emplPositionId;
    String statusId;
    String partyId;
    String budgetId;
    String budgetItemSeqId;
    String emplPositionTypeId;
    LocalDateTime estimatedFromDate;
    LocalDateTime estimatedThruDate;
    String salaryFlag;
    String exemptFlag;
    String fulltimeFlag;
    String temporaryFlag;
    LocalDateTime actualFromDate;
    LocalDateTime actualThruDate;
    LocalDateTime lastUpdatedStamp;
    LocalDateTime lastUpdatedTxStamp;
    LocalDateTime createdStamp;
    LocalDateTime createdTxStamp;
    
}
