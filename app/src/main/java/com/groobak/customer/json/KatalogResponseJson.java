package com.groobak.customer.json;

import com.groobak.customer.models.KatalogModel;

import java.util.ArrayList;
import java.util.List;

public class KatalogResponseJson {
    private String message;
    private List<KatalogModel> data = new ArrayList<>();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<KatalogModel> getData() {
        return data;
    }

    public void setData(List<KatalogModel> data) {
        this.data = data;
    }
}
