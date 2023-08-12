package com.brainstormideas.caballeroaztecaventas.data.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "productos")
public class Producto {
    @PrimaryKey
    @NonNull
    private Long id;
    @NonNull
    private String code;
    @Nullable
    private String nombre;
    @Nullable
    private String marca;
    private float cca;
    private float p4;
    private float p3;
    private float p2;
    private float p1;
    private float lista;

    @Ignore
    public Producto(@NonNull Long id, @NonNull String code, @Nullable String nombre,
                    @Nullable String marca, float cca, float p4, float p3, float p2, float p1,
                    float lista) {
        this.id = id;
        this.code = code;
        this.nombre = nombre;
        this.marca = marca;
        this.cca = cca;
        this.p4 = p4;
        this.p3 = p3;
        this.p2 = p2;
        this.p1 = p1;
        this.lista = lista;
    }

    public Producto() {

        id = null;
        code = null;
    }

    @Nullable
    public String getNombre() {
        return nombre;
    }

    public void setNombre(@Nullable String nombre) {
        this.nombre = nombre;
    }

    @Nullable
    public String getMarca() {
        return marca;
    }

    public void setMarca(@Nullable String marca) {
        this.marca = marca;
    }

    @NonNull
    public Long getId() {
        return id;
    }

    public void setId(@NonNull Long id) {
        this.id = id;
    }

    @NonNull
    public String getCode() {
        return code;
    }

    public void setCode(@NonNull String code) {
        this.code = code;
    }

    public float getCca() {
        return cca;
    }

    public void setCca(float cca) {
        this.cca = cca;
    }

    public float getP4() {
        return p4;
    }

    public void setP4(float p4) {
        this.p4 = p4;
    }

    public float getP3() {
        return p3;
    }

    public void setP3(float p3) {
        this.p3 = p3;
    }

    public float getP2() {
        return p2;
    }

    public void setP2(float p2) {
        this.p2 = p2;
    }

    public float getP1() {
        return p1;
    }

    public void setP1(float p1) {
        this.p1 = p1;
    }

    public float getLista() {
        return lista;
    }

    public void setLista(float lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public String toString() {
        return "Producto{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", nombre='" + nombre + '\'' +
                ", marca='" + marca + '\'' +
                ", cca=" + cca +
                ", p4=" + p4 +
                ", p3=" + p3 +
                ", p2=" + p2 +
                ", p1=" + p1 +
                ", lista=" + lista +
                '}';
    }
}
