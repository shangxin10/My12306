package com.neu.entity;

import java.io.Serializable;

/**
 * Created by zhang on 2016/9/1.
 */
public class Seat implements Serializable{
    private String seatName;
    private float seatPrice;
    private int seatNum;

    public String getSeatName() {
        return seatName;
    }

    public void setSeatName(String seatName) {
        this.seatName = seatName;
    }

    public float getSeatPrice() {
        return seatPrice;
    }

    public void setSeatPrice(float seatPrice) {
        this.seatPrice = seatPrice;
    }

    public int getSeatNum() {
        return seatNum;
    }

    public void setSeatNum(int seatNum) {
        this.seatNum = seatNum;
    }
}
