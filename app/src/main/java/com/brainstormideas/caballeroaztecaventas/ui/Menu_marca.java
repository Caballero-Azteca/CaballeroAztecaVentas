package com.brainstormideas.caballeroaztecaventas.ui;

import static com.brainstormideas.caballeroaztecaventas.ui.MainActivity.isInitialized;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.brainstormideas.caballeroaztecaventas.R;
import com.brainstormideas.caballeroaztecaventas.entidad.Item;
import com.brainstormideas.caballeroaztecaventas.ui.adapters.ControllerRecyclerViewAdapter;
import com.brainstormideas.caballeroaztecaventas.ui.adapters.RecyclerViewAdapter;
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

public class Menu_marca extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private Button detalles_btn;
    private ImageButton home_btn;
    private Button agregar_btn;
    private ImageButton direct_home_btn;

    RecyclerView recyclerView;
    RecyclerViewAdapter adapter;
    RecyclerView.LayoutManager manager;

    String cliente_seleccionado;
    FirebaseAuth mAuth;
    FirebaseUser user;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference dbProductosReferencia;

    ArrayList<Item> nombresProductos = new ArrayList<>();

    ProgressDialog progressDialog;

    String marcaSeleccionada;
    String ruta;
    String tipoCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_marca);

        initializedFirebaseService();
        ControllerRecyclerViewAdapter.itemSeleccionado = null;
        ControllerRecyclerViewAdapter.posicion = 0;

        marcaSeleccionada = getIntent().getExtras().get("marca").toString();
        ruta = getIntent().getExtras().get("ruta").toString();
        if (!ruta.equals("verificadorPrecio")) {
            tipoCliente = getIntent().getExtras().get("tipoCliente").toString();
        } else {
            tipoCliente = "consulta";
        }


        getSupportActionBar().setTitle(marcaSeleccionada);

        FirebaseApp.initializeApp(this);
        dbProductosReferencia = FirebaseDatabase.getInstance().getReference().child("Producto");
        cargarProductosDeMarca(marcaSeleccionada);

        detalles_btn = findViewById(R.id.detalles_btn);
        home_btn = findViewById(R.id.home_button);
        agregar_btn = findViewById(R.id.agregar_btn);
        direct_home_btn = findViewById(R.id.direct_home_btn);

        progressDialog = new ProgressDialog(this);

        recyclerView = findViewById(R.id.lista_productos_marca_tv);
        manager = new LinearLayoutManager(this);
        adapter = new RecyclerViewAdapter(this, nombresProductos);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        detalles_btn.setOnClickListener(v -> {
            if (ControllerRecyclerViewAdapter.itemSeleccionado != null) {
                detallesDeProducto();
            } else {
                Toast.makeText(getApplicationContext(), "Seleccione un articulo primero", Toast.LENGTH_LONG).show();
            }
        });

        agregar_btn.setOnClickListener(v -> {
            if (ControllerRecyclerViewAdapter.itemSeleccionado != null) {
                obtenerProducto();
                Toast.makeText(getApplicationContext(), "Articulo seleccionado: " + ControllerRecyclerViewAdapter.itemSeleccionado.getTitulo(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Seleccione un articulo primero", Toast.LENGTH_LONG).show();
            }
        });

        direct_home_btn.setOnClickListener(v -> goHome());


        home_btn.setOnClickListener(v -> home());

        if (ruta.equals("verificadorPrecio")) {
            agregar_btn.setVisibility(View.INVISIBLE);
        }

    }

    private void initializedFirebaseService() {
        try {
            if (!isInitialized) {
                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                isInitialized = true;
            } else {
                Log.d("ATENCION-FIREBASE:", "Already Initialized");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void goHome() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
    }

    private void cargarProductosDeMarca(final String marcaIngresada) {


        dbProductosReferencia.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {

                        if (data.child("code").getValue() != null && data.child("nombre").getValue() != null
                                && data.child("marca").getValue() != null && data.child("lista").getValue() != null
                                && data.child("cca").getValue() != null && data.child("p1").getValue() != null
                                && data.child("p2").getValue() != null && data.child("p3").getValue() != null
                                && data.child("p4").getValue() != null) {

                            DecimalFormat df = new DecimalFormat("#.00");

                            String code = Objects.requireNonNull(data.child("code").getValue()).toString();
                            String nombre = Objects.requireNonNull(data.child("nombre").getValue()).toString();
                            String marca = Objects.requireNonNull(data.child("marca").getValue()).toString();

                            double lista = 0.0;
                            double cca = 0.0;
                            double p1 = 0.0;
                            double p2 = 0.0;
                            double p3 = 0.0;
                            double p4 = 0.0;

                            if (marcaIngresada.equals(marca)) {

                                if (data.child("lista").getValue() != null
                                        && data.child("cca").getValue() != null && data.child("p1").getValue() != null
                                        && data.child("p2").getValue() != null && data.child("p3").getValue() != null
                                        && data.child("p4").getValue() != null) {

                                    lista = Double.parseDouble(Objects.requireNonNull(data.child("lista").getValue()).toString());
                                    cca = Double.parseDouble(Objects.requireNonNull(data.child("cca").getValue()).toString());
                                    p1 = Double.parseDouble(Objects.requireNonNull(data.child("p1").getValue()).toString());
                                    p2 = Double.parseDouble(Objects.requireNonNull(data.child("p2").getValue()).toString());
                                    p3 = Double.parseDouble(Objects.requireNonNull(data.child("p3").getValue()).toString());
                                    p4 = Double.parseDouble(Objects.requireNonNull(data.child("p4").getValue()).toString());
                                }

                                Item item = new Item(code, nombre, marca, df.format(lista),
                                        df.format(cca), df.format(p1), df.format(p2),
                                        df.format(p3), df.format(p4), null);

                                nombresProductos.add(item);
                                adapter.notifyDataSetChanged();

                            }
                        }
                    }
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

    private void obtenerProducto() {

        String codigoProductoSeleccionado = ControllerRecyclerViewAdapter.itemSeleccionado.getId();
        Intent i = new Intent(getApplicationContext(), Menu_pedidos.class);
        i.putExtra("seleccionable", true);
        i.putExtra("code", codigoProductoSeleccionado);
        i.putExtra("tipoCliente", "clienteExpress");
        i.putExtra("ruta", ruta);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);

    }

    private void home() {

        switch (ruta) {

            case "verificadorPrecio":

                Intent int1 = new Intent(getApplicationContext(), Marcas.class);
                int1.putExtra("tipoCliente", tipoCliente);
                int1.putExtra("ruta", ruta);
                int1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(int1);

                break;
            case "MenuPedidos":

                Intent int2 = new Intent(getApplicationContext(), Menu_pedidos.class);
                int2.putExtra("tipoCliente", tipoCliente);
                int2.putExtra("ruta", ruta);
                int2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(int2);

                break;

            default:

                break;
        }

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


    @Override
    public void onBackPressed() {
        home();
    }
}
