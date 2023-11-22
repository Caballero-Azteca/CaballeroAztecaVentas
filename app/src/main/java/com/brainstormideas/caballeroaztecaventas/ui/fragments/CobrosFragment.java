package com.brainstormideas.caballeroaztecaventas.ui.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.brainstormideas.caballeroaztecaventas.R;
import com.brainstormideas.caballeroaztecaventas.data.models.Cliente;
import com.brainstormideas.caballeroaztecaventas.data.models.Cobro;
import com.brainstormideas.caballeroaztecaventas.ui.adapters.CobroAdapter;
import com.brainstormideas.caballeroaztecaventas.ui.viewmodels.CobroViewModel;

import java.io.Serializable;
import java.util.ArrayList;


public class CobrosFragment extends Fragment implements CobroAdapter.OnItemClickListener {

    EditText busquedaEtx;
    private Button irAMain;
    private CobroViewModel cobroViewModel;
    ArrayList<Cobro> cobros = new ArrayList<>();
    private ProgressDialog progressDialog;
    CobroAdapter cobroAdapter;
    private  Cliente cliente;

    public CobrosFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_cobros, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();

        Bundle args = getArguments();
        if (args != null) {
            cliente = (Cliente) args.getSerializable("cliente");
        }

        irAMain = view.findViewById(R.id.back_btn);
        irAMain.setOnClickListener(view1 -> irAMain());

        ActionBar actionBar = activity.getSupportActionBar();

        if (activity != null) {
            if (actionBar != null) {
                SpannableString spannableString = new SpannableString(cliente.getRazon());
                spannableString.setSpan(new TextAppearanceSpan(activity, R.style.ActionBarTextStyle), 0, spannableString.length(), 0);
                actionBar.setTitle(spannableString);
            }
        }

        progressDialog = new ProgressDialog(getContext());
        cobroViewModel = new CobroViewModel(getContext());
        cargarCobros();
        RecyclerView recyclerView = view.findViewById(R.id.cobros_rv);
        cobroAdapter = new CobroAdapter(cobros);
        busquedaEtx = view.findViewById(R.id.txtCodigo);
        cobroAdapter.setOnItemClickListener(cobro -> {
            abrirCobro(cobro);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(cobroAdapter);

        busquedaEtx.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                ArrayList<Cobro> cobrosFiltrados = new ArrayList<>();

                if (TextUtils.isEmpty(s)) {
                    cobrosFiltrados.addAll(cobros);
                } else {
                    for (Cobro cobro : cobros) {
                        if (cobro.getFactura().toLowerCase().contains(s)) {
                            cobrosFiltrados.add(cobro);
                        }
                    }
                }

                cobroAdapter.setCobros(cobrosFiltrados);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    private void abrirCobro(Cobro cobro) {
        DetallesCobroFragment detallesCobroFragment = new DetallesCobroFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("cobro", (Serializable) cobro);
        bundle.putSerializable("cliente",(Serializable) cliente);
        detallesCobroFragment.setArguments(bundle);
        FragmentTransaction transaction = requireFragmentManager().beginTransaction();
        transaction.replace(R.id.container, detallesCobroFragment, "detallesCobro");
        transaction.addToBackStack("detallesCobro");
        transaction.commit();
    }

    private void irAMain(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        CobranzaScreen cobranzaScreen = new CobranzaScreen();
        transaction.replace(R.id.container, cobranzaScreen, "cobranza");
        transaction.addToBackStack("cobranza");
        transaction.commit();
    }

    private void cargarCobros() {
        progressDialog.setMessage("Cargando cobros...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        cobros.clear();
        cobroViewModel.getCobros().observe(getViewLifecycleOwner(), cobrosNuevos -> {
            ArrayList<Cobro> cobrosClienteEspecifico = new ArrayList<>();

            for (Cobro cobro : cobrosNuevos) {
                if (cobro.getCodigoCliente() != null && cobro.getCodigoCliente().equals(cliente.getCode())) {
                    cobrosClienteEspecifico.add(cobro);
                }
            }
            cobros.addAll(cobrosClienteEspecifico);
            cobroAdapter.setCobros(cobros);
            progressDialog.dismiss();
        });
    }

    @Override
    public void onItemClick(Cobro cobro) {
    }
}