package com.brainstormideas.caballeroaztecaventas.data.local.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.brainstormideas.caballeroaztecaventas.data.local.converters.Converters;
import com.brainstormideas.caballeroaztecaventas.data.local.dao.ClienteDAO;
import com.brainstormideas.caballeroaztecaventas.data.local.dao.CobroDAO;
import com.brainstormideas.caballeroaztecaventas.data.local.dao.PagoDAO;
import com.brainstormideas.caballeroaztecaventas.data.local.dao.ProductoDAO;
import com.brainstormideas.caballeroaztecaventas.data.local.dao.VendedorDAO;
import com.brainstormideas.caballeroaztecaventas.data.models.Cliente;
import com.brainstormideas.caballeroaztecaventas.data.models.Cobro;
import com.brainstormideas.caballeroaztecaventas.data.models.Folio;
import com.brainstormideas.caballeroaztecaventas.data.models.Pago;
import com.brainstormideas.caballeroaztecaventas.data.models.Producto;
import com.brainstormideas.caballeroaztecaventas.data.models.Vendedor;

@Database(entities = {Producto.class, Cliente.class, Vendedor.class, Cobro.class, Pago.class}, version = 6, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class RoomLocalDatabase extends RoomDatabase {

    public abstract ProductoDAO productoDAO();

    public abstract ClienteDAO clienteDAO();

    public abstract VendedorDAO vendedorDAO();

    public abstract CobroDAO cobroDAO();

    public abstract PagoDAO pagoDAO();

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
