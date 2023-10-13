package com.brainstormideas.caballeroaztecaventas.data.repository;

import android.content.Context;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.brainstormideas.caballeroaztecaventas.data.local.dao.PagoDAO;
import com.brainstormideas.caballeroaztecaventas.data.local.database.RoomLocalDatabase;
import com.brainstormideas.caballeroaztecaventas.data.models.Pago;
import com.brainstormideas.caballeroaztecaventas.data.models.PagoCheque;
import com.brainstormideas.caballeroaztecaventas.data.models.PagoEfectivo;
import com.brainstormideas.caballeroaztecaventas.data.models.PagoTransferencia;
import com.brainstormideas.caballeroaztecaventas.data.models.TipoPago;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PagoRepository {

    private final PagoDAO pagoDAO;
    private final DatabaseReference pagoRef;

    public PagoRepository(Context context) {
        RoomLocalDatabase database = RoomLocalDatabase.getInstance(context);
        pagoDAO = database.pagoDAO();
        pagoRef = FirebaseDatabase.getInstance().getReference().child("Pago");
    }

    public LiveData<List<Pago>> getAllPagos() {
        final MutableLiveData<List<Pago>> firebaseData = new MutableLiveData<>();

        new AsyncTask<Void, Void, LiveData<List<Pago>>>() {
            @Override
            protected LiveData<List<Pago>> doInBackground(Void... voids) {
                return pagoDAO.getAllPagos();
            }

            @Override
            protected void onPostExecute(LiveData<List<Pago>> localPagos) {
                pagoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Pago> pagos = new ArrayList<>();
                        for (DataSnapshot pagoSnapshot : snapshot.getChildren()) {
                            // Aquí identificamos el tipo de pago en base a los datos recibidos
                            Pago pago = identificarTipoDePago(pagoSnapshot);

                            if (pago != null) {
                                System.out.println(pago.toString());
                                pagos.add(pago);
                            }
                        }

                        // Combinar los datos locales y de Firebase
                        List<Pago> combinedList = new ArrayList<>();
                        if (localPagos.getValue() != null) {
                            combinedList.addAll(localPagos.getValue());
                        }
                        combinedList.addAll(pagos);
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

    public LiveData<Pago> getPago(long pagoId) {
        MutableLiveData<Pago> pagoLiveData = new MutableLiveData<>();

        // Consultar la caché local primero
        Pago cachedPago = pagoDAO.getPago(pagoId).getValue();
        if (cachedPago != null) {
            pagoLiveData.setValue(cachedPago);
        } else {
            // Consultar en Firebase si no se encuentra localmente
            CompletableFuture.runAsync(() -> {
                Query query = pagoRef.orderByChild("id").equalTo(pagoId);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                Pago pago = identificarTipoDePago(snapshot);

                                if (pago != null) {
                                    pagoLiveData.postValue(pago);
                                } else {
                                    pagoLiveData.postValue(snapshot.getValue(Pago.class));
                                }
                                break;
                            }
                        } else {
                            pagoLiveData.postValue(null);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        pagoLiveData.postValue(null);
                    }
                });
            });
        }

        return pagoLiveData;
    }

    private Pago identificarTipoDePago(DataSnapshot pagoSnapshot) {

        String tipoPago = pagoSnapshot.child("tipoPago").getValue(String.class);
        if (tipoPago != null) {
            switch (tipoPago) {
                case "efectivo":
                    return pagoSnapshot.getValue(PagoEfectivo.class);
                case "cheque":
                    return pagoSnapshot.getValue(PagoCheque.class);
                case "transferencia":
                    return pagoSnapshot.getValue(PagoTransferencia.class);
                default:
                    return null;
            }
        }

        return null;
    }

    public CompletableFuture<Void> insertPago(Pago pago) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        new InsertPagoAsyncTask(pagoDAO, pagoRef, future).execute(pago);

        return future;
    }

    private static class InsertPagoAsyncTask extends AsyncTask<Pago, Void, Void> {
        private PagoDAO pagoDAO;
        private DatabaseReference pagoRef;
        private CompletableFuture<Void> future;

        public InsertPagoAsyncTask(PagoDAO pagoDAO, DatabaseReference pagoRef, CompletableFuture<Void> future) {
            this.pagoDAO = pagoDAO;
            this.pagoRef = pagoRef;
            this.future = future;
        }

        @Override
        protected Void doInBackground(Pago... pagos) {
            // Insertar en la base de datos local
            pagoDAO.insertPago(pagos[0]);
            DatabaseReference newPagoRef = pagoRef.push();
            newPagoRef.setValue(pagos[0])
                    .addOnSuccessListener(aVoid -> {
                        future.complete(null);
                    })
                    .addOnFailureListener(e -> {
                        future.completeExceptionally(e);
                    });

            return null;
        }
    }

    public void deletePago(Pago pago) {
        pagoDAO.deletePago(pago);
    }

    public void updatePago(Pago pago) {
        pagoDAO.updatePago(pago);
    }
}

