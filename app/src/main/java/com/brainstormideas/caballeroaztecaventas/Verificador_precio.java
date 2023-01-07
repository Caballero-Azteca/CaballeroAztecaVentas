package com.brainstormideas.caballeroaztecaventas;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brainstormideas.caballeroaztecaventas.adaptadores.ControllerRecyclerViewAdapter;
import com.brainstormideas.caballeroaztecaventas.adaptadores.RecyclerViewAdapter;
import com.brainstormideas.caballeroaztecaventas.entidad.Item;
import com.brainstormideas.caballeroaztecaventas.tools.SessionManager;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

public class Verificador_precio extends AppCompatActivity implements SearchView.OnQueryTextListener {

    ImageButton home_button;
    Button detalles_btn;
    Button buscar_button;
    Button agregar_producto_btn;

    RecyclerView recyclerView;
    RecyclerViewAdapter adapter;
    RecyclerView.LayoutManager manager;

    FirebaseAuth mAuth;
    FirebaseUser user;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference dbProductosReferencia;
    ArrayList<Item> nombresProductos = new ArrayList<>();

    ProgressDialog progressDialog;
    SessionManager sessionManager;

    static boolean isInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verificador_precio);

        ControllerRecyclerViewAdapter.itemSeleccionado = null;

        initializedFirebaseService();

        dbProductosReferencia = FirebaseDatabase.getInstance().getReference().child("Producto");

        sessionManager = new SessionManager(this);

        recyclerView = findViewById(R.id.lista_productos);
        manager = new LinearLayoutManager(this);
        adapter = new RecyclerViewAdapter(this, nombresProductos);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        progressDialog = new ProgressDialog(this);

        detalles_btn = findViewById(R.id.detalles_btn);
        detalles_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ControllerRecyclerViewAdapter.itemSeleccionado != null) {
                    detallesDeProducto();

                } else {
                    Toast.makeText(getApplicationContext(), "Seleccione un artículo primero.", Toast.LENGTH_LONG).show();
                }
            }
        });
        buscar_button = findViewById(R.id.buscar_btn);
        home_button = findViewById(R.id.home_button);
        home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                home();
            }
        });
        buscar_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irBuscar();
            }
        });

        agregar_producto_btn = findViewById(R.id.agregar_producto_btn);
        agregar_producto_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irAgregarProductor();
            }
        });

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if(!sessionManager.getUsuario().equals("admin")){
            agregar_producto_btn.setEnabled(false);
        }

        obtenerProductos();

    }

    private void initializedFirebaseService() {
        FirebaseApp.initializeApp(this);
        try{
            if(!isInitialized){
                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                isInitialized = true;
            }else {
                Log.d("ATENCION-FIREBASE:","Already Initialized");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void irBuscar() {

        Intent i = new Intent(getApplicationContext(), Marcas.class);
        i.putExtra("tipoCliente", "consulta");
        i.putExtra("ruta", "verificadorPrecio");
        startActivity(i);

    }

    private void home() {

        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private void irAgregarProductor() {
        Intent i = new Intent(this, Agregar_producto.class);
        startActivity(i);
    }

    private void obtenerProductos() {

        progressDialog.setMessage("Cargando productos");
        progressDialog.setCancelable(false);
        progressDialog.show();

        dbProductosReferencia.addValueEventListener(new ValueEventListener() {

            double lista = 0.0;
            double cca = 0.0;
            double p1 = 0.0;
            double p2 = 0.0;
            double p3 = 0.0;
            double p4 = 0.0;

            String id="";
            String nombre="";
            String marca="";

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    for (DataSnapshot data : snapshot.getChildren()) {

                        if (data.child("id").getValue() != null && data.child("nombre").getValue() != null
                                && data.child("marca").getValue() != null && data.child("lista").getValue() != null
                                && data.child("cca").getValue() != null && data.child("p1").getValue() != null
                                && data.child("p2").getValue() != null && data.child("p3").getValue() != null
                                && data.child("p4").getValue() != null) {

                            DecimalFormat df = new DecimalFormat("#.00");

                            id = Objects.requireNonNull(data.child("id").getValue()).toString();
                            nombre = Objects.requireNonNull(data.child("nombre").getValue()).toString();
                            marca = Objects.requireNonNull(data.child("marca").getValue()).toString();

                            lista = Double.parseDouble(Objects.requireNonNull(data.child("lista").getValue()).toString());
                            cca = Double.parseDouble(Objects.requireNonNull(data.child("cca").getValue()).toString());
                            p1 = Double.parseDouble(Objects.requireNonNull(data.child("p1").getValue()).toString());
                            p2 = Double.parseDouble(Objects.requireNonNull(data.child("p2").getValue()).toString());
                            p3 = Double.parseDouble(Objects.requireNonNull(data.child("p3").getValue()).toString());
                            p4 = Double.parseDouble(Objects.requireNonNull(data.child("p4").getValue()).toString());


                            Item item = new Item(id, nombre, marca, df.format(lista),
                                    df.format(cca), df.format(p1),df.format(p2),
                                    df.format(p3), df.format(p4), null);

                            nombresProductos.add(item);
                            adapter.notifyDataSetChanged();

                        }
                    }

                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void detallesDeProducto() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Detalles del producto");
        builder.setMessage("Consulta.");
        View viewInflated = LayoutInflater.from(getApplicationContext()).inflate(R.layout.detalles_producto, null);
        builder.setView(viewInflated);

        final TextView productoNameTv = viewInflated.findViewById(R.id.productoNameTv);
        final TextView cotigoTv = viewInflated.findViewById(R.id.cotigoTv);
        final TextView marcaTv = viewInflated.findViewById(R.id.marcaTv);
        final ListView preciosLv = viewInflated.findViewById(R.id.preciosLv);

        productoNameTv.setText("PRODUCTO: " + ControllerRecyclerViewAdapter.itemSeleccionado.getTitulo());
        cotigoTv.setText("CODIGO: " + ControllerRecyclerViewAdapter.itemSeleccionado.getId());
        marcaTv.setText("MARCA: " + ControllerRecyclerViewAdapter.itemSeleccionado.getDato1());

        ArrayList<String> precios = new ArrayList<String>();
        precios.add("P3: " + ControllerRecyclerViewAdapter.itemSeleccionado.getDato6());
        precios.add("LISTA: " + ControllerRecyclerViewAdapter.itemSeleccionado.getDato2());
        precios.add("P1: " + ControllerRecyclerViewAdapter.itemSeleccionado.getDato4());
        precios.add("P2: " + ControllerRecyclerViewAdapter.itemSeleccionado.getDato5());
        precios.add("P4: " + ControllerRecyclerViewAdapter.itemSeleccionado.getDato7());
        precios.add("CCA: " + ControllerRecyclerViewAdapter.itemSeleccionado.getDato3());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, precios);
        preciosLv.setAdapter(adapter);

        builder.setPositiveButton("Atras", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_buscador, menu);
        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
        MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                adapter.setFilter(nombresProductos);
                adapter.notifyDataSetChanged();
                return true;
            }
        });
        return true;
    }

    @Override
    public void onBackPressed() {
        home();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        try {
            ArrayList<Item> listaFiltrada = filter(nombresProductos, newText);
            adapter.setFilter(listaFiltrada);
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private ArrayList<Item> filter(ArrayList<Item> items, String texto) {
        ArrayList<Item> listaFiltrada = new ArrayList<>();
        try {
            String textoMiniscula = texto.toLowerCase();
            for (Item item : items) {
                String itemFilter = item.getTitulo().toLowerCase();
                if (itemFilter.contains(textoMiniscula)) {
                    listaFiltrada.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaFiltrada;
    }

}
