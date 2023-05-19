package com.brainstormideas.caballeroaztecaventas.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.brainstormideas.caballeroaztecaventas.R;
import com.brainstormideas.caballeroaztecaventas.data.models.Pedido;
import com.brainstormideas.caballeroaztecaventas.utils.InternetManager;
import com.brainstormideas.caballeroaztecaventas.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button pedido_btn;
    Button cotizacion_btn;

    Button verificador_precio;
    Button exit_button;

    FloatingActionButton mostrar_usuarios;
    SessionManager session;
    FirebaseAuth mAuth;
    FirebaseUser user;

    TextView usuarioActual_txt;
    TextView cnx_state;
    InternetManager internetManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        Pedido.setObservaciones("");
        Pedido.setFolio("");
        Pedido.setListaDeProductos(new ArrayList<>());
        Pedido.preciosConIVA = true;
        Pedido.setTipo("pedido");

        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if(!comprobarSesion()){
            irALogin();
        }
        usuarioActual_txt = findViewById(R.id.rfc_etx);
        cnx_state = findViewById(R.id.cnx_state);
        cnx_state.setText("MODO: Online. V4.2");

        String usuarioActualTexto = "Usuario: " + session.getName();
        usuarioActual_txt.setText(usuarioActualTexto);

        internetManager = new InternetManager(this);
        if(!internetManager.isInternetAvaible()){
            internetNoDisponibleAviso();
        }

        mostrar_usuarios = findViewById(R.id.mostrar_usuario_btn);

        pedido_btn = findViewById(R.id.pedido_btn);
        cotizacion_btn = findViewById(R.id.cotizacion_btn);

        verificador_precio = findViewById(R.id.verificador_precio_button);
        exit_button = findViewById(R.id.exit_button);

        pedido_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pedido("pedido");
            }
        });

        cotizacion_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pedido("cotizacion");
            }
        });

        verificador_precio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irVerificadorDePrecio();
            }
        });

        mostrar_usuarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irAMostrarUsuarios();
            }
        });
        exit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preguntarSiSalir();
            }
        });

    }

    private boolean comprobarSesion() {
        return session.getUsuario().equals("admin") || session.getEmail().equals(user.getEmail());
    }

    private void pedido(String tipoPedido) {

        String title = tipoPedido.equals("pedido") ? "Pedido" : "Cotizacion";
        final String[] tiposCliente = new String[]{"Cliente existente", "Cliente express"};
        final int[] checkedItem = {-1};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setSingleChoiceItems(tiposCliente, checkedItem[0], new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkedItem[0] = which;
                Pedido.setTipo(tipoPedido);
                switch (checkedItem[0]){
                    case 0:
                        irMenuClienteExistente();
                        break;
                    case 1:
                        irMenuClienteNuevo();
                        break;
                    default:
                        irMenuClienteExistente();
                        break;
                }
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog customAlertDialog = builder.create();
        customAlertDialog.show();
    }

    void preguntarSiSalir() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Salir");
        builder.setMessage("Desea salir?");
        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                session.logoutUser();
                signOut();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void irVerificadorDePrecio() {
        Intent i = new Intent(this, Verificador_precio.class);
        startActivity(i);
    }

    private void irMenuClienteExistente() {
        Intent i = new Intent(this, Lista_clientes.class);
        startActivity(i);
    }

    private void irMenuClienteNuevo() {
        Intent i = new Intent(this, Agregar_cliente.class);
        startActivity(i);
    }

    private void irAMostrarUsuarios() {
        Intent i = new Intent(this, Lista_usuarios.class);
        startActivity(i);
    }

    private void signOut() {
        mAuth.signOut();
        session.logoutUser();
    }

    private void irALogin(){
        Intent i = new Intent(getApplicationContext(), Login.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private void irAScanner() {
        Intent i = new Intent(this, QrScanner.class);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    private void irAAdministradorCorreos(){
        Intent i = new Intent(getApplicationContext(), MailManager.class);
        startActivity(i);
    }

    public void internetNoDisponibleAviso(){
        cnx_state.setText("MODO: Offline. V4.2");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.qrScanner:
                irAScanner();
                return true;
            case R.id.mailAdmin:
                irAAdministradorCorreos();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
