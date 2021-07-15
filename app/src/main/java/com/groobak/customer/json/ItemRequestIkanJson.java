package com.groobak.customer.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ItemRequestIkanJson {
    @Expose
    @SerializedName("id_driver")
    private String idDriver;

    @Expose
    @SerializedName("nama_ikan")
    private String namaIkan;

    public String getIdDriver() {
        return idDriver;
    }

    public void setIdDriver(String idDriver) {
        this.idDriver = idDriver;
    }

    public String getNamaIkan() {
        return namaIkan;
    }

    public void setNamaIkan(String namaIkan) {
        this.namaIkan = namaIkan;
    }
}
