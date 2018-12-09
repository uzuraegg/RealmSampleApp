package com.diesen.realmsample;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Model extends RealmObject {
    @PrimaryKey
    private int pk;
    private String data;

    public int getPk() {
        return pk;
    }

    public void setPk(int pk) {
        this.pk = pk;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}