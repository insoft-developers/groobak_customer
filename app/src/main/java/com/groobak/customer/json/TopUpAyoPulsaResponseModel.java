package com.groobak.customer.json;

import com.groobak.customer.models.ayopulsa.TopUpDetailModel;

import java.io.Serializable;

public class TopUpAyoPulsaResponseModel implements Serializable {

    private String message;
    private TopUpDetailModel data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public TopUpDetailModel getData() {
        return data;
    }

    public void setData(TopUpDetailModel data) {
        this.data = data;
    }
}
