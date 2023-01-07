package com.brainstormideas.caballeroaztecaventas.models;

import java.util.ArrayList;

public class Vendedor {

    private String id;
    private String nombre;
    private String userName;
    private String password;
    private String telefono;
    private String email;

    public Vendedor(String id, String nombre, String userName, String password, String telefono, String email) {
        this.id = id;
        this.nombre = nombre;
        this.userName = userName;
        this.password = password;
        this.telefono = telefono;
        this.email = email;
    }

    public Vendedor() {

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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Vendedor{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", userName='" + userName + '\'' +
                ", telefono='" + telefono + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
