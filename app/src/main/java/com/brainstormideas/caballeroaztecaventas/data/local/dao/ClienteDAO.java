package com.brainstormideas.caballeroaztecaventas.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.brainstormideas.caballeroaztecaventas.data.models.Cliente;

import java.util.List;

@Dao
public interface ClienteDAO {

    @Query("SELECT * FROM clientes")
    LiveData<List<Cliente>> getAllClientes();

    @Query("SELECT * FROM productos WHERE id LIKE :code")
    LiveData<Cliente> getCliente(String code);

    @Insert
    void insertCliente(Cliente cliente);

    @Delete
    void deleteCliente(Cliente cliente);

    @Update
    void updateCliente(Cliente cliente);
}
