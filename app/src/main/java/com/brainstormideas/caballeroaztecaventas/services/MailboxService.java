package com.brainstormideas.caballeroaztecaventas.services;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.brainstormideas.caballeroaztecaventas.data.models.PedidoFolio;
import com.brainstormideas.caballeroaztecaventas.managers.PedidoManager;
import com.brainstormideas.caballeroaztecaventas.utils.InternetManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.Set;

public class MailboxService extends Service {

    private PedidoManager pedidoManager;
    private InternetManager internetManager;

    DatabaseReference dbFoliosReferencia;
    DatabaseReference dbCotizacionReferencia;
    StorageReference mStorageRef;

    StorageReference subidaPdf;
    StorageReference subidaExcel;

    private static final String PEDIDO = "pedido";

    @Override
    public void onCreate() {
        super.onCreate();
        dbFoliosReferencia = FirebaseDatabase.getInstance().getReference().child("Folio");
        dbCotizacionReferencia = FirebaseDatabase.getInstance().getReference().child("Cotizacion");
        mStorageRef = FirebaseStorage.getInstance().getReference();
        pedidoManager = PedidoManager.getInstance(this);
        internetManager = new InternetManager(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        System.out.println("SERVICIO DE MAILBOX INICIADO...");

        if (intent != null && intent.hasExtra(PEDIDO)) {

            PedidoFolio pedido = intent.getParcelableExtra(PEDIDO);
            pedidoManager.agregarPedido(pedido);
            System.out.println("PEDIDO AGREGADO A LA COLA: " + pedido.getFolio());

        }

        procesarPedidos();

        return START_STICKY;
    }

    private void procesarPedidos() {

        Set<PedidoFolio> pedidos = pedidoManager.getListaPedidos();

        if (isInternetAvailable()) {

            for (PedidoFolio pedido : pedidos) {

                subidaPdf = mStorageRef.child(pedido.getVendedor().getUsuario() + "/" + pedido.getTipo().toUpperCase().charAt(0) + "/" + pedido.getFolio() + "/CAPedido.pdf");
                subidaExcel = mStorageRef.child(pedido.getVendedor().getUsuario() + "/" + pedido.getTipo().toUpperCase().charAt(0) + "/" + pedido.getFolio() + "/CAPedido.xls");
                Log.e(" ---------   REFERENCIA DEL STORAGE ES: -----------", pedido.getVendedor().getUsuario() + "/" + pedido.getTipo().toUpperCase().charAt(0) + "/" + pedido.getFolio() + "/CAPedido.pdf");

                try {
                    agregarFolioACliente(pedido);
                    System.out.println("PEDIDO PROCESADO: " + pedido.getFolio());
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("ERROR Agregando folio", e.toString());
                }
            }
        }
    }

    public void agregarFolioACliente(PedidoFolio pedidoFolio) {

        DatabaseReference dbReferencia = getDatabaseReferenceByTipoPedido(pedidoFolio.getTipo());

        dbReferencia.orderByChild("folio").equalTo(pedidoFolio.getFolio())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Log.i("Duplicado", "El folio ya existe en la base de datos.");
                            pedidoManager.eliminarPedido(pedidoFolio);
                        } else {
                            dbReferencia.push().setValue(pedidoFolio).addOnSuccessListener(unused -> {
                                pedidoManager.eliminarPedido(pedidoFolio);
                                if (pedidoFolio.getUriExcel() != null && pedidoFolio.getUriPdf() != null) {
                                    subirArchivo(subidaPdf, Uri.parse(pedidoFolio.getUriPdf()));
                                    subirArchivo(subidaExcel, Uri.parse(pedidoFolio.getUriExcel()));
                                }
                                Log.i("EXITO", "Pedido: " + pedidoFolio.getFolio() + " enviado con exito.");
                            }).addOnFailureListener(e -> Log.i("EXITO", "Error al enviar: " + pedidoFolio.getFolio() + "Error: " + e));
                            Log.e("FOLIO AGREGADO A LA BASE DE DATOS", pedidoFolio.getFolio());
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("Error", "Error al verificar duplicados: " + databaseError.getMessage());
                    }
                });
    }


    private void subirArchivo(StorageReference storageRef, Uri fileUri) {
        storageRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                })
                .addOnFailureListener(exception ->
                        Toast.makeText(getApplicationContext(), "Error al subir el archivo. Contacte a soporte.", Toast.LENGTH_SHORT).show());
    }

    private DatabaseReference getDatabaseReferenceByTipoPedido(String tipoPedido) {
        if (tipoPedido.equals(PEDIDO)) {
            return dbFoliosReferencia;
        } else if (tipoPedido.equals("cotizacion")) {
            return dbCotizacionReferencia;
        } else {
            throw new IllegalArgumentException("Tipo de pedido no válido: " + tipoPedido);
        }
    }

    @Override
    public void onDestroy() {
        System.out.println("DESTRUYENDO EL SERVICIO DE MAILBOX");
        super.onDestroy();
    }

    private boolean isInternetAvailable() {
        return internetManager.isInternetAvailable();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
