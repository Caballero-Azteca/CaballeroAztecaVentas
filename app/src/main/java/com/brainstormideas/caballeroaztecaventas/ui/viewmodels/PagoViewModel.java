package com.brainstormideas.caballeroaztecaventas.ui.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.brainstormideas.caballeroaztecaventas.data.models.Pago;
import com.brainstormideas.caballeroaztecaventas.data.repository.PagoRepository;

import java.util.List;

public class PagoViewModel extends ViewModel {

    private final PagoRepository pagoRepository;
    private final LiveData<List<Pago>> pagos;

    public PagoViewModel(Context context) {
        pagoRepository = new PagoRepository(context);
        pagos = pagoRepository.getAllPagos();
    }

    public LiveData<List<Pago>> getPagos() {
        return pagos;
    }

    public LiveData<Pago> getPago(long pagoId) {
        return pagoRepository.getPago(pagoId);
    }

    public void insertPago(Pago pago) {
        pagoRepository.insertPago(pago);
    }

    public void deletePago(Pago pago) {
        pagoRepository.deletePago(pago);
    }

    public void updatePago(Pago pago) {
        pagoRepository.updatePago(pago);
    }
}

