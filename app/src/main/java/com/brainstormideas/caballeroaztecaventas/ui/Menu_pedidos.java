package com.brainstormideas.caballeroaztecaventas.ui;

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
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brainstormideas.caballeroaztecaventas.R;
import com.brainstormideas.caballeroaztecaventas.ui.adapters.RecyclerViewProductosPedidoAdapter;
import com.brainstormideas.caballeroaztecaventas.entidad.ItemProductoPedido;
import com.brainstormideas.caballeroaztecaventas.data.models.Cliente;
import com.brainstormideas.caballeroaztecaventas.data.models.Pedido;
import com.brainstormideas.caballeroaztecaventas.data.models.Vendedor;
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

import java.text.DecimalFormat;
import java.util.Objects;

import static com.brainstormideas.caballeroaztecaventas.ui.Verificador_precio.isInitialized;

public class Menu_pedidos extends AppCompatActivity {

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
    RecyclerView.LayoutManager manager;
    RecyclerView recyclerView;

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

    FirebaseUser user;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_pedidos);

        vendedor = null;
        pedido = 0;

        initializedFirebaseService();

        tipoCliente = getIntent().getExtras().get("tipoCliente").toString();
        seleccionable = getIntent().getBooleanExtra("seleccionable", false);
        progressDialog = new ProgressDialog(this);

        dbProductosReferencia = FirebaseDatabase.getInstance().getReference().child("Producto");
        dbVendedoresReferencia = FirebaseDatabase.getInstance().getReference().child("Usuario");
        dbFoliosReferencia = FirebaseDatabase.getInstance().getReference().child("Folio");
        dbCotizacionesReferencia = FirebaseDatabase.getInstance().getReference().child("Cotizacion");

        sessionManager = new SessionManager(this);

        vendedorActual = sessionManager.getUsuario();
        obtenerVendedor();

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
                if(tipoSeleccionado.equals("OTRO")){
                    precioManual();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        recyclerView = findViewById(R.id.lista_productos);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter = new RecyclerViewProductosPedidoAdapter(this, Pedido.getListaDeProductos());
        recyclerView.setAdapter(adapter);

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
        marcas_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Marcas.class);
                i.putExtra("ruta", "MenuPedidos");
                i.putExtra("tipoCliente", tipoCliente);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });

        if (seleccionable) {
            productoSeleccionado = getIntent().getExtras().get("codigo").toString();
            codigo_tv.setText(productoSeleccionado);
        }


        limpiar_btn = findViewById(R.id.limpiar_btn);

        agregar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipoSeleccionado = tipoSelector.getSelectedItem().toString();
                obtenerProducto();
            }
        });
        limpiar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limpiarPedido();
            }
        });

        finalizar_pedido = findViewById(R.id.finalizar_pedido);
        finalizar_pedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalizarPedido();
            }
        });

        obtenerFolio();

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
                if(!text.equals(text.toUpperCase()))
                {
                    text = text.toUpperCase();
                    codigo_tv.setText(text);
                    codigo_tv.setSelection(text.length());
                }
            }
        });

        codigo_tv.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if(actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                        if(!cantidad_tv.getText().equals("")){
                            obtenerProducto();
                            inputMethodManager.hideSoftInputFromWindow(codigo_tv.getWindowToken(), 0);
                        } else {
                            Toast.makeText(getApplicationContext(), "Ingresar código.", Toast.LENGTH_SHORT).show();
                        }

                    return true;

                }
                return false;
            }
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
                if(!text.equals(text.toUpperCase()))
                {
                    text = text.toUpperCase();
                    cantidad_tv.setText(text);
                    cantidad_tv.setSelection(text.length());
                }
            }
        });

        cantidad_tv.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if(actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {

                    if(!cantidad_tv.getText().equals("")){
                        obtenerProducto();
                        inputMethodManager.hideSoftInputFromWindow(cantidad_tv.getWindowToken(), 0);
                    } else {
                        Toast.makeText(getApplicationContext(), "Ingresar cantidad.", Toast.LENGTH_SHORT).show();
                    }

                    return true;

                }
                return false;
            }
        });

    }


    private void initializedFirebaseService() {
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

    private Cliente obtenerCliente() {
        return Pedido.getCliente();
    }

    private void obtenerVendedor() {

        if (vendedorActual.equals("admin")) {

            vendedor = new Vendedor("ad", "ADMINISTRADOR", "admin","admin", "3327257746", "admin");

        } else {
            Query query = dbVendedoresReferencia.orderByChild("email").equalTo(sessionManager.getEmail());
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot data : snapshot.getChildren()) {
                        if (data.child("id").getValue() != null && data.child("nombre").getValue() != null && data.child("password").getValue() != null &&
                                data.child("telefono").getValue() != null && data.child("usuario").getValue() != null) {

                            String id = data.child("id").getValue().toString();
                            String nombre = data.child("nombre").getValue().toString();
                            String password = data.child("password").getValue().toString();
                            String telefono = data.child("telefono").getValue().toString();
                            String usuario = data.child("usuario").getValue().toString();
                            String email = data.child("email").getValue().toString();

                            vendedor = new Vendedor(id, nombre, usuario, password, telefono, email);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), "Error al buscar vendedor.", Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    private void obtenerProducto() {

        final String cantidad = cantidad_tv.getText().toString().trim();
        final String codigo = codigo_tv.getText().toString().trim();

        progressDialog.setMessage("Buscando producto...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        if (!cantidad.equals("") && !codigo.equals("")) {

            if (Tools.isNumeric(codigo)) {

                final int id = Integer.parseInt(codigo);

                Query query = dbProductosReferencia.orderByChild("id").equalTo(id);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(!snapshot.exists()){
                            Toast.makeText(getApplicationContext(), "No se encontro ningun producto con ese código.", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }

                        for (DataSnapshot data : snapshot.getChildren()) {

                            if (data.child("id").getValue() != null && data.child("nombre").getValue() != null && data.child("marca").getValue() != null &&
                                    data.child("cca").getValue() != null && data.child("p4").getValue() != null && data.child("p3").getValue() != null &&
                                    data.child("p2").getValue() != null && data.child("p1").getValue() != null && data.child("lista").getValue() != null) {

                                DecimalFormat df = new DecimalFormat("#.00");

                                String id = Objects.requireNonNull(data.child("id").getValue()).toString();
                                String nombre = Objects.requireNonNull(data.child("nombre").getValue()).toString();
                                String marca = Objects.requireNonNull(data.child("marca").getValue()).toString();
                                double lista = Double.parseDouble(Objects.requireNonNull(data.child("lista").getValue()).toString());
                                double cca = Double.parseDouble(Objects.requireNonNull(data.child("cca").getValue()).toString());
                                double p1 = Double.parseDouble(Objects.requireNonNull(data.child("p1").getValue()).toString());
                                double p2 = Double.parseDouble(Objects.requireNonNull(data.child("p2").getValue()).toString());
                                double p3 = Double.parseDouble(Objects.requireNonNull(data.child("p3").getValue()).toString());
                                double p4 = Double.parseDouble(Objects.requireNonNull(data.child("p4").getValue()).toString());

                                ItemProductoPedido producto = new ItemProductoPedido();
                                producto.setId(id);
                                producto.setNombre(nombre);
                                producto.setMarca(marca);
                                producto.setCantidad(cantidad);
                                producto.setTipo(tipoSeleccionado);

                                switch (tipoSeleccionado) {
                                    case "CCA":
                                        producto.setPrecio(df.format(cca));
                                        break;
                                    case "P4":
                                        producto.setPrecio(df.format(p4));
                                        break;
                                    case "P3":
                                        producto.setPrecio(df.format(p3));
                                        break;
                                    case "P2":
                                        producto.setPrecio(df.format(p2));
                                        break;
                                    case "P1":
                                        producto.setPrecio(df.format(p1));
                                        break;
                                    case "OTRO":
                                        producto.setPrecio(df.format(precioManual));
                                        break;
                                    default:
                                        producto.setPrecio(df.format(lista));
                                }

                                try {

                                    Pedido.getListaDeProductos().add(producto);
                                    adapter.notifyDataSetChanged();
                                    clearProductoInfo();
                                    Toast.makeText(getApplicationContext(), "Producto Agregado.", Toast.LENGTH_SHORT).show();
                                    Log.e("PRODUCCCCCCCCCCCCCTOOOOOOOOOOOOOOOOOOOOOOOOO",String.valueOf(producto.getNombre()));
                                    break;

                                } catch (Exception e){
                                    Toast.makeText(getApplicationContext(), "No se pudo agregar el producto.", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                    progressDialog.dismiss();
                                }

                            }
                        }

                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "No se encontro ningun producto con ese codigo. " + error, Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                });

            } else {

                Query query = dbProductosReferencia.orderByChild("id").equalTo(codigo);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(!snapshot.exists()){
                            Toast.makeText(getApplicationContext(), "No se encontro ningun producto con ese código.", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }

                        for (DataSnapshot data : snapshot.getChildren()) {

                            if (data.child("id").getValue() != null && data.child("nombre").getValue() != null && data.child("marca").getValue() != null &&
                                    data.child("cca").getValue() != null && data.child("p4").getValue() != null && data.child("p3").getValue() != null &&
                                    data.child("p2").getValue() != null && data.child("p1").getValue() != null && data.child("lista").getValue() != null) {

                                DecimalFormat df = new DecimalFormat("#.00");

                                String id = Objects.requireNonNull(data.child("id").getValue()).toString();
                                String nombre = Objects.requireNonNull(data.child("nombre").getValue()).toString();
                                String marca = Objects.requireNonNull(data.child("marca").getValue()).toString();
                                double lista = Double.parseDouble(Objects.requireNonNull(data.child("lista").getValue()).toString());
                                double cca = Double.parseDouble(Objects.requireNonNull(data.child("cca").getValue()).toString());
                                double p1 = Double.parseDouble(Objects.requireNonNull(data.child("p1").getValue()).toString());
                                double p2 = Double.parseDouble(Objects.requireNonNull(data.child("p2").getValue()).toString());
                                double p3 = Double.parseDouble(Objects.requireNonNull(data.child("p3").getValue()).toString());
                                double p4 = Double.parseDouble(Objects.requireNonNull(data.child("p4").getValue()).toString());

                                ItemProductoPedido producto = new ItemProductoPedido();
                                producto.setId(id);
                                producto.setNombre(nombre);
                                producto.setMarca(marca);
                                producto.setCantidad(cantidad);
                                producto.setTipo(tipoSeleccionado);

                                switch (tipoSeleccionado) {
                                    case "CCA":
                                        producto.setPrecio(df.format(cca));
                                        break;
                                    case "P4":
                                        producto.setPrecio(df.format(p4));
                                        break;
                                    case "P3":
                                        producto.setPrecio(df.format(p3));
                                        break;
                                    case "P2":
                                        producto.setPrecio(df.format(p2));
                                        break;
                                    case "P1":
                                        producto.setPrecio(df.format(p1));
                                        break;
                                    case "OTRO":
                                        producto.setPrecio(df.format(precioManual));
                                        break;
                                    default:
                                        producto.setPrecio(df.format(lista));
                                }

                                try {

                                    Pedido.getListaDeProductos().add(producto);
                                    adapter.notifyDataSetChanged();
                                    clearProductoInfo();
                                    Toast.makeText(getApplicationContext(), "Producto Agregado.", Toast.LENGTH_SHORT).show();
                                    Log.e("PRODUCCCCCCCCCCCCCTOOOOOOOOOOOOOOOOOOOOOOOOO",String.valueOf(producto.getNombre()));
                                    break;
                                } catch (Exception e){
                                    Toast.makeText(getApplicationContext(), "No se pudo agregar el producto.", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                    progressDialog.dismiss();
                                }

                            }
                        }
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "No se encontro ningun producto con ese código. " + error, Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                });
            }


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
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clear();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();

        adapter.notifyDataSetChanged();
    }

    public void clearProductoInfo(){
        codigo_tv.setText("");
        cantidad_tv.setText("");
        tipoSelector.setSelection(0);
    }

    private String generarFolio() {

        int cantidadDeCifrasPedido = String.valueOf(pedido).length();

        String folioPedido = "";

        String folioGenerado = Pedido.getVendedor().getUserName();

        String tipo = "";

        if(Pedido.getTipo().equals("pedido")){
            tipo = "P";
        } else if (Pedido.getTipo().equals("cotizacion")){
            tipo = "C";
        }

        switch (cantidadDeCifrasPedido) {
            case 1:
                folioPedido = "000" + pedido;
                break;
            case 2:
                folioPedido = "00" + pedido;
                break;
            case 3:
                folioPedido = "0" + pedido;
                break;
            case 4:
                folioPedido = String.valueOf(pedido);
        }

        return tipo + folioGenerado + "-" + folioPedido;

    }

    private void obtenerFolio() {

        if(vendedorActual.equals("admin")){
           pedido = 0;
           return;
        }

        if(Pedido.getTipo().equals("pedido")){

            dbFoliosReferencia.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {

                        int mayor = 0;
                        int actual;

                        for (DataSnapshot data : snapshot.getChildren()) {

                            if(data.child("folio").getValue()!=null){
                                
                                if(data.child("folio").getValue().toString().startsWith("P")) {

                                    if (vendedor.getUserName().equals(data.child("folio").getValue().toString().split("-")[0].substring(1, 3))) {

                                        actual = Integer.parseInt(data.child("folio").getValue().toString().split("-")[1].substring(0, 4));

                                        if (actual > mayor) {
                                            mayor = actual;
                                        }
                                        pedido = mayor + 1;
                                    }
                                }
                            }
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else if(Pedido.getTipo().equals("cotizacion")){

            dbCotizacionesReferencia.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {

                        int mayor = 0;
                        int actual;

                        for (DataSnapshot data : snapshot.getChildren()) {

                            if (data.child("folio").getValue().toString().startsWith("P")) {
                                if (data.child("folio").getValue() != null) {

                                    if (vendedor.getUserName().equals(data.child("folio").getValue().toString().split("-")[0].substring(1, 3))) {
                                        actual = Integer.parseInt(data.child("folio").getValue().toString().split("-")[1].substring(0, 4));

                                        if (actual > mayor) {
                                            mayor = actual;
                                        }
                                        pedido = mayor + 1;
                                    }
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
    }

    public String calcularTotal() {

        DecimalFormat df = new DecimalFormat("#.00");
        double total = 0.0;
        for (ItemProductoPedido producto : Pedido.getListaDeProductos()) {
            double monto = Double.parseDouble(producto.getPrecio().replace(",",".")) * Double.parseDouble(producto.getCantidad().replace(",","."));
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
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        String value = input.getText().toString();
                        if (input.getText().toString().trim().length() == 0) {
                            Toast.makeText(getApplicationContext(),"No ha ingresado ningun precio", Toast.LENGTH_SHORT).show();
                        } else {
                            precioManual = Double.parseDouble(value);
                        }
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                    }

                });

        builder.show();
        input.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

    }

    private void back() {

        if(Pedido.getListaDeProductos().size()!=0){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Ir a lista de clientes");
            builder.setMessage("¿Desea cancelar su pedido?");
            builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Pedido.setObservaciones(null);
                    if (tipoCliente.equals("clienteEscaneado")) {
                        Intent intent = new Intent(getApplicationContext(), QrScanner.class);
                        intent.putExtra("tipoCliente", tipoCliente);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else if (tipoCliente.equals("clienteRegistrado") || tipoCliente.equals("clienteExpress")){
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
                }
            });
            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.show();
        } else {
            Pedido.setObservaciones(null);
            if(tipoCliente.equals("clienteEscaneado")){
                Intent intent = new Intent(getApplicationContext(), QrScanner.class);
                intent.putExtra("tipoCliente", tipoCliente);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else if(tipoCliente.equals("clienteRegistrado")){
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
}
