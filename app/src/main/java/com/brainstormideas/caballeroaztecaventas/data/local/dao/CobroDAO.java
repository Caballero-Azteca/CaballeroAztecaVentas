package com.brainstormideas.caballeroaztecaventas.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.brainstormideas.caballeroaztecaventas.data.models.Cobro;

import java.util.List;

@Dao
public interface CobroDAO {

    @Query("SELECT * FROM cobros")
    LiveData<List<Cobro>> getAllCobros();

    @Query("SELECT * FROM cobros WHERE id = :cobroId")
    LiveData<Cobro> getCobro(long cobroId);

    @Insert
    void insertCobro(Cobro cobro);

    @Delete
    void deleteCobro(Cobro cobro);

    @Update
    void updateCobro(Cobro cobro);
}

