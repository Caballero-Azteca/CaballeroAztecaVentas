package com.brainstormideas.caballeroaztecaventas.data.repository;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.brainstormideas.caballeroaztecaventas.managers.PedidoManager;
import com.brainstormideas.caballeroaztecaventas.utils.InternetManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FolioRepository {

    private final DatabaseReference folioRef;
    private final PedidoManager pedidoManager;
    private final InternetManager internetManager;
    private final Context context;

    public FolioRepository(Context context) {
        folioRef = FirebaseDatabase.getInstance().getReference().child("Folio");
        pedidoManager = PedidoManager.getInstance(context);
        internetManager = new InternetManager(context);
        this.context = context;
    }

    public LiveData<List<String>> getAllFolios(String indice, String tipo) {
        final MutableLiveData<List<String>> foliosGeneral = new MutableLiveData<>();

        // Construir la consulta para obtener folios que cumplen con los criterios
        Query query = folioRef.orderByChild("folio").startAt(tipo + indice).endAt(tipo + indice + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> folios = new ArrayList<>();
                for (DataSnapshot folioSnapshot : snapshot.getChildren()) {
                    String folioString = folioSnapshot.child("folio").getValue(String.class);
                    if (folioString != null && folioString.length() >= 8) {
                        folios.add(folioString);
                    }
                }
                foliosGeneral.setValue(folios);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar error
            }
        });

        return foliosGeneral;
    }

    public LiveData<String> getHighestFolio(String indice, String tipoPedido) {
        final MediatorLiveData<String> highestFolio = new MediatorLiveData<>();
        if(internetManager.isInternetAvailable()){
            LiveData<List<String>> allFolios = getAllFolios(indice, tipoPedido);
            highestFolio.addSource(allFolios, folios -> {
                if (folios != null && !folios.isEmpty()) {
                    // Encontrar el folio más alto
                    String maxFolio = Collections.max(folios);

                    // Dividir el folio en las partes correspondientes
                    String tipoPart = maxFolio.substring(0, 1);  // "P" o "C"
                    String indicePart = maxFolio.substring(1, 3);  // PXX
                    String numerosPart = maxFolio.substring(4);  // Ignorar el guion

                    // Obtener el número más alto y agregar 1
                    int highestNumber = Integer.parseInt(numerosPart);
                    highestNumber++;

                    // Formatear el resultado
                    String formattedNumber = String.format("%04d", highestNumber);
                    String result = tipoPart + indicePart + "-" + formattedNumber;

                    saveLastFolioLocal(result, tipoPedido);
                    highestFolio.setValue(result);
                } else {
                    // No hay folios, devuelve el primer folio
                    highestFolio.setValue(indice.substring(0, 1) + indice.substring(1, 3) + "-0001");
                }
            });
        } else {
            highestFolio.setValue(getLastFolioLocalAndIncrement(tipoPedido));
        }
        return highestFolio;
    }

    private void saveLastFolioLocal(String folio, String tipo) {
        if(tipo.equals("P")){
            pedidoManager.guardarUltimoFolioPedidos(folio);
        } else {
            pedidoManager.guardarUltimoFolioCotizaciones(folio);
        }
    }

    private String getLastFolioLocalAndIncrement(String tipo) {
        String ultimoFolio = "";

        if (tipo.equals("P")) {
            ultimoFolio = pedidoManager.getUltimoFolioPedidos();
            pedidoManager.guardarUltimoFolioPedidos(incrementarFolio(ultimoFolio));
        } else {
            ultimoFolio = pedidoManager.getUltimoFolioCotizaciones();
            pedidoManager.guardarUltimoFolioCotizaciones(incrementarFolio(ultimoFolio));
        }

        return ultimoFolio;
    }

    private String incrementarFolio(String folio) {
        // Dividir el folio en partes
        String tipoPart = folio.substring(0, 1);
        String indicePart = folio.substring(1, 3);
        String numerosPart = folio.substring(4);

        // Obtener el número actual y agregar 1
        int numeroActual = Integer.parseInt(numerosPart);
        numeroActual++;

        // Formatear el resultado
        String formattedNumber = String.format("%04d", numeroActual);
        return tipoPart + indicePart + "-" + formattedNumber;
    }

}

