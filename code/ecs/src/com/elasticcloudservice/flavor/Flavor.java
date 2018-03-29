package com.elasticcloudservice.flavor;

import java.util.Date;

/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5
 * created in  20:22 2018/3/28.
 * @since sdk-java
 */

public class Flavor {
    private String uuid;
    private String flavorName;
    private Date createTime;
    private String day;
    private String time;

    public Flavor(String uuid, String flavorName, Date createTime, String day, String time) {
        this.uuid = uuid;
        this.flavorName = flavorName;
        this.createTime = createTime;
        this.day = day;
        this.time = time;
    }

    @Override
    public String toString() {
        return "Flavor{" +
                "uuid='" + uuid + '\'' +
                ", flavorName='" + flavorName + '\'' +
                ", createTime=" + createTime +
                '}';
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFlavorName() {
        return flavorName;
    }

    public void setFlavorName(String flavorName) {
        this.flavorName = flavorName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public int getMonthAndDay() {
        return createTime.getMonth();
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
