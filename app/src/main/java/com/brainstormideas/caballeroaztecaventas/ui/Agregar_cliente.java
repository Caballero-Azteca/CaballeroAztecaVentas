package com.brainstormideas.caballeroaztecaventas.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.brainstormideas.caballeroaztecaventas.R;
import com.brainstormideas.caballeroaztecaventas.data.models.Cliente;
import com.brainstormideas.caballeroaztecaventas.data.models.Pedido;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Agregar_cliente extends AppCompatActivity {

    EditText r_razon_txt;
    EditText r_rfc_txt;
    EditText r_municipio_txt;
    EditText r_estado_txt;
    EditText r_ruta_txt;
    EditText r_calle_txt;
    EditText r_colonia_txt;
    EditText r_numeroExterior_txt;
    EditText r_numeroInterior_txt;
    EditText r_cp_txt;
    EditText r_telefono_txt;
    EditText r_correo_txt;


    ImageButton home_button;
    Button abrir_pedido;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_cliente);

        progressDialog = new ProgressDialog(this);

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
        r_ruta_txt = findViewById(R.id.r_ruta_txt);

        home_button = findViewById(R.id.home_button);
        home_button.setOnClickListener(view -> home());
        abrir_pedido = findViewById(R.id.abrir_pedido);
        abrir_pedido.setOnClickListener(view -> abrirPedido());

    }

    private void abrirPedido() {

        String razon = r_razon_txt.getText().toString().trim().toUpperCase();
        String rfc = r_rfc_txt.getText().toString().trim().toUpperCase();
        String municipio = r_municipio_txt.getText().toString().trim().toUpperCase();
        String estado = r_estado_txt.getText().toString().trim().toUpperCase();
        String calle = r_calle_txt.getText().toString().trim().toUpperCase();
        String colonia = r_colonia_txt.getText().toString().trim().toUpperCase();
        String numeroExterior = r_numeroExterior_txt.getText().toString().trim();
        String numeroInterior = r_numeroInterior_txt.getText().toString().trim();
        String cp = r_cp_txt.getText().toString().trim();
        String telefono = r_telefono_txt.getText().toString().trim();
        String correo = r_correo_txt.getText().toString().trim();
        String ruta = r_ruta_txt.getText().toString().trim().toUpperCase();


        if (!razon.equals("") && !rfc.equals("") && !municipio.equals("") && !calle.equals("")
                && !cp.equals("") && !numeroExterior.equals("") && !numeroInterior.equals("")
                && !telefono.equals("") && !colonia.equals("") && !correo.equals("")
                && !estado.equals("") && !ruta.equals("")) {

            progressDialog.setMessage("Registrando nuevo cliente...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            Cliente cliente = new Cliente();
            cliente.setCode("EXPRESS");
            cliente.setRazon(razon);
            cliente.setRfc(rfc);
            cliente.setMunicipio(municipio);
            cliente.setEstado(estado);
            cliente.setCalle(calle);
            cliente.setColonia(colonia);
            cliente.setRuta(ruta);
            cliente.setNumeroExterior(numeroExterior);
            cliente.setNumeroInterior(numeroInterior);
            cliente.setCp(cp);
            cliente.setTelefono(telefono);
            cliente.setEmail(correo);

            Pedido.setCliente(cliente);

            progressDialog.dismiss();
            limpiarCampos();

            Intent i = new Intent(this, Menu_pedidos.class);
            i.putExtra("tipoCliente", "clienteExpress");
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);

        } else {
            Toast.makeText(getApplicationContext(), "Debe rellenar todos los datos solicitados.", Toast.LENGTH_SHORT).show();
        }
    }

    private void home() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private void limpiarCampos() {
        r_razon_txt.setText("");
        r_rfc_txt.setText("");
        r_municipio_txt.setText("");
        r_calle_txt.setText("");
        r_numeroExterior_txt.setText("");
        r_cp_txt.setText("");
        r_colonia_txt.setText("");
        r_estado_txt.setText("");
        r_correo_txt.setText("");
        r_telefono_txt.setText("");
        r_ruta_txt.setText("");
    }
}
