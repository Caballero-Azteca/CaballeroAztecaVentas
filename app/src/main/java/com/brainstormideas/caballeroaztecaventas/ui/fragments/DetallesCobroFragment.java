package com.brainstormideas.caballeroaztecaventas.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.brainstormideas.caballeroaztecaventas.R;
import com.brainstormideas.caballeroaztecaventas.data.models.Cobro;

public class DetallesCobroFragment extends Fragment {

    Button atras_btn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detalles_cobro, container, false);

        Bundle args = getArguments();
        if (args != null) {
            Cobro cobro = (Cobro) args.getSerializable("cobro");

            TextView textViewFactura = view.findViewById(R.id.textViewFactura);
            TextView textViewNotaCredito = view.findViewById(R.id.textViewNotaCredito);
            TextView textViewCodigoCliente = view.findViewById(R.id.textViewCodigoCliente);
            TextView textViewAgente = view.findViewById(R.id.textViewAgente);
            TextView textViewFechaEmision = view.findViewById(R.id.textViewFechaEmision);
            TextView textViewRuta = view.findViewById(R.id.textViewRuta);
            TextView textViewVencidas = view.findViewById(R.id.textViewVencidas);
            TextView textViewNombreCliente = view.findViewById(R.id.textViewNombreCliente);
            TextView textViewImporteFactura = view.findViewById(R.id.textViewImporteFactura);
            TextView textViewImporteNotaCredito = view.findViewById(R.id.textViewImporteNotaCredito);
            TextView textViewImportePorPagar = view.findViewById(R.id.textViewImportePorPagar);
            TextView textViewAbono = view.findViewById(R.id.textViewAbono);
            TextView textViewSaldo = view.findViewById(R.id.textViewSaldo);
            TextView textViewEfectivo = view.findViewById(R.id.textViewEfectivo);
            TextView textViewOtros = view.findViewById(R.id.textViewOtros);
            TextView textViewObservaciones = view.findViewById(R.id.textViewObservaciones);
            atras_btn = view.findViewById(R.id.detalles_back_btn);

            if (cobro != null) {
                // Asigna los datos a las vistas
                textViewFactura.setText("Factura: " + cobro.getFactura());
                textViewNotaCredito.setText("Nota de Crédito: " + cobro.getNotaCredito());
                textViewCodigoCliente.setText("Código de Cliente: " + cobro.getCodigoCliente());
                textViewAgente.setText("Agente: " + cobro.getAgente());
                textViewFechaEmision.setText("Fecha de Emisión: " + cobro.getFechaEmision());
                textViewRuta.setText("Ruta: " + cobro.getRuta());

                // La propiedad 'vencidas' es un boolean, conviértelo a texto para mostrarlo
                String vencidas = cobro.isVencidas() ? "Sí" : "No";
                textViewVencidas.setText("Vencidas: " + vencidas);

                textViewNombreCliente.setText("Nombre del Cliente: " + cobro.getNombreCliente());
                textViewImporteFactura.setText("Importe de la Factura: " + cobro.getImporteFactura());
                textViewImporteNotaCredito.setText("Importe de la Nota de Crédito: " + cobro.getImporteNotaCredito());
                textViewImportePorPagar.setText("Importe por Pagar: " + cobro.getImportePorPagar());
                textViewAbono.setText("Abono: " + cobro.getAbono());
                textViewSaldo.setText("Saldo: " + cobro.getSaldo());
                textViewEfectivo.setText("Efectivo: " + cobro.getEfectivo());
                textViewOtros.setText("Otros: " + cobro.getOtros());
                textViewObservaciones.setText("Observaciones: " + cobro.getObservaciones());

                atras_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        irAtras();
                    }
                });

            }
        }

        return view;
    }

    private void irAtras(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        CobranzaScreen cobranzaScreen = new CobranzaScreen();
        transaction.replace(R.id.container, cobranzaScreen);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}