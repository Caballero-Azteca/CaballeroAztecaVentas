package com.brainstormideas.caballeroaztecaventas.data.repository;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.brainstormideas.caballeroaztecaventas.data.local.dao.CobroDAO;
import com.brainstormideas.caballeroaztecaventas.data.local.database.RoomLocalDatabase;
import com.brainstormideas.caballeroaztecaventas.data.models.Cobro;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class CobroRepository {

    private final CobroDAO cobroDAO;
    private final DatabaseReference cobroRef;

    public CobroRepository(Context context) {

        RoomLocalDatabase database = RoomLocalDatabase.getInstance(context);
        cobroDAO = database.cobroDAO();
        cobroRef = FirebaseDatabase.getInstance().getReference().child("Cobranza");

    }

    public LiveData<List<Cobro>> getAllCobros() {

        final MutableLiveData<List<Cobro>> firebaseData = new MutableLiveData<>();

        new AsyncTask<Void, Void, LiveData<List<Cobro>>>() {
            @Override
            protected LiveData<List<Cobro>> doInBackground(Void... voids) {
                return cobroDAO.getAllCobros();
            }

            @Override
            protected void onPostExecute(LiveData<List<Cobro>> localCobros) {
                cobroRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Cobro> cobros = new ArrayList<>();
                        for (DataSnapshot cobroSnapshot : snapshot.getChildren()) {
                            Cobro cobro = cobroSnapshot.getValue(Cobro.class);
                            System.out.println(cobro.toString());
                            cobros.add(cobro);
                        }
                        // Combinar los datos locales y de Firebase
                        List<Cobro> combinedList = new ArrayList<>();
                        if (localCobros.getValue() != null) {
                            combinedList.addAll(localCobros.getValue());
                        }
                        combinedList.addAll(cobros);
                        firebaseData.setValue(combinedList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Manejar error
                    }
                });
            }
        }.execute();

        return firebaseData;
    }

    public LiveData<Cobro> getCobro(long cobroId) {
        MutableLiveData<Cobro> cobroLiveData = new MutableLiveData<>();

        // Consultar la caché local primero
        Cobro cachedCobro = cobroDAO.getCobro(cobroId).getValue();
        if (cachedCobro != null) {
            cobroLiveData.setValue(cachedCobro);
        } else {
            // Consultar en Firebase si no se encuentra localmente
            CompletableFuture.runAsync(() -> {
                Query query = cobroRef.orderByChild("id").equalTo(cobroId);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Cobro cobro = snapshot.getValue(Cobro.class);
                                cobroLiveData.postValue(cobro);
                                break;  // Solo obtenemos el primer Cobro encontrado
                            }
                        } else {
                            cobroLiveData.postValue(null);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        cobroLiveData.postValue(null);
                    }
                });
            });
        }

        return cobroLiveData;
    }

    public void insertCobro(Cobro cobro) {
        cobroDAO.insertCobro(cobro);
    }

    public void deleteCobro(Cobro cobro) {
        cobroDAO.deleteCobro(cobro);
    }

    public CompletableFuture<Void> updateCobro(Cobro cobro) {

        CompletableFuture<Void> localUpdateFuture = CompletableFuture.runAsync(() -> {
            cobroDAO.updateCobro(cobro);
        });

        CompletableFuture<Void> firebaseUpdateFuture = new CompletableFuture<>();
        Query query = cobroRef.orderByChild("factura").equalTo(cobro.getFactura());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Cobro firebaseCobro = snapshot.getValue(Cobro.class);
                    if (firebaseCobro != null && firebaseCobro.getFactura().equals(cobro.getFactura())) {
                        snapshot.getRef().setValue(cobro)
                                .addOnSuccessListener(aVoid -> {
                                    firebaseUpdateFuture.complete(null);
                                })
                                .addOnFailureListener(firebaseUpdateFuture::completeExceptionally);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                firebaseUpdateFuture.completeExceptionally(databaseError.toException());
            }
        });

        return CompletableFuture.allOf(localUpdateFuture, firebaseUpdateFuture);
    }
}

