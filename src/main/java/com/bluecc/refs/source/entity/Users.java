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
//@ApiModel(value = "Users对象", description = "")
public class Users implements Serializable {

    private static final long serialVersionUID = 1L;

//    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String name;

    private String fullname;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    @Override
    public String toString() {
        return "Users{" +
        "id=" + id +
        ", name=" + name +
        ", fullname=" + fullname +
        "}";
    }
}
