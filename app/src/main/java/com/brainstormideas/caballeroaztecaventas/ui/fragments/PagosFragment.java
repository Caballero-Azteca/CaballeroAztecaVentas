package com.brainstormideas.caballeroaztecaventas.ui.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.brainstormideas.caballeroaztecaventas.R;
import com.brainstormideas.caballeroaztecaventas.data.models.Cliente;
import com.brainstormideas.caballeroaztecaventas.data.models.Cobro;
import com.brainstormideas.caballeroaztecaventas.data.models.Pago;
import com.brainstormideas.caballeroaztecaventas.ui.adapters.CobroAdapter;
import com.brainstormideas.caballeroaztecaventas.ui.adapters.PagosAdapter;
import com.brainstormideas.caballeroaztecaventas.ui.viewmodels.CobroViewModel;
import com.brainstormideas.caballeroaztecaventas.ui.viewmodels.PagoViewModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PagosFragment extends Fragment {

    Button back_btn;
    Button agregarPago_btn;
    private ProgressDialog progressDialog;
    private PagoViewModel pagoViewModel;
    private PagosAdapter pagosAdapter;
    private ArrayList<Pago> pagos = new ArrayList<>();
    private Cobro cobro;
    private Cliente cliente;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pagos, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        Bundle args = getArguments();
        if (args != null) {
            cobro = (Cobro) args.getSerializable("cobro");
            cliente = (Cliente) args.getSerializable("cliente");
        }

        ActionBar actionBar = activity.getSupportActionBar();

        if (activity != null) {
            if (actionBar != null) {
                SpannableString spannableString = new SpannableString("PAGOS - " + cliente.getRazon());
                spannableString.setSpan(new TextAppearanceSpan(activity, R.style.ActionBarTextStyle), 0, spannableString.length(), 0);
                actionBar.setTitle(spannableString);
            }
        }

        back_btn = view.findViewById(R.id.back_btn);
        agregarPago_btn = view.findViewById(R.id.agregarPago_btn);

        progressDialog = new ProgressDialog(getContext());
        pagoViewModel = new PagoViewModel(getContext());
        cargarPagos();
        RecyclerView recyclerView = view.findViewById(R.id.pagos_rv);
        pagosAdapter = new PagosAdapter(pagos);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(pagosAdapter);

        agregarPago_btn.setOnClickListener(view12 -> {
            mostrarDialogo();
        });

        back_btn.setOnClickListener(view1 -> {
            irAtras(cliente, cobro);
        });

        return view;
    }

    private void irAtras(Cliente cliente, Cobro cobro){
        DetallesCobroFragment detallesCobroFragment = new DetallesCobroFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("cliente", (Serializable) cliente);
        bundle.putSerializable("cobro", (Serializable) cobro);
        detallesCobroFragment.setArguments(bundle);
        FragmentTransaction transaction = requireFragmentManager().beginTransaction();
        transaction.replace(R.id.container, detallesCobroFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void cargarPagos() {
        progressDialog.setMessage("Cargando pagos...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        pagoViewModel.getPagos().observe(this, pagosNuevos -> {

            ArrayList<Pago> pagosFacturaEspecifica = new ArrayList<>();
            for (Pago pago : pagosNuevos) {
                if (pago.getFactura().equals(cobro.getFactura())) {
                    pagosFacturaEspecifica.add(pago);

                }
            }
            pagos.addAll(pagosFacturaEspecifica);
            pagosAdapter.setPagos(pagos);
            progressDialog.dismiss();
        });
    }

    private void mostrarDialogo() {
        CrearPagoFragment dialogFragment = new CrearPagoFragment();
        Bundle args = new Bundle();
        args.putSerializable("cliente", cliente);
        dialogFragment.setArguments(args);
        dialogFragment.show(getFragmentManager(), "CrearPagoDialogFragment");
    }
}