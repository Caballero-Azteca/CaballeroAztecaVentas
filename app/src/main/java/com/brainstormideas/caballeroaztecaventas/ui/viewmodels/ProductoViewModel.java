package com.brainstormideas.caballeroaztecaventas.ui.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.brainstormideas.caballeroaztecaventas.data.models.Producto;
import com.brainstormideas.caballeroaztecaventas.data.repository.ProductoRepository;

import java.util.List;

public class ProductoViewModel extends ViewModel {
    private ProductoRepository productoRepository;
    private LiveData<List<Producto>> productos;

    public ProductoViewModel(Context context) {
        productoRepository = new ProductoRepository(context);
        productos = productoRepository.getAllProductos();
    }

    public LiveData<List<Producto>> getProductos() {
        return productos;
    }

    public void insertProducto(Producto producto) {
        productoRepository.insertProducto(producto);
    }
}
