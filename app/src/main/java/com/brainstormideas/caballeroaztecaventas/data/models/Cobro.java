package com.brainstormideas.caballeroaztecaventas.data.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "cobros")
public class Cobro implements Serializable {

    @PrimaryKey
    @NonNull
    private Long id;
    @NonNull
    private String factura;
    @Nullable
    private String notaCredito;
    @Nullable
    private String codigoCliente;
    @Nullable
    private String agente;
    @Nullable
    private String fechaEmision;
    @Nullable
    private String ruta;
    @Nullable
    private boolean vencidas;
    @Nullable
    private String nombreCliente;
    @Nullable
    private double importeFactura;
    @Nullable
    private double importeNotaCredito;
    @Nullable
    private double importePorPagar;
    @Nullable
    private double abono;
    @Nullable
    private double saldo;
    @Nullable
    private String observaciones;
    @Nullable
    private double pago;
    @Nullable
    private String banco;
    @Nullable
    private String metodopago;
    @Nullable
    private double numeroCheque;

    public Cobro() {
        id = null;
        factura= null;
    }

    public Cobro(@NonNull Long id, @NonNull String factura, @Nullable String notaCredito, @Nullable String codigoCliente, @Nullable String agente, @Nullable String fechaEmision, @Nullable String ruta, boolean vencidas, @Nullable String nombreCliente, double importeFactura, double importeNotaCredito, double importePorPagar, double abono, double saldo, @Nullable String observaciones, @Nullable double pago, @Nullable String banco, @Nullable String metodopago, double numeroCheque) {
        this.id = id;
        this.factura = factura;
        this.notaCredito = notaCredito;
        this.codigoCliente = codigoCliente;
        this.agente = agente;
        this.fechaEmision = fechaEmision;
        this.ruta = ruta;
        this.vencidas = vencidas;
        this.nombreCliente = nombreCliente;
        this.importeFactura = importeFactura;
        this.importeNotaCredito = importeNotaCredito;
        this.importePorPagar = importePorPagar;
        this.abono = abono;
        this.saldo = saldo;
        this.observaciones = observaciones;
        this.pago = pago;
        this.banco = banco;
        this.metodopago = metodopago;
        this.numeroCheque = numeroCheque;
    }

    @NonNull
    public Long getId() {
        return id;
    }

    public void setId(@NonNull Long id) {
        this.id = id;
    }

    @NonNull
    public String getFactura() {
        return factura;
    }

    public void setFactura(@NonNull String factura) {
        this.factura = factura;
    }

    @Nullable
    public String getNotaCredito() {
        return notaCredito;
    }

    public void setNotaCredito(@Nullable String notaCredito) {
        this.notaCredito = notaCredito;
    }

    @Nullable
    public String getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(@Nullable String codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    @Nullable
    public String getAgente() {
        return agente;
    }

    public void setAgente(@Nullable String agente) {
        this.agente = agente;
    }

    @Nullable
    public String getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(@Nullable String fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    @Nullable
    public String getRuta() {
        return ruta;
    }

    public void setRuta(@Nullable String ruta) {
        this.ruta = ruta;
    }

    @Nullable
    public boolean isVencidas() {
        return vencidas;
    }

    public void setVencidas(boolean vencidas) {
        this.vencidas = vencidas;
    }

    @Nullable
    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(@Nullable String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public double getImporteFactura() {
        return importeFactura;
    }

    public void setImporteFactura(double importeFactura) {
        this.importeFactura = importeFactura;
    }

    public double getImporteNotaCredito() {
        return importeNotaCredito;
    }

    public void setImporteNotaCredito(double importeNotaCredito) {
        this.importeNotaCredito = importeNotaCredito;
    }

    public double getImportePorPagar() {
        return importePorPagar;
    }

    public void setImportePorPagar(double importePorPagar) {
        this.importePorPagar = importePorPagar;
    }

    public double getAbono() {
        return abono;
    }

    public void setAbono(double abono) {
        this.abono = abono;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    @Nullable
    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(@Nullable String observaciones) {
        this.observaciones = observaciones;
    }

    @Nullable
    public String getBanco() {
        return banco;
    }

    public void setBanco(@Nullable String banco) {
        this.banco = banco;
    }

    @Nullable
    public String getMetodopago() {
        return metodopago;
    }

    public void setMetodopago(@Nullable String metodopago) {
        this.metodopago = metodopago;
    }

    public double getNumeroCheque() {
        return numeroCheque;
    }

    public void setNumeroCheque(double numeroCheque) {
        this.numeroCheque = numeroCheque;
    }

    public double getPago() {
        return pago;
    }

    public void setPago(double pago) {
        this.pago = pago;
    }

    @Override
    public String toString() {
        return "Cobro{" +
                "id=" + id +
                ", factura='" + factura + '\'' +
                ", notaCredito='" + notaCredito + '\'' +
                ", codigoCliente='" + codigoCliente + '\'' +
                ", agente='" + agente + '\'' +
                ", fechaEmision='" + fechaEmision + '\'' +
                ", ruta='" + ruta + '\'' +
                ", vencidas=" + vencidas +
                ", nombreCliente='" + nombreCliente + '\'' +
                ", importeFactura=" + importeFactura +
                ", importeNotaCredito=" + importeNotaCredito +
                ", importePorPagar=" + importePorPagar +
                ", abono=" + abono +
                ", saldo=" + saldo +
                ", observaciones='" + observaciones + '\'' +
                ", pago=" + pago +
                ", banco='" + banco + '\'' +
                ", metodopago='" + metodopago + '\'' +
                ", numeroCheque=" + numeroCheque +
                '}';
    }
}
