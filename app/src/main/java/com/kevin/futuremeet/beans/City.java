package com.kevin.futuremeet.beans;

/**
 * Created by carver on 2016/3/19.
 */
public class City {
    private String name;
    private String pinyin;

    public City(String name, String pinyin) {
        this.name = name;
        this.pinyin = pinyin;
    }

    public String getName() {
        return name;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }
}
