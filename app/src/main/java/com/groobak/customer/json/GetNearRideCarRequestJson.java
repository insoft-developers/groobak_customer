package com.groobak.customer.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Ourdevelops Team on 10/17/2019.
 */

public class GetNearRideCarRequestJson {

    @Expose
    @SerializedName("latitude")
    private double latitude;

    @Expose
    @SerializedName("longitude")
    private double longitude;

    @Expose
    @SerializedName("fitur")
    private String fitur;

    @Expose
    @SerializedName("nama_ikan")
    private String nama_ikan;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getFitur() {
        return fitur;
    }

    public void setFitur(String fitur) {
        this.fitur = fitur;
    }

    public String getNama_ikan() {
        return nama_ikan;
    }

    public void setNama_ikan(String nama_ikan) {
        this.nama_ikan = nama_ikan;
    }
}
