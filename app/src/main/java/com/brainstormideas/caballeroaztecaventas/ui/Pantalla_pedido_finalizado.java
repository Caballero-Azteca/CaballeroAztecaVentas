package com.brainstormideas.caballeroaztecaventas.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.brainstormideas.caballeroaztecaventas.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.brainstormideas.caballeroaztecaventas.ui.Verificador_precio.isInitialized;

public class Pantalla_pedido_finalizado extends AppCompatActivity {

    Button finalizar_btn;
    Button otra_vez_btn;

    String tipoCliente;

    DatabaseReference dbUsuariosReferencia;
    FirebaseUser user;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_pedido_finalizado);

        initializedFirebaseService();

        tipoCliente = getIntent().getExtras().get("tipoCliente").toString();

        dbUsuariosReferencia = FirebaseDatabase.getInstance().getReference().child("Usuario");

        Toast.makeText(getApplicationContext(), "Proceso ejecutado.", Toast.LENGTH_LONG).show();

        finalizar_btn = findViewById(R.id.finalizar_btn);
        otra_vez_btn = findViewById(R.id.otra_vez_btn);

        finalizar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                home();
            }
        });
        otra_vez_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atras();
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

    private void atras() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ir atras.");
        builder.setMessage("¿Seguro que desea realizar nuevamente el pedido o quizás algún cambio?");
        builder.setPositiveButton("Atras", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(getApplicationContext(), Menu_final.class);
                intent.putExtra("tipoCliente", "cliente");
                intent.putExtra("candadoModificar", getIntent().getExtras().getBoolean("candadoModificar", false));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
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

    private void home() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ir al menú principal");
        builder.setMessage("¿Seguro que desea ir al menú principal? (Su pedido se cerrara).");
        builder.setPositiveButton("Ir al menú principal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
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


    @Override
    public void onBackPressed() {
        home();
    }

}
