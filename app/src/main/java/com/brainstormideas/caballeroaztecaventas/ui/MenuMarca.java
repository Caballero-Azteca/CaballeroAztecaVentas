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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brainstormideas.caballeroaztecaventas.R;
import com.brainstormideas.caballeroaztecaventas.data.models.Producto;
import com.brainstormideas.caballeroaztecaventas.ui.adapters.ControllerRecyclerViewAdapter;
import com.brainstormideas.caballeroaztecaventas.ui.adapters.ProductosAdapter;
import com.brainstormideas.caballeroaztecaventas.ui.viewmodels.ProductoViewModel;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

public class MenuMarca extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private Button detalles_btn;
    private ImageButton home_btn;
    private Button agregar_btn;
    private ImageButton direct_home_btn;

    private ProductoViewModel productoViewModel;
    
    RecyclerView recyclerView;
    ProductosAdapter adapter;
    RecyclerView.LayoutManager manager;

    DatabaseReference dbProductosReferencia;

    ArrayList<Producto> productos = new ArrayList<>();

    ProgressDialog progressDialog;

    String marcaSeleccionada;
    String ruta;
    String tipoCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_marca);

        ControllerRecyclerViewAdapter.productoSeleccionado = null;
        ControllerRecyclerViewAdapter.posicionProducto = 0;

        progressDialog = new ProgressDialog(this);

        marcaSeleccionada = getIntent().getExtras().get("marca").toString();
        ruta = getIntent().getExtras().get("ruta").toString();
        if (!ruta.equals("verificadorPrecio")) {
            tipoCliente = getIntent().getExtras().get("tipoCliente").toString();
        } else {
            tipoCliente = "consulta";
        }

        Objects.requireNonNull(getSupportActionBar()).setTitle(marcaSeleccionada);

        FirebaseApp.initializeApp(this);
        productoViewModel = new ProductoViewModel(getApplicationContext());
        cargarProductosDeMarca(marcaSeleccionada);

        detalles_btn = findViewById(R.id.detalles_btn);
        home_btn = findViewById(R.id.home_button);
        agregar_btn = findViewById(R.id.agregar_btn);
        direct_home_btn = findViewById(R.id.direct_home_btn);

        recyclerView = findViewById(R.id.lista_productos_marca_tv);
        manager = new LinearLayoutManager(this);
        adapter = new ProductosAdapter(this, productos);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        detalles_btn.setOnClickListener(v -> {
            if (ControllerRecyclerViewAdapter.productoSeleccionado != null) {
                detallesDeProducto();
            } else {
                Toast.makeText(getApplicationContext(), "Seleccione un articulo primero", Toast.LENGTH_LONG).show();
            }
        });

        agregar_btn.setOnClickListener(v -> {
            if (ControllerRecyclerViewAdapter.productoSeleccionado != null) {
                obtenerProducto();
                Toast.makeText(getApplicationContext(), "Articulo seleccionado: " + ControllerRecyclerViewAdapter.productoSeleccionado.getNombre(), Toast.LENGTH_LONG).show();
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

    private void goHome() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
    }

    private void cargarProductosDeMarca(String marcaEspecifica) {
        
        progressDialog.setMessage("Cargando productos...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        productos.clear();
        productoViewModel.getProductos().observe(this, productosNuevos -> {

            ArrayList<Producto> productosMarcaEspecifica = new ArrayList<>();

            for (Producto producto : productosNuevos) {
                if (producto.getMarca().equals(marcaEspecifica)) {
                    productosMarcaEspecifica.add(producto);
                }
            }

            productos.addAll(productosMarcaEspecifica);
            adapter.setProductos(productos);
            adapter.notifyDataSetChanged();
            progressDialog.dismiss();
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

        productoNameTv.setText("PRODUCTO: " + ControllerRecyclerViewAdapter.productoSeleccionado.getNombre());
        cotigoTv.setText("CODIGO: " + ControllerRecyclerViewAdapter.productoSeleccionado.getCode());
        marcaTv.setText("MARCA: " + ControllerRecyclerViewAdapter.productoSeleccionado.getMarca());

        ArrayList<String> precios = new ArrayList<String>();
        precios.add("P3: " + ControllerRecyclerViewAdapter.productoSeleccionado.getP3());
        precios.add("LISTA: " + ControllerRecyclerViewAdapter.productoSeleccionado.getLista());
        precios.add("P1: " + ControllerRecyclerViewAdapter.productoSeleccionado.getP1());
        precios.add("P2: " + ControllerRecyclerViewAdapter.productoSeleccionado.getP2());
        precios.add("P4: " + ControllerRecyclerViewAdapter.productoSeleccionado.getP4());
        precios.add("CCA: " + ControllerRecyclerViewAdapter.productoSeleccionado.getCca());

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

        String codigoProductoSeleccionado = ControllerRecyclerViewAdapter.productoSeleccionado.getCode();
        Intent i = new Intent(getApplicationContext(), MenuPedidos.class);
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

                Intent int2 = new Intent(getApplicationContext(), MenuPedidos.class);
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
                adapter.setProductos(productos);
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
            ArrayList<Producto> listaFiltrada = filter(productos, newText);
            adapter.setProductos(listaFiltrada);
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private ArrayList<Producto> filter(ArrayList<Producto> productos, String texto) {
        ArrayList<Producto> listaFiltrada = new ArrayList<>();
        try {
            String textoMiniscula = texto.toLowerCase();
            for (Producto producto : productos) {
                String itemFilter = producto.getNombre().toLowerCase();
                if (itemFilter.contains(textoMiniscula)) {
                    listaFiltrada.add(producto);
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
