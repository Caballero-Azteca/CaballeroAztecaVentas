package com.brainstormideas.caballeroaztecaventas.data.local.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.brainstormideas.caballeroaztecaventas.data.local.dao.ClienteDAO;
import com.brainstormideas.caballeroaztecaventas.data.local.dao.CobroDAO;
import com.brainstormideas.caballeroaztecaventas.data.local.dao.ProductoDAO;
import com.brainstormideas.caballeroaztecaventas.data.local.dao.VendedorDAO;
import com.brainstormideas.caballeroaztecaventas.data.models.Cliente;
import com.brainstormideas.caballeroaztecaventas.data.models.Cobro;
import com.brainstormideas.caballeroaztecaventas.data.models.Producto;
import com.brainstormideas.caballeroaztecaventas.data.models.Vendedor;

@Database(entities = {Producto.class, Cliente.class, Vendedor.class, Cobro.class}, version = 3, exportSchema = false)
public abstract class RoomLocalDatabase extends RoomDatabase {
    public abstract ProductoDAO productoDAO();

    public abstract ClienteDAO clienteDAO();

    public abstract VendedorDAO vendedorDAO();

    public abstract CobroDAO cobroDAO();


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
