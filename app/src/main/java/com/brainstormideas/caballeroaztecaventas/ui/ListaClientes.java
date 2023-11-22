package com.brainstormideas.caballeroaztecaventas.ui;

import static com.brainstormideas.caballeroaztecaventas.ui.MainActivity.isInitialized;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brainstormideas.caballeroaztecaventas.R;
import com.brainstormideas.caballeroaztecaventas.data.callbacks.ClientesDiffCallback;
import com.brainstormideas.caballeroaztecaventas.data.models.Cliente;
import com.brainstormideas.caballeroaztecaventas.data.models.Pedido;
import com.brainstormideas.caballeroaztecaventas.ui.adapters.ControllerRecyclerViewAdapter;
import com.brainstormideas.caballeroaztecaventas.ui.adapters.ClientesAdapter;
import com.brainstormideas.caballeroaztecaventas.ui.viewmodels.ClienteViewModel;
import com.brainstormideas.caballeroaztecaventas.utils.SessionManager;
import com.brainstormideas.caballeroaztecaventas.utils.Tools;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListaClientes extends AppCompatActivity implements SearchView.OnQueryTextListener {

    ImageButton home_button;
    Button abrir_pedido;
    Button btn_eliminar;
    Button btn_editar;
    TextView cliente_view;

    RecyclerView recyclerView;
    ClientesAdapter adapter;
    RecyclerView.LayoutManager manager;
    DatabaseReference dbClientesReferencia;

    List<Cliente> listaClientes = new ArrayList<>();

    private ProgressDialog progressDialog;
    SessionManager sessionManager;

    ClienteViewModel clienteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_clientes);

        ControllerRecyclerViewAdapter.clienteSeleccionado = null;
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
        adapter = new ClientesAdapter(this, listaClientes);
        recyclerView.setAdapter(adapter);

        cliente_view = findViewById(R.id.cliente_view);

        home_button = findViewById(R.id.home_button);
        home_button.setOnClickListener(view -> home());
        abrir_pedido = findViewById(R.id.abrir);
        btn_editar = findViewById(R.id.btn_editar);
        btn_editar.setOnClickListener(v -> {
            if (ControllerRecyclerViewAdapter.clienteSeleccionado != null) {
                //editarCliente();
            } else {
                Toast.makeText(getApplicationContext(), "Debe seleccionar un cliente", Toast.LENGTH_LONG).show();
            }
        });

        btn_eliminar = findViewById(R.id.btn_eliminar);
        btn_eliminar.setOnClickListener(v -> eliminarCliente());
        abrir_pedido.setOnClickListener(view -> {
            if (ControllerRecyclerViewAdapter.clienteSeleccionado != null) {
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

    private static void initializedFirebaseService() {
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

    private void abrirPedido() {

        String codigoCliente = ControllerRecyclerViewAdapter.clienteSeleccionado.getCode();

        progressDialog.setMessage("Cargando informacion del cliente...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        clienteViewModel.getCliente(codigoCliente).observe(this, cliente -> {

            Pedido.setCliente(cliente);
            Intent i = new Intent(getApplicationContext(), MenuPedidos.class);
            i.putExtra("seleccionable", false);
            i.putExtra("tipoCliente", "clienteRegistrado");
            startActivity(i);

            progressDialog.dismiss();
        });
    }

    private void editarCliente() {


        String codigoCliente = ControllerRecyclerViewAdapter.clienteSeleccionado.getCode();

        progressDialog.setMessage("Cargando informacion del cliente...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        progressDialog.dismiss();

    }

    private void cargarClientes() {
        progressDialog.setMessage("Cargando clientes...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        clienteViewModel.getClientes().observe(this, nuevosClientes -> {
            if (nuevosClientes != null) {
                if (listaClientes.isEmpty()) {
                    listaClientes.addAll(nuevosClientes);
                    adapter.notifyDataSetChanged();
                } else {
                    DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ClientesDiffCallback(listaClientes, nuevosClientes));

                    listaClientes.clear();
                    listaClientes.addAll(nuevosClientes);

                    diffResult.dispatchUpdatesTo(adapter);
                }
            }
            progressDialog.dismiss();
        });
    }



    private void eliminarCliente() {

        if (ControllerRecyclerViewAdapter.clienteSeleccionado != null) {

            String nombreClienteSeleccionado = ControllerRecyclerViewAdapter.clienteSeleccionado.getRazon();
            final String idClienteSeleccionado = ControllerRecyclerViewAdapter.clienteSeleccionado.getCode();
            final int posicionGlobal = ControllerRecyclerViewAdapter.posicionCliente;

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Alerta de seguridad.");
            builder.setMessage("¿Seguro que desea eliminar al cliente " + nombreClienteSeleccionado + " ?");
            builder.setPositiveButton("Eliminar", (dialog, which) -> {

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
                    listaClientes.remove(posicionGlobal);
                    cargarClientes();
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
                    listaClientes.remove(posicionGlobal);
                    cargarClientes();
                }
            });
            builder.setNegativeButton("Cancelar", (dialog, which) -> {
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
                adapter.setClientes(listaClientes);
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
            ArrayList<Cliente> listaFiltrada = filter((ArrayList<Cliente>) listaClientes, newText);
            adapter.setClientes(listaFiltrada);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private ArrayList<Cliente> filter(ArrayList<Cliente> clientes, String texto) {
        ArrayList<Cliente> listaFiltrada = new ArrayList<>();
        try {
            String textoMiniscula = texto.toLowerCase();
            for (Cliente cliente : clientes) {
                assert cliente.getRazon() != null;
                String itemFilter = cliente.getRazon().toLowerCase();
                if (itemFilter.contains(textoMiniscula)) {
                    listaFiltrada.add(cliente);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaFiltrada;
    }

    private void home() {
        ControllerRecyclerViewAdapter.clienteSeleccionado = null;
        ControllerRecyclerViewAdapter.posicionCliente = -1;
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
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
}