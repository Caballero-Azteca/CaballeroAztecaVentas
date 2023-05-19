package com.brainstormideas.caballeroaztecaventas.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.brainstormideas.caballeroaztecaventas.R;
import com.brainstormideas.caballeroaztecaventas.entidad.ItemProductoPedido;
import com.brainstormideas.caballeroaztecaventas.data.models.Cliente;
import com.brainstormideas.caballeroaztecaventas.data.models.Pedido;
import com.brainstormideas.caballeroaztecaventas.data.models.PedidoFolio;
import com.brainstormideas.caballeroaztecaventas.data.models.Vendedor;
import com.brainstormideas.caballeroaztecaventas.utils.filesTools.ImageConverter;
import com.brainstormideas.caballeroaztecaventas.utils.InternetManager;
import com.brainstormideas.caballeroaztecaventas.utils.filesTools.ReportsGenerator;
import com.brainstormideas.caballeroaztecaventas.utils.SessionManager;
import com.brainstormideas.caballeroaztecaventas.utils.filesTools.TemplatePDF;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itextpdf.text.DocumentException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Menu_final extends AppCompatActivity {

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
    DatabaseReference dbFoliosReferencia;
    DatabaseReference dbCotizacionReferencia;
    StorageReference mStorageRef;

    Button btnModificar;
    Button btnEnviar;
    Button pdfview_btn;

    Drawable imagen;

    TemplatePDF templatePDF;
    ReportsGenerator reporte;

    FirebaseUser user;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    String tipoCliente;

    String emailVendedor = Pedido.getVendedor().getEmail();
    String emailCliente = Pedido.getCliente().getEmail();

    String[] mails = {};

    DatabaseReference dbVendedoresReferencia;

    boolean existe = false;

    InternetManager internetManager;

    boolean candadoModificar;

    String totalConIVA;
    String totalSinIVA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_final);

        candadoModificar = getIntent().getExtras().getBoolean("candadoModificar", true);

        tipoCliente = getIntent().getExtras().get("tipoCliente").toString();
        sessionManager = new SessionManager(this);

        if(!sessionManager.isActiveVendedor()){
            emailVendedor = "";
        }

        if(!sessionManager.isActiveCliente()){
            emailCliente = "";
        }

        mails = new String[]{sessionManager.getAlmacenEmail(),sessionManager.getComprasEmail(),
                sessionManager.getPrimaryEmail(), sessionManager.getSecondaryEmail(), emailVendedor
                , emailCliente};

        solicitarPermisos();

        dbVendedoresReferencia = FirebaseDatabase.getInstance().getReference().child("Usuario");
        dbUsuariosReferencia = FirebaseDatabase.getInstance().getReference().child("Usuario");
        dbFoliosReferencia = FirebaseDatabase.getInstance().getReference().child("Folio");
        dbCotizacionReferencia = FirebaseDatabase.getInstance().getReference().child("Cotizacion");

        mStorageRef = FirebaseStorage.getInstance().getReference();

        comprobarFolio(Pedido.getFolio());

        vendedorActual = sessionManager.getUsuario();
        vendedorEmail = sessionManager.getEmail();

        etxObservaciones = findViewById(R.id.etxObservaciones);

        if(Pedido.getObservaciones()!=null){
            etxObservaciones.setText(Pedido.getObservaciones());
        }

        obtenerVendedor();

        imagen = ContextCompat.getDrawable(this, R.drawable.logo);

        tvFolio = findViewById(R.id.tvFolio);

        if (Pedido.getFolio() != null) {
            tvFolio.setText("FOLIO: " + Pedido.getFolio());
        }

        tvRazon = findViewById(R.id.tvRazon);

        if (Pedido.getFolio() != null) {
            tvRazon.setText(Pedido.getCliente().getRazon());
        }
        tvRuta = findViewById(R.id.tvRuta);
        tvRuta.setText(Pedido.getCliente().getRuta());
        tvTotal = findViewById(R.id.tvTotal);
        tvTotal.setText("TOTAL: " + Pedido.getTotal());
        rbFactura = findViewById(R.id.rbFactura);
        rbRemision = findViewById(R.id.rbRemision);

        if(Pedido.preciosConIVA){
            rbRemision.setChecked(true);
        }else {
            rbFactura.setChecked(true);
        }

        pdfview_btn = findViewById(R.id.pdfview_btn);
        pdfview_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    validadDocumento();
                    generarVistaPdf();
                } catch (DocumentException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
        btnModificar = findViewById(R.id.btnModificar);
        if(!candadoModificar){
            btnModificar.setEnabled(false);
        }
        btnEnviar = findViewById(R.id.btnEnviar);
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Menu_final.this);
                builder.setTitle("Enviar pedido");
                builder.setMessage("¿Seguro que desea realizar el pedido?\n\n" +
                                    "Se generará un folio para su pedido, además se guardara el PDF y el Excel en la nube.\n" +
                                    "Usted debera seleccionar una applicación de terceros para enviar esta infromación.");
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        enviarPedido();
                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });
        btnModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modificar();
            }
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

                            String id = data.child("id").getValue().toString();
                            String nombre = data.child("nombre").getValue().toString();
                            String usuario = data.child("usuario").getValue().toString();
                            String password = data.child("password").getValue().toString();
                            String numero = data.child("telefono").getValue().toString();
                            String email = data.child("email").getValue().toString();

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

            Vendedor vendedor = new Vendedor("0", "ADMINISTRADOR", vendedorActual,"admin", "3333333333", vendedorEmail);
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

        calcularPreciosTotalSinIVA();

        String observaciones = etxObservaciones.getText().toString().trim();
        Pedido.setObservaciones(observaciones);

        String precioTitulo = "PRECIO";
        String[] titulos;
        if(Pedido.getDocumento().equals("Factura")){
            precioTitulo = "PRECIO (SIN IVA)";
        }
        titulos = new String[]{"CANTIDAD", "CODIGO", "MARCA", "DESCRIPCION DEL ARTICULO", precioTitulo};

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
        templatePDF.createTable(titulos, listaDeProductosTexto());
        templatePDF.addParagraph("TOTAL: " + "$" + Pedido.getTotal());
        if (!observaciones.equals("")) {
            templatePDF.addParagraph("OBSERVACIONES: " + Pedido.getObservaciones().toUpperCase());
        } else {
            templatePDF.addParagraph("SIN OBSERVACIONES");
        }
        templatePDF.closeDocument();
        templatePDF.abrirVistaDePDF(candadoModificar);
    }

    public void generarPdf() throws DocumentException, IOException {

        calcularPreciosTotalSinIVA();

        String observaciones = etxObservaciones.getText().toString().trim();
        Pedido.setObservaciones(observaciones);

        String precioTitulo = "PRECIO";
        String[] titulos;
        if(Pedido.getDocumento().equals("Factura")){
            precioTitulo = "PRECIO (SIN IVA)";
        }
        titulos = new String[]{"CANTIDAD", "CODIGO", "MARCA", "DESCRIPCION DEL ARTICULO", precioTitulo};

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
        templatePDF.createTable(titulos, listaDeProductosTexto());
        templatePDF.addParagraph("TOTAL: " + "$" + Pedido.getTotal());
        if (!observaciones.equals("")) {
            templatePDF.addParagraph("OBSERVACIONES: " + Pedido.getObservaciones().toUpperCase());
        } else {
            templatePDF.addParagraph("SIN OBSERVACIONES");
        }
        templatePDF.closeDocument();

    }

    public void enviarPedido() {

        calcularPreciosTotalSinIVA();

        String observaciones = etxObservaciones.getText().toString().trim();
        Pedido.setObservaciones(observaciones);

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
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "No se pudo enviar el pedido.", Toast.LENGTH_LONG).show();
            return;
        }
        reporte.guardar();

        envioDeEmails();

    }

    private String generarCodigoVendedor() {

        int cantidadDeCifrasVendedor = Pedido.getVendedor().getUserName().length();
        String codigoVendedor = "";
        switch (cantidadDeCifrasVendedor) {
            case 1:
                codigoVendedor = "0" + Pedido.getVendedor().getUserName().toUpperCase();
                break;
            case 2:
                codigoVendedor = Pedido.getVendedor().getUserName().toUpperCase();
                break;
        }
        return codigoVendedor;
    }

    public void envioDeEmails(){

        ArrayList<Uri> uris = new ArrayList<>();
        Uri uriExcel = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", reporte.obtenerReporte());
        Uri uriPDF = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", templatePDF.obtenerReporte());

        uris.add(uriExcel);
        uris.add(uriPDF);

        String texto = "DISTRIBUIDORA FERRETERA CABALLERO AZTECA"
                      +"\n"
                      +"\n"
                      +"\n"
                      +"Datos del vendedor"
                      +"\n"
                      +"\n"
                      +"\n"
                      +"Vendedor:  " + Pedido.getVendedor().getNombre() + "\n\n"
                      +"Codigo:  " + generarCodigoVendedor() + "\n\n"
                      +"Email:  " + Pedido.getVendedor().getEmail() + "\n"
                      +"\n"
                      +"\n"
                      +"\n"
                      +"Datos del pedido"
                      +"\n"
                      +"\n"
                      +"\n"
                      +"Folio : " + Pedido.getFolio() + "\n\n"
                      +"Código de cliente: " + Pedido.getCliente().getId() + "\n\n"
                      +"Cliente: " + Pedido.getCliente().getRazon() + "\n\n"
                      +"Factura o registro: " + Pedido.getDocumento() + "\n\n"
                      +"Ruta: " + Pedido.getCliente().getRuta() + "\n\n"
                      +"Monto del pedido: " + " $" + Pedido.getTotal() + "\n"
                      +"\n"
                      +"\n"
                      +"\n"
                      +"Fecha y hora de captura: \n" + reporte.getFecha() + "\n"
                      +"\n"
                      +"\n"
                      +"Visite: www.caballeroazteca.com.mx\n"
                      +"\n"
                      +"\n"
                      +"Teléfonos: 3338237632 ó 3338543697.";

        String subject = "Pedido con folio: " + Pedido.getFolio();


        StorageReference subidaPdf = mStorageRef.child(Pedido.getVendedor().getUserName()+ "/" + Pedido.getTipo().toUpperCase().charAt(0) + "/" + Pedido.getFolio() + "/CAPedido.pdf");
        StorageReference subidaExcel = mStorageRef.child(Pedido.getVendedor().getUserName()+ "/" + Pedido.getTipo().toUpperCase().charAt(0) + "/" + Pedido.getFolio() + "/CAPedido.xls");

        Log.e(" ---------   REFERENCIA DEL STORAGE ES: -----------", Pedido.getVendedor().getUserName()+ "/" + Pedido.getTipo().toUpperCase().charAt(0) + "/" + Pedido.getFolio() + "/CAPedido.pdf");

        if(comprobarInternet()){
            subidaPdf.putFile(uriPDF)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(getApplicationContext(), "Error al subir el archivo. Contacte a soporte.", Toast.LENGTH_SHORT).show();
                        }
                    });

            subidaExcel.putFile(uriExcel)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(getApplicationContext(), "Error al subir el archivo. Contacte a soporte.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "Internet no disponible. No se cargaran los folios", Toast.LENGTH_LONG).show();
        }

        try {

            if(!existe){
                agregarFolioACliente();
            }

            Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            emailIntent.putExtra(Intent.EXTRA_EMAIL, mails);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            emailIntent.putExtra(Intent.EXTRA_TEXT, texto);
            emailIntent.putExtra(Intent.EXTRA_STREAM, uris);
            emailIntent.setType("text/plain");
            Intent chooser = Intent.createChooser(emailIntent, "Seleccione la aplicacion para enviar su correo:");
            startActivityForResult(chooser, REQUEST_CODE);
        } catch (Exception e){
            Toast.makeText(getApplicationContext(), "No se ha enviado el correo.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    public void modificar() {
        Intent i = new Intent(getApplicationContext(), Menu_pedidos.class);
        calcularPreciosTotalConIVA();
        i.putExtra("tipoCliente", "cliente");
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    public void pantallaFinal(){
        Intent i = new Intent(getApplicationContext(), Pantalla_pedido_finalizado.class);
        i.putExtra("tipoCliente", "cliente");
        i.putExtra("candadoModificar", false);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){
            Toast.makeText( getApplicationContext(),"Pedido enviado exitosamente.", Toast.LENGTH_LONG).show();
            pantallaFinal();
        } else if (resultCode == RESULT_CANCELED){
            Toast.makeText( getApplicationContext(),"Ha cancelado la operacion.", Toast.LENGTH_LONG).show();
        } else if (resultCode == RESULT_OK){
            Toast.makeText( getApplicationContext(),"Accion completada", Toast.LENGTH_LONG).show();
        }
    }

    public ArrayList<String[]> listaDeProductosTexto() {
        ArrayList<String[]> rows = new ArrayList<>();
        for (ItemProductoPedido item : Pedido.getListaDeProductos()) {
            String[] cadena = new String[5];
            cadena[0] = item.getCantidad();
            cadena[1] = item.getId();
            cadena[2] = item.getMarca();
            cadena[3] = item.getNombre();
            cadena[4] = item.getPrecio();

            rows.add(cadena);
        }
        return rows;
    }

    public List<ItemProductoPedido> listaDeProductosTextoFolio() {
        List<ItemProductoPedido> products = new ArrayList<>();
        for (ItemProductoPedido item : Pedido.getListaDeProductos()) {
            String cantidad = item.getCantidad();
            String id = item.getId();
            String marca = item.getMarca();
            String nombre = item.getNombre();
            String precio = item.getPrecio();

            ItemProductoPedido itemProducto = new ItemProductoPedido(id, nombre, marca, cantidad, precio, null, null);
            products.add(itemProducto);

        }
        return products;
    }

    public void agregarFolioACliente(){

        if(vendedorActual.equals("admin")){
            Toast.makeText( getApplicationContext(),"No se generan folio con la cuenta de administrador.", Toast.LENGTH_LONG).show();
            return;
        }


        String domicilio = Pedido.getCliente().getCalle() + " " +
                "#" + Pedido.getCliente().getNumeroExterior() + "INT: " + Pedido.getCliente().getNumeroInterior() +
                " COLONIA " + Pedido.getCliente().getColonia() + " C.P. " + Pedido.getCliente().getCp();

        String historial = "Abierto por: " + Pedido.getVendedor().getNombre();

        PedidoFolio pedidoFolio = new PedidoFolio(Pedido.getFolio(), Pedido.getTipo(), Pedido.getVendedor().getNombre(), Pedido.getCliente().getRuta(),
                Pedido.getDocumento(), Pedido.getCliente().getId(), Pedido.getCliente().getRazon(), Pedido.getCliente().getRfc(),
                domicilio, Pedido.getCliente().getMunicipio(), Pedido.getCliente().getEstado(), Pedido.getCliente().getTelefono(),
                Pedido.getCliente().getEmail(), Pedido.getTotal(), Pedido.getObservaciones(), listaDeProductosTextoFolio(),
                "noautorizado", "0000", "0","SIN REPARTIDOR",
                new SimpleDateFormat("dd-MM-yyyy").format(new Date()),
                totalSinIVA, totalConIVA, historial);

        user = mAuth.getCurrentUser();
        assert user != null;

        if(comprobarInternet()){

            if(Pedido.getTipo().equals("pedido")){
                dbFoliosReferencia.push().setValue(pedidoFolio);
            } else if(Pedido.getTipo().equals("cotizacion")){
                dbCotizacionReferencia.push().setValue(pedidoFolio);
            }

            Log.e("ENLACE GENERADO", pedidoFolio.getFolio());
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(Menu_final.this);
            builder.setCancelable(false);
            builder.setTitle("AVISO IMPORTANTE");
            builder.setMessage("En estos momentos usted no cuenta con conexión a internet. Si continua el proceso el email será enviado cuando se reconecte.\nPero es posible que el folio no sea registrado correctamente.\n Para corregir esta situación mantega la aplicación abierta y reconectese a internet o intente realizar el pedido nuevamente más tarde.");
            builder.setNeutralButton("Entendido", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.show();
        }
    }

    public void calcularPreciosTotalSinIVA(){

        validadDocumento();

        if(Pedido.preciosConIVA) {
            DecimalFormat df = new DecimalFormat("#.00");
            if (Pedido.getDocumento().equals("Factura")) {
                for (int i = 0; i < Pedido.getListaDeProductos().size(); i++) {
                    double nuevoPrecio = Double.parseDouble(Pedido.getListaDeProductos().get(i).getPrecio().replace(",", ".")) / 1.16;
                    Pedido.getListaDeProductos().get(i).setPrecio(df.format(nuevoPrecio));
                }
                Pedido.preciosConIVA = false;
            }
        } else if(Pedido.getDocumento().equals("Remision")){
            calcularPreciosTotalConIVA();
        }
        totalSinIVA = String.valueOf(calcularTotal());
        totalConIVA = String.valueOf(Double.parseDouble(calcularTotal())*1.16);
        Pedido.setTotal(calcularTotal());

    }

    public void calcularPreciosTotalConIVA(){

        if(!Pedido.preciosConIVA) {
            DecimalFormat df = new DecimalFormat("#.00");
            for (int i = 0; i < Pedido.getListaDeProductos().size(); i++) {
                double nuevoPrecio = Double.parseDouble(Pedido.getListaDeProductos().get(i).getPrecio().replace(",", ".")) * 1.16;
                Pedido.getListaDeProductos().get(i).setPrecio(df.format(nuevoPrecio));
            }
            totalSinIVA = String.valueOf(Double.parseDouble(calcularTotal())/1.16);
            totalConIVA = String.valueOf(calcularTotal());
            Pedido.preciosConIVA = true;
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

    @Override
    public void onBackPressed() {
        if(candadoModificar){
            back();
        }

    }

    private void back() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alerta");
        builder.setMessage("¿Desea modificar su pedido?");
        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Pedido.setObservaciones(null);
                calcularPreciosTotalConIVA();
                Intent intent = new Intent(getApplicationContext(), Menu_pedidos.class);
                intent.putExtra("tipoCliente", tipoCliente);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();

    }

    private void comprobarFolio(String folioRecibido) {

        if(Pedido.getTipo().equals("pedido")){
            dbFoliosReferencia.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            if(data.child("folio").getValue()!=null){
                                String folioActual = data.child("folio").getValue().toString();
                                if(folioActual.equals(folioRecibido)){
                                    existe = true;
                                    return;
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else if (Pedido.getTipo().equals("cotizacion")){
            dbCotizacionReferencia.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            if(data.child("folio").getValue()!=null){
                                String folioActual = data.child("folio").getValue().toString();
                                if(folioActual.equals(folioRecibido)){
                                    existe = true;
                                    return;
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

    public boolean comprobarInternet(){
        internetManager = new InternetManager(this);
        return  internetManager.isInternetAvaible();
    }

    public void solicitarPermisos() {

        int permisoSMS = ActivityCompat.checkSelfPermission(Menu_final.this, Manifest.permission.INTERNET);
        int permisoLocation = ActivityCompat.checkSelfPermission(Menu_final.this, Manifest.permission.SEND_SMS);

        if (permisoSMS != PackageManager.PERMISSION_GRANTED ||
                permisoLocation != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE}, RESQUEST_ASK_CODE_PERMISSION);
            }
        }
    }
}
