package com.brainstormideas.caballeroaztecaventas.entidad;

import android.widget.ImageButton;

public class ItemProductoPedido {

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

    public ImageButton getBtnEliminar() {
        return btnEliminar;
    }

    public void setBtnEliminar(ImageButton btnEliminar) {
        this.btnEliminar = btnEliminar;
    }
}
