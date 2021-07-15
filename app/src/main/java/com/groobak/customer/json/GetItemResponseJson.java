package com.groobak.customer.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.groobak.customer.models.ItemModel;

import java.util.ArrayList;
import java.util.List;

public class GetItemResponseJson {
    @Expose
    @SerializedName("data")
    private List<ItemModel> data = new ArrayList<>();

    @Expose
    @SerializedName("code")
    private String code;

    @Expose
    @SerializedName("message")
    private String message;

    public List<ItemModel> getData() {
        return data;
    }

    public void setData(List<ItemModel> data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
