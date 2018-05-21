package com.yuan.model;

/**
 * Created by lynn on 2018/5/16.
 */
public enum  DoorCategory {
    ECOLOGICAL_DOOR(0,"生态门"),
    STEEL_WOOD_DOOR(1,"钢木门");

    private int code;
    private String name;
    DoorCategory(int code,String name){
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
