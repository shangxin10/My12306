package com.neu.entity;

import java.io.Serializable;

/**
 * Created by zhang on 2016/8/30.
 */
public class Contact implements Serializable{
    private String  id;
    private String name;
    private String type;
    private String tel;
    private String idType;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", tel='" + tel + '\'' +
                ", idType='" + idType + '\'' +
                '}';
    }
}
