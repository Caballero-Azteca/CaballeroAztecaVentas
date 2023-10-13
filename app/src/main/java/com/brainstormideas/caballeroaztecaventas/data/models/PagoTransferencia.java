package com.brainstormideas.caballeroaztecaventas.data.models;

import androidx.room.Entity;

@Entity(tableName = "pagos_transferencia")
public class PagoTransferencia extends Pago {

    private String numeroTransferencia;

    public PagoTransferencia(Long id, String factura, String idUnico, double importe, String fecha, String banco, String numeroTransferencia, String tipoPago) {
        super(id, factura, idUnico, importe, fecha, banco, TipoPago.TRANSFERENCIA);
        this.numeroTransferencia = numeroTransferencia;
    }

    public PagoTransferencia() {
    }

    public String getNumeroTransferencia() {
        return numeroTransferencia;
    }

    public void setNumeroTransferencia(String numeroTransferencia) {
        this.numeroTransferencia = numeroTransferencia;
    }
}
