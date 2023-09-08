package com.brainstormideas.caballeroaztecaventas.ui.viewmodels;

import android.content.Context;
import androidx.lifecycle.LiveData;
import com.brainstormideas.caballeroaztecaventas.data.models.Cobro;
import com.brainstormideas.caballeroaztecaventas.data.repository.CobroRepository;
import java.util.List;
import androidx.lifecycle.ViewModel;

public class CobroViewModel extends ViewModel {

    private final CobroRepository cobroRepository;
    private final LiveData<List<Cobro>> cobros;

    public CobroViewModel(Context context) {
        cobroRepository = new CobroRepository(context);
        cobros = cobroRepository.getAllCobros();
    }

    public LiveData<List<Cobro>> getCobros() {
        return cobros;
    }

    public LiveData<Cobro> getCobro(long cobroId) {
        return cobroRepository.getCobro(cobroId);
    }

    public void insertCobro(Cobro cobro) {
        cobroRepository.insertCobro(cobro);
    }

    public void deleteCobro(Cobro cobro) {
        cobroRepository.deleteCobro(cobro);
    }

    public void updateCobro(Cobro cobro) {
        cobroRepository.updateCobro(cobro);
    }
}

