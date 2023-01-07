package com.brainstormideas.caballeroaztecaventas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.brainstormideas.caballeroaztecaventas.adaptadores.ControllerRecyclerViewAdapter;
import com.brainstormideas.caballeroaztecaventas.models.Pedido;
import com.brainstormideas.caballeroaztecaventas.tools.TextTools;
import com.brainstormideas.caballeroaztecaventas.tools.Tools;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.brainstormideas.caballeroaztecaventas.Verificador_precio.isInitialized;

public class EditarCliente extends AppCompatActivity {

    EditText r_codigo_txt;
    EditText r_razon_txt;
    EditText r_rfc_txt;
    EditText r_municipio_txt;
    EditText r_estado_txt;
    EditText r_calle_txt;
    EditText r_colonia_txt;
    EditText r_numeroExterior_txt;
    EditText r_numeroInterior_txt;
    EditText r_cp_txt;
    EditText r_telefono_txt;
    EditText r_correo_txt;

    ImageButton home_button;
    Button guardar;

    DatabaseReference dbClientesReferencia;
    private ProgressDialog progressDialog;
    String fixedDirection;
    String direccion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_cliente);

        initializedFirebaseService();

        dbClientesReferencia = FirebaseDatabase.getInstance().getReference().child("Cliente");
        r_codigo_txt = findViewById(R.id.r_codigo_txt);
        r_razon_txt = findViewById(R.id.r_razon_txt);
        r_rfc_txt = findViewById(R.id.r_rfc_txt);
        r_municipio_txt = findViewById(R.id.r_municipio_txt);
        r_estado_txt = findViewById(R.id.r_estado_txt);
        r_calle_txt = findViewById(R.id.r_calle_txt);
        r_colonia_txt = findViewById(R.id.r_colonia_txt);
        r_numeroExterior_txt = findViewById(R.id.r_numeroExterior_txt);
        r_numeroInterior_txt = findViewById(R.id.r_numeroInterior_txt);
        r_cp_txt = findViewById(R.id.r_cp_txt);
        r_telefono_txt = findViewById(R.id.r_telefono_txt);
        r_correo_txt = findViewById(R.id.r_correo_txt);
        cargarCliente();
        home_button = findViewById(R.id.home_button);
        home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                home();
            }
        });
        guardar = findViewById(R.id.guardar);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarCambios();
            }
        });

        obtenerCliente();
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

    private void home() {
        Intent i = new Intent(getApplicationContext(), Lista_clientes.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
    }

    private void cargarCliente() {

        String id = ControllerRecyclerViewAdapter.itemSeleccionado.getId();
        String razon = Pedido.getCliente().getRazon();
        String rfc = Pedido.getCliente().getRfc();
        String municipio = Pedido.getCliente().getMunicipio();
        String estado = Pedido.getCliente().getEstado();
        String calle = Pedido.getCliente().getCalle();
        String colonia = Pedido.getCliente().getColonia();
        String numeroExterior = Pedido.getCliente().getNumeroExterior();
        String numeroInterior = Pedido.getCliente().getNumeroInterior();
        String cp = Pedido.getCliente().getCp();
        String telefono = Pedido.getCliente().getTelefono();
        String correo = Pedido.getCliente().getEmail();

        r_codigo_txt.setText(id);
        r_razon_txt.setText(razon);
        r_rfc_txt.setText(rfc);
        r_municipio_txt.setText(municipio);
        r_estado_txt.setText(estado);
        r_calle_txt.setText(calle);
        r_colonia_txt.setText(colonia);
        r_numeroExterior_txt.setText(numeroExterior);
        r_numeroInterior_txt.setText(numeroInterior);
        r_cp_txt.setText(cp);
        r_telefono_txt.setText(telefono);
        r_correo_txt.setText(correo);


    }

    private void guardarCambios() {

        String id = r_codigo_txt.getText().toString().trim();
        String razon = r_razon_txt.getText().toString().trim();
        String rfc = r_rfc_txt.getText().toString().trim();
        String municipio = r_municipio_txt.getText().toString().trim();
        String estado = r_estado_txt.getText().toString().trim();
        String calle = r_calle_txt.getText().toString().trim();
        String colonia = r_colonia_txt.getText().toString().trim();
        String numeroExterior = r_numeroExterior_txt.getText().toString().trim();
        String numeroInterior = r_numeroInterior_txt.getText().toString().trim();
        String cp = r_cp_txt.getText().toString().trim();
        String telefono = r_telefono_txt.getText().toString().trim();
        String correo = r_correo_txt.getText().toString().trim();

        if (!id.equals("") && !razon.equals("") && !rfc.equals("") &&
                !numeroExterior.equals("")) {

            Map<String, Object> cliente = new HashMap<>();
            cliente.put("id", id);
            cliente.put("razon", razon);
            cliente.put("rfc", rfc);
            cliente.put("municipio", municipio);
            cliente.put("estado", estado);
            cliente.put("calle", calle);
            cliente.put("colonia", colonia);
            cliente.put("numeroExterior", numeroExterior);
            cliente.put("numeroInterior", numeroInterior);
            cliente.put("cp", cp);
            cliente.put("telefono", telefono);
            cliente.put("correo", correo);

            dbClientesReferencia.child(direccion).updateChildren(cliente);
            Toast.makeText(getApplicationContext(), "Se han guardado sus cambios exitosamente", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(getApplicationContext(), "Debe proporcionar todos los datos necesarios.", Toast.LENGTH_LONG).show();
        }

    }

    public void obtenerCliente() {


        String codigoCliente = ControllerRecyclerViewAdapter.itemSeleccionado.getId();

        if (Tools.isNumeric(codigoCliente)) {

            int codigoNumerico = Integer.parseInt(codigoCliente);

            Query query = dbClientesReferencia.orderByChild("id").equalTo(codigoNumerico);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    direccion = snapshot.getKey();
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
                    direccion = snapshot.getKey();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }

}