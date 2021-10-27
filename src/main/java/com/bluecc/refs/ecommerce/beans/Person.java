package com.bluecc.refs.ecommerce.beans;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.*;

@Data
public class Person implements Serializable {
    private static final long serialVersionUID = 1L;

    String partyId;
    String salutation;
    String firstName;
    String middleName;
    String lastName;
    String personalTitle;
    String suffix;
    String nickname;
    String firstNameLocal;
    String middleNameLocal;
    String lastNameLocal;
    String otherLocal;
    String memberId;
    String gender;
    Date birthDate;
    Date deceasedDate;
    Double height;
    Double weight;
    String mothersMaidenName;
    String maritalStatus;
    String socialSecurityNumber;
    String passportNumber;
    Date passportExpireDate;
    Double totalYearsWorkExperience;
    String comments;
    String employmentStatusEnumId;
    String residenceStatusEnumId;
    String occupation;
    BigDecimal yearsWithEmployer;
    BigDecimal monthsWithEmployer;
    String existingCustomer;
    String cardId;
    LocalDateTime lastUpdatedStamp;
    LocalDateTime lastUpdatedTxStamp;
    LocalDateTime createdStamp;
    LocalDateTime createdTxStamp;
    
}
