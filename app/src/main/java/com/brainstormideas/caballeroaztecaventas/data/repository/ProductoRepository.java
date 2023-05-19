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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class ProductoRepository {
    private ProductoDAO productoDAO;
    private DatabaseReference productoRef;

    public ProductoRepository(Context context) {

        RoomLocalDatabase database = RoomLocalDatabase.getInstance(context);
        productoDAO = database.productoDAO();
        productoRef = FirebaseDatabase.getInstance().getReference().child("Producto");

    }

    public LiveData<List<Producto>> getAllProductos() {

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
//                final LiveData<List<Producto>> localData = (LiveData<List<Producto>>) productoDAO.getAllProductos();
            }
        });

        final MutableLiveData<List<Producto>> firebaseData = new MutableLiveData<>();

        productoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Producto> productos = new ArrayList<>();
                for (DataSnapshot productoSnapshot : snapshot.getChildren()) {
                    Producto producto = productoSnapshot.getValue(Producto.class);
                    productos.add(producto);
                }
                // Combinar los datos locales y de Firebase
                List<Producto> combinedList = new ArrayList<>();
                //combinedList.addAll(localData.getValue());
                combinedList.addAll(productos);
                firebaseData.setValue(combinedList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar error
            }
        });

        return firebaseData;
    }

    public Producto getProducto(String id) {
        return productoDAO.getProducto(id);
    }

    public void insertProducto(Producto Producto) {
        productoDAO.insertProducto(Producto);
    }
}
