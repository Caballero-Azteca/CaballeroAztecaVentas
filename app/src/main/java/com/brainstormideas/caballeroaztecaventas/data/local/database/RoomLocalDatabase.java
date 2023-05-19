package com.brainstormideas.caballeroaztecaventas.data.local.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.brainstormideas.caballeroaztecaventas.data.local.dao.ProductoDAO;
import com.brainstormideas.caballeroaztecaventas.data.models.Producto;

@Database(entities = {Producto.class}, version = 1, exportSchema = false)
public abstract class RoomLocalDatabase extends RoomDatabase {
    public abstract ProductoDAO productoDAO();

    private static RoomLocalDatabase instance;

    public static synchronized RoomLocalDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            RoomLocalDatabase.class, "local_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
