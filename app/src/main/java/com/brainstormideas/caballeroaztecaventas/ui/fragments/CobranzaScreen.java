package com.brainstormideas.caballeroaztecaventas.ui.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.brainstormideas.caballeroaztecaventas.R;
import com.brainstormideas.caballeroaztecaventas.data.models.Cobro;
import com.brainstormideas.caballeroaztecaventas.ui.adapters.CobroAdapter;
import com.brainstormideas.caballeroaztecaventas.ui.viewmodels.CobroViewModel;

import java.util.ArrayList;

public class CobranzaScreen extends Fragment {

    private Button irAMain;

    private CobroViewModel cobroViewModel;

    ArrayList<Cobro> cobros = new ArrayList<>();

    private ProgressDialog progressDialog;
    CobroAdapter cobroAdapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cobranza_screen, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        irAMain = view.findViewById(R.id.back_btn);
        irAMain.setOnClickListener(view1 -> irAMain());

        if (activity != null) {
            activity.getSupportActionBar().setTitle("Cobranza");
        }

        progressDialog = new ProgressDialog(getContext());
        cobroViewModel = new CobroViewModel(getContext());
        cargarCobros();
        RecyclerView recyclerView = view.findViewById(R.id.cobranza_rv);
        cobroAdapter = new CobroAdapter(cobros);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(cobroAdapter);

        return view;
    }

    private void irAMain(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        MainScreen mainScreen = new MainScreen();
        transaction.replace(R.id.container, mainScreen);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    private void cargarCobros() {
        progressDialog.setMessage("Cargando cobros...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        cobroViewModel.getCobros().observe(this, cobrosNuevos -> {
            ArrayList<Cobro> nuevaListaCobros = new ArrayList<>(cobros);

            for (Cobro cobro : cobrosNuevos) {
                nuevaListaCobros.add(cobro);
            }

            cobros.clear();
            cobros.addAll(nuevaListaCobros);
            cobroAdapter.notifyDataSetChanged();

            progressDialog.dismiss();
        });
    }

}