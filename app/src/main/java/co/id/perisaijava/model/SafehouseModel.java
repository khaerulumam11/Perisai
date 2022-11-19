package co.id.perisaijava.model;

import com.google.android.gms.maps.model.LatLng;

public class SafehouseModel {
    private String name;
    private LatLng latLng;
    private String alamat;
    private String foto;

    public SafehouseModel() {
    }

    public SafehouseModel(String name, LatLng latLng, String alamat, String foto) {
        this.name = name;
        this.latLng = latLng;
        this.alamat = alamat;
        this.foto = foto;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
