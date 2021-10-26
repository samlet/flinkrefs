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
//@ApiModel(value = "Hotel对象", description = "")
public class Hotel implements Serializable {

    private static final long serialVersionUID = 1L;

//    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String name;

    private String city;

    private String priceRange;

    private Boolean breakfastIncluded;

    private Boolean freeWifi;

    private Boolean swimmingPool;

    private Integer starRating;


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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(String priceRange) {
        this.priceRange = priceRange;
    }

    public Boolean getBreakfastIncluded() {
        return breakfastIncluded;
    }

    public void setBreakfastIncluded(Boolean breakfastIncluded) {
        this.breakfastIncluded = breakfastIncluded;
    }

    public Boolean getFreeWifi() {
        return freeWifi;
    }

    public void setFreeWifi(Boolean freeWifi) {
        this.freeWifi = freeWifi;
    }

    public Boolean getSwimmingPool() {
        return swimmingPool;
    }

    public void setSwimmingPool(Boolean swimmingPool) {
        this.swimmingPool = swimmingPool;
    }

    public Integer getStarRating() {
        return starRating;
    }

    public void setStarRating(Integer starRating) {
        this.starRating = starRating;
    }

    @Override
    public String toString() {
        return "Hotel{" +
        "id=" + id +
        ", name=" + name +
        ", city=" + city +
        ", priceRange=" + priceRange +
        ", breakfastIncluded=" + breakfastIncluded +
        ", freeWifi=" + freeWifi +
        ", swimmingPool=" + swimmingPool +
        ", starRating=" + starRating +
        "}";
    }
}
