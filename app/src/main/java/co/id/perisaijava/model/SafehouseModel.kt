package co.id.perisaijava.model

import com.google.android.gms.maps.model.LatLng

class SafehouseModel {
    var name: String? = null
    var latLng: LatLng? = null
    var alamat: String? = null
    var foto: String? = null

    constructor() {}
    constructor(name: String?, latLng: LatLng?, alamat: String?, foto: String?) {
        this.name = name
        this.latLng = latLng
        this.alamat = alamat
        this.foto = foto
    }
}