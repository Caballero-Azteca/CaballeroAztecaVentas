package com.brainstormideas.caballeroaztecaventas.entidad;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageButton;

public class ItemProductoPedido implements Parcelable {

    private String id;
    private String nombre;
    private String marca;
    private String cantidad;
    private String precio;
    private String tipo;
    private ImageButton btnEliminar;

    public ItemProductoPedido(String id, String nombre, String marca, String cantidad, String precio, String tipo, ImageButton btnEliminar) {
        this.id = id;
        this.nombre = nombre;
        this.marca = marca;
        this.cantidad = cantidad;
        this.precio = precio;
        this.tipo = tipo;
        this.btnEliminar = btnEliminar;
    }

    protected ItemProductoPedido(Parcel in) {

        id = in.readString();
        nombre = in.readString();
        marca = in.readString();
        cantidad = in.readString();
        precio = in.readString();
        tipo = in.readString();
    }

    public static final Creator<ItemProductoPedido> CREATOR = new Creator<ItemProductoPedido>() {
        @Override
        public ItemProductoPedido createFromParcel(Parcel in) {
            return new ItemProductoPedido(in);
        }

        @Override
        public ItemProductoPedido[] newArray(int size) {
            return new ItemProductoPedido[size];
        }
    };

    public ItemProductoPedido() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(nombre);
        dest.writeString(marca);
        dest.writeString(cantidad);
        dest.writeString(precio);
        dest.writeString(tipo);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
