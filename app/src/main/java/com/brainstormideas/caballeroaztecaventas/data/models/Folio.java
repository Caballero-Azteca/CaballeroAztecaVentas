package com.brainstormideas.caballeroaztecaventas.data.models;

public class Folio {

    private String folio;
    private String fecha;

    public Folio() {

    }

    public Folio(String folio, String fecha) {
        this.folio = folio;
        this.fecha = fecha;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "Folio{" +
                "folio='" + folio + '\'' +
                ", fecha='" + fecha + '\'' +
                '}';
    }
}
