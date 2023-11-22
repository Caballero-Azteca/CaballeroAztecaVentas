package com.brainstormideas.caballeroaztecaventas.ui.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.brainstormideas.caballeroaztecaventas.data.repository.FolioRepository;

import java.util.List;

public class FolioViewModel extends ViewModel {

    private final FolioRepository folioRepository;

    public FolioViewModel(Context context) {
        folioRepository = new FolioRepository(context);
    }

    public LiveData<List<String>> getAllFolios(String indice, String tipoPedido) {
        return folioRepository.getAllFolios(indice, tipoPedido);
    }

    public LiveData<String> getHighestFolio(String indice, String tipoPedido) {
        return folioRepository.getHighestFolio(indice, tipoPedido);
    }
}

