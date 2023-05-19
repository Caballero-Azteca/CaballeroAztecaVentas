package com.brainstormideas.caballeroaztecaventas.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.brainstormideas.caballeroaztecaventas.R;
import com.brainstormideas.caballeroaztecaventas.utils.SessionManager;

public class MailManager extends AppCompatActivity {

    EditText almacenMail_etx;
    EditText comprasMail_etx;
    EditText primaryMail_etx;
    EditText secondaryMail_etx;

    CheckBox cliente_cb;
    CheckBox vendedor_cb;


    Button aceptar_btn;
    Button exit_btn;

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_manager);

        getSupportActionBar().setTitle("Administrador de correos");

        sessionManager = new SessionManager(this);

        almacenMail_etx = findViewById(R.id.almacenMail_etx);
        comprasMail_etx = findViewById(R.id.comprasMail_etx);
        primaryMail_etx = findViewById(R.id.primaryMail_etx);
        secondaryMail_etx = findViewById(R.id.secondaryMail_etx);

        cliente_cb = findViewById(R.id.cliente_cb);
        vendedor_cb = findViewById(R.id.vendedor_cb);

        exit_btn = findViewById(R.id.exit_btn);
        exit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salir();
            }
        });
        aceptar_btn = findViewById(R.id.aceptar_btn);
        aceptar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarCorreos();
            }
        });

        obtenerCorreosActuales();

    }

    public void actualizarCorreos(){

        String almacen = almacenMail_etx.getText().toString().trim();
        String compras = comprasMail_etx.getText().toString().trim();
        String primary = primaryMail_etx.getText().toString().trim();
        String secondary = secondaryMail_etx.getText().toString().trim();

        sessionManager.setAlmacenEmail(almacen);
        sessionManager.setComprasMail(compras);
        sessionManager.setPrimaryEmail(primary);
        sessionManager.setSecondaryEmail(secondary);

        sessionManager.setActiveClienteMail(cliente_cb.isChecked());

        sessionManager.setActiveVendedorMail(vendedor_cb.isChecked());

        Toast.makeText(getApplicationContext(), "Correos actualizados correctamente", Toast.LENGTH_SHORT).show();

    }

    private void obtenerCorreosActuales(){

        String almacen = sessionManager.getAlmacenEmail();
        String compras = sessionManager.getComprasEmail();
        String primary = sessionManager.getPrimaryEmail();
        String secondary = sessionManager.getSecondaryEmail();

        almacenMail_etx.setText(almacen);
        comprasMail_etx.setText(compras);
        primaryMail_etx.setText(primary);
        secondaryMail_etx.setText(secondary);

        cliente_cb.setChecked(sessionManager.isActiveCliente());

        vendedor_cb.setChecked(sessionManager.isActiveVendedor());

    }

    private void salir(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Atención");
        builder.setMessage("¿Desea regresar al menu principal?");
        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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
}