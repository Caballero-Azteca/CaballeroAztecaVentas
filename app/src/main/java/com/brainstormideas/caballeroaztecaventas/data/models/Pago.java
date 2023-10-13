package com.brainstormideas.caballeroaztecaventas.data.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "pagos")
public class Pago implements Serializable {

    @PrimaryKey
    @NonNull
    private Long id;
    @NonNull
    private String factura;
    @NonNull
    private String idUnico;
    @Nullable
    private double importe;
    @Nullable
    private String fecha;
    @Nullable
    private String banco;
    @Nullable
    private String tipoPago;


    public Pago(@NonNull Long id, @NonNull String factura, String idUnico, double importe, @Nullable String fecha, @Nullable String banco, @Nullable String tipoPago) {
        this.id = id;
        this.factura = factura;
        this.idUnico = idUnico;
        this.importe = importe;
        this.fecha = fecha;
        this.banco = banco;
        this.tipoPago = tipoPago;
    }

    public Pago() {
        id = null;
        factura= null;
        idUnico=null;
    }

    @NonNull
    public Long getId() {
        return id;
    }

    public void setId(@NonNull Long id) {
        this.id = id;
    }

    public String getFactura() {
        return factura;
    }

    public void setFactura(String factura) {
        this.factura = factura;
    }

    @NonNull
    public String getIdUnico() {
        return idUnico;
    }

    public void setIdUnico(@NonNull String idUnico) {
        this.idUnico = idUnico;
    }

    public double getImporte() {
        return importe;
    }

    public void setImporte(double importe) {
        this.importe = importe;
    }

    @Nullable
    public String getFecha() {
        return fecha;
    }

    public void setFecha(@Nullable String fecha) {
        this.fecha = fecha;
    }

    @Nullable
    public String getBanco() {
        return banco;
    }

    public void setBanco(@Nullable String banco) {
        this.banco = banco;
    }

    @Nullable
    public String getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(@Nullable String tipoPago) {
        this.tipoPago = tipoPago;
    }

    @NonNull
    @Override
    public String toString() {
        return "Pago{" +
                "factura='" + factura + '\'' +
                ", importe=" + importe +
                ", fecha='" + fecha + '\'' +
                ", banco='" + banco + '\'' +
                ", tipoPago='" + tipoPago + '\'' +
                '}';
    }
}
