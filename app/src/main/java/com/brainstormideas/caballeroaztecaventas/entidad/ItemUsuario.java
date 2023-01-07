package com.brainstormideas.caballeroaztecaventas.entidad;

import android.widget.ImageButton;

public class ItemUsuario {

    private String id;
    private String nombre;
    private String email;
    private String usuario;
    private String numero;
    private String pass;
    private ImageButton btnEditar;
    private ImageButton btnEliminar;

    public ItemUsuario() {

    }

    public ItemUsuario(String id, String nombre, String email, String usuario, String numero, String pass, ImageButton btnEditar, ImageButton btnEliminar) {

        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.numero = numero;
        this.pass = pass;
        this.usuario = usuario;
        this.btnEditar = btnEditar;
        this.btnEliminar = btnEliminar;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public ImageButton getBtnEditar() {
        return btnEditar;
    }

    public ImageButton getBtnEliminar() {
        return btnEliminar;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
