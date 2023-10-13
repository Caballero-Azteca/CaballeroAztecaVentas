package com.brainstormideas.caballeroaztecaventas.data.models;

import androidx.room.Entity;

@Entity(tableName = "pagos_efectivo")
public class PagoEfectivo extends Pago {

    public PagoEfectivo(Long id, String factura, String idUnico, double importe, String fecha, String banco, String tipoPago) {
        super(id, factura, idUnico, importe, fecha, banco, TipoPago.EFECTIVO);
    }

    public PagoEfectivo() {
    }
}
