package com.brainstormideas.caballeroaztecaventas.data.models;

import java.util.ArrayList;
import java.util.List;

public class TipoPago {
    public static final String EFECTIVO = "efectivo";
    public static final String CHEQUE = "cheque";
    public static final String TRANSFERENCIA = "transferencia";

    public static List<String> getTiposDePago() {
        List<String> tipos = new ArrayList<>();
        tipos.add(EFECTIVO);
        tipos.add(CHEQUE);
        tipos.add(TRANSFERENCIA);
        return tipos;
    }
}