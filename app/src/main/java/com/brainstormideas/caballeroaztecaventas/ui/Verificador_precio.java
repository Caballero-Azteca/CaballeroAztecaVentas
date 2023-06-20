package com.brainstormideas.caballeroaztecaventas.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brainstormideas.caballeroaztecaventas.R;
import com.brainstormideas.caballeroaztecaventas.data.models.Producto;
import com.brainstormideas.caballeroaztecaventas.entidad.Item;
import com.brainstormideas.caballeroaztecaventas.ui.adapters.ControllerRecyclerViewAdapter;
import com.brainstormideas.caballeroaztecaventas.ui.adapters.RecyclerViewAdapter;
import com.brainstormideas.caballeroaztecaventas.ui.viewmodels.ProductoViewModel;
import com.brainstormideas.caballeroaztecaventas.utils.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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
    List<Item> nombresProductos = new ArrayList<>();

    ProgressDialog progressDialog;
    SessionManager sessionManager;

    private ProductoViewModel productoViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verificador_precio);

        ControllerRecyclerViewAdapter.itemSeleccionado = null;

        dbProductosReferencia = FirebaseDatabase.getInstance().getReference().child("Producto");

        sessionManager = new SessionManager(this);

        recyclerView = findViewById(R.id.lista_productos);
        manager = new LinearLayoutManager(this);
        adapter = new RecyclerViewAdapter(this, nombresProductos);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        progressDialog = new ProgressDialog(this);

        detalles_btn = findViewById(R.id.detalles_btn);
        detalles_btn.setOnClickListener(view -> {

            if (ControllerRecyclerViewAdapter.itemSeleccionado != null) {
                detallesDeProducto();

            } else {
                Toast.makeText(getApplicationContext(), "Seleccione un artículo primero.", Toast.LENGTH_LONG).show();
            }
        });
        buscar_button = findViewById(R.id.buscar_btn);
        home_button = findViewById(R.id.home_button);
        home_button.setOnClickListener(view -> home());
        buscar_button.setOnClickListener(view -> irBuscar());

        agregar_producto_btn = findViewById(R.id.agregar_producto_btn);
        agregar_producto_btn.setOnClickListener(v -> irAgregarProductor());

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (!sessionManager.getUsuario().equals("admin")) {
            agregar_producto_btn.setEnabled(false);
        }

        productoViewModel = new ProductoViewModel(this);
        productoViewModel.getProductos().observe(this, productos -> {
            for (Producto producto : productos) {

                double lista;
                double cca;
                double p1;
                double p2;
                double p3;
                double p4;

                DecimalFormat df = new DecimalFormat("#.00");

                lista = producto.getLista();
                cca = producto.getCca();
                p1 = producto.getP1();
                p2 = producto.getP2();
                p3 = producto.getP3();
                p4 = producto.getP4();

                Item item = new Item(producto.getCode(), producto.getNombre(),
                        producto.getMarca(), df.format(lista),
                        df.format(cca), df.format(p1), df.format(p2),
                        df.format(p3), df.format(p4), null);
                nombresProductos.add(item);
                adapter.notifyDataSetChanged();
            }
        });
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

        ArrayList<String> precios = new ArrayList<>();
        precios.add("P3: " + ControllerRecyclerViewAdapter.itemSeleccionado.getDato6());
        precios.add("LISTA: " + ControllerRecyclerViewAdapter.itemSeleccionado.getDato2());
        precios.add("P1: " + ControllerRecyclerViewAdapter.itemSeleccionado.getDato4());
        precios.add("P2: " + ControllerRecyclerViewAdapter.itemSeleccionado.getDato5());
        precios.add("P4: " + ControllerRecyclerViewAdapter.itemSeleccionado.getDato7());
        precios.add("CCA: " + ControllerRecyclerViewAdapter.itemSeleccionado.getDato3());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, precios);
        preciosLv.setAdapter(adapter);

        builder.setPositiveButton("Atras", (dialog, which) -> {});
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    public void onBackPressed() {
        home();
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
            ArrayList<Item> listaFiltrada = filter((ArrayList<Item>) nombresProductos, newText);
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
