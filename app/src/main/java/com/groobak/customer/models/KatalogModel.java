package com.groobak.customer.models;

public class KatalogModel {

    private String id_item;
    private String id_driver;
    private String nama_driver;
    private String nama_item;
    private String stok;
    private String like;
    private String bawa;
    private String foto;

    public String getId_item() {
        return id_item;
    }

    public void setId_item(String id_item) {
        this.id_item = id_item;
    }

    public String getId_driver() {
        return id_driver;
    }

    public void setId_driver(String id_driver) {
        this.id_driver = id_driver;
    }

    public String getNama_driver() {
        return nama_driver;
    }

    public void setNama_driver(String nama_driver) {
        this.nama_driver = nama_driver;
    }

    public String getNama_item() {
        return nama_item;
    }

    public void setNama_item(String nama_item) {
        this.nama_item = nama_item;
    }

    public String getStok() {
        return stok;
    }

    public void setStok(String stok) {
        this.stok = stok;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getBawa() {
        return bawa;
    }

    public void setBawa(String bawa) {
        this.bawa = bawa;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
