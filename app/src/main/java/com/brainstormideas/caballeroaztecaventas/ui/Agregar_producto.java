package com.brainstormideas.caballeroaztecaventas.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.brainstormideas.caballeroaztecaventas.R;
import com.brainstormideas.caballeroaztecaventas.data.models.Producto;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static com.brainstormideas.caballeroaztecaventas.ui.Verificador_precio.isInitialized;

public class Agregar_producto extends AppCompatActivity {

    private ImageButton home_button;
    private Button agregar_producto;

    private EditText codigo_txt;
    private EditText producto_txt;
    private EditText marca_txt;
    private EditText precioLista_txt;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_producto);

        getSupportActionBar().setTitle("AGREGAR PRODUCTO");
        initializedFirebaseService();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        agregar_producto = findViewById(R.id.agregar_producto_btn);
        codigo_txt = findViewById(R.id.codigo_txt);
        marca_txt = findViewById(R.id.marca_txt);
        producto_txt = findViewById(R.id.producto_txt);
        precioLista_txt = findViewById(R.id.precioLista_txt);

        agregar_producto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregar_producto();
            }
        });

        home_button = findViewById(R.id.home_button);
        home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                home();
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

    private void agregar_producto() {

        final String codigo = codigo_txt.getText().toString().trim();
        final String producto = producto_txt.getText().toString().trim();
        final String marca = marca_txt.getText().toString().trim();
        final String precioLista = precioLista_txt.getText().toString().trim();

        if (codigo.isEmpty()) {
            Toast.makeText(getApplicationContext(), "No ha ingresado un producto.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (marca.isEmpty()) {
            Toast.makeText(getApplicationContext(), "No ha ingresado una marca.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (producto.isEmpty()) {
            Toast.makeText(getApplicationContext(), "No ha ingresado una descripcion.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (precioLista.isEmpty()) {
            Toast.makeText(getApplicationContext(), "No ha ingresado una precio.", Toast.LENGTH_SHORT).show();
            return;
        }


        progressDialog.setMessage("Registrando nuevo producto...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        float lista = Float.parseFloat(precioLista);

        Producto nuevoProducto = new Producto();
        nuevoProducto.setId(codigo);
        nuevoProducto.setNombre(producto);
        nuevoProducto.setMarca(marca);
        nuevoProducto.setCca(precios(lista).get(0));
        nuevoProducto.setP4(precios(lista).get(1));
        nuevoProducto.setP3(precios(lista).get(2));
        nuevoProducto.setP2(precios(lista).get(3));
        nuevoProducto.setP1(precios(lista).get(4));
        nuevoProducto.setLista(lista);

        DatabaseReference productos = databaseReference.child("Producto");
        productos.push().setValue(nuevoProducto);

        progressDialog.dismiss();

        Toast.makeText(getApplicationContext(), "Producto registrado correctamente.", Toast.LENGTH_LONG).show();

        limpiarCampos();

    }

    private void home() {
        Intent i = new Intent(getApplicationContext(), Verificador_precio.class);
        startActivity(i);
    }

    private ArrayList<Float> precios(float lista) {

        ArrayList<Float> precios = new ArrayList<>();
        float cca = lista * (1 - 1 * 0.1F);
        float p4 = lista * (1 - 1 * 0.08F);
        float p3 = lista * (1 - 1 * 0.06F);
        float p2 = lista * (1 - 1 * 0.04F);
        float p1 = lista * (1 - 1 * 0.02F);
        precios.add(cca);
        precios.add(p4);
        precios.add(p3);
        precios.add(p2);
        precios.add(p1);
        return precios;

    }

    private void limpiarCampos() {
        codigo_txt.setText("");
        producto_txt.setText("");
        marca_txt.setText("");
        precioLista_txt.setText("");
    }
}