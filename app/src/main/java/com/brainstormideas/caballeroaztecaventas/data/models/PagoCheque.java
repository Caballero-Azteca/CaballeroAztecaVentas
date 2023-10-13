package com.brainstormideas.caballeroaztecaventas.data.models;

import androidx.room.Entity;

@Entity(tableName = "pagos_cheque")
public class PagoCheque extends Pago {

    private String numeroCheque;

    public PagoCheque(Long id, String factura, String idUnico, double importe, String fecha, String banco, String numeroCheque, String tipoPago) {
        super(id, factura, idUnico,importe, fecha, banco, TipoPago.CHEQUE);
        this.numeroCheque = numeroCheque;
    }

    public PagoCheque(){

    }

    public PagoCheque(String numeroCheque) {
        this.numeroCheque = numeroCheque;
    }

    public String getNumeroCheque() {
        return numeroCheque;
    }

    public void setNumeroCheque(String numeroCheque) {
        this.numeroCheque = numeroCheque;
    }
}
