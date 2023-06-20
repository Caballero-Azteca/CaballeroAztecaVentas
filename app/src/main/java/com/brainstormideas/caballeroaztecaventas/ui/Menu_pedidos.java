package com.brainstormideas.caballeroaztecaventas.ui;

import static com.brainstormideas.caballeroaztecaventas.ui.MainActivity.isInitialized;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
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
import com.brainstormideas.caballeroaztecaventas.data.models.Producto;
import com.brainstormideas.caballeroaztecaventas.data.models.Vendedor;
import com.brainstormideas.caballeroaztecaventas.entidad.Item;
import com.brainstormideas.caballeroaztecaventas.entidad.ItemProductoPedido;
import com.brainstormideas.caballeroaztecaventas.managers.PedidoManager;
import com.brainstormideas.caballeroaztecaventas.ui.adapters.ControllerRecyclerViewAdapter;
import com.brainstormideas.caballeroaztecaventas.ui.adapters.RecyclerViewAdapter;
import com.brainstormideas.caballeroaztecaventas.ui.adapters.RecyclerViewProductosPedidoAdapter;
import com.brainstormideas.caballeroaztecaventas.ui.viewmodels.ProductoViewModel;
import com.brainstormideas.caballeroaztecaventas.ui.viewmodels.VendedorViewModel;
import com.brainstormideas.caballeroaztecaventas.utils.InternetManager;
import com.brainstormideas.caballeroaztecaventas.utils.SessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Menu_pedidos extends AppCompatActivity implements SearchView.OnQueryTextListener {

    ImageButton home_button;
    TextView cliente_view;

    EditText cantidad_tv;
    EditText codigo_tv;
    Spinner tipoSelector;

    Button agregar_btn;
    Button marcas_btn;
    Button limpiar_btn;
    Button finalizar_pedido;

    RecyclerViewProductosPedidoAdapter adapter;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager manager;

    DatabaseReference dbProductosReferencia;
    DatabaseReference dbVendedoresReferencia;
    DatabaseReference dbFoliosReferencia;
    DatabaseReference dbCotizacionesReferencia;

    ArrayAdapter<CharSequence> spinnerAdapter;

    String productoSeleccionado;
    Boolean seleccionable;
    String tipoCliente;

    int pedido;
    String tipoSeleccionado;

    SessionManager sessionManager;
    Vendedor vendedor;

    double precioManual = 0.0;

    String vendedorActual;

    ProgressDialog progressDialog;

    private ProductoViewModel productoViewModel;

    private RecyclerView recyclerViewBusqueda;
    private RecyclerViewAdapter adapterBusqueda;
    private RecyclerView.LayoutManager managerBusqueda;
    private LinearLayout container;

    private ProgressBar progressBar;

    List<Item> listaProductosBusqueda = new ArrayList<>();

    private InternetManager internetManager;

    private VendedorViewModel vendedorViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_pedidos);

        vendedor = null;
        pedido = 0;

        sessionManager = new SessionManager(this);
        internetManager = new InternetManager(this);

        this.setTitle("Buscar producto");

        initializedFirebaseService();

        LayoutInflater inflater = getLayoutInflater();
        View inflatedView = inflater.inflate(R.layout.products_result, null);

        container = findViewById(R.id.lista_busqueda_productos);
        container.addView(inflatedView);

        // Recyclerview de busqueda de productos
        managerBusqueda = new LinearLayoutManager(container.getContext());

        recyclerViewBusqueda = container.findViewById(R.id.productos_busqueda);
        recyclerViewBusqueda.setLayoutManager(managerBusqueda);
        adapterBusqueda = new RecyclerViewAdapter(this, listaProductosBusqueda);
        recyclerViewBusqueda.setAdapter(adapterBusqueda);
        container.setVisibility(View.GONE);

        progressBar = container.findViewById(R.id.progressBar);

        // Load Products with ViewModel
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
                listaProductosBusqueda.add(item);
            }
            progressBar.setVisibility(View.GONE);
            adapterBusqueda.notifyDataSetChanged();
        });

        // Recyclerview de productos agregados

        manager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.productos_listados);
        recyclerView.setLayoutManager(manager);
        adapter = new RecyclerViewProductosPedidoAdapter(this, Pedido.getListaDeProductos());
        recyclerView.setAdapter(adapter);

        tipoCliente = getIntent().getExtras().get("tipoCliente").toString();
        seleccionable = getIntent().getBooleanExtra("seleccionable", false);
        progressDialog = new ProgressDialog(this);

        dbProductosReferencia = FirebaseDatabase.getInstance().getReference().child("Producto");
        dbVendedoresReferencia = FirebaseDatabase.getInstance().getReference().child("Usuario");
        dbFoliosReferencia = FirebaseDatabase.getInstance().getReference().child("Folio");
        dbCotizacionesReferencia = FirebaseDatabase.getInstance().getReference().child("Cotizacion");

        vendedorActual = sessionManager.getUsuario();
        vendedorViewModel = new VendedorViewModel(this);
        vendedorViewModel.getVendedor(sessionManager.getEmail()).observe(this, vendedorObtained -> {
            vendedor = vendedorObtained;
            System.out.println("USUARIO ACTUAL:       " + vendedor.getUsuario());
            if (vendedor != null) {
                obtenerFolio();
            }
        });

        cliente_view = findViewById(R.id.cliente_view);
        if (obtenerCliente() != null) {
            cliente_view.setText(obtenerCliente().getRazon());
        }

        tipoSelector = findViewById(R.id.tipoSelector);
        spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.tipos, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipoSelector.setAdapter(spinnerAdapter);

        tipoSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tipoSeleccionado = tipoSelector.getSelectedItem().toString();
                if (tipoSeleccionado.equals("OTRO")) {
                    precioManual();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        home_button = findViewById(R.id.home_button);
        home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });

        codigo_tv = findViewById(R.id.codigo_tv);

        cantidad_tv = findViewById(R.id.cantidad_tv);
        agregar_btn = findViewById(R.id.agregar_btn);
        marcas_btn = findViewById(R.id.marcas_btn);
        marcas_btn.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), Marcas.class);
            i.putExtra("ruta", "MenuPedidos");
            i.putExtra("tipoCliente", tipoCliente);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });

        if (seleccionable) {
            productoSeleccionado = Objects.requireNonNull(getIntent().getExtras().get("codigo")).toString();
            codigo_tv.setText(productoSeleccionado);
        }

        limpiar_btn = findViewById(R.id.limpiar_btn);

        agregar_btn.setOnClickListener(v -> {
            tipoSeleccionado = tipoSelector.getSelectedItem().toString();
            obtenerProducto(codigo_tv.getText().toString());
        });
        limpiar_btn.setOnClickListener(v -> limpiarPedido());

        finalizar_pedido = findViewById(R.id.finalizar_pedido);
        finalizar_pedido.setOnClickListener(v -> finalizarPedido());

        codigo_tv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (!text.equals(text.toUpperCase())) {
                    text = text.toUpperCase();
                    codigo_tv.setText(text);
                    codigo_tv.setSelection(text.length());
                }
            }
        });

        codigo_tv.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (actionId == EditorInfo.IME_ACTION_DONE
                    || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                    || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                if (!cantidad_tv.getText().equals("")) {
                    obtenerProducto(codigo_tv.getText().toString());
                    inputMethodManager.hideSoftInputFromWindow(codigo_tv.getWindowToken(), 0);
                } else {
                    Toast.makeText(getApplicationContext(), "Ingresar código.", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        });

        cantidad_tv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (!text.equals(text.toUpperCase())) {
                    text = text.toUpperCase();
                    cantidad_tv.setText(text);
                    cantidad_tv.setSelection(text.length());
                }
            }
        });

        cantidad_tv.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (actionId == EditorInfo.IME_ACTION_DONE
                    || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                    || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {

                if (!cantidad_tv.getText().equals("")) {
                    obtenerProducto(codigo_tv.getText().toString());
                    inputMethodManager.hideSoftInputFromWindow(cantidad_tv.getWindowToken(), 0);
                } else {
                    Toast.makeText(getApplicationContext(), "Ingresar cantidad.", Toast.LENGTH_SHORT).show();
                }

                return true;

            }
            return false;
        });

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

    private Cliente obtenerCliente() {
        return Pedido.getCliente();
    }

    private void obtenerProducto(String code) {

        String cantidad = cantidad_tv.getText().toString().trim();

        if (!cantidad.equals("") && (code != null && !code.equals(""))) {

            progressDialog.setMessage("Buscando producto...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            productoViewModel.getProducto(code).observe(this, producto -> {
                if (producto != null) {

                    DecimalFormat df = new DecimalFormat("#.00");

                    ItemProductoPedido itemProductoPedidoproducto = new ItemProductoPedido();
                    itemProductoPedidoproducto.setId(producto.getCode());
                    itemProductoPedidoproducto.setNombre(producto.getNombre());
                    itemProductoPedidoproducto.setMarca(producto.getMarca());
                    itemProductoPedidoproducto.setCantidad(cantidad);
                    itemProductoPedidoproducto.setTipo(tipoSeleccionado);

                    switch (tipoSeleccionado) {
                        case "CCA":
                            itemProductoPedidoproducto.setPrecio(df.format(producto.getCca()));
                            break;
                        case "P4":
                            itemProductoPedidoproducto.setPrecio(df.format(producto.getP4()));
                            break;
                        case "P3":
                            itemProductoPedidoproducto.setPrecio(df.format(producto.getP3()));
                            break;
                        case "P2":
                            itemProductoPedidoproducto.setPrecio(df.format(producto.getP2()));
                            break;
                        case "P1":
                            itemProductoPedidoproducto.setPrecio(df.format(producto.getP1()));
                            break;
                        case "OTRO":
                            itemProductoPedidoproducto.setPrecio(df.format(precioManual));
                            break;
                        default:
                            itemProductoPedidoproducto.setPrecio(df.format(producto.getLista()));
                    }

                    try {
                        Pedido.getListaDeProductos().add(itemProductoPedidoproducto);
                        adapter.notifyDataSetChanged();
                        clearProductoInfo();
                        Toast.makeText(getApplicationContext(), "Producto Agregado.", Toast.LENGTH_SHORT).show();
                        Log.e("PRODUCCCCCCCCCCCCCTOOOOOOOOOOOOOOOOOOOOOOOOO", String.valueOf(producto.getNombre()));

                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "No se pudo agregar el producto.", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "No se pudo agregar el producto.", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
                progressDialog.dismiss();
            });

        } else {
            Toast.makeText(getApplicationContext(), "Debe ingresar un código y una cantidad de productos.", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }

    }

    private void finalizarPedido() {

        if (Pedido.getListaDeProductos() != null && Pedido.getListaDeProductos().size() != 0) {
            Pedido.setVendedor(vendedor);
            Pedido.setFolio(generarFolio());
            Pedido.setTotal(calcularTotal());
            Intent i = new Intent(getApplicationContext(), Menu_final.class);
            i.putExtra("tipoCliente", tipoCliente);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("candadoModificar", true);
            startActivity(i);
        } else {
            Toast.makeText(getApplicationContext(), "Debe ingresar almenos un producto a la lista", Toast.LENGTH_LONG).show();
        }


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

    public void limpiarPedido() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Limpiar pedido");
        builder.setMessage("Se eliminaran todos los productos agregados a la lista.");
        builder.setPositiveButton("Aceptar", (dialog, which) -> clear());
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        builder.show();

        adapter.notifyDataSetChanged();
    }

    public void clearProductoInfo() {
        codigo_tv.setText("");
        cantidad_tv.setText("");
        tipoSelector.setSelection(0);
    }

    private String generarFolio() {

        String tipo = "";

        if (Pedido.getTipo().equals("pedido")) {
            tipo = "P";
        } else if (Pedido.getTipo().equals("cotizacion")) {
            tipo = "C";
        }

        String folioGenerado = Pedido.getVendedor().getUsuario();
        String folioPedido = String.format(Locale.getDefault(), "%04d", pedido);

        return tipo + folioGenerado + "-" + folioPedido;
    }

    private void obtenerFolio() {

        String tipoPedido = Pedido.getTipo();
        int ultimoPedido = PedidoManager.getInstance(getApplicationContext()).getUltimoFolioPedidos();
        int ultimoCotizacion = PedidoManager.getInstance(getApplicationContext()).getUltimoFolioCotizaciones();


        System.out.println("ULTIMO FOLIO: " + PedidoManager.getInstance(getApplicationContext()).getUltimoFolioPedidos());
        System.out.println("ENTRANDO A OBTENER FOLIOS....");

        if (tipoPedido.equals("pedido")) {
            if (isInternetAvailable()) {
                DatabaseReference referencia = dbFoliosReferencia;
                obtenerMayorFolio(referencia, "P");
            } else {
                pedido = ultimoPedido + 1;
                PedidoManager.getInstance(getApplicationContext()).guardarUltimoFolioPedidos(pedido);

            }
        } else if (tipoPedido.equals("cotizacion")) {
            if (isInternetAvailable()) {
                DatabaseReference referencia = dbCotizacionesReferencia;
                obtenerMayorFolio(referencia, "C");
            } else {
                pedido = ultimoCotizacion + 1;
                PedidoManager.getInstance(getApplicationContext()).guardarUltimoFolioCotizaciones(pedido);

            }
        }
    }

    private void obtenerMayorFolio(@NonNull DatabaseReference referencia, String tipo) {


        System.out.println("OBTENIENDO FOLIO MAYOR......");

        Query query = referencia.orderByChild("folio").startAt(tipo + vendedor.getUsuario()).endAt(tipo + vendedor.getUsuario() + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int mayor = 0;
                int actual;

                for (DataSnapshot data : snapshot.getChildren()) {
                    System.out.println("FOLIO ENCONTRADO.........." + data.child("folio").getValue());

                    String folioValue = data.child("folio").getValue().toString();
                    actual = Integer.parseInt(folioValue.split("-")[1].substring(0, 4));

                    if (actual > mayor) {
                        mayor = actual;
                    }
                }

                pedido = mayor + 1;

                if (tipo.equals("P")) {
                    PedidoManager.getInstance(getApplicationContext()).guardarUltimoFolioPedidos(pedido);
                } else if (tipo.equals("C")) {
                    PedidoManager.getInstance(getApplicationContext()).guardarUltimoFolioCotizaciones(pedido);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public String calcularTotal() {

        DecimalFormat df = new DecimalFormat("#.00");
        double total = 0.0;
        for (ItemProductoPedido producto : Pedido.getListaDeProductos()) {
            double monto = Double.parseDouble(producto.getPrecio().replace(",", ".")) * Double.parseDouble(producto.getCantidad().replace(",", "."));
            total = total + monto;
        }
        return df.format(total);
    }

    private void precioManual() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        input.setGravity(Gravity.CENTER);
        input.setMaxLines(1);
        builder
                .setTitle("Pecio manual")
                .setMessage("Agregue el precio manualmente")
                .setView(input)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    String value = input.getText().toString();
                    if (input.getText().toString().trim().length() == 0) {
                        Toast.makeText(getApplicationContext(), "No ha ingresado ningun precio", Toast.LENGTH_SHORT).show();
                    } else {
                        precioManual = Double.parseDouble(value);
                    }
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                })
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                });

        builder.show();
        input.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

    }

    private void back() {

        if (Pedido.getListaDeProductos().size() != 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Ir a lista de clientes");
            builder.setMessage("¿Desea cancelar su pedido?");
            builder.setPositiveButton("SI", (dialogInterface, i) -> {
                Pedido.setObservaciones(null);
                if (tipoCliente.equals("clienteEscaneado")) {
                    Intent intent = new Intent(getApplicationContext(), QrScanner.class);
                    intent.putExtra("tipoCliente", tipoCliente);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else if (tipoCliente.equals("clienteRegistrado") || tipoCliente.equals("clienteExpress")) {
                    Intent intent = new Intent(getApplicationContext(), Lista_clientes.class);
                    intent.putExtra("tipoCliente", tipoCliente);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), Lista_clientes.class);
                    intent.putExtra("tipoCliente", tipoCliente);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("NO", (dialogInterface, i) -> {});
            builder.show();
        } else {
            Pedido.setObservaciones(null);
            if (tipoCliente.equals("clienteEscaneado")) {
                Intent intent = new Intent(getApplicationContext(), QrScanner.class);
                intent.putExtra("tipoCliente", tipoCliente);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else if (tipoCliente.equals("clienteRegistrado")) {
                Intent intent = new Intent(getApplicationContext(), Lista_clientes.class);
                intent.putExtra("tipoCliente", tipoCliente);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

        }

    }

    @Override
    public void onBackPressed() {
        back();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_buscador, menu);
        MenuItem item = menu.findItem(R.id.search);
        item.setTooltipText("Ingresar codigo");
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
        MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                container.setVisibility(View.VISIBLE);
                ControllerRecyclerViewAdapter.itemSeleccionado = null;
                adapterBusqueda.notifyDataSetChanged();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                container.setVisibility(View.GONE);
                if (!cantidad_tv.getText().equals("") && ControllerRecyclerViewAdapter.itemSeleccionado != null) {
                    obtenerProducto(ControllerRecyclerViewAdapter.itemSeleccionado.getId());
                } else if (cantidad_tv.getText().equals("")) {
                    Toast.makeText(getApplicationContext(), "Ingresar cantidad.", Toast.LENGTH_SHORT).show();
                }
                adapterBusqueda.setFilter(listaProductosBusqueda);
                adapterBusqueda.notifyDataSetChanged();
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        recyclerViewBusqueda.setVisibility(View.VISIBLE);
        try {
            ArrayList<Item> listaFiltrada = filter((ArrayList<Item>) listaProductosBusqueda, newText);
            adapterBusqueda.setFilter(listaFiltrada);
            adapterBusqueda.notifyDataSetChanged();
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

    private boolean isInternetAvailable() {
        return internetManager.isInternetAvaible();
    }
}
