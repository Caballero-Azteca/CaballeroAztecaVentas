package com.brainstormideas.caballeroaztecaventas.ui.fragments;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.brainstormideas.caballeroaztecaventas.R;
import com.brainstormideas.caballeroaztecaventas.data.models.Cliente;
import com.brainstormideas.caballeroaztecaventas.data.models.Cobro;

import java.io.Serializable;

public class DetallesCobroFragment extends Fragment {

    Button atras_btn;
    Button pagos_btn;
    private Cliente cliente;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detalles_cobro, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();

        Bundle args = getArguments();

        if (args != null) {
            cliente = (Cliente) args.getSerializable("cliente");
            Cobro cobro = (Cobro) args.getSerializable("cobro");

            ActionBar actionBar = activity.getSupportActionBar();

            if (activity != null) {
                if (actionBar != null) {
                    SpannableString spannableString = new SpannableString(cobro.getFactura() +" - "+ cliente.getRazon());
                    spannableString.setSpan(new TextAppearanceSpan(activity, R.style.ActionBarTextStyle), 0, spannableString.length(), 0);
                    actionBar.setTitle(spannableString);
                }
            }

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
            TextView textViewPago = view.findViewById(R.id.textViewPago);
            TextView textViewBanco = view.findViewById(R.id.textViewBanco);
            TextView textViewMetodoPago = view.findViewById(R.id.textViewMetodoPago);
            TextView textViewNumeroCheque = view.findViewById(R.id.textViewNumeroCheque);
            pagos_btn = view.findViewById(R.id.pagos_btn);
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
                textViewPago.setText(String.valueOf(cobro.getPago()));
                textViewBanco.setText(cobro.getBanco());
                textViewMetodoPago.setText(cobro.getMetodoPago());
                textViewNumeroCheque.setText(""+ cobro.getNumeroCheque());

                pagos_btn.setOnClickListener(view1 -> irAPagos(cliente, cobro));
                atras_btn.setOnClickListener(view1 -> irAtras(cliente));
            }
        }

        return view;
    }

    private void irAtras(Cliente cliente){
        CobrosFragment cobrosFragment = new CobrosFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("cliente", (Serializable) cliente);
        cobrosFragment.setArguments(bundle);
        FragmentTransaction transaction = requireFragmentManager().beginTransaction();
        transaction.replace(R.id.container, cobrosFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private  void irAPagos(Cliente cliente, Cobro cobro){
        PagosFragment pagosFragment = new PagosFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("cliente", (Serializable) cliente);
        bundle.putSerializable("cobro", (Serializable) cobro);
        pagosFragment.setArguments(bundle);
        FragmentTransaction transaction = requireFragmentManager().beginTransaction();
        transaction.replace(R.id.container, pagosFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}