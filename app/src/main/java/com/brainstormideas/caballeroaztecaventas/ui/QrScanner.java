package com.brainstormideas.caballeroaztecaventas.ui;

import static com.brainstormideas.caballeroaztecaventas.ui.MainActivity.isInitialized;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.brainstormideas.caballeroaztecaventas.R;
import com.brainstormideas.caballeroaztecaventas.data.models.Cliente;
import com.brainstormideas.caballeroaztecaventas.data.models.Pedido;
import com.brainstormideas.caballeroaztecaventas.utils.Tools;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.Objects;

public class QrScanner extends AppCompatActivity {

    private Button volver;
    private CameraSource cameraSource;
    private SurfaceView cameraView;
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private String token = "";
    private String tokenanterior = "";
    DatabaseReference dbClientesReferencia;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);

        dbClientesReferencia = FirebaseDatabase.getInstance().getReference().child("Cliente");
        progressDialog = new ProgressDialog(this);

        volver = findViewById(R.id.volver);
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volver();
            }
        });
        cameraView = findViewById(R.id.surface_camera);
        initQR();
    }

    public void initQR() {

        BarcodeDetector barcodeDetector =
                new BarcodeDetector.Builder(this)
                        .setBarcodeFormats(Barcode.ALL_FORMATS)
                        .build();

        cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1600, 1024)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                if (ActivityCompat.checkSelfPermission(QrScanner.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        if (shouldShowRequestPermissionRationale(
                                Manifest.permission.CAMERA)) ;
                        requestPermissions(new String[]{Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_CAMERA);
                    }
                } else {
                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException ie) {
                        Log.e("CAMERA SOURCE", ie.getMessage());
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }


            @Override
            public void receiveDetections(@NonNull Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() > 0) {

                    token = barcodes.valueAt(0).displayValue;

                    if (!token.equals(tokenanterior)) {

                        tokenanterior = token;
                        Log.i("token", token);

                        if (token != null) {
                            abrirPedido();
                        }

                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    synchronized (this) {
                                        wait(5000);
                                        // limpiamos el token
                                        tokenanterior = "";
                                    }
                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    Log.e("Error", "Waiting didnt work!!");
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                }
            }
        });

    }

    public void volver() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    private void abrirPedido() {

        String codigoCliente = token;

        if (Tools.isNumeric(codigoCliente)) {

            int codigoNumerico = Integer.parseInt(codigoCliente);

            Query query = dbClientesReferencia.orderByChild("id").equalTo(codigoNumerico);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot data : snapshot.getChildren()) {

                        progressDialog.setMessage("Cargando informacion del cliente...");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();

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
                            Intent i = new Intent(getApplicationContext(), MenuPedidos.class);
                            i.putExtra("seleccionable", false);
                            i.putExtra("tipoCliente", "clienteEscaneado");
                            startActivity(i);

                            progressDialog.dismiss();

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

                        progressDialog.setMessage("Cargando informacion del cliente...");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();

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
                            Intent i = new Intent(getApplicationContext(), MenuPedidos.class);
                            i.putExtra("seleccionable", false);
                            i.putExtra("tipoCliente", "clienteEscaneado");
                            startActivity(i);

                            progressDialog.dismiss();

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
    }

    public void refresh() {
        Intent intent = new Intent(QrScanner.this, QrScanner.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}