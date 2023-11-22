package com.brainstormideas.caballeroaztecaventas.ui;

import android.app.ProgressDialog;
import android.content.Context;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brainstormideas.caballeroaztecaventas.R;
import com.brainstormideas.caballeroaztecaventas.data.models.Cliente;
import com.brainstormideas.caballeroaztecaventas.data.models.Pedido;
import com.brainstormideas.caballeroaztecaventas.data.models.Producto;
import com.brainstormideas.caballeroaztecaventas.data.models.Vendedor;
import com.brainstormideas.caballeroaztecaventas.entidad.ItemProductoPedido;
import com.brainstormideas.caballeroaztecaventas.ui.adapters.ControllerRecyclerViewAdapter;
import com.brainstormideas.caballeroaztecaventas.ui.adapters.ProductosAdapter;
import com.brainstormideas.caballeroaztecaventas.ui.adapters.ProductosPedidoAdapter;
import com.brainstormideas.caballeroaztecaventas.ui.viewmodels.FolioViewModel;
import com.brainstormideas.caballeroaztecaventas.ui.viewmodels.ProductoViewModel;
import com.brainstormideas.caballeroaztecaventas.ui.viewmodels.VendedorViewModel;
import com.brainstormideas.caballeroaztecaventas.utils.InternetManager;
import com.brainstormideas.caballeroaztecaventas.utils.SessionManager;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MenuPedidos extends AppCompatActivity implements SearchView.OnQueryTextListener {

    ImageButton home_button;
    TextView cliente_view;

    EditText cantidad_tv;
    EditText codigo_tv;
    Spinner tipoSelector;

    Button agregar_btn;
    Button marcas_btn;
    Button limpiar_btn;
    Button finalizar_pedido;

    DatabaseReference dbProductosReferencia;
    DatabaseReference dbVendedoresReferencia;
    DatabaseReference dbFoliosReferencia;
    DatabaseReference dbCotizacionesReferencia;

    ArrayAdapter<CharSequence> spinnerAdapter;

    String productoSeleccionado;
    boolean seleccionable;
    String tipoCliente;

    int pedido;
    String tipoSeleccionado;

    SessionManager sessionManager;
    Vendedor vendedor;

    double precioManual = 0.0;

    String vendedorActual;

    ProgressDialog progressDialog;

    ProductosPedidoAdapter adapter;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager manager;

    private ProductoViewModel productoViewModel;
    private RecyclerView recyclerViewBusqueda;
    private ProductosAdapter adapterBusqueda;
    private RecyclerView.LayoutManager managerBusqueda;
    List<Producto> listaProductosBusqueda = new ArrayList<>();
    private LinearLayout container;

    private ProgressBar progressBar;

    private InternetManager internetManager;

    private VendedorViewModel vendedorViewModel;
    private FolioViewModel folioViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_pedidos);

        vendedor = null;
        pedido = 0;

        sessionManager = new SessionManager(this);
        internetManager = new InternetManager(this);

        this.setTitle("Buscar producto");

        LayoutInflater inflater = getLayoutInflater();
        View inflatedView = inflater.inflate(R.layout.products_result, null);

        progressDialog = new ProgressDialog(this);

        container = findViewById(R.id.lista_busqueda_productos);
        container.addView(inflatedView);

        // Recyclerview de busqueda de productos
        managerBusqueda = new LinearLayoutManager(container.getContext());

        recyclerViewBusqueda = container.findViewById(R.id.productos_busqueda);
        recyclerViewBusqueda.setLayoutManager(managerBusqueda);
        adapterBusqueda = new ProductosAdapter(this, listaProductosBusqueda);
        recyclerViewBusqueda.setAdapter(adapterBusqueda);
        container.setVisibility(View.GONE);

        progressBar = container.findViewById(R.id.progressBar);

        // Load Products with ViewModel
        productoViewModel = new ProductoViewModel(this);
        cargarProductos();

        // Recyclerview de productos agregados

        manager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.productos_listados);
        recyclerView.setLayoutManager(manager);
        adapter = new ProductosPedidoAdapter(this, Pedido.getListaDeProductos());
        recyclerView.setAdapter(adapter);

        tipoCliente = getIntent().getExtras().get("tipoCliente").toString();
        seleccionable = getIntent().getBooleanExtra("seleccionable", false);

        dbProductosReferencia = FirebaseDatabase.getInstance().getReference().child("Producto");
        dbVendedoresReferencia = FirebaseDatabase.getInstance().getReference().child("Usuario");
        dbFoliosReferencia = FirebaseDatabase.getInstance().getReference().child("Folio");
        dbCotizacionesReferencia = FirebaseDatabase.getInstance().getReference().child("Cotizacion");

        vendedorActual = sessionManager.getUsuario();
        vendedorViewModel = new VendedorViewModel(this);
        vendedorViewModel.getVendedor(sessionManager.getEmail()).observe(this, vendedorObtained -> {
            vendedor = vendedorObtained;
            System.out.println("USUARIO ACTUAL: " + vendedor.getUsuario());
            Pedido.setVendedor(vendedor);
            if(Pedido.getFolio() != null && Pedido.getFolio().isEmpty()){
                //TODO Fix el tipo de pedido como variable
                obtenerFolio(vendedor.getUsuario(), "P");
            }
        });

        folioViewModel = new FolioViewModel(this);

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
                // TODO document why this method is empty
            }
        });

        home_button = findViewById(R.id.home_button);
        home_button.setOnClickListener(view -> back());

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
            productoSeleccionado = Objects.requireNonNull(getIntent().getExtras().get("code")).toString();
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
                if (!cantidad_tv.getText().toString().equals("")) {
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

                if (!cantidad_tv.getText().toString().equals("")) {
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

    private void cargarProductos() {

        productoViewModel.getProductos().observe(this, productos -> {

            int oldSize = listaProductosBusqueda.size();
            listaProductosBusqueda.clear();
            listaProductosBusqueda.addAll(productos);
            int newSize = listaProductosBusqueda.size();

            adapterBusqueda.notifyItemRangeChanged(0, Math.min(oldSize, newSize));
            if (newSize > oldSize) {
                adapterBusqueda.notifyItemRangeInserted(oldSize, newSize - oldSize);
            } else if (newSize < oldSize) {
                adapterBusqueda.notifyItemRangeRemoved(newSize, oldSize - newSize);
            }

            progressBar.setVisibility(View.GONE);
        });
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
                    itemProductoPedidoproducto.setCode(producto.getCode());
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

    private void obtenerFolio(String indice, String tipoPedido) {
        folioViewModel.getHighestFolio(indice, tipoPedido).observe(this, Pedido::setFolio);
    }

    private void finalizarPedido() {

        Pedido.setTotal(calcularTotal());

        if (Pedido.getListaDeProductos() != null && !Pedido.getListaDeProductos().isEmpty()) {
            vendedorViewModel.getVendedor(sessionManager.getEmail()).observe(this, vendedorObtained -> {
                vendedor = vendedorObtained;
                Pedido.setVendedor(vendedor);
                if (vendedor != null && (Pedido.getFolio() == null || Pedido.getFolio().isEmpty())) {
                    Pedido.setVendedor(vendedor);
                }
            });
            if (!Pedido.getFolio().isEmpty()) {
                Intent i = new Intent(getApplicationContext(), MenuFinal.class);
                i.putExtra("tipoCliente", tipoCliente);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("candadoModificar", true);
                startActivity(i);
            } else {
                Toast.makeText(getApplicationContext(), "Aun no se genera el folio, espere un momento...", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Debe ingresar almenos un producto a la lista", Toast.LENGTH_LONG).show();
        }
    }

    public void clear() {
        int size = Pedido.getListaDeProductos().size();
        if (size > 0) {
            Pedido.getListaDeProductos().subList(0, size).clear();
            synchronized (Pedido.getListaDeProductos()) {
                Pedido.getListaDeProductos().notifyAll();
                adapter.notifyItemRangeRemoved(0, size);
            }
        }
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

        if (Pedido.getListaDeProductos().isEmpty()) {
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
                } else {
                    Intent intent = new Intent(getApplicationContext(), ListaClientes.class);
                    intent.putExtra("tipoCliente", tipoCliente);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("NO", (dialogInterface, i) -> {
            });
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
                Intent intent = new Intent(getApplicationContext(), ListaClientes.class);
                intent.putExtra("tipoCliente", tipoCliente);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getApplicationContext(), ListaClientes.class);
                intent.putExtra("tipoCliente", "clienteRegistrado");
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
                ControllerRecyclerViewAdapter.productoSeleccionado = null;
                adapterBusqueda.notifyDataSetChanged();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                container.setVisibility(View.GONE);
                if (!cantidad_tv.getText().equals("") && ControllerRecyclerViewAdapter.productoSeleccionado != null) {
                    obtenerProducto(ControllerRecyclerViewAdapter.productoSeleccionado.getCode());
                } else if (cantidad_tv.getText().equals("")) {
                    Toast.makeText(getApplicationContext(), "Ingresar cantidad.", Toast.LENGTH_SHORT).show();
                }
                adapterBusqueda.setProductos(listaProductosBusqueda);
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
            ArrayList<Producto> listaFiltrada = filter((ArrayList<Producto>) listaProductosBusqueda, newText);
            adapterBusqueda.setProductos(listaFiltrada);
            adapterBusqueda.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private ArrayList<Producto> filter(ArrayList<Producto> items, String texto) {
        ArrayList<Producto> listaFiltrada = new ArrayList<>();
        try {
            String textoMiniscula = texto.toLowerCase();
            for (Producto producto : items) {
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

    public void limpiarPedido() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Limpiar pedido");
        builder.setMessage("Se eliminarán todos los productos agregados a la lista.");
        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            clear();
            adapter.notifyDataSetChanged();
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    public void clearProductoInfo() {
        codigo_tv.setText("");
        cantidad_tv.setText("");
    }
}
