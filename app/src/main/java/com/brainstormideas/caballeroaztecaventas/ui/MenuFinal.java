package com.brainstormideas.caballeroaztecaventas.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.brainstormideas.caballeroaztecaventas.R;
import com.brainstormideas.caballeroaztecaventas.data.models.Cliente;
import com.brainstormideas.caballeroaztecaventas.data.models.Pedido;
import com.brainstormideas.caballeroaztecaventas.data.models.PedidoFolio;
import com.brainstormideas.caballeroaztecaventas.data.models.Vendedor;
import com.brainstormideas.caballeroaztecaventas.entidad.ItemProductoPedido;
import com.brainstormideas.caballeroaztecaventas.services.MailboxService;
import com.brainstormideas.caballeroaztecaventas.utils.SessionManager;
import com.brainstormideas.caballeroaztecaventas.utils.filestools.ImageConverter;
import com.brainstormideas.caballeroaztecaventas.utils.filestools.ReportsGenerator;
import com.brainstormideas.caballeroaztecaventas.utils.filestools.TemplatePDF;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.DocumentException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class MenuFinal extends AppCompatActivity {

    private static final int REQUEST_CODE = 222;
    private static final int RESQUEST_ASK_CODE_PERMISSION = 111;

    TextView tvFolio;
    TextView tvRazon;
    TextView tvRuta;
    TextView tvTotal;

    RadioButton rbFactura;
    RadioButton rbRemision;

    EditText etxObservaciones;
    SessionManager sessionManager;
    String vendedorActual;
    String vendedorEmail;
    DatabaseReference dbUsuariosReferencia;

    Button btnModificar;
    Button btnEnviar;
    Button pdfview_btn;

    Drawable imagen;

    TemplatePDF templatePDF;
    ReportsGenerator reporte;

    String tipoCliente;

    String emailVendedor = Pedido.getVendedor().getEmail();
    String emailCliente = Pedido.getCliente().getEmail();

    String[] mails = {};

    DatabaseReference dbVendedoresReferencia;

    boolean candadoModificar;

    String totalConIVA;
    String totalSinIVA;

    int pedido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_final);

        pedido = 0;

        candadoModificar = Objects.requireNonNull(getIntent().getExtras()).getBoolean("candadoModificar", true);

        tipoCliente = Objects.requireNonNull(getIntent().getExtras().get("tipoCliente")).toString();
        sessionManager = new SessionManager(this);

        if (!sessionManager.isActiveVendedor()) {
            emailVendedor = "";
        }

        if (!sessionManager.isActiveCliente()) {
            emailCliente = "";
        }

        mails = new String[]{sessionManager.getAlmacenEmail(), sessionManager.getComprasEmail(),
                sessionManager.getPrimaryEmail(), sessionManager.getSecondaryEmail(), emailVendedor,
                emailCliente};

        solicitarPermisos();

        dbVendedoresReferencia = FirebaseDatabase.getInstance().getReference().child("Usuario");
        dbUsuariosReferencia = FirebaseDatabase.getInstance().getReference().child("Usuario");

        vendedorActual = sessionManager.getUsuario();
        vendedorEmail = sessionManager.getEmail();

        etxObservaciones = findViewById(R.id.etxObservaciones);
        etxObservaciones.setText(Pedido.getObservaciones());

        obtenerVendedor();

        imagen = ContextCompat.getDrawable(this, R.drawable.logo);

        tvFolio = findViewById(R.id.tvFolio);
        tvFolio.setText("FOLIO: " + Pedido.getFolio());

        tvRazon = findViewById(R.id.tvRazon);
        tvRazon.setText(Pedido.getCliente().getRazon());

        tvRuta = findViewById(R.id.tvRuta);
        tvRuta.setText(Pedido.getCliente().getRuta());

        tvTotal = findViewById(R.id.tvTotal);
        tvTotal.setText("TOTAL: " + Pedido.getTotal());

        rbFactura = findViewById(R.id.rbFactura);
        rbRemision = findViewById(R.id.rbRemision);
        rbRemision.setChecked(Pedido.preciosConIVA);
        rbFactura.setChecked(!Pedido.preciosConIVA);

        rbFactura.setOnCheckedChangeListener((buttonView, isChecked) -> {
            calcularPreciosTotalSinIVA();
            tvTotal.setText("TOTAL: " + Pedido.getTotal());
        });

        rbRemision.setOnCheckedChangeListener((buttonView, isChecked) -> {
            calcularPreciosTotalConIVA();
            tvTotal.setText("TOTAL: " + Pedido.getTotal());
        });

        pdfview_btn = findViewById(R.id.pdfview_btn);
        pdfview_btn.setOnClickListener(v -> {
            try {
                validadDocumento();
                generarVistaPdf();
            } catch (DocumentException | IOException e) {
                e.printStackTrace();
            }
        });

        btnModificar = findViewById(R.id.btnModificar);
        btnModificar.setEnabled(candadoModificar);
        btnModificar.setOnClickListener(v -> modificar());

        btnEnviar = findViewById(R.id.btnEnviar);
        btnEnviar.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MenuFinal.this);
            builder.setTitle("Enviar pedido");
            builder.setMessage("¿Seguro que desea realizar el pedido?\n\n" +
                    "Se generará un folio para su pedido, además se guardara el PDF y el Excel en la nube.\n" +
                    "Usted debera seleccionar una applicación de terceros para enviar esta infromación.");
            builder.setPositiveButton("Aceptar", (dialogInterface, i) -> enviarPedido());
            builder.setNegativeButton("Cancelar", (dialogInterface, i) -> dialogInterface.dismiss());
            builder.show();
        });
    }

    private Cliente obtenerCliente() {
        return Pedido.getCliente();
    }

    private void obtenerVendedor() {

        if (!vendedorActual.equals("admin")) {

            Query query = dbUsuariosReferencia.orderByChild("email").equalTo(vendedorEmail);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot data : snapshot.getChildren()) {

                        if (data.child("id").getValue() != null && data.child("email").getValue() != null && data.child("nombre").getValue() != null && data.child("password").getValue() != null &&
                                data.child("telefono").getValue() != null && data.child("usuario").getValue() != null) {

                            int id = Integer.parseInt(Objects.requireNonNull(data.child("id").getValue()).toString());
                            String nombre = Objects.requireNonNull(data.child("nombre").getValue()).toString();
                            String usuario = Objects.requireNonNull(data.child("usuario").getValue()).toString();
                            String password = Objects.requireNonNull(data.child("password").getValue()).toString();
                            String numero = Objects.requireNonNull(data.child("telefono").getValue()).toString();
                            String email = Objects.requireNonNull(data.child("email").getValue()).toString();

                            Vendedor vendedor = new Vendedor(id, nombre, usuario, password, numero, email);
                            Pedido.setVendedor(vendedor);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else {

            Vendedor vendedor = new Vendedor(0, "ADMINISTRADOR", vendedorActual, "admin", "3333333333", vendedorEmail);
            Pedido.setVendedor(vendedor);
        }
    }

    private ArrayList<ItemProductoPedido> obtenerListaDePedidos() {
        return Pedido.getListaDeProductos();
    }

    public void validadDocumento() {
        if (rbFactura.isChecked()) {
            Pedido.setDocumento("Factura");
        } else if (rbRemision.isChecked()) {
            Pedido.setDocumento("Remision");
        }
    }

    public void generarVistaPdf() throws DocumentException, IOException {
        prepararPdf();
        templatePDF.abrirVistaDePDF(candadoModificar);
    }

    public void generarPdf() throws DocumentException, IOException {
        prepararPdf();
    }

    private void prepararPdf() throws DocumentException, IOException {
        calcularPreciosTotalSinIVA();
        String observaciones = etxObservaciones.getText().toString().trim();
        Pedido.setObservaciones(observaciones);

        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        String subtituloPdf = "J O S E   M A R I A   C O S S   #   1 2 7 8 - A\t\t\t\n" +
                "T E L. 38 23 76 32;  FAX. 38 54 36 97\t\t\t\n" +
                "G U A D A L A J A R A,   J A L I S C O.\t\t\t\n";

        templatePDF = new TemplatePDF(getApplicationContext());
        templatePDF.openDocument();
        templatePDF.addData("Pedido", "Lista de productos", "Caballero Azteca");
        templatePDF.addTitles("DISTRIBUIDORA FERRETERA CABALLERO AZTECA, S. A. DE C. V.", subtituloPdf, currentDateTimeString);
        templatePDF.addParagraph("Informacion general:");
        templatePDF.generalInfoTable();
        templatePDF.addParagraph("Informacion sobre el cliente:");
        templatePDF.clientInfoTable();
        templatePDF.createTable(getTitulosTabla(), listaDeProductosTexto());
        templatePDF.addParagraph("TOTAL: " + "$" + Pedido.getTotal());
        if (!observaciones.equals("")) {
            templatePDF.addParagraph("OBSERVACIONES: " + Pedido.getObservaciones().toUpperCase());
        } else {
            templatePDF.addParagraph("SIN OBSERVACIONES");
        }
        templatePDF.closeDocument();
    }

    private String[] getTitulosTabla() {
        String precioTitulo = "PRECIO";
        if (Pedido.getDocumento().equals("Factura")) {
            precioTitulo = "PRECIO (SIN IVA)";
        }
        return new String[]{"CANTIDAD", "CODIGO", "MARCA", "DESCRIPCION DEL ARTICULO", precioTitulo};
    }

    public void enviarPedido() {

        ArrayList<Uri> uris = new ArrayList<>();
        String observaciones = etxObservaciones.getText().toString().trim();
        Pedido.setObservaciones(observaciones);

        String domicilio = Pedido.getCliente().getCalle() + " " +
                "#" + Pedido.getCliente().getNumeroExterior() + "INT: " + Pedido.getCliente().getNumeroInterior() +
                " COLONIA " + Pedido.getCliente().getColonia() + " C.P. " + Pedido.getCliente().getCp();

        String historial = "Abierto por: " + Pedido.getVendedor().getNombre();

        calcularPreciosTotalSinIVA();

        try {
            validadDocumento();
            generarPdf();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }

        ImageConverter ic = new ImageConverter();
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        reporte = new ReportsGenerator(this, obtenerCliente(), Pedido.getVendedor(), obtenerListaDePedidos(), currentDateTimeString, ic.convertDrawableToStream(imagen));

        try {
            reporte.generarReporte();
            reporte.guardar();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "No se pudo enviar el pedido.", Toast.LENGTH_LONG).show();
            return;
        }

        Uri uriExcel = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", reporte.obtenerReporte());
        Uri uriPDF = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", templatePDF.obtenerReporte());
        uris.add(uriExcel);
        uris.add(uriPDF);

        this.grantUriPermission(
                "com.brainstormideas.caballeroaztecaventas", // Reemplaza con el nombre de tu paquete
                uriExcel,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
        );

        this.grantUriPermission(
                "com.brainstormideas.caballeroaztecaventas", // Reemplaza con el nombre de tu paquete
                uriPDF,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
        );


        PedidoFolio pedidoFolio = new PedidoFolio(Pedido.getFolio(), Pedido.getTipo(), Pedido.getVendedor(), Pedido.getCliente().getRuta(),
                Pedido.getDocumento(), Pedido.getCliente().getCode(), Pedido.getCliente().getRazon(), Pedido.getCliente().getRfc(),
                domicilio, Pedido.getCliente().getMunicipio(), Pedido.getCliente().getEstado(), Pedido.getCliente().getTelefono(),
                Pedido.getCliente().getEmail(), Pedido.getTotal(), Pedido.getObservaciones(), listaDeProductosTextoFolio(),
                "noautorizado", "0000", "0", "SIN REPARTIDOR",
                new SimpleDateFormat("dd-MM-yyyy").format(new Date()),
                totalSinIVA, totalConIVA, historial, uriExcel.toString(), uriPDF.toString());

        String texto = createEmailText();
        String subject = "Pedido con folio: " + pedidoFolio.getFolio();

        try {
            enviarCorreo(uris, subject, texto);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "No se ha enviado el correo.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        Intent mailBoxService = new Intent(this, MailboxService.class);
        mailBoxService.putExtra("pedido", pedidoFolio);
        startService(mailBoxService);
    }

    private String createEmailText() {
        return "DISTRIBUIDORA FERRETERA CABALLERO AZTECA" +
                "\n\n\n" +
                "Datos del vendedor" +
                "\n\n\n" +
                "Vendedor: " + Pedido.getVendedor().getNombre() + "\n\n" +
                "Codigo: " + generarCodigoVendedor() + "\n\n" +
                "Email: " + Pedido.getVendedor().getEmail() + "\n" +
                "\n\n\n" +
                "Datos del pedido" +
                "\n\n\n" +
                "Folio : " + Pedido.getFolio() + "\n\n" +
                "Código de cliente: " + Pedido.getCliente().getId() + "\n\n" +
                "Cliente: " + Pedido.getCliente().getRazon() + "\n\n" +
                "Factura o registro: " + Pedido.getDocumento() + "\n\n" +
                "Ruta: " + Pedido.getCliente().getRuta() + "\n\n" +
                "Monto del pedido: $" + Pedido.getTotal() + "\n" +
                "\n\n\n" +
                "Fecha y hora de captura: \n" + reporte.getFecha() + "\n" +
                "\n\n" +
                "Visite: www.caballeroazteca.com.mx\n" +
                "\n\n" +
                "Teléfonos: 3338237632 ó 3338543697.";
    }

    private void enviarCorreo(ArrayList<Uri> uris, String subject, String texto) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, mails);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, texto);
        emailIntent.putExtra(Intent.EXTRA_STREAM, uris);
        emailIntent.setType("text/plain");
        Intent chooser = Intent.createChooser(emailIntent, "Seleccione la aplicación para enviar su correo:");
        startActivityForResult(chooser, REQUEST_CODE);
    }

    private String generarCodigoVendedor() {

        int cantidadDeCifrasVendedor = Pedido.getVendedor().getUsuario().length();
        String codigoVendedor = "";
        switch (cantidadDeCifrasVendedor) {
            case 1:
                codigoVendedor = "0" + Pedido.getVendedor().getUsuario().toUpperCase();
                break;
            case 2:
                codigoVendedor = Pedido.getVendedor().getUsuario().toUpperCase();
                break;
        }
        return codigoVendedor;
    }

    public void modificar() {
        Intent i = new Intent(getApplicationContext(), MenuPedidos.class);
        calcularPreciosTotalConIVA();
        i.putExtra("tipoCliente", "cliente");
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    public void pantallaFinal() {
        Intent i = new Intent(getApplicationContext(), PantallaPedidoFinalizado.class);
        i.putExtra("tipoCliente", "cliente");
        i.putExtra("candadoModificar", false);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            Toast.makeText(getApplicationContext(), "Pedido enviado exitosamente.", Toast.LENGTH_LONG).show();
            pantallaFinal();
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getApplicationContext(), "Ha cancelado la operacion.", Toast.LENGTH_LONG).show();
        } else if (resultCode == RESULT_OK) {
            Toast.makeText(getApplicationContext(), "Accion completada", Toast.LENGTH_LONG).show();
        }
    }

    public ArrayList<String[]> listaDeProductosTexto() {
        ArrayList<String[]> rows = new ArrayList<>();
        for (ItemProductoPedido item : Pedido.getListaDeProductos()) {
            String[] cadena = new String[5];
            cadena[0] = item.getCantidad();
            cadena[1] = item.getCode();
            cadena[2] = item.getMarca();
            cadena[3] = item.getNombre();
            cadena[4] = item.getPrecio();

            rows.add(cadena);
        }
        return rows;
    }

    public ArrayList<ItemProductoPedido> listaDeProductosTextoFolio() {
        ArrayList<ItemProductoPedido> products = new ArrayList<>();
        for (ItemProductoPedido item : Pedido.getListaDeProductos()) {
            String cantidad = item.getCantidad();
            String code = item.getCode();
            String marca = item.getMarca();
            String nombre = item.getNombre();
            String precio = item.getPrecio();

            ItemProductoPedido itemProducto = new ItemProductoPedido(code, nombre, marca, cantidad, precio, null, null);
            products.add(itemProducto);

        }
        return products;
    }

    public void calcularPreciosTotalSinIVA() {

        validadDocumento();

        if (Pedido.preciosConIVA) {
            DecimalFormat df = new DecimalFormat("#.00");
            if (Pedido.getDocumento().equals("Factura")) {
                for (int i = 0; i < Pedido.getListaDeProductos().size(); i++) {
                    double nuevoPrecio = Double.parseDouble(Pedido.getListaDeProductos().get(i).getPrecio().replace(",", ".")) / 1.16;
                    Pedido.getListaDeProductos().get(i).setPrecio(df.format(nuevoPrecio));
                }
                Pedido.preciosConIVA = false;
            }
        } else if (Pedido.getDocumento().equals("Remision")) {
            calcularPreciosTotalConIVA();
        }
        totalSinIVA = String.valueOf(calcularTotal());
        totalConIVA = String.valueOf(Double.parseDouble(calcularTotal()) * 1.16);
        Pedido.setTotal(calcularTotal());

    }

    public void calcularPreciosTotalConIVA() {

        if (!Pedido.preciosConIVA) {
            DecimalFormat df = new DecimalFormat("#.00");
            for (int i = 0; i < Pedido.getListaDeProductos().size(); i++) {
                double nuevoPrecio = Double.parseDouble(Pedido.getListaDeProductos().get(i).getPrecio().replace(",", ".")) * 1.16;
                Pedido.getListaDeProductos().get(i).setPrecio(df.format(nuevoPrecio));
            }
            totalSinIVA = String.valueOf(Double.parseDouble(calcularTotal()) / 1.16);
            totalConIVA = String.valueOf(calcularTotal());
            Pedido.preciosConIVA = true;
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

    public void solicitarPermisos() {

        int permisoSMS = ContextCompat.checkSelfPermission(MenuFinal.this, Manifest.permission.INTERNET);
        int permisoLocation = ContextCompat.checkSelfPermission(MenuFinal.this, Manifest.permission.SEND_SMS);
        int permisoAlmacenamiento = ContextCompat.checkSelfPermission(MenuFinal.this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permisoSMS != PackageManager.PERMISSION_GRANTED ||
                permisoLocation != PackageManager.PERMISSION_GRANTED ||
                permisoAlmacenamiento != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, RESQUEST_ASK_CODE_PERMISSION);
        }
    }

    @Override
    public void onBackPressed() {
        if (candadoModificar) {
            back();
        }

    }

    private void back() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alerta");
        builder.setMessage("¿Desea modificar su pedido?");
        builder.setPositiveButton("SI", (dialogInterface, i) -> {
            Pedido.setObservaciones(null);
            calcularPreciosTotalConIVA();
            Intent intent = new Intent(getApplicationContext(), MenuPedidos.class);
            intent.putExtra("tipoCliente", tipoCliente);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
        builder.setNegativeButton("NO", (dialogInterface, i) -> {});
        builder.show();
    }
}
