package com.bluecc.refs.ecommerce.beans;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.*;

@Data
public class InventoryItem implements Serializable {
    private static final long serialVersionUID = 1L;

    String inventoryItemId;
    String inventoryItemTypeId;
    String productId;
    String partyId;
    String ownerPartyId;
    String statusId;
    LocalDateTime datetimeReceived;
    LocalDateTime datetimeManufactured;
    LocalDateTime expireDate;
    String facilityId;
    String containerId;
    String lotId;
    String uomId;
    String binNumber;
    String locationSeqId;
    String comments;
    BigDecimal quantityOnHandTotal;
    BigDecimal availableToPromiseTotal;
    BigDecimal accountingQuantityTotal;
    BigDecimal quantityOnHand;
    BigDecimal availableToPromise;
    String serialNumber;
    String softIdentifier;
    String activationNumber;
    LocalDateTime activationValidThru;
    BigDecimal unitCost;
    String currencyUomId;
    String fixedAssetId;
    LocalDateTime lastUpdatedStamp;
    LocalDateTime lastUpdatedTxStamp;
    LocalDateTime createdStamp;
    LocalDateTime createdTxStamp;
    
}
