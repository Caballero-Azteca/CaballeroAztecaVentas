package com.brainstormideas.caballeroaztecaventas.data.repository;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.brainstormideas.caballeroaztecaventas.data.local.dao.ProductoDAO;
import com.brainstormideas.caballeroaztecaventas.data.local.database.RoomLocalDatabase;
import com.brainstormideas.caballeroaztecaventas.data.models.Producto;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ProductoRepository {

    private static ProductoRepository instance;
    private final ProductoDAO productoDAO;
    private final DatabaseReference productoRef;

    public ProductoRepository(Context context) {

        RoomLocalDatabase database = RoomLocalDatabase.getInstance(context);
        productoDAO = database.productoDAO();
        productoRef = FirebaseDatabase.getInstance().getReference().child("Producto");
    }

    public static synchronized ProductoRepository getInstance(Context context) {
        if (instance == null) {
            instance = new ProductoRepository(context);
        }
        return instance;
    }

    public LiveData<List<Producto>> getAllProductos(Context context) {

        MutableLiveData<List<Producto>> firebaseData = new MutableLiveData<>();

        new FetchDataAsyncTask(context, firebaseData).execute();

        return firebaseData;
    }

    public LiveData<Producto> getProducto(String code) {
        MutableLiveData<Producto> productoLiveData = new MutableLiveData<>();

        Producto cachedProducto = productoDAO.getProducto(code).getValue();
        if (cachedProducto != null) {
            productoLiveData.setValue(cachedProducto);
        } else {
            CompletableFuture.runAsync(() -> {
                Query query = productoRef.orderByChild("code").equalTo(code);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Producto producto = snapshot.getValue(Producto.class);
                                productoLiveData.postValue(producto);
                                break;  // Solo obtenemos el primer producto encontrado
                            }
                        } else {
                            productoLiveData.postValue(null);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        productoLiveData.postValue(null);
                    }
                });
            });
        }

        return productoLiveData;
    }

    public void insertProducto(Producto Producto) {
        productoDAO.insertProducto(Producto);
    }

    private static class FetchDataAsyncTask extends AsyncTask<Void, Void, LiveData<List<Producto>>> {

        private WeakReference<Context> contextRef;
        private MutableLiveData<List<Producto>> firebaseData;

        FetchDataAsyncTask(Context context, MutableLiveData<List<Producto>> firebaseData) {
            this.contextRef = new WeakReference<>(context);
            this.firebaseData = firebaseData;
        }

        @Override
        protected LiveData<List<Producto>> doInBackground(Void... voids) {
            Context context = contextRef.get();
            if (context != null) {
                RoomLocalDatabase database = RoomLocalDatabase.getInstance(context);
                ProductoDAO productoDAO = database.productoDAO();
                return productoDAO.getAllProductos();
            }
            return null;
        }

        @Override
        protected void onPostExecute(LiveData<List<Producto>> localProductos) {
            Context context = contextRef.get();
            if (context != null) {
                DatabaseReference productoRef = FirebaseDatabase.getInstance().getReference().child("Producto");
                productoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Producto> productos = new ArrayList<>();
                        for (DataSnapshot productoSnapshot : snapshot.getChildren()) {
                            Producto producto = productoSnapshot.getValue(Producto.class);
                            productos.add(producto);
                        }
                        // Combinar los datos locales y de Firebase
                        List<Producto> combinedList = new ArrayList<>();
                        if (localProductos != null && localProductos.getValue() != null) {
                            combinedList.addAll(localProductos.getValue());
                        }
                        combinedList.addAll(productos);
                        firebaseData.setValue(combinedList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Manejar error
                    }
                });
            }
        }
    }
}
