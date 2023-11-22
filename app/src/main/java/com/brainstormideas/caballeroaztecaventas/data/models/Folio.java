package com.brainstormideas.caballeroaztecaventas.data.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.List;

@Entity(tableName = "folios")
public class Folio implements Serializable {

    @PrimaryKey
    @NonNull
    private Long id;
    @NonNull
    private String indice;
    @NonNull
    private List<String> folios;

    public Folio() {

    }

    public Folio(@NonNull Long id, @NonNull String indice, @NonNull List<String> folios) {
        this.id = id;
        this.indice = indice;
        this.folios = folios;
    }

    @NonNull
    public Long getId() {
        return id;
    }

    public void setId(@NonNull Long id) {
        this.id = id;
    }

    @NonNull
    public String getIndice() {
        return indice;
    }

    public void setIndice(@NonNull String indice) {
        this.indice = indice;
    }

    @NonNull
    public List<String> getFolios() {
        return folios;
    }

    public void setFolios(@NonNull List<String> folios) {
        this.folios = folios;
    }

    @Override
    public String toString() {
        return "Folio{" +
                "id=" + id +
                ", indice='" + indice + '\'' +
                ", folios=" + folios +
                '}';
    }
}
