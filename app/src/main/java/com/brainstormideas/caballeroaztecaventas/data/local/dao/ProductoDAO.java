package com.brainstormideas.caballeroaztecaventas.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.brainstormideas.caballeroaztecaventas.data.models.Producto;

import java.util.List;

@Dao
public interface ProductoDAO {
    @Query("SELECT * FROM productos")
    List<Producto> getAllProductos();

    @Query("SELECT * FROM productos WHERE id LIKE :uuid")
    Producto getProducto(String uuid);
    @Insert
    void insertProducto(Producto producto);

    @Delete
    void deleteProducto(Producto producto);

    @Update
    void updateProducto(Producto producto);
}
