package com.brainstormideas.caballeroaztecaventas;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.brainstormideas.caballeroaztecaventas.adaptadores.GridAdapter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import static com.brainstormideas.caballeroaztecaventas.Verificador_precio.isInitialized;

public class Marcas extends AppCompatActivity {


    private GridView marcas_gv;
    private GridAdapter gridAdapter;
    private ImageButton home_button;
    private ImageButton direct_home_btn;
    private DatabaseReference marcasReferencia;

    private final ArrayList<String> marcas = new ArrayList<>();

    private String ruta;
    private String tipoCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marcas);

        initializedFirebaseService();
        marcasReferencia = FirebaseDatabase.getInstance().getReference().child("Producto");
        cargarMarcas();

        ruta = getIntent().getExtras().get("ruta").toString();
        tipoCliente = getIntent().getExtras().get("tipoCliente").toString();


        marcas_gv = findViewById(R.id.marcas_gv);
        gridAdapter = new GridAdapter(this, marcas);
        marcas_gv.setAdapter(gridAdapter);


        marcas_gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String marca = gridAdapter.getItem(position).toString();
                irMarca(marca);
            }
        });

        direct_home_btn = findViewById(R.id.direct_home_btn);
        direct_home_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goHome();
            }
        });

        home_button = findViewById(R.id.home_button);
        home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

    public void cargarMarcas() {

        marcasReferencia.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    if (data.child("marca").getValue() != null) {
                        String marca = Objects.requireNonNull(data.child("marca").getValue()).toString();
                        if (!marcas.isEmpty()) {
                            if (!existeMarca(marca)) {
                                marcas.add(marca);
                                gridAdapter.notifyDataSetChanged();
                            }

                        } else {

                            marcas.add(marca);
                            gridAdapter.notifyDataSetChanged();
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ERROR", "NO ES POSIBLE REALIZAR LA LECTURA");
            }
        });
    }

    private void goHome(){
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
    }

    private void irMarca(String marca) {

        Intent i = new Intent(getApplicationContext(), Menu_marca.class);
        i.putExtra("marca", marca);
        i.putExtra("ruta", ruta);
        i.putExtra("tipoCliente", tipoCliente);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);

    }

    private void home() {

        switch (ruta) {

            case "verificadorPrecio":

                Intent int1 = new Intent(getApplicationContext(), Verificador_precio.class);
                int1.putExtra("tipoCliente", tipoCliente);
                int1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(int1);

                break;
            case "MenuPedidos":

                Intent int2 = new Intent(getApplicationContext(), Menu_pedidos.class);
                int2.putExtra("tipoCliente", tipoCliente);
                int2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(int2);

                break;

            default:

                break;
        }


    }

    private boolean existeMarca(String marca) {
        for (String marcaItem : marcas) {
            if (marcaItem.equals(marca)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        home();
    }
}
