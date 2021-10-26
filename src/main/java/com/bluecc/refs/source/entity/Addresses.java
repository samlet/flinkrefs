package com.bluecc.refs.source.entity;


import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author samlet
 * @since 2021-10-17
 */
//@ApiModel(value = "Addresses对象", description = "")
public class Addresses implements Serializable {

    private static final long serialVersionUID = 1L;

//    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer userId;

    private String emailAddress;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Override
    public String toString() {
        return "Addresses{" +
        "id=" + id +
        ", userId=" + userId +
        ", emailAddress=" + emailAddress +
        "}";
    }
}
