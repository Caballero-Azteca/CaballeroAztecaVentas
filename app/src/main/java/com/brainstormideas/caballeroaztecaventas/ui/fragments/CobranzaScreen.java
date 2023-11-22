package com.brainstormideas.caballeroaztecaventas.ui.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.brainstormideas.caballeroaztecaventas.R;
import com.brainstormideas.caballeroaztecaventas.data.models.Cliente;
import com.brainstormideas.caballeroaztecaventas.data.models.Cobro;
import com.brainstormideas.caballeroaztecaventas.ui.adapters.ClienteConCobroAdapter;
import com.brainstormideas.caballeroaztecaventas.ui.adapters.CobroAdapter;
import com.brainstormideas.caballeroaztecaventas.ui.viewmodels.CobroViewModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CobranzaScreen extends Fragment implements CobroAdapter.OnItemClickListener {

    EditText busquedaEtx;
    private Button irAMain;
    private CobroViewModel cobroViewModel;
    ArrayList<Cliente> clientesConCobros = new ArrayList<>();
    private ProgressDialog progressDialog;
    ClienteConCobroAdapter clientesConCobroAdapter;

    public CobranzaScreen() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_cobranza_screen, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();

        irAMain = view.findViewById(R.id.back_btn);
        irAMain.setOnClickListener(view1 -> irAMain());

        if (activity != null) {
            activity.getSupportActionBar().setTitle("Cobranza");
        }
        progressDialog = new ProgressDialog(getContext());
        cobroViewModel = new CobroViewModel(getContext());
        cargarClientesConCobros();

        RecyclerView recyclerView = view.findViewById(R.id.cobranza_rv);
        clientesConCobroAdapter = new ClienteConCobroAdapter(clientesConCobros);
        busquedaEtx = view.findViewById(R.id.txtCodigo);
        clientesConCobroAdapter.setOnItemClickListener(cliente -> {
            abrirCobrosCliente(cliente);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(clientesConCobroAdapter);

        busquedaEtx.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                ArrayList<Cliente> cobrosFiltrados = new ArrayList<>();

                if (s.toString().isEmpty()) {
                    cobrosFiltrados.addAll(clientesConCobros);
                } else {
                    for (Cliente cliente : clientesConCobros) {
                        if (cliente.getCode().toLowerCase().contains(s)
                                || cliente.getRazon().toLowerCase().contains(s)) {
                            cobrosFiltrados.add(cliente);
                        }
                    }
                }

                clientesConCobroAdapter.setClientesConCobros(cobrosFiltrados);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    private void irAMain(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        MainScreen mainScreen = new MainScreen();
        transaction.replace(R.id.container, mainScreen,"main");
        transaction.addToBackStack("main");
        transaction.commit();
    }

    private void abrirCobrosCliente(Cliente cliente) {
        CobrosFragment cobrosFragment = new CobrosFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("cliente", (Serializable) cliente);
        cobrosFragment.setArguments(bundle);
        FragmentTransaction transaction = requireFragmentManager().beginTransaction();
        transaction.replace(R.id.container, cobrosFragment, "cobro");
        transaction.addToBackStack("cobro");
        transaction.commit();
    }


    private void cargarClientesConCobros() {
        progressDialog.setMessage("Cargando cobros...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        clientesConCobros.clear();
        cobroViewModel.getCobros().observe(this, cobros -> {

            Set<String> codigoClientes = new HashSet<>();

            for (Cobro cobro : cobros) {
                String codigoCliente = cobro.getCodigoCliente();
                String nombreCliente = cobro.getNombreCliente();

                if (!codigoClientes.contains(codigoCliente)) {
                    codigoClientes.add(codigoCliente);
                    Cliente cliente = new Cliente(codigoCliente, nombreCliente);
                    clientesConCobros.add(cliente);
                }
            }
            clientesConCobroAdapter.setClientesConCobros(clientesConCobros);
            progressDialog.dismiss();
        });
    }

    @Override
    public void onItemClick(Cobro cobro) {

    }
}