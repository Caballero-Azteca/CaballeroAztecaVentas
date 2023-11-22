package com.brainstormideas.caballeroaztecaventas.ui;

import static com.brainstormideas.caballeroaztecaventas.ui.MainActivity.isInitialized;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brainstormideas.caballeroaztecaventas.R;
import com.brainstormideas.caballeroaztecaventas.entidad.ItemUsuario;
import com.brainstormideas.caballeroaztecaventas.ui.adapters.UsuariosAdapter;
import com.brainstormideas.caballeroaztecaventas.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListaUsuarios extends AppCompatActivity {

    RecyclerView recyclerView;
    UsuariosAdapter adapter;
    RecyclerView.LayoutManager manager;

    Button atrasBtn;
    FloatingActionButton cliente_nuevo_button;

    FirebaseAuth mAuth;
    FirebaseUser user;
    SessionManager sessionManager;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference dbUsuariosReferencia;
    ArrayList<ItemUsuario> nombresVendedores = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_usuarios);

        FirebaseApp.initializeApp(this);
        initializedFirebaseService();
        dbUsuariosReferencia = FirebaseDatabase.getInstance().getReference().child("Usuario");
        obtenerUsuarios();

        sessionManager = new SessionManager(this);
        recyclerView = findViewById(R.id.lista_usuarios_scroll);
        manager = new LinearLayoutManager(this);
        adapter = new UsuariosAdapter(this, nombresVendedores);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        cliente_nuevo_button = findViewById(R.id.agregar_usuario_btn);
        cliente_nuevo_button.setOnClickListener(v -> irAgregarUsuario());
        atrasBtn = findViewById(R.id.atrasBtn);
        atrasBtn.setOnClickListener(v -> volver());

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }

    private void initializedFirebaseService() {
        try {
            if (!isInitialized) {
                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                isInitialized = true;
            } else {
                Log.d("ATENCION-FIREBASE:", "Already Initialized");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void obtenerUsuarios() {

        dbUsuariosReferencia.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {

                        if (data.child("nombre").getValue() != null && data.child("usuario").getValue() != null && data.child("telefono").getValue() != null &&
                                data.child("email").getValue() != null && data.child("password").getValue() != null) {

                            String id = data.child("id").getValue().toString();
                            String nombre = data.child("nombre").getValue().toString();
                            String usuario = data.child("usuario").getValue().toString();
                            String numero = data.child("telefono").getValue().toString();
                            String email = data.child("email").getValue().toString();
                            String pass = data.child("password").getValue().toString();

                            ItemUsuario itemUsuario = new ItemUsuario();
                            itemUsuario.setId(id);
                            itemUsuario.setNombre(nombre);
                            itemUsuario.setEmail(email);
                            itemUsuario.setUsuario(usuario);
                            itemUsuario.setNumero(numero);
                            itemUsuario.setPass(pass);

                            nombresVendedores.add(itemUsuario);
                            adapter.notifyDataSetChanged();
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void volver() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void irAgregarUsuario() {
        if (sessionManager.getUsuario().equals("admin")) {
            Intent i = new Intent(this, AgregarUsuario.class);
            startActivity(i);
        } else {
            Toast.makeText(getApplicationContext(), "Usted no es el administrador del sistema.", Toast.LENGTH_LONG).show();
        }

    }

    private void refrescar() {
        Intent i = new Intent(ListaUsuarios.this, ListaUsuarios.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

}