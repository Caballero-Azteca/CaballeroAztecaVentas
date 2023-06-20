package com.brainstormideas.caballeroaztecaventas.ui.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.brainstormideas.caballeroaztecaventas.data.models.Vendedor;
import com.brainstormideas.caballeroaztecaventas.data.repository.VendedorRepository;

import java.util.List;

public class VendedorViewModel extends ViewModel {

    private final VendedorRepository vendedorRepository;
    private final LiveData<List<Vendedor>> vendedores;

    public VendedorViewModel(Context context) {
        vendedorRepository = new VendedorRepository(context);
        vendedores = vendedorRepository.getAllVendedores();
    }

    public LiveData<List<Vendedor>> getVendedores() {
        return vendedores;
    }

    public LiveData<Vendedor> getVendedor(String vendeorId) {
        return vendedorRepository.getVendedor(vendeorId);
    }

    public void insertVendedor(Vendedor vendedor) {
        vendedorRepository.insertVendedor(vendedor);
    }
}
