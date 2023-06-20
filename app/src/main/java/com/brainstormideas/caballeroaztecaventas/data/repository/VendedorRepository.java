package com.brainstormideas.caballeroaztecaventas.data.repository;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.brainstormideas.caballeroaztecaventas.data.local.dao.VendedorDAO;
import com.brainstormideas.caballeroaztecaventas.data.local.database.RoomLocalDatabase;
import com.brainstormideas.caballeroaztecaventas.data.models.Vendedor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class VendedorRepository {

    private final VendedorDAO vendedorDAO;
    private final DatabaseReference vendedorRef;

    public VendedorRepository(Context context) {
        RoomLocalDatabase database = RoomLocalDatabase.getInstance(context);
        vendedorDAO = database.vendedorDAO();
        vendedorRef = FirebaseDatabase.getInstance().getReference().child("Usuario");
    }

    public LiveData<List<Vendedor>> getAllVendedores() {

        final MutableLiveData<List<Vendedor>> firebaseData = new MutableLiveData<>();

        new AsyncTask<Void, Void, LiveData<List<Vendedor>>>() {
            @Override
            protected LiveData<List<Vendedor>> doInBackground(Void... voids) {
                return vendedorDAO.getAllVendedores();
            }

            @Override
            protected void onPostExecute(LiveData<List<Vendedor>> localVendedores) {
                vendedorRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Vendedor> vendedores = new ArrayList<>();
                        for (DataSnapshot vendedorSnapshot : snapshot.getChildren()) {
                            Vendedor vendedor = vendedorSnapshot.getValue(Vendedor.class);
                            vendedores.add(vendedor);
                        }
                        // Combinar los datos locales y de Firebase
                        List<Vendedor> combinedList = new ArrayList<>();
                        if (localVendedores.getValue() != null) {
                            combinedList.addAll(localVendedores.getValue());
                        }
                        combinedList.addAll(vendedores);
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

    public LiveData<Vendedor> getVendedor(String code) {
        MutableLiveData<Vendedor> vendedorLiveData = new MutableLiveData<>();

        // Consultar la caché local primero
        Vendedor cachedVendedor = vendedorDAO.getVendedor(code).getValue();
        if (cachedVendedor != null) {
            vendedorLiveData.setValue(cachedVendedor);
        } else {
            CompletableFuture.runAsync(() -> {
                Query query = vendedorRef.orderByChild("email").equalTo(code);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Vendedor vendedor = snapshot.getValue(Vendedor.class);
                                vendedorLiveData.postValue(vendedor);
                                break;  // Solo obtenemos el primer producto encontrado
                            }
                        } else {
                            vendedorLiveData.postValue(null);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        vendedorLiveData.postValue(null);
                    }
                });
            });
        }

        return vendedorLiveData;
    }

    public void insertVendedor(Vendedor vendedor) {
        vendedorDAO.insertVendedor(vendedor);
    }

}
