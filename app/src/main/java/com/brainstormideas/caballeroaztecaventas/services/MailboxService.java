package com.brainstormideas.caballeroaztecaventas.services;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.brainstormideas.caballeroaztecaventas.data.models.Pedido;
import com.brainstormideas.caballeroaztecaventas.data.models.PedidoFolio;
import com.brainstormideas.caballeroaztecaventas.managers.PedidoManager;
import com.brainstormideas.caballeroaztecaventas.utils.InternetManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;
import java.util.Objects;

public class MailboxService extends Service {

    private PedidoManager pedidoManager;
    private InternetManager internetManager;

    DatabaseReference dbFoliosReferencia;
    DatabaseReference dbCotizacionReferencia;
    StorageReference mStorageRef;

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

        if (intent != null && intent.hasExtra("pedido")) {
            PedidoFolio pedido = intent.getParcelableExtra("pedido");

            pedidoManager.agregarPedido(pedido);
            System.out.println("PEDIDO AGREGADO A LA COLA: " + pedido.getFolio());
        }

        procesarPedidos();

        return START_STICKY;
    }

    private void procesarPedidos() {

        List<PedidoFolio> pedidos = pedidoManager.getListaPedidos();

        if (isInternetAvailable()) {

            for (PedidoFolio pedido : pedidos) {

                StorageReference subidaPdf = mStorageRef.child(pedido.getVendedor().getUsuario() + "/" + pedido.getTipo().toUpperCase().charAt(0) + "/" + pedido.getFolio() + "/CAPedido.pdf");
                StorageReference subidaExcel = mStorageRef.child(pedido.getVendedor().getUsuario() + "/" + pedido.getTipo().toUpperCase().charAt(0) + "/" + pedido.getFolio() + "/CAPedido.xls");

                Log.e(" ---------   REFERENCIA DEL STORAGE ES: -----------", pedido.getVendedor().getUsuario() + "/" + pedido.getTipo().toUpperCase().charAt(0) + "/" + pedido.getFolio() + "/CAPedido.pdf");

                if (pedido.getUriExcel() != null && pedido.getUriPdf() != null) {
                    subirArchivo(subidaPdf, Uri.parse(pedido.getUriPdf()));
                    subirArchivo(subidaExcel, Uri.parse(pedido.getUriExcel()));
                }

                try {
                    agregarFolioACliente(pedido);
                    pedidoManager.eliminarPedido(pedido);
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
        dbReferencia.push().setValue(pedidoFolio);
        Log.e("FOLIO AGREGADO A LA BASE DE DATOS", pedidoFolio.getFolio());
    }

    private boolean comprobarFolio(String folioRecibido) {

        DatabaseReference dbReferencia = getDatabaseReferenceByTipoPedido(Pedido.getTipo());

        dbReferencia.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        if (data.child("folio").getValue() != null) {
                            String folioActual = Objects.requireNonNull(data.child("folio").getValue()).toString();
                            if (folioActual.equals(folioRecibido)) {
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
        return false;
    }

    private void subirArchivo(StorageReference storageRef, Uri fileUri) {
        storageRef.putFile(fileUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Se ha subido el archivo exitosamente
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(), "Error al subir el archivo. Contacte a soporte.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private DatabaseReference getDatabaseReferenceByTipoPedido(String tipoPedido) {
        if (tipoPedido.equals("pedido")) {
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
        return internetManager.isInternetAvaible();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
