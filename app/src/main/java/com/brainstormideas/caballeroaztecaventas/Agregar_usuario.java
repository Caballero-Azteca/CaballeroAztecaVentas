package com.brainstormideas.caballeroaztecaventas;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import static com.brainstormideas.caballeroaztecaventas.Verificador_precio.isInitialized;

public class Agregar_usuario extends AppCompatActivity {

    private EditText nombre_txt;
    private EditText user_txt;
    private EditText email_txt;
    private EditText telefono_txt;
    private EditText pass_txt;
    private EditText second_pass;
    private RadioGroup permisos_rg;


    private Button agregarUsuario_btn;
    private Button atrasUsuario_btn;

    private FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference dbVendedoresReferencia;

    private ProgressDialog progressDialog;

    int numeroVendedores;
    private String permisos = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_usuario);

        getSupportActionBar().setTitle("AGREGAR VENDEDOR");
        initializedFirebaseService();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        numeroVendedores = 0;
        dbVendedoresReferencia = FirebaseDatabase.getInstance().getReference().child("Usuario");

        nombre_txt = findViewById(R.id.name_txt);
        user_txt = findViewById(R.id.user_txt);
        email_txt = findViewById(R.id.email_txt);
        telefono_txt = findViewById(R.id.telefono_txt);
        pass_txt = findViewById(R.id.pass_txt);
        second_pass = findViewById(R.id.second_pass);
        agregarUsuario_btn = findViewById(R.id.agregarUsuario_btn);
        atrasUsuario_btn = findViewById(R.id.atrasUsuario_btn);
        permisos_rg = findViewById(R.id.permisos_rg);

        permisos_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.administrador_rb:
                        permisos = "administrador";
                        break;
                    case R.id.almacen_rb:
                        permisos = "almacen";
                        break;
                    case R.id.chofer_rb:
                        permisos = "chofer";
                        break;
                    case R.id.facturacion_rb:
                        permisos = "facturacion";
                        break;
                    case R.id.superusuario_rb:
                        permisos = "superusuario";
                        break;
                    case R.id.vendedor_rb:
                        permisos = "vendedor";
                        break;
                }
            }
        });

        agregarUsuario_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarUsuario();
            }
        });
        atrasUsuario_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volver();
            }
        });

        obtenerNumeroDeUsuarios();

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

    private void registrarUsuario() {

        final String nombre = nombre_txt.getText().toString().trim();
        final String usuario = user_txt.getText().toString().trim();
        final String email = email_txt.getText().toString().trim();
        final String telefono = telefono_txt.getText().toString().trim();
        final String pass = pass_txt.getText().toString().trim();
        final String pass_second = second_pass.getText().toString().trim();

        if (nombre.isEmpty() || nombre.length() < 3) {
            Toast.makeText(getApplicationContext(), "Debe escribir un nombre de almenos 3 carateres", Toast.LENGTH_SHORT).show();
            return;
        }
        if (usuario.isEmpty() || usuario.length() > 4 || usuario.length() < 2) {
            Toast.makeText(getApplicationContext(), "Ingrese un usuario de entre 2 a 4 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getApplicationContext(), "Debe escribir un email valido", Toast.LENGTH_SHORT).show();
            return;
        }
        if (telefono.isEmpty() || telefono.length() < 10) {
            Toast.makeText(getApplicationContext(), "Ingrese un telefono de almenos 10 digitos.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pass.isEmpty() || pass.length() < 4) {
            Toast.makeText(getApplicationContext(), "Debe escribir una pass de almenos 4 carateres", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pass_second.isEmpty() || pass_second.length() < 4) {
            Toast.makeText(getApplicationContext(), "Vuelva a escribir la pass de almenos 4 carateres", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!pass.equals(pass_second)) {
            Toast.makeText(getApplicationContext(), "Las pass no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        if(permisos.isEmpty()){
            Toast.makeText(getApplicationContext(), "Debe seleccionar los permisos para el usuario", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Registrando nuevo usuario...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        try {
            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        user = mAuth.getCurrentUser();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(nombre).build();
                        user.updateProfile(profileUpdates);
                        DatabaseReference dbTemporalReference = FirebaseDatabase.getInstance().getReference()
                                .child("Usuario").child(user.getUid());
                        dbTemporalReference.child("id").setValue(numeroVendedores);
                        dbTemporalReference.child("nombre").setValue(nombre);
                        dbTemporalReference.child("usuario").setValue(usuario);
                        dbTemporalReference.child("email").setValue(email);
                        dbTemporalReference.child("telefono").setValue(telefono);
                        dbTemporalReference.child("password").setValue(pass);
                        dbTemporalReference.child("folios").push().setValue("folios");
                        dbTemporalReference.child("permisos").setValue(permisos);
                        Toast.makeText(getApplicationContext(), "Ha sido registrado correctamente.",
                                Toast.LENGTH_SHORT).show();
                        mAuth.setLanguageCode("es");
                        user.sendEmailVerification();

                        AlertDialog.Builder builder = new AlertDialog.Builder(Agregar_usuario.this);
                        builder.setCancelable(false);
                        builder.setTitle("Registro exitoso.");
                        builder.setMessage("Se ha registrado exitosamente a Caballero Azteca Ventas. \n\nSe ha enviado un correo electrónico a su cuenta para verificar el mismo. Gracias!");
                        builder.setNeutralButton("Entendido", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                limpiarCampos();
                            }
                        });
                        builder.show();



                    } else {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(getApplicationContext(), "Ya esta registrado una cuenta con este correo.",
                                    Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            return;
                        } else {
                            Toast.makeText(getApplicationContext(), "Fallo al registrar esta cuenta.",
                                    Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            return;
                        }

                    }
                    progressDialog.dismiss();
                }
            });

        }  catch (Exception e){
            Toast.makeText(getApplicationContext(), "No se pudo agregar al usuario. Error: "+ e.toString(), Toast.LENGTH_LONG);
        }
    }

    private void volver() {
        Intent intent = new Intent(this, Lista_usuarios.class);
        startActivity(intent);
    }

    private void obtenerNumeroDeUsuarios() {

        dbVendedoresReferencia.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    long actual = 0;
                    long mayor = 0;
                    for (DataSnapshot data : snapshot.getChildren()) {

                        if (data.child("id").getValue() != null && data.child("nombre").getValue() != null && data.child("usuario").getValue() != null && data.child("telefono").getValue() != null &&
                                data.child("email").getValue() != null && data.child("password").getValue() != null) {

                                actual = Long.parseLong(Objects.requireNonNull(data.child("id").getValue()).toString());
                                if(actual > mayor){
                                    mayor = actual;
                                }
                        }
                        numeroVendedores = (int) mayor;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void limpiarCampos() {
        nombre_txt.setText("");
        user_txt.setText("");
        email_txt.setText("");
        telefono_txt.setText("");
        pass_txt.setText("");
        second_pass.setText("");
    }
}