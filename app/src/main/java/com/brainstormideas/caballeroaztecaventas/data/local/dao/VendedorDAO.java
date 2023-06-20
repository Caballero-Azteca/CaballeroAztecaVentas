package com.brainstormideas.caballeroaztecaventas.data.local.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.brainstormideas.caballeroaztecaventas.data.models.Vendedor;

import java.util.List;

@Dao
public interface VendedorDAO {

    @Query("SELECT * FROM vendedores")
    LiveData<List<Vendedor>> getAllVendedores();

    @Query("SELECT * FROM vendedores WHERE id LIKE :code")
    LiveData<Vendedor> getVendedor(String code);

    @Insert
    void insertVendedor(Vendedor vendedor);

    @Delete
    void deleteVendedor(Vendedor vendedor);

    @Update
    void updateVendedor(Vendedor vendedor);
}
