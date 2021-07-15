package com.groobak.customer.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Balqis Studio on 1/5/2017.
 */

public class PesananMerchant extends RealmObject implements Serializable {

    @PrimaryKey
    @Expose
    @SerializedName("id_item")
    private int idItem;

    @Expose
    @SerializedName("nama_item")
    private String namaItem;

    @Expose
    @SerializedName("harga_sastuan")
    private int hargaSatuan;

    @Expose
    @SerializedName("total_harga")
    private long totalHarga;

    @Expose
    @SerializedName("qty")
    private int qty;

    @Expose
    @SerializedName("catatan")
    private String catatan = "";

    @Expose
    @SerializedName("foto")
    private String foto;

    public int getIdItem() {
        return idItem;
    }

    public void setIdItem(int idItem) {
        this.idItem = idItem;
    }

    public String getNamaItem() {
        return namaItem;
    }

    public void setNamaItem(String namaItem) {
        this.namaItem = namaItem;
    }

    public int getHargaSatuan() {
        return hargaSatuan;
    }

    public void setHargaSatuan(int hargaSatuan) {
        this.hargaSatuan = hargaSatuan;
    }

    public long getTotalHarga() {
        return totalHarga;
    }

    public void setTotalHarga(long totalHarga) {
        this.totalHarga = totalHarga;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
