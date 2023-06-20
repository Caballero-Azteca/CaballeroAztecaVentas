package com.brainstormideas.caballeroaztecaventas.data.repository;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.brainstormideas.caballeroaztecaventas.data.local.dao.ClienteDAO;
import com.brainstormideas.caballeroaztecaventas.data.local.database.RoomLocalDatabase;
import com.brainstormideas.caballeroaztecaventas.data.models.Cliente;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ClienteRepository {

    private final ClienteDAO clienteDAO;
    private final DatabaseReference clienteRef;

    public ClienteRepository(Context context) {

        RoomLocalDatabase database = RoomLocalDatabase.getInstance(context);
        clienteDAO = database.clienteDAO();
        clienteRef = FirebaseDatabase.getInstance().getReference().child("Cliente");

    }

    public LiveData<List<Cliente>> getAllClientes() {

        final MutableLiveData<List<Cliente>> firebaseData = new MutableLiveData<>();

        new AsyncTask<Void, Void, LiveData<List<Cliente>>>() {
            @Override
            protected LiveData<List<Cliente>> doInBackground(Void... voids) {
                return clienteDAO.getAllClientes();
            }

            @Override
            protected void onPostExecute(LiveData<List<Cliente>> localClientes) {
                clienteRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Cliente> clientes = new ArrayList<>();
                        for (DataSnapshot clienteSnapshot : snapshot.getChildren()) {
                            Cliente cliente = clienteSnapshot.getValue(Cliente.class);
                            clientes.add(cliente);
                        }
                        // Combinar los datos locales y de Firebase
                        List<Cliente> combinedList = new ArrayList<>();
                        if (localClientes.getValue() != null) {
                            combinedList.addAll(localClientes.getValue());
                        }
                        combinedList.addAll(clientes);
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

    public LiveData<Cliente> getCliente(String code) {
        MutableLiveData<Cliente> clienteLiveData = new MutableLiveData<>();

        // Consultar la caché local primero
        Cliente cachedCliente = clienteDAO.getCliente(code).getValue();
        if (cachedCliente != null) {
            clienteLiveData.setValue(cachedCliente);
        } else {
            CompletableFuture.runAsync(() -> {
                Query query = clienteRef.orderByChild("code").equalTo(code);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Cliente cliente = snapshot.getValue(Cliente.class);
                                clienteLiveData.postValue(cliente);
                                break;  // Solo obtenemos el primer Cliente encontrado
                            }
                        } else {
                            clienteLiveData.postValue(null);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        clienteLiveData.postValue(null);
                    }
                });
            });
        }

        return clienteLiveData;
    }

    public void insertCliente(Cliente cliente) {
        clienteDAO.insertCliente(cliente);
    }

}
