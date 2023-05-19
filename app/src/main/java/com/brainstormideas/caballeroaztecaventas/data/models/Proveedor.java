package com.brainstormideas.caballeroaztecaventas.data.models;

public class Proveedor {

    private int id;
    private String nombre;
    private int telefono;

    public Proveedor(int id, String nombre, int telefono) {

        this.nombre = nombre;
        this.telefono = telefono;
    }

    public Proveedor() {

    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }


    @Override
    public String toString() {
        return "Proveedor{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", telefono=" + telefono +
                '}';
    }
}
