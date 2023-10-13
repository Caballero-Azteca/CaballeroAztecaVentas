package com.brainstormideas.caballeroaztecaventas.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.brainstormideas.caballeroaztecaventas.data.models.Pago;

import java.util.List;

@Dao
public interface PagoDAO {
    @Query("SELECT * FROM pagos")
    LiveData<List<Pago>> getAllPagos();

    @Query("SELECT * FROM pagos WHERE id = :id")
    LiveData<Pago> getPago(Long id);

    @Insert
    void insertPago(Pago pago);

    @Update
    void updatePago(Pago pago);

    @Delete
    void deletePago(Pago pago);
}
