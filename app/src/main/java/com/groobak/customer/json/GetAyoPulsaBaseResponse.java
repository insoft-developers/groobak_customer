package com.groobak.customer.json;

import com.groobak.customer.models.ayopulsa.PriceListPulsaGeneralModel;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class GetAyoPulsaBaseResponse implements Serializable {

    @SerializedName("data")
    private List<PriceListPulsaGeneralModel> dataX;

    public List<PriceListPulsaGeneralModel> getDataX() {
        return dataX;
    }

    public void setDataX(List<PriceListPulsaGeneralModel> dataX) {
        this.dataX = dataX;
    }
}
