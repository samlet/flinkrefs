package com.bluecc.refs.source.entity;

//import com.baomidou.mybatisplus.annotation.IdType;
//import com.baomidou.mybatisplus.annotation.TableId;
//import io.swagger.annotations.ApiModel;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author samlet
 * @since 2021-10-17
 */
//@ApiModel(value = "Restaurant对象", description = "")
public class Restaurant implements Serializable {

    private static final long serialVersionUID = 1L;

//    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String name;

    private String cuisine;

    private String priceRange;

    private Boolean outsideSeating;


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

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public String getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(String priceRange) {
        this.priceRange = priceRange;
    }

    public Boolean getOutsideSeating() {
        return outsideSeating;
    }

    public void setOutsideSeating(Boolean outsideSeating) {
        this.outsideSeating = outsideSeating;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
        "id=" + id +
        ", name=" + name +
        ", cuisine=" + cuisine +
        ", priceRange=" + priceRange +
        ", outsideSeating=" + outsideSeating +
        "}";
    }
}
