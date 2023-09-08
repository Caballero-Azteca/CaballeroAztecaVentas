package com.brainstormideas.caballeroaztecaventas.ui;

import static com.brainstormideas.caballeroaztecaventas.ui.MainActivity.isInitialized;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brainstormideas.caballeroaztecaventas.R;
import com.brainstormideas.caballeroaztecaventas.data.models.Cliente;
import com.brainstormideas.caballeroaztecaventas.data.models.Pedido;
import com.brainstormideas.caballeroaztecaventas.entidad.Item;
import com.brainstormideas.caballeroaztecaventas.ui.adapters.ControllerRecyclerViewAdapter;
import com.brainstormideas.caballeroaztecaventas.ui.adapters.RecyclerViewAdapter;
import com.brainstormideas.caballeroaztecaventas.ui.viewmodels.ClienteViewModel;
import com.brainstormideas.caballeroaztecaventas.utils.SessionManager;
import com.brainstormideas.caballeroaztecaventas.utils.Tools;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Lista_clientes extends AppCompatActivity implements SearchView.OnQueryTextListener {

    ImageButton home_button;
    Button abrir_pedido;
    Button btn_eliminar;
    Button btn_editar;
    TextView cliente_view;

    RecyclerView recyclerView;
    RecyclerViewAdapter adapter;
    RecyclerView.LayoutManager manager;
    DatabaseReference dbClientesReferencia;

    ArrayList<Item> nombresClientes = new ArrayList<>();

    private ProgressDialog progressDialog;
    SessionManager sessionManager;

    ClienteViewModel clienteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_clientes);

        ControllerRecyclerViewAdapter.itemSeleccionado = null;
        initializedFirebaseService();

        clienteViewModel = new ClienteViewModel(this);

        dbClientesReferencia = FirebaseDatabase.getInstance().getReference().child("Cliente");
        sessionManager = new SessionManager(this);
        progressDialog = new ProgressDialog(this);

        cargarClientes();
        clear();

        recyclerView = findViewById(R.id.lista_clientes);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter = new RecyclerViewAdapter(this, nombresClientes);
        recyclerView.setAdapter(adapter);

        cliente_view = findViewById(R.id.cliente_view);

        home_button = findViewById(R.id.home_button);
        home_button.setOnClickListener(view -> home());
        abrir_pedido = findViewById(R.id.abrir);
        btn_editar = findViewById(R.id.btn_editar);
        btn_editar.setOnClickListener(v -> {
            if (ControllerRecyclerViewAdapter.itemSeleccionado != null) {
                //editarCliente();
            } else {
                Toast.makeText(getApplicationContext(), "Debe seleccionar un cliente", Toast.LENGTH_LONG).show();
            }
        });

        btn_eliminar = findViewById(R.id.btn_eliminar);
        btn_eliminar.setOnClickListener(v -> eliminarCliente());
        abrir_pedido.setOnClickListener(view -> {
            if (ControllerRecyclerViewAdapter.itemSeleccionado != null) {
                abrirPedido();
            } else {
                Toast.makeText(getApplicationContext(), "Debe seleccionar un cliente", Toast.LENGTH_LONG).show();
            }

        });

        if (!sessionManager.getUsuario().equals("admin")) {
            btn_editar.setEnabled(false);
            btn_eliminar.setEnabled(false);
        }
    }

    private void initializedFirebaseService() {
        try {
            if (!isInitialized) {
                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                FirebaseDatabase.getInstance().getReference("Cliente")
                        .keepSynced(true);
                isInitialized = true;
            } else {
                Log.d("ATENCION-FIREBASE:", "Already Initialized");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void home() {
        ControllerRecyclerViewAdapter.itemSeleccionado = null;
        ControllerRecyclerViewAdapter.posicion = -1;
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
    }

    private void abrirPedido() {

        String codigoCliente = ControllerRecyclerViewAdapter.itemSeleccionado.getId();

        progressDialog.setMessage("Cargando informacion del cliente...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        clienteViewModel.getCliente(codigoCliente).observe(this, new Observer<Cliente>() {
            @Override
            public void onChanged(Cliente cliente) {

                Pedido.setCliente(cliente);
                Intent i = new Intent(getApplicationContext(), Menu_pedidos.class);
                i.putExtra("seleccionable", false);
                i.putExtra("tipoCliente", "clienteRegistrado");
                startActivity(i);

                progressDialog.dismiss();
            }
        });
    }

    private void editarCliente() {


        String codigoCliente = ControllerRecyclerViewAdapter.itemSeleccionado.getId();

        progressDialog.setMessage("Cargando informacion del cliente...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (Tools.isNumeric(codigoCliente)) {

            int codigoNumerico = Integer.parseInt(codigoCliente);
            Query query = dbClientesReferencia.orderByChild("id").equalTo(codigoNumerico);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot data : snapshot.getChildren()) {


                        if (data.child("id").getValue() != null && data.child("razon").getValue() != null && data.child("rfc").getValue() != null &&
                                data.child("municipio").getValue() != null && data.child("estado").getValue() != null && data.child("calle").getValue() != null
                                && data.child("colonia").getValue() != null && data.child("numeroExterior").getValue() != null &&
                                data.child("cp").getValue() != null && data.child("email").getValue() != null && data.child("agenteVenta").getValue() != null
                                && data.child("agenteCobro").getValue() != null && data.child("ruta").getValue() != null) {


                            String numeroInterior = "";
                            String telefono = "";

                            String id = Objects.requireNonNull(data.child("id").getValue()).toString();
                            String code = Objects.requireNonNull(data.child("code").getValue()).toString();
                            String razon = Objects.requireNonNull(data.child("razon").getValue()).toString();
                            String rfc = Objects.requireNonNull(data.child("rfc").getValue()).toString();
                            String municipio = Objects.requireNonNull(data.child("municipio").getValue()).toString();
                            String estado = Objects.requireNonNull(data.child("estado").getValue()).toString();
                            String calle = Objects.requireNonNull(data.child("calle").getValue()).toString();
                            String colonia = Objects.requireNonNull(data.child("colonia").getValue()).toString();
                            String numeroExterior = Objects.requireNonNull(data.child("numeroExterior").getValue()).toString();
                            if (data.child("numeroInterior").getValue() != null) {
                                numeroInterior = Objects.requireNonNull(data.child("numeroInterior").getValue()).toString();
                            }
                            String cp = Objects.requireNonNull(data.child("cp").getValue()).toString();
                            if (data.child("telefono").getValue() != null) {
                                telefono = Objects.requireNonNull(data.child("telefono").getValue()).toString();
                            }
                            String email = Objects.requireNonNull(data.child("email").getValue()).toString();
                            String agenteVenta = Objects.requireNonNull(data.child("agenteVenta").getValue()).toString();
                            String agenteCobro = Objects.requireNonNull(data.child("agenteCobro").getValue()).toString();
                            String ruta = Objects.requireNonNull(data.child("ruta").getValue()).toString();

                            Cliente cliente = new Cliente(Long.getLong(id), code, razon, rfc, municipio, estado, calle, colonia, numeroExterior,
                                    numeroInterior, cp, telefono, email, ruta, agenteVenta, agenteCobro);

                            Pedido.setCliente(cliente);
                            Intent i = new Intent(getApplicationContext(), EditarCliente.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            i.putExtra("seleccionable", false);
                            i.putExtra("tipoCliente", "clienteRegistrado");
                            startActivity(i);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else {

            Query query = dbClientesReferencia.orderByChild("id").equalTo(codigoCliente);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot data : snapshot.getChildren()) {


                        if (data.child("id").getValue() != null && data.child("razon").getValue() != null && data.child("rfc").getValue() != null &&
                                data.child("municipio").getValue() != null && data.child("estado").getValue() != null && data.child("calle").getValue() != null
                                && data.child("colonia").getValue() != null && data.child("numeroExterior").getValue() != null &&
                                data.child("cp").getValue() != null && data.child("email").getValue() != null && data.child("agenteVenta").getValue() != null
                                && data.child("agenteCobro").getValue() != null && data.child("ruta").getValue() != null) {

                            String numeroInterior = "";
                            String telefono = "";

                            String id = Objects.requireNonNull(data.child("id").getValue()).toString();
                            String code = Objects.requireNonNull(data.child("code").getValue()).toString();
                            String razon = Objects.requireNonNull(data.child("razon").getValue()).toString();
                            String rfc = Objects.requireNonNull(data.child("rfc").getValue()).toString();
                            String municipio = Objects.requireNonNull(data.child("municipio").getValue()).toString();
                            String estado = Objects.requireNonNull(data.child("estado").getValue()).toString();
                            String calle = Objects.requireNonNull(data.child("calle").getValue()).toString();
                            String colonia = Objects.requireNonNull(data.child("colonia").getValue()).toString();
                            String numeroExterior = Objects.requireNonNull(data.child("numeroExterior").getValue()).toString();
                            if (data.child("numeroInterior").getValue() != null) {
                                numeroInterior = Objects.requireNonNull(data.child("numeroInterior").getValue()).toString();
                            }
                            String cp = Objects.requireNonNull(data.child("cp").getValue()).toString();
                            if (data.child("telefono").getValue() != null) {
                                telefono = Objects.requireNonNull(data.child("telefono").getValue()).toString();
                            }
                            String email = Objects.requireNonNull(data.child("email").getValue()).toString();
                            String agenteVenta = Objects.requireNonNull(data.child("agenteVenta").getValue()).toString();
                            String agenteCobro = Objects.requireNonNull(data.child("agenteCobro").getValue()).toString();
                            String ruta = Objects.requireNonNull(data.child("ruta").getValue()).toString();

                            Cliente cliente = new Cliente(Long.getLong(id), code, razon, rfc, municipio, estado, calle, colonia, numeroExterior,
                                    numeroInterior, cp, telefono, email, ruta, agenteVenta, agenteCobro);

                            Pedido.setCliente(cliente);
                            Intent i = new Intent(getApplicationContext(), EditarCliente.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            i.putExtra("seleccionable", false);
                            i.putExtra("tipoCliente", "clienteRegistrado");
                            startActivity(i);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }

        progressDialog.dismiss();

    }

    private void cargarClientes() {

        progressDialog.setMessage("Cargando clientes...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        clienteViewModel.getClientes().observe(this, new Observer<List<Cliente>>() {
            @Override
            public void onChanged(List<Cliente> clientes) {

                for (Cliente cliente : clientes) {

                    String id = cliente.getCode();
                    String nombre = cliente.getRazon();
                    String rfc = null;

                    if (cliente.getRfc() != null) {
                        rfc = cliente.getRfc();
                    } else {
                        rfc = "N/A";
                    }
                    Item item = new Item();
                    item.setId(id);
                    item.setTitulo(nombre);
                    item.setDato1(rfc);

                    nombresClientes.add(item);
                    adapter.notifyDataSetChanged();
                }
                progressDialog.dismiss();
            }
        });

    }

    private void eliminarCliente() {

        if (ControllerRecyclerViewAdapter.itemSeleccionado != null) {

            String nombreClienteSeleccionado = ControllerRecyclerViewAdapter.itemSeleccionado.getTitulo();
            final String idClienteSeleccionado = ControllerRecyclerViewAdapter.itemSeleccionado.getId();
            final int posicionGlobal = ControllerRecyclerViewAdapter.posicion;

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Alerta de seguridad.");
            builder.setMessage("¿Seguro que desea eliminar al cliente " + nombreClienteSeleccionado + " ?");
            builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (Tools.isNumeric(idClienteSeleccionado)) {

                        int codigoNumerico = Integer.parseInt(idClienteSeleccionado);
                        Query query = dbClientesReferencia.orderByChild("id").equalTo(codigoNumerico);
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    ds.getRef().removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        nombresClientes.remove(posicionGlobal);
                        adapter.notifyDataSetChanged();

                    } else {

                        Query query = dbClientesReferencia.orderByChild("id").equalTo(idClienteSeleccionado);
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    ds.getRef().removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        nombresClientes.remove(posicionGlobal);
                        adapter.notifyDataSetChanged();

                    }

                }

            });
            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            Toast.makeText(getApplicationContext(), "Debe seleccionar un item.", Toast.LENGTH_LONG).show();
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
                adapter.setFilter(nombresClientes);
                adapter.notifyDataSetChanged();
                return true;
            }
        });
        return true;
    }

    public void clear() {

        int size = Pedido.getListaDeProductos().size();
        if (size > 0) {
            Pedido.getListaDeProductos().subList(0, size).clear();
            synchronized (Pedido.getListaDeProductos()) {
                Pedido.getListaDeProductos().notify();
            }

        }
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
            ArrayList<Item> listaFiltrada = filter(nombresClientes, newText);
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