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
            TextView textViewObservaciones = view.findViewById(R.id.textViewObservaciones);
            atras_btn = view.findViewById(R.id.detalles_back_btn);

            if (cobro != null) {
                textViewFactura.setText(cobro.getFactura());
                textViewNotaCredito.setText( cobro.getNotaCredito());
                textViewCodigoCliente.setText(cobro.getCodigoCliente());
                textViewAgente.setText(cobro.getAgente());
                textViewFechaEmision.setText(cobro.getFechaEmision());
                textViewRuta.setText(cobro.getRuta());

                String vencidas = cobro.isVencidas() ? "Sí" : "No";
                textViewVencidas.setText(vencidas);

                textViewNombreCliente.setText(cobro.getNombreCliente());
                textViewImporteFactura.setText(""+ cobro.getImporteFactura());
                textViewImporteNotaCredito.setText(""+cobro.getImporteNotaCredito());
                textViewImportePorPagar.setText(""+cobro.getImportePorPagar());
                textViewAbono.setText(""+cobro.getAbono());
                textViewSaldo.setText("" + cobro.getSaldo());
                textViewObservaciones.setText(cobro.getObservaciones());

                atras_btn.setOnClickListener(view1 -> irAtras());

            }
        }

        return view;
    }

    private void irAtras(){
        FragmentManager fragmentManager = getFragmentManager();
        assert fragmentManager != null;
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        CobranzaScreen cobranzaScreen = new CobranzaScreen();
        transaction.replace(R.id.container, cobranzaScreen);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}